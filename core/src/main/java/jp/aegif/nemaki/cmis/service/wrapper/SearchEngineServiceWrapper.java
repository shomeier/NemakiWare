package jp.aegif.nemaki.cmis.service.wrapper;

import java.util.List;

import org.apache.chemistry.opencmis.commons.data.ExtensionsData;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.server.CallContext;
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

	@Autowired
	private SearchEngineService searchEngineService;

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		System.out.println("--->> YEAH...called getRepositoryInfos");
		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);

		return getWrappedService().getRepositoryInfos(extension);
	}

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		System.out.println("--->> YEAH...called getRepositoryInfo");
		CallContext callContext = getCallContext();
		searchEngineService.updateIndex(callContext);
		return getWrappedService().getRepositoryInfo(repositoryId, extension);
	}
}
