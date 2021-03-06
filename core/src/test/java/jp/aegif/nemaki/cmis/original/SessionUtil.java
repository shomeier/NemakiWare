package jp.aegif.nemaki.cmis.original;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;

public class SessionUtil {

	public static Session createCmisSession(String repositoryId, String userId, String password) {
		// System.setProperty("http.maxConnections", "500");

		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		// TODO enable change a user
		parameter.put(SessionParameter.USER, userId);
		parameter.put(SessionParameter.PASSWORD, password);

		// session locale
		parameter.put(SessionParameter.LOCALE_ISO3166_COUNTRY, "");
		parameter.put(SessionParameter.LOCALE_ISO639_LANGUAGE, "");

		// repository
		parameter.put(SessionParameter.REPOSITORY_ID, repositoryId);
		// parameter.put(org.apache.chemistry.opencmis.commons.impl.Constants.PARAM_REPOSITORY_ID,
		// NemakiConfig.getValue(PropertyKey.NEMAKI_CORE_URI_REPOSITORY));

		parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());

		String coreAtomUri = "http://localhost:8080/core/atom/" + repositoryId;

		// TODO: Get rid of using a cmisselector for browser binding url
		String coreBrowserUri = "http://localhost:8080/core/browser/" + repositoryId + "?cmisselector=repositoryInfo"; // TODO

		parameter.put(SessionParameter.ATOMPUB_URL, coreAtomUri);
		parameter.put(SessionParameter.BROWSER_URL, coreBrowserUri);

		// timeout
		// parameter.put(SessionParameter.CONNECT_TIMEOUT, "30000");
		// parameter.put(SessionParameter.READ_TIMEOUT, "30000");

		// parameter.put(SessionParameter.HTTP_INVOKER_CLASS,
		// "org.apache.chemistry.opencmis.client.bindings.spi.http.ApacheClientHttpInvoker");

		SessionFactory f = SessionFactoryImpl.newInstance();
		Session session = f.createSession(parameter);

		boolean includeAllowableActions = true;
		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext();
		opCtx.setIncludeAllowableActions(includeAllowableActions);

//		OperationContext opCtx = session.createOperationContext(null, false, includeAllowableActions, false,
//				IncludeRelationships.NONE, null, false, null, true, 100);
		session.setDefaultContext(opCtx);

		return session;
	}
}
