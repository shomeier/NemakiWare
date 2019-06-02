package jp.aegif.nemaki.cmis.service.wrapper;

import java.math.BigInteger;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Resource;
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
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfo;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

import jp.aegif.nemaki.cmis.api.AbstractCmisServiceWrapper;
import jp.aegif.nemaki.cmis.api.ServiceCallInterceptor;

@Named
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Order(value = 40000)
public class ServiceCallInterceptorBus extends AbstractCmisServiceWrapper {

	@Resource
	private List<ServiceCallInterceptor> interceptors;

	// --- service methods ---

	private Object intercept(Supplier supplier) {
		CallContext callContext = getCallContext();
		for (ServiceCallInterceptor interceptor : interceptors) {
			interceptor.onServiceCallEnter(callContext);
		}
		Object result = supplier.get();
		for (ServiceCallInterceptor interceptor : interceptors) {
			result = interceptor.onServiceCallExit(callContext, result);
		}

		return result;
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {

		return (List<RepositoryInfo>) intercept(() -> getWrappedService().getRepositoryInfos(extension));
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		return (RepositoryInfo) intercept(() -> getWrappedService().getRepositoryInfo(repositoryId, extension));
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (TypeDefinitionList) intercept(() -> getWrappedService().getTypeChildren(repositoryId, typeId,
				includePropertyDefinitions, maxItems, skipCount, extension));
	}

	@Override
	public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
			Boolean includePropertyDefinitions, ExtensionsData extension) {
		return (List<TypeDefinitionContainer>) intercept(() -> getWrappedService().getTypeDescendants(repositoryId,
				typeId, depth, includePropertyDefinitions, extension));
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
		return (TypeDefinition) intercept(() -> getWrappedService().getTypeDefinition(repositoryId, typeId, extension));
	}

	@Override
	public TypeDefinition createType(String repositoryId, TypeDefinition type, ExtensionsData extension) {
		return (TypeDefinition) intercept(() -> getWrappedService().createType(repositoryId, type, extension));
	}

	@Override
	public TypeDefinition updateType(String repositoryId, TypeDefinition type, ExtensionsData extension) {
		return (TypeDefinition) intercept(() -> getWrappedService().updateType(repositoryId, type, extension));
	}

	@Override
	public void deleteType(String repositoryId, String typeId, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().deleteType(repositoryId, typeId, extension);
			return null;
		});
	}

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (ObjectInFolderList) intercept(
				() -> getWrappedService().getChildren(repositoryId, folderId, filter, orderBy, includeAllowableActions,
						includeRelationships, renditionFilter, includePathSegment, maxItems, skipCount, extension));
	}

	@Override
	public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
		return (List<ObjectInFolderContainer>) intercept(
				() -> getWrappedService().getDescendants(repositoryId, folderId, depth, filter, includeAllowableActions,
						includeRelationships, renditionFilter, includePathSegment, extension));
	}

	@Override
	public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth,
			String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePathSegment, ExtensionsData extension) {
		return (List<ObjectInFolderContainer>) intercept(
				() -> getWrappedService().getFolderTree(repositoryId, folderId, depth, filter, includeAllowableActions,
						includeRelationships, renditionFilter, includePathSegment, extension));
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			Boolean includeRelativePathSegment, ExtensionsData extension) {
		return (List<ObjectParentData>) intercept(
				() -> getWrappedService().getObjectParents(repositoryId, objectId, filter, includeAllowableActions,
						includeRelationships, renditionFilter, includeRelativePathSegment, extension));
	}

	@Override
	public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {
		return (ObjectData) intercept(
				() -> getWrappedService().getFolderParent(repositoryId, folderId, filter, extension));
	}

	@Override
	public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (ObjectList) intercept(() -> getWrappedService().getCheckedOutDocs(repositoryId, folderId, filter,
				orderBy, includeAllowableActions, includeRelationships, renditionFilter, maxItems, skipCount,
				extension));
	}

	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createDocument(repositoryId, properties, folderId,
				contentStream, versioningState, policies, addAces, removeAces, extension));
	}

	@Override
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties, String folderId,
			VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createDocumentFromSource(repositoryId, sourceId, properties,
				folderId, versioningState, policies, addAces, removeAces, extension));
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createFolder(repositoryId, properties, folderId, policies,
				addAces, removeAces, extension));
	}

	@Override
	public String createRelationship(String repositoryId, Properties properties, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createRelationship(repositoryId, properties, policies,
				addAces, removeAces, extension));
	}

	@Override
	public String createPolicy(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createPolicy(repositoryId, properties, folderId, policies,
				addAces, removeAces, extension));
	}

	@Override
	public String createItem(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().createItem(repositoryId, properties, folderId, policies,
				addAces, removeAces, extension));
	}

	@Override
	public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
		return (AllowableActions) intercept(
				() -> getWrappedService().getAllowableActions(repositoryId, objectId, extension));
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		return (ObjectData) intercept(
				() -> getWrappedService().getObject(repositoryId, objectId, filter, includeAllowableActions,
						includeRelationships, renditionFilter, includePolicyIds, includeAcl, extension));
	}

	@Override
	public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
		return (Properties) intercept(
				() -> getWrappedService().getProperties(repositoryId, objectId, filter, extension));
	}

	@Override
	public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (List<RenditionData>) intercept(() -> getWrappedService().getRenditions(repositoryId, objectId,
				renditionFilter, maxItems, skipCount, extension));
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
			IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
			Boolean includeAcl, ExtensionsData extension) {
		return (ObjectData) intercept(
				() -> getWrappedService().getObjectByPath(repositoryId, path, filter, includeAllowableActions,
						includeRelationships, renditionFilter, includePolicyIds, includeAcl, extension));
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset,
			BigInteger length, ExtensionsData extension) {
		return (ContentStream) intercept(() -> getWrappedService().getContentStream(repositoryId, objectId, streamId,
				offset, length, extension));
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			Properties properties, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().updateProperties(repositoryId, objectId, changeToken, properties, extension);
			return null;
		});
	}

	@Override
	public List<BulkUpdateObjectIdAndChangeToken> bulkUpdateProperties(String repositoryId,
			List<BulkUpdateObjectIdAndChangeToken> objectIdsAndChangeTokens, Properties properties,
			List<String> addSecondaryTypeIds, List<String> removeSecondaryTypeIds, ExtensionsData extension) {
		return (List<BulkUpdateObjectIdAndChangeToken>) intercept(
				() -> getWrappedService().bulkUpdateProperties(repositoryId, objectIdsAndChangeTokens, properties,
						addSecondaryTypeIds, removeSecondaryTypeIds, extension));
	}

	@Override
	public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().moveObject(repositoryId, objectId, targetFolderId, sourceFolderId, extension);
			return null;
		});
	}

	@Override
	public void deleteObject(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().deleteObject(repositoryId, objectId, allVersions, extension);
			return null;
		});
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
		return (FailedToDeleteData) intercept(() -> getWrappedService().deleteTree(repositoryId, folderId, allVersions,
				unfileObjects, continueOnFailure, extension));
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag,
			Holder<String> changeToken, ContentStream contentStream, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().setContentStream(repositoryId, objectId, overwriteFlag, changeToken, contentStream,
					extension);
			return null;
		});
	}

	@Override
	public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().deleteContentStream(repositoryId, objectId, changeToken, extension);
			return null;
		});
	}

	@Override
	public void appendContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			ContentStream contentStream, boolean isLastChunk, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().appendContentStream(repositoryId, objectId, changeToken, contentStream, isLastChunk,
					extension);
			return null;
		});
	}

	@Override
	public void checkOut(String repositoryId, Holder<String> objectId, ExtensionsData extension,
			Holder<Boolean> contentCopied) {
		intercept(() -> {
			getWrappedService().checkOut(repositoryId, objectId, extension, contentCopied);
			return null;
		});
	}

	@Override
	public void cancelCheckOut(String repositoryId, String objectId, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().cancelCheckOut(repositoryId, objectId, extension);
			return null;
		});
	}

	@Override
	public void checkIn(String repositoryId, Holder<String> objectId, Boolean major, Properties properties,
			ContentStream contentStream, String checkinComment, List<String> policies, Acl addAces, Acl removeAces,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().checkIn(repositoryId, objectId, major, properties, contentStream, checkinComment,
					policies, addAces, removeAces, extension);
			return null;
		});
	}

	@Override
	public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, Boolean includeAllowableActions, IncludeRelationships includeRelationships,
			String renditionFilter, Boolean includePolicyIds, Boolean includeAcl, ExtensionsData extension) {
		return (ObjectData) intercept(() -> getWrappedService().getObjectOfLatestVersion(repositoryId, objectId,
				versionSeriesId, major, filter, includeAllowableActions, includeRelationships, renditionFilter,
				includePolicyIds, includeAcl, extension));
	}

	@Override
	public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId,
			Boolean major, String filter, ExtensionsData extension) {
		return (Properties) intercept(() -> getWrappedService().getPropertiesOfLatestVersion(repositoryId, objectId,
				versionSeriesId, major, filter, extension));
	}

	@Override
	public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
			Boolean includeAllowableActions, ExtensionsData extension) {
		return (List<ObjectData>) intercept(() -> getWrappedService().getAllVersions(repositoryId, objectId,
				versionSeriesId, filter, includeAllowableActions, extension));
	}

	@Override
	public ObjectList query(String repositoryId, String statement, Boolean searchAllVersions,
			Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (ObjectList) intercept(() -> getWrappedService().query(repositoryId, statement, searchAllVersions,
				includeAllowableActions, includeRelationships, renditionFilter, maxItems, skipCount, extension));
	}

	@Override
	public ObjectList getContentChanges(String repositoryId, Holder<String> changeLogToken, Boolean includeProperties,
			String filter, Boolean includePolicyIds, Boolean includeAcl, BigInteger maxItems,
			ExtensionsData extension) {
		return (ObjectList) intercept(() -> getWrappedService().getContentChanges(repositoryId, changeLogToken,
				includeProperties, filter, includePolicyIds, includeAcl, maxItems, extension));
	}

	@Override
	public void addObjectToFolder(String repositoryId, String objectId, String folderId, Boolean allVersions,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().addObjectToFolder(repositoryId, objectId, folderId, allVersions, extension);
			return null;
		});
	}

	@Override
	public void removeObjectFromFolder(String repositoryId, String objectId, String folderId,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().removeObjectFromFolder(repositoryId, objectId, folderId, extension);
			return null;
		});
	}

	@Override
	public ObjectList getObjectRelationships(String repositoryId, String objectId, Boolean includeSubRelationshipTypes,
			RelationshipDirection relationshipDirection, String typeId, String filter, Boolean includeAllowableActions,
			BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		return (ObjectList) intercept(() -> getWrappedService().getObjectRelationships(repositoryId, objectId,
				includeSubRelationshipTypes, relationshipDirection, typeId, filter, includeAllowableActions, maxItems,
				skipCount, extension));
	}

	@Override
	public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {
		return (Acl) intercept(
				() -> getWrappedService().getAcl(repositoryId, objectId, onlyBasicPermissions, extension));
	}

	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl addAces, Acl removeAces,
			AclPropagation aclPropagation, ExtensionsData extension) {
		return (Acl) intercept(() -> getWrappedService().applyAcl(repositoryId, objectId, addAces, removeAces,
				aclPropagation, extension));
	}

	@Override
	public void applyPolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().applyPolicy(repositoryId, policyId, objectId, extension);
			return null;
		});
	}

	@Override
	public void removePolicy(String repositoryId, String policyId, String objectId, ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().removePolicy(repositoryId, policyId, objectId, extension);
			return null;
		});
	}

	@Override
	public List<ObjectData> getAppliedPolicies(String repositoryId, String objectId, String filter,
			ExtensionsData extension) {
		return (List<ObjectData>) intercept(
				() -> getWrappedService().getAppliedPolicies(repositoryId, objectId, filter, extension));
	}

	@Override
	public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, ExtensionsData extension) {
		return (String) intercept(() -> getWrappedService().create(repositoryId, properties, folderId, contentStream,
				versioningState, policies, extension));
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions,
			ExtensionsData extension) {
		intercept(() -> {
			getWrappedService().deleteObjectOrCancelCheckOut(repositoryId, objectId, allVersions, extension);
			return null;
		});
	}

	@Override
	public Acl applyAcl(String repositoryId, String objectId, Acl aces, AclPropagation aclPropagation) {
		return (Acl) intercept(() -> getWrappedService().applyAcl(repositoryId, objectId, aces, aclPropagation));
	}

	@Override
	public ObjectInfo getObjectInfo(String repositoryId, String objectId) {
		return (ObjectInfo) intercept(() -> getWrappedService().getObjectInfo(repositoryId, objectId));
	}
}
