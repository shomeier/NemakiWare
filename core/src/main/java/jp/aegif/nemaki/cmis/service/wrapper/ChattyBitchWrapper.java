package jp.aegif.nemaki.cmis.service.wrapper;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ProgressControlCmisService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jp.aegif.nemaki.cmis.api.AbstractCmisServiceWrapper;
import jp.aegif.nemaki.util.constant.CallContextKey;
import jp.aegif.nemaki.util.constant.CmisService;

@Component
@Scope("prototype")
// must be after CallContext Decorator In
@Order(value = 45000)
public class ChattyBitchWrapper extends AbstractCmisServiceWrapper {

	private static final Log log = LogFactory.getLog(ChattyBitchWrapper.class);

	@Override
	public ProgressControlCmisService.Progress beforeServiceCall() {

		CallContext callContext = getCallContext();
		CmisService cmisService = (CmisService) callContext.get(CallContextKey.SERVICE);
		log.info("IN -->> " + cmisService);

		if (getWrappedService() instanceof ProgressControlCmisService) {
			return ((ProgressControlCmisService) getWrappedService()).beforeServiceCall();
		}

		return ProgressControlCmisService.Progress.CONTINUE;
	}

	@Override
	public ProgressControlCmisService.Progress afterServiceCall() {

		CallContext callContext = getCallContext();
		CmisService cmisService = (CmisService) callContext.get(CallContextKey.SERVICE);
		log.info("OUT -->> " + cmisService);

		if (getWrappedService() instanceof ProgressControlCmisService) {
			return ((ProgressControlCmisService) getWrappedService()).afterServiceCall();
		}

		return ProgressControlCmisService.Progress.CONTINUE;
	}
}
