/*******************************************************************************
 * Copyright (c) 2013 aegif.
 *
 * This file is part of NemakiWare.
 *
 * NemakiWare is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NemakiWare is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with NemakiWare.
 * If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *     linzhixing(https://github.com/linzhixing) - initial API and implementation
 ******************************************************************************/
package jp.aegif.nemaki.tracker;

import static org.apache.solr.handler.extraction.ExtractingParams.UNKNOWN_FIELD_PREFIX;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.chemistry.opencmis.client.api.ChangeEvent;
import org.apache.chemistry.opencmis.client.api.ChangeEvents;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.spi.CmisBinding;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.UpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CloseHook;
import org.apache.solr.core.SolrCore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.aegif.nemaki.NemakiCoreAdminHandler;
import jp.aegif.nemaki.util.CmisSessionFactory;
import jp.aegif.nemaki.util.Constant;
import jp.aegif.nemaki.util.NemakiCacheManager;
import jp.aegif.nemaki.util.NemakiTokenManager;
import jp.aegif.nemaki.util.PropertyKey;
import jp.aegif.nemaki.util.PropertyManager;
import jp.aegif.nemaki.util.StringPool;
import jp.aegif.nemaki.util.impl.PropertyManagerImpl;
import jp.aegif.nemaki.util.yaml.RepositorySettings;

/**
 * Index tracking class
 *
 * @author linzhixing
 *
 */
public class CoreTracker extends CloseHook {

	private static final Object LOCK = new Object();

	private static final Logger logger = LoggerFactory.getLogger(CoreTracker.class);

	NemakiCoreAdminHandler adminHandler;
	SolrCore core;
	SolrServer indexServer;
	SolrServer tokenServer;

	CmisBinding cmisBinding;
	NemakiTokenManager nemakiTokenManager;
	PropertyManagerImpl propertyManager;

	public CoreTracker(NemakiCoreAdminHandler adminHandler, SolrCore core, SolrServer indexServer,
			SolrServer tokenServer) {
		super();

		this.adminHandler = adminHandler;
		this.core = core;
		this.indexServer = indexServer;
		this.tokenServer = tokenServer;
		this.nemakiTokenManager = new NemakiTokenManager();
		this.propertyManager = new PropertyManagerImpl(StringPool.PROPERTIES_NAME);
	}

	public SolrServer getIndexServer() {
		return indexServer;
	}

	@Override
	public void preClose(SolrCore core) {
	}

	@Override
	public void postClose(SolrCore core) {
	}

	/**
	 * Initialize a specified Solr core
	 */
	public void initCore() {
		synchronized (LOCK) {
			try {
				// Initialize all documents
				indexServer.deleteByQuery("*:*");
				indexServer.commit();
				logger.info("{}:Successfully initialized!", core.getName());

				tokenServer.deleteByQuery("*:*");
				tokenServer.commit();
				logger.info("{}:Successfully initialized!", core.getName());
			} catch (SolrServerException e) {
				logger.error("{}:Initialization failed!", core.getName(), e);
			} catch (IOException e) {
				logger.error("{}:Initialization failed!", core.getName(), e);
			}
		}
	}

	public void initCore(String repositoryId) {
		synchronized (LOCK) {
			try {
				// Initialize all documents
				indexServer.deleteByQuery(Constant.FIELD_REPOSITORY_ID + ":" + repositoryId);
				indexServer.commit();
				logger.info("{}:Successfully initialized!", core.getName());

				storeLatestChangeToken("", repositoryId);

			} catch (SolrServerException e) {
				logger.error("{}:Initialization failed!", core.getName(), e);
			} catch (IOException e) {
				logger.error("{}:Initialization failed!", core.getName(), e);
			}
		}
	}

	/**
	 * Read CMIS change logs and Index them
	 *
	 * @param trackingType
	 */
	public void index(String trackingType) {
		RepositorySettings settings = CmisSessionFactory.getRepositorySettings();
		for (String repositoryId : settings.getIds()) {
			try {
				// TODO multi-threding
				index(trackingType, repositoryId);
			} catch (Exception ex) {
				logger.error("Indexing error repository : {}", repositoryId, ex);
			}
		}
	}

	public void index(String trackingType, String repositoryId) {
		synchronized (LOCK) {
			do {
				ChangeEvents changeEvents = getCmisChangeLog(trackingType, repositoryId);

				if (changeEvents == null) {
					logger.info("change evensts is null");
					return;
				}
				logger.info("size of change events: " + changeEvents.getTotalNumItems());
				logger.info("Start indexing of events : Repo={} Count={}", repositoryId,
						changeEvents.getTotalNumItems());
				List<ChangeEvent> events = changeEvents.getChangeEvents();

				// After 2nd crawling, discard the first item
				// Because the specs say that it's included in the results
				logger.info("actual num of events: " + events.size());
				String token = readLatestChangeToken(repositoryId);
				logger.info("latest token: +" + token);
				if (StringUtils.isNotEmpty(token) && CollectionUtils.isNotEmpty(events)) {
					events.remove(0);
				}
				if (events.isEmpty())
					return;

				// Parse filtering configuration
//				PropertyManager pm = new PropertyManagerImpl(StringPool.PROPERTIES_NAME);
				boolean fulltextEnabled = Boolean.TRUE.toString()
						.equalsIgnoreCase(propertyManager.readValue(PropertyKey.SOLR_TRACKING_FULLTEXT_ENABLED));
				boolean mimeTypeFilterEnabled = false; // default
				List<String> allowedMimeTypeFilter = new ArrayList<String>(); // default
				if (fulltextEnabled) {
					String _filter = propertyManager.readValue(PropertyKey.SOLR_TRACKING_MIMETYPE_FILTER_ENABLED);
					mimeTypeFilterEnabled = Boolean.TRUE.toString().equalsIgnoreCase(_filter);
					if (mimeTypeFilterEnabled) {
						allowedMimeTypeFilter = propertyManager.readValues(PropertyKey.SOLR_TRACKING_MIMETYPE);
					}
				}

				// Extract only the last events of each objectId
				logger.info("extraction start");
				List<ChangeEvent> list = extractChangeEvent(events);
				logger.info("Extracted indexing of events : Repo={} Count={}", repositoryId, list.size());

//				PropertyManager propMgr = new PropertyManagerImpl(StringPool.PROPERTIES_NAME);
				int numberOfThread = Integer
						.valueOf(propertyManager.readValue(PropertyKey.SOLR_TRACKING_NUMBER_OF_THREAD));
				int numberPerThread = list.size() / numberOfThread;
				if (list.size() < numberOfThread || numberPerThread == 0) {
					numberOfThread = list.size();
					numberPerThread = 1;
				}
				int diff = list.size() - (numberOfThread * numberPerThread);
				int toIndex = 0;
				int fromIndex = 0;
				Session cmisSession = CmisSessionFactory.getSession(repositoryId);
				NemakiCacheManager cache = new NemakiCacheManager(repositoryId);
				for (int i = 0; i <= numberOfThread; i++) {
					fromIndex = toIndex;
					toIndex += numberPerThread;
					if (i < diff) {
						toIndex += 1;
					}
					if (toIndex > list.size()) {
						continue;
					}

					List<ChangeEvent> listPerThread = list.subList(fromIndex, toIndex);
					logger.info("Num of change events for this thread : Repo={} Count={}", repositoryId,
							listPerThread.size());
					Registration registration = new Registration(cmisSession, core, indexServer, listPerThread,
							fulltextEnabled, mimeTypeFilterEnabled, allowedMimeTypeFilter, cache);
					Thread t = new Thread(registration);
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						logger.error("Thred interuptted! : Repo={} Ex={}", repositoryId, e);
					}

				}

				// Save the latest token
				storeLatestChangeToken(changeEvents.getLatestChangeLogToken(), repositoryId);

			} while (Constant.MODE_FULL.equals(trackingType));// In case of FUll mode, repeat until indexing all change
																// logs
		}
	}

	/**
	 * Get the last change token stored in Solr
	 *
	 * @return
	 */
	private String readLatestChangeToken(String repositoryId) {
		logger.info("Start readLatest : {}", repositoryId);
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(Constant.FIELD_REPOSITORY_ID + ":" + repositoryId);

		QueryResponse resp = null;
		try {
			resp = tokenServer.query(solrQuery);
		} catch (SolrServerException e) {
			logger.error("Read latest ChangeToken query failed : {} ", solrQuery, e);
		}

		String latestChangeToken = "";
		if (resp != null && resp.getResults() != null && resp.getResults().getNumFound() != 0) {
			SolrDocument doc = resp.getResults().get(0);
			latestChangeToken = (String) doc.get(Constant.FIELD_TOKEN);
		} else {
			logger.info("No latest change token found for repository: {}", repositoryId);
			logger.info("Set blank latest change token for repository: {}", repositoryId);
			storeLatestChangeToken("", repositoryId);
		}
		return latestChangeToken;
	}

	/**
	 * Store the last change token in Solr
	 *
	 * @return
	 */
	private void storeLatestChangeToken(String token, String repositoryId) {
		logger.info("Start storeLatestChangeToken");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(Constant.FIELD_REPOSITORY_ID, repositoryId);
		map.put(Constant.FIELD_TOKEN, token);

		AbstractUpdateRequest req = buildUpdateRequest(map);

		try {
			tokenServer.request(req);
		} catch (SolrServerException e) {
			logger.error("Failed to store latest change token in Solr!", e);
		} catch (IOException e) {
			logger.error("Failed to store latest change token in Solr!", e);
		}
	}

	/**
	 * Get CMIS change logs
	 *
	 * @param trackingType
	 * @return
	 */
	private ChangeEvents getCmisChangeLog(String trackingType, String repositoryId) {
		PropertyManager propMgr = new PropertyManagerImpl(StringPool.PROPERTIES_NAME);
		logger.info("Start getCmisChangeLog : Repo={} Type={}", repositoryId, trackingType);
		String _latestToken = readLatestChangeToken(repositoryId);
		String latestToken = (StringUtils.isEmpty(_latestToken)) ? null : _latestToken;

		long _numItems = 0;
		if (Constant.MODE_DELTA.equals(trackingType)) {
			_numItems = Long.valueOf(propMgr.readValue(PropertyKey.CMIS_CHANGELOG_ITEMS_DELTA));
		} else if (Constant.MODE_FULL.equals(trackingType)) {
			_numItems = Long.valueOf(propMgr.readValue(PropertyKey.CMIS_CHANGELOG_ITEMS_FULL));
		}

		long numItems = (-1 == _numItems) ? Long.MAX_VALUE : Long.valueOf(_numItems);
		logger.info("Call CMIS LastChangeToken={} Items={}", latestToken, numItems);

		Session cmisSession = CmisSessionFactory.getSession(repositoryId);
		logger.info("Session aquired");
		if (cmisSession == null) {
			logger.info("Cannot create cmis session to {}.", repositoryId);
			return null;
		}

		try {
			// No need for Sorting
			// (Specification requires they are returned by ASCENDING)
			return cmisSession.getContentChanges(latestToken, false, numItems);
		} catch (CmisRuntimeException ex) {
			// On error reset session.
			CmisSessionFactory.clearSession(repositoryId);
			throw ex;
		}
	}

	/**
	 *
	 * @param events
	 * @return
	 */
	private List<ChangeEvent> extractChangeEvent(List<ChangeEvent> events) {
		List<ChangeEvent> list = new ArrayList<ChangeEvent>();
		Set<String> objectIds = new HashSet<String>();

		int size = events.size();
		ListIterator<ChangeEvent> iterator = events.listIterator(size);
		while (iterator.hasPrevious()) {
			ChangeEvent event = iterator.previous();
			if (objectIds.contains(event.getObjectId())) {
				continue;
			} else {
				objectIds.add(event.getObjectId());
				list.add(event);
			}
		}

		Collections.reverse(list);
		return list;
	}

	/**
	 * Build an update request to Solr without file
	 *
	 * @param content
	 * @return
	 */
	// TODO Unify that of Registration class
	private AbstractUpdateRequest buildUpdateRequest(Map<String, Object> map) {
		logger.info("Start buildUpdateRequest");
		UpdateRequest up = new UpdateRequest();
		SolrInputDocument sid = new SolrInputDocument();

		// Set SolrDocument parameters
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			sid.addField(key, map.get(key));
		}

		// Set UpdateRequest
		up.add(sid);
		// Ignored(for schema.xml, ignoring some SolrCell meta fields)
		up.setParam(UNKNOWN_FIELD_PREFIX, "ignored_");

		// Set Solr action parameter
		up.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		logger.info(up.toString());
		return up;
	}
}