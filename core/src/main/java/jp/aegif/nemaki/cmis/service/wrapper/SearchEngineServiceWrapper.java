package jp.aegif.nemaki.cmis.service.wrapper;

import java.util.List;

import org.apache.chemistry.opencmis.commons.data.Acl;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.FailedToDeleteData;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jp.aegif.nemaki.cmis.api.AbstractCmisServiceWrapper;
import jp.aegif.nemaki.cmis.api.SearchEngineService;

@Component
@Scope("prototype")
@Order(value = 10000)
public class SearchEngineServiceWrapper extends AbstractCmisServiceWrapper {

	private static final Log log = LogFactory.getLog(SearchEngineServiceWrapper.class);

	@Autowired
	private SearchEngineService searchEngineService;

	@Override
	public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
			VersioningState versioningState, List<String> policies, ExtensionsData extension) {

		String cmisId = getWrappedService().create(repositoryId, properties, folderId, contentStream, versioningState,
				policies, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return cmisId;
	}

	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId,
			ContentStream contentStream, VersioningState versioningState, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {

		String cmisId = getWrappedService().createDocument(repositoryId, properties, folderId, contentStream,
				versioningState, policies, addAces, removeAces, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return cmisId;
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies,
			Acl addAces, Acl removeAces, ExtensionsData extension) {
		String cmisId = getWrappedService().createFolder(repositoryId, properties, folderId, policies, addAces,
				removeAces, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return cmisId;
	}

	@Override
	public String createRelationship(String repositoryId, Properties properties, List<String> policies, Acl addAces,
			Acl removeAces, ExtensionsData extension) {
		String cmisId = getWrappedService().createRelationship(repositoryId, properties, policies, addAces, removeAces,
				extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return cmisId;
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken,
			Properties properties, ExtensionsData extension) {
		getWrappedService().updateProperties(repositoryId, objectId, changeToken, properties, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);
	}

	@Override
	public void deleteObject(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {

		getWrappedService().deleteObject(repositoryId, objectId, allVersions, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions,
			UnfileObject unfileObjects, Boolean continueOnFailure, ExtensionsData extension) {
		FailedToDeleteData deleteTree = getWrappedService().deleteTree(repositoryId, folderId, allVersions,
				unfileObjects, continueOnFailure, extension);

		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return deleteTree;
	}
}
