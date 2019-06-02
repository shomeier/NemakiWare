package jp.aegif.nemaki.cmis.api;

import org.apache.chemistry.opencmis.commons.server.CallContext;

public interface ServiceCallInterceptor {

	void onServiceCallEnter(CallContext callContext);

	Object onServiceCallExit(CallContext callContext, Object result);
}
