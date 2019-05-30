package jp.aegif.nemaki.cmis.api;

import org.apache.chemistry.opencmis.commons.server.CallContext;

public interface SearchEngineService {
	void updateIndex(CallContext callContext);
}
