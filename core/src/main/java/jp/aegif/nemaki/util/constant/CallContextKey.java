package jp.aegif.nemaki.util.constant;

import org.apache.chemistry.opencmis.commons.server.CallContext;

public interface CallContextKey {
	final String IS_ADMIN = "is_admin";
	final String IS_SU = "is_su";

	// service params
	final String SERVICE = "service";
	final String SERVICE_PARAMS = "service_params";

	final String PARAM_REPOSITORY_ID = CallContext.REPOSITORY_ID;
	final String PARAM_TYPE_ID = "typeId";
	final String PARAM_FOLDER_ID = "folderId";
	final String PARAM_TYPE = "type";
	final String PARAM_DEPTH = "depth";
	final String PARAM_INCLUDE_PROPERTY_DEFINITIONS = "includePropertyDefinitions";
	final String PARAM_MAX_ITEMS = "maxItems";
	final String PARAM_SKIP_COUNT = "skipCount";
	final String PARAM_FILTER = "filter";
	final String PARAM_INCLUDE_ALLOWABLE = "orderBy";
	final String PARAM_EXTENSION = "extension";

	// Auth token
	final String AUTH_TOKEN = "nemaki_auth_token";
	final String AUTH_TOKEN_APP = "nemaki_auth_token_app";
}