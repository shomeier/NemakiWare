package jp.aegif.nemaki.cmis.query.solr;

import java.text.MessageFormat;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.aegif.nemaki.cmis.api.SearchEngineService;
import jp.aegif.nemaki.cmis.aspect.query.solr.SolrUtil;

@Component
public class SolrSearchEngineService implements SearchEngineService {

	private static final Log log = LogFactory.getLog(SolrSearchEngineService.class);

	@Autowired
	private SolrUtil solrUtil;

	@Override
	public void updateIndex(CallContext callContext) {
		log.debug(MessageFormat.format("Updating Solr index for repository with id={0} ...",
				callContext.getRepositoryId()));

		// this call is done synchronously
		solrUtil.callSolrIndexingNew(callContext.getRepositoryId());
		log.debug(MessageFormat.format("... successfully updated Solr index for repository with id={0}",
				callContext.getRepositoryId()));
	}

}
