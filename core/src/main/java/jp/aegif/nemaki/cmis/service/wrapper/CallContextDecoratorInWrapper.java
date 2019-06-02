package jp.aegif.nemaki.cmis.service.wrapper;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.AllowableActions;
import org.apache.chemistry.opencmis.commons.data.BulkUpdateObjectIdAndChangeToken;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderContainer;
import org.apache.chemistry.opencmis.commons.data.ObjectInFolderList;
import org.apache.chemistry.opencmis.commons.data.ObjectList;
import org.apache.chemistry.opencmis.commons.data.ObjectParentData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RenditionData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.AclPropagation;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.server.MutableCallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

import jp.aegif.nemaki.cmis.api.AbstractCmisServiceWrapper;
import jp.aegif.nemaki.util.constant.CallContextKey;
import jp.aegif.nemaki.util.constant.CmisService;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
// must be one of the very first wrappers
@Order(value = 50000)
public class CallContextDecoratorInWrapper extends AbstractCmisServiceWrapper {
	private static final Log log = LogFactory.getLog(CallContextDecoratorInWrapper.class);

	public CallContextDecoratorInWrapper() {
		super();
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_REPOSITORY_INFOS);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.getRepositoryInfos(extension);
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_REPOSITORY_INFO);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.getRepositoryInfo(repositoryId, extension);
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_TYPE_CHILDREN);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE_ID, typeId);
		params.put(CallContextKey.PARAM_INCLUDE_PROPERTY_DEFINITIONS, includePropertyDefinitions);
		params.put(CallContextKey.PARAM_MAX_ITEMS, maxItems);
		params.put(CallContextKey.PARAM_SKIP_COUNT, skipCount);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.getTypeChildren(repositoryId, typeId, includePropertyDefinitions, maxItems, skipCount, extension);
	}

	@Override
	public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_TYPE_DESCENDANTS);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE_ID, typeId);
		params.put(CallContextKey.PARAM_DEPTH, depth);
		params.put(CallContextKey.PARAM_INCLUDE_PROPERTY_DEFINITIONS, includePropertyDefinitions);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.getTypeDescendants(repositoryId, typeId, depth, includePropertyDefinitions, extension);
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_TYPE_DEFINITION);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE_ID, typeId);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.getTypeDefinition(repositoryId, typeId, extension);
	}

	@Override
	public TypeDefinition createType(String repositoryId, TypeDefinition type, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_TYPE);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE, type);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.createType(repositoryId, type, extension);
	}

	@Override
	public TypeDefinition updateType(String repositoryId, TypeDefinition type, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.UPDATE_TYPE);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE, type);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		return super.updateType(repositoryId, type, extension);
	}

	@Override
	public void deleteType(String repositoryId, String typeId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.DELETE_TYPE);
		Map<String, Object> params = new HashMap<>();
		params.put(CallContextKey.PARAM_REPOSITORY_ID, repositoryId);
		params.put(CallContextKey.PARAM_TYPE_ID, typeId);
		params.put(CallContextKey.PARAM_EXTENSION, extension);
		callContext.put(CallContextKey.SERVICE_PARAMS, params);
		// setCallContext(callContext);

		super.deleteType(repositoryId, typeId, extension);
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_CHILDREN);
		// setCallContext(callContext);

		return super.getChildren(repositoryId, folderId, filter, orderBy, includeAllowableActions, includeRelationships,
				renditionFilter, includePathSegment, maxItems, skipCount, extension);
	}

	@Override
	public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_DESCENDANTS);
		// setCallContext(callContext);

		return super.getDescendants(repositoryId, folderId, depth, filter, includeAllowableActions,
				includeRelationships, renditionFilter, includePathSegment, extension);
	}

	@Override
	public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_FOLDER_TREE);
		// setCallContext(callContext);

		return super.getFolderTree(repositoryId, folderId, depth, filter, includeAllowableActions, includeRelationships,
				renditionFilter, includePathSegment, extension);
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT_PARENTS);
		// setCallContext(callContext);

		return super.getObjectParents(repositoryId, objectId, filter, includeAllowableActions, includeRelationships,
				renditionFilter, includeRelativePathSegment, extension);
	}

	@Override
	public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_FOLDER_PARENT);
		// setCallContext(callContext);

		return super.getFolderParent(repositoryId, folderId, filter, extension);
	}

	@Override
	public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_CHECKED_OUT_DOCS);
		// setCallContext(callContext);

		return super.getCheckedOutDocs(repositoryId, folderId, filter, orderBy, includeAllowableActions,
				includeRelationships, renditionFilter, maxItems, skipCount, extension);
	}

	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_DOCUMENT);
		// setCallContext(callContext);

		return super.createDocument(repositoryId, properties, folderId, contentStream, versioningState, policies,
				addAces, removeAces, extension);
	}

	@Override
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_DOCUMENT_FROM_SOURCE);
		// setCallContext(callContext);

		return super.createDocumentFromSource(repositoryId, sourceId, properties, folderId, versioningState, policies,
				addAces, removeAces, extension);
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_FOLDER);
		// setCallContext(callContext);

		return super.createFolder(repositoryId, properties, folderId, policies, addAces, removeAces, extension);
	}

	@Override
	public String createRelationship(String repositoryId, Properties properties, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_RELATIONSHIP);
		// setCallContext(callContext);

		return super.createRelationship(repositoryId, properties, policies, addAces, removeAces, extension);
	}

	@Override
	public String createPolicy(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_POLICY);
		// setCallContext(callContext);

		return super.createPolicy(repositoryId, properties, folderId, policies, addAces, removeAces, extension);
	}

	@Override
	public String createItem(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE_ITEM);
		// setCallContext(callContext);

		return super.createItem(repositoryId, properties, folderId, policies, addAces, removeAces, extension);
	}

	@Override
	public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_ALLOWABLE_ACTIONS);
		// setCallContext(callContext);

		return super.getAllowableActions(repositoryId, objectId, extension);
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT);
		// setCallContext(callContext);

		return super.getObject(repositoryId, objectId, filter, includeAllowableActions, includeRelationships,
				renditionFilter, includePolicyIds, includeAcl, extension);
	}

	@Override
	public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_PROPERTIES);
		// setCallContext(callContext);

		return super.getProperties(repositoryId, objectId, filter, extension);
	}

	@Override
	public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_RENDITIONS);
		// setCallContext(callContext);

		return super.getRenditions(repositoryId, objectId, renditionFilter, maxItems, skipCount, extension);
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT_BY_PATH);
		// setCallContext(callContext);

		return super.getObjectByPath(repositoryId, path, filter, includeAllowableActions, includeRelationships,
				renditionFilter, includePolicyIds, includeAcl, extension);
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
			BigInteger length, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_CONTENT_STREAM);
		// setCallContext(callContext);

		return super.getContentStream(repositoryId, objectId, streamId, offset, length, extension);
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			Properties properties, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.UPDATE_PROPERTIES);
		// setCallContext(callContext);

		super.updateProperties(repositoryId, objectId, changeToken, properties, extension);
	}

	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String repositoryId,
			List<BulkUpdateObjectIdAndChangeToken> objectIdsAndChangeTokens, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.BULK_UPDATE_PROPERTIES);
		// setCallContext(callContext);

		return super.bulkUpdateProperties(repositoryId, objectIdsAndChangeTokens, properties, addSecondaryTypeIds,
				removeSecondaryTypeIds, extension);
	}

	@Override
	public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.MOVE_OBJECT);
		// setCallContext(callContext);

		super.moveObject(repositoryId, objectId, targetFolderId, sourceFolderId, extension);
	}

	@Override
	public void deleteObject(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.DELETE_OBJECT);
		// setCallContext(callContext);

		super.deleteObject(repositoryId, objectId, allVersions, extension);
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.DELETE_TREE);
		// setCallContext(callContext);

		return super.deleteTree(repositoryId, folderId, allVersions, unfileObjects, continueOnFailure, extension);
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.SET_CONTENT_STREAM);
		// setCallContext(callContext);

		super.setContentStream(repositoryId, objectId, overwriteFlag, changeToken, contentStream, extension);
	}

	@Override
	public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.DELETE_CONTENT_STREAM);
		// setCallContext(callContext);

		super.deleteContentStream(repositoryId, objectId, changeToken, extension);
	}

	@Override
	public void appendContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ContentStream contentStream, boolean isLastChunk, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.APPEND_CONTENT_STREAM);
		// setCallContext(callContext);

		super.appendContentStream(repositoryId, objectId, changeToken, contentStream, isLastChunk, extension);
	}

	@Override
	public void checkOut(String repositoryId, Holder<String> objectId, ExtensionsData extension,
			Holder<Boolean> contentCopied) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CHECK_OUT);
		// setCallContext(callContext);

		super.checkOut(repositoryId, objectId, extension, contentCopied);
	}

	@Override
	public void cancelCheckOut(String repositoryId, String objectId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CANCEL_CHECK_OUT);
		// setCallContext(callContext);

		super.cancelCheckOut(repositoryId, objectId, extension);
	}

	@Override
	public void checkIn(String repositoryId, Holder<String> objectId, Boolean major, Properties properties,
			ContentStream contentStream, String checkinComment, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CHECK_IN);
		// setCallContext(callContext);

		super.checkIn(repositoryId, objectId, major, properties, contentStream, checkinComment, policies, addAces,
				removeAces, extension);
	}

	@Override
	public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT_OF_LATEST_VERSION);
		// setCallContext(callContext);

		return super.getObjectOfLatestVersion(repositoryId, objectId, versionSeriesId, major, filter,
				includeAllowableActions, includeRelationships, renditionFilter, includePolicyIds, includeAcl,
				extension);
	}

	@Override
	public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_PROPERTIES_OF_LATEST_VERSION);
		// setCallContext(callContext);

		return super.getPropertiesOfLatestVersion(repositoryId, objectId, versionSeriesId, major, filter, extension);
	}

	@Override
	public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
			Boolean includeAllowableActions, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_ALL_VERSIONS);
		// setCallContext(callContext);

		return super.getAllVersions(repositoryId, objectId, versionSeriesId, filter, includeAllowableActions,
				extension);
	}

	@Override
	public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.QUERY);
		// setCallContext(callContext);

		return super.query(repositoryId, statement, searchAllVersions, includeAllowableActions, includeRelationships,
				renditionFilter, maxItems, skipCount, extension);
	}

	@Override
	public ObjectList getContentChanges(String repositoryId, Holder<String> changeLogToken, Boolean includeProperties,
			String filter, Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_CONTENT_CHANGES);
		// setCallContext(callContext);

		return super.getContentChanges(repositoryId, changeLogToken, includeProperties, filter, includePolicyIds,
				includeAcl, maxItems, extension);
	}

	@Override
	public void addObjectToFolder(String repositoryId, String objectId, String folderId, Boolean allVersions,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.ADD_OBJECT_TO_FOLDER);
		// setCallContext(callContext);

		super.addObjectToFolder(repositoryId, objectId, folderId, allVersions, extension);
	}

	@Override
	public void removeObjectFromFolder(String repositoryId, String objectId, String folderId,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.REMOVE_OBJECT_FROM_FOLDER);
		// setCallContext(callContext);

		super.removeObjectFromFolder(repositoryId, objectId, folderId, extension);
	}

	@Override
	public ObjectList getObjectRelationships(String repositoryId, String objectId, Boolean includeSubRelationshipTypes,
			RelationshipDirection relationshipDirection, String typeId, String filter, Boolean includeAllowableActions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT_RELATIONSHIPS);
		// setCallContext(callContext);

		return super.getObjectRelationships(repositoryId, objectId, includeSubRelationshipTypes, relationshipDirection,
				typeId, filter, includeAllowableActions, maxItems, skipCount, extension);
	}

	@Override
	public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_ACL);
		// setCallContext(callContext);

		return super.getAcl(repositoryId, objectId, onlyBasicPermissions, extension);
	}

	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl addAces, Acl removeAces,
			AclPropagation aclPropagation, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.APPLY_ACL);
		// setCallContext(callContext);

		return super.applyAcl(repositoryId, objectId, addAces, removeAces, aclPropagation, extension);
	}

	@Override
	public void applyPolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.APPLY_POLICY);
		// setCallContext(callContext);

		super.applyPolicy(repositoryId, policyId, objectId, extension);
	}

	@Override
	public void removePolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.REMOVE_POLICY);
		// setCallContext(callContext);

		super.removePolicy(repositoryId, policyId, objectId, extension);
	}

	@Override
	public List<ObjectData> getAppliedPolicies(String repositoryId, String objectId, String filter,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_APPLIED_POLICIES);
		// setCallContext(callContext);

		return super.getAppliedPolicies(repositoryId, objectId, filter, extension);
	}

	@Override
	public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.CREATE);
		// setCallContext(callContext);

		return super.create(repositoryId, properties, folderId, contentStream, versioningState, policies, extension);
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
			ExtensionsData extension) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.DELETE_OBJECT_OR_CANCEL_CHECKOUT);
		// setCallContext(callContext);

		super.deleteObjectOrCancelCheckOut(repositoryId, objectId, allVersions, extension);
	}

	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl aces, AclPropagation aclPropagation) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();

		// TODO: THERE ARE 2 ACLS Calls!! Check the difference!!
		callContext.put(CallContextKey.SERVICE, CmisService.APPLY_ACL);
		// setCallContext(callContext);

		return super.applyAcl(repositoryId, objectId, aces, aclPropagation);
	}

	@Override
	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {

		MutableCallContext callContext = (MutableCallContext) getCallContext();
		callContext.put(CallContextKey.SERVICE, CmisService.GET_OBJECT_INFO);
		// setCallContext(callContext);

		return super.getObjectInfo(repositoryId, objectId);
	}

	@Override
	public void close() {
		super.close();
	}
}
