package jp.aegif.nemaki.cmis.service.interceptor;

import javax.inject.Named;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.Order;

import jp.aegif.nemaki.cmis.api.ServiceCallInterceptor;
import jp.aegif.nemaki.util.constant.CallContextKey;
import jp.aegif.nemaki.util.constant.CmisService;

@Named
@Order(value = 50000)
public class ChattyBitch implements ServiceCallInterceptor {

	private static final Log log = LogFactory.getLog(ChattyBitch.class);

	@Override
	public void onServiceCallEnter(CallContext callContext) {
		CmisService cmisService = (CmisService) callContext.get(CallContextKey.SERVICE);
		log.info("\nIN -->> " + cmisService);
	}

	@Override
	public Object onServiceCallExit(CallContext callContext, Object result) {

		CmisService cmisService = (CmisService) callContext.get(CallContextKey.SERVICE);
		log.info("\nOUT <<-- " + cmisService + "\n\tResult: " + result);

		return result;
	}

}
