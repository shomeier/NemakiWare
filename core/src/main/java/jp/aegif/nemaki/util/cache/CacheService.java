package jp.aegif.nemaki.util.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.chemistry.opencmis.commons.data.ObjectData;

import jp.aegif.nemaki.model.Acl;
import jp.aegif.nemaki.model.AttachmentNode;
import jp.aegif.nemaki.model.Change;
import jp.aegif.nemaki.model.Configuration;
import jp.aegif.nemaki.model.Content;
import jp.aegif.nemaki.model.GroupItem;
import jp.aegif.nemaki.model.NemakiTypeDefinition;
import jp.aegif.nemaki.model.UserItem;
import jp.aegif.nemaki.model.VersionSeries;
import jp.aegif.nemaki.util.SpringPropertyManager;
import jp.aegif.nemaki.util.YamlManager;
import jp.aegif.nemaki.util.cache.model.NemakiCache;
import jp.aegif.nemaki.util.cache.model.Tree;
import jp.aegif.nemaki.util.constant.PropertyKey;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class CacheService {
	private final CacheManager cacheManager;
	private final Map<String, Boolean> enabled = new HashMap<>();
	private final String CONFIG_CACHE = "configCache";
	private final String OBJECT_DATA_CACHE = "objectDataCache";
	private final String PROPERTIES_CACHE = "propertisCache";
	private final String TYPE_CACHE = "typeCache";
	private final String CONTENT_CACHE = "contentCache";
	private final String TREE_CACHE = "treeCache";
	private final String VERSION_SERIES_CACHE = "versionSeriesCache";
	private final String ATTACHMENTS_CACHE = "attachmentCache";
	private final String CHANGE_EVENT_CACHE = "changeEventCache";
	private final String LATEST_CHANGE_TOKEN_CACHE = "latestChangeTokenCache";
	private final String USER_CACHE = "userCache";
	private final String USERS_CACHE = "usersCache";
	private final String GROUP_CACHE = "groupCache";
	private final String GROUPS_CACHE = "groupsCache";

	private final String ACL_CACHE = "aclCache";
	private final String JOINED_GROUP_CACHE = "joinedGroupCache";

	private final String repositoryId;

	public CacheService(String repositoryId, SpringPropertyManager propertyManager) {
		this.repositoryId = repositoryId;

		cacheManager = CacheManager.newInstance();

		loadConfig(propertyManager);
	}

	private void loadConfig(SpringPropertyManager propertyManager) {
		String configFile = propertyManager.readValue(PropertyKey.CACHE_CONFIG);
		YamlManager manager = new YamlManager(configFile);
		Map<String, Map<String, Object>> yml = (Map<String, Map<String, Object>>) manager.loadYml();

		// default
		Map<String, Object> defaultConfigMap = yml.get("default");
		yml.remove("default");

		for (Entry<String, Map<String, Object>> configMap : yml.entrySet()) {
			NemakiCacheConfig config = new NemakiCacheConfig();
			config.override(defaultConfigMap);
			if (configMap.getValue() != null) {
				config.override(configMap.getValue());
			}

			Cache cache = new Cache(repositoryId + "_" + configMap.getKey(), config.maxElementsInMemory.intValue(),
					config.overflowToDisc, config.eternal, config.timeToLiveSeconds, config.timeToIdleSeconds);
			cacheManager.addCache(cache);
			enabled.put(cache.getName(), config.cacheEnabled);
		}
	}

	private class NemakiCacheConfig {
		private Boolean cacheEnabled;
		private Long maxElementsInMemory;
		private Boolean overflowToDisc;
		private Boolean eternal;
		private Long timeToLiveSeconds;
		private Long timeToIdleSeconds;

		private void override(Map<String, Object> map) {
			if (map.get("cacheEnabled") != null)
				cacheEnabled = (Boolean) map.get("cacheEnabled");
			if (map.get("maxElementsInMemory") != null)
				maxElementsInMemory = (Long) map.get("maxElementsInMemory");
			if (map.get("overflowToDisc") != null)
				overflowToDisc = (Boolean) map.get("overflowToDisc");
			if (map.get("eternal") != null)
				eternal = (Boolean) map.get("eternal");
			if (map.get("timeToLiveSeconds") != null)
				timeToLiveSeconds = (Long) map.get("timeToLiveSeconds");
			if (map.get("timeToIdleSeconds") != null)
				timeToIdleSeconds = (Long) map.get("timeToIdleSeconds");
		}
	}

	public NemakiCache<Configuration> getConfigCache() {
		String name = repositoryId + "_" + CONFIG_CACHE;
		return new NemakiCache<Configuration>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<ObjectData> getObjectDataCache() {
		String name = repositoryId + "_" + OBJECT_DATA_CACHE;
		return new NemakiCache<ObjectData>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<?> getPropertiesCache() {
		String name = repositoryId + "_" + PROPERTIES_CACHE;
		return new NemakiCache<>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<List<NemakiTypeDefinition>> getTypeCache() {
		String name = repositoryId + "_" + TYPE_CACHE;
		return new NemakiCache<>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<Content> getContentCache() {
		String name = repositoryId + "_" + CONTENT_CACHE;
		return new NemakiCache<>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<Tree> getTreeCache() {
		String name = repositoryId + "_" + TREE_CACHE;
		return new NemakiCache<Tree>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<VersionSeries> getVersionSeriesCache() {
		String name = repositoryId + "_" + VERSION_SERIES_CACHE;
		return new NemakiCache<VersionSeries>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<AttachmentNode> getAttachmentCache() {
		String name = repositoryId + "_" + ATTACHMENTS_CACHE;
		return new NemakiCache<AttachmentNode>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<ObjectData> getChangeEventCache() {
		String name = repositoryId + "_" + CHANGE_EVENT_CACHE;
		return new NemakiCache<ObjectData>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<Change> getLatestChangeTokenCache() {
		String name = repositoryId + "_" + LATEST_CHANGE_TOKEN_CACHE;
		return new NemakiCache<Change>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<UserItem> getUserItemCache() {
		String name = repositoryId + "_" + USER_CACHE;
		return new NemakiCache<UserItem>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<List<UserItem>> getUserItemsCache() {
		String name = repositoryId + "_" + USERS_CACHE;
		return new NemakiCache<List<UserItem>>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<GroupItem> getGroupItemCache() {
		String name = repositoryId + "_" + GROUP_CACHE;
		return new NemakiCache<GroupItem>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<List<GroupItem>> getGroupsCache() {
		String name = repositoryId + "_" + GROUPS_CACHE;
		return new NemakiCache<List<GroupItem>>(enabled.get(name), cacheManager.getCache(name));
	}

	/***
	 * Acl cache related tree cache.
	 * 
	 * @see jp.aegif.nemaki.businesslogic.impl.ContentServiceImpl.calculateAcl(String,
	 *      Content)
	 * @see jp.aegif.nemaki.cmis.service.impl.AclServiceImpl.applyAcl(CallContext,
	 *      String, String, Acl, AclPropagation)
	 */
	public NemakiCache<Acl> getAclCache() {
		String name = repositoryId + "_" + ACL_CACHE;
		return new NemakiCache<Acl>(enabled.get(name), cacheManager.getCache(name));
	}

	public NemakiCache<List<String>> getJoinedGroupCache() {
		String name = repositoryId + "_" + JOINED_GROUP_CACHE;
		return new NemakiCache<List<String>>(enabled.get(name), cacheManager.getCache(name));
	}

	public void removeCmisCache(String objectId) {
		getObjectDataCache().remove(objectId);
	}

	public void removeCmisAndContentCache(String objectId) {
		getContentCache().remove(objectId);
		getAclCache().remove(objectId);
		removeCmisCache(objectId);
	}
}