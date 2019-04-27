package jp.aegif.nemaki.util;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class NemakiTokenManager {
	private static final Logger logger = LoggerFactory.getLogger(NemakiTokenManager.class);

	private final String restEndpoint;
	private Map<String, Token> tokenMap;

	public NemakiTokenManager() {
		tokenMap = new HashMap<String, Token>();

		restEndpoint = NemakiServer.getRestEndpoint();
	}

	public String register(String repositoryId, String userName, String password) {
		String apiResult = null;
		String restUri = getRestUri(repositoryId);
		try {
			Client c = Client.create();
			c.setConnectTimeout(3 * 1000);
			c.setReadTimeout(5 * 1000);
			c.setFollowRedirects(Boolean.TRUE);
			c.addFilter(new HTTPBasicAuthFilter(userName, password));

			apiResult = c.resource(restUri).path(userName + "/register").queryParam("app", "solr")
					.accept(MediaType.APPLICATION_JSON_TYPE).get(String.class);
		} catch (Exception e) {
			logger.error("Cannot connect to Core REST API : {}", restUri, e);
			throw e;
		}

		try {
			JSONObject result = (JSONObject) new JSONParser().parse(apiResult);
			if ("success".equals(result.get("status").toString())) {
				JSONObject value = (JSONObject) result.get("value");
				String token = value.get("token").toString();
				long expiration = (Long) value.get("expiration");
				tokenMap.put(userName, new Token(repositoryId, userName, token, expiration));
				return token;
			} else {
				logger.error("Return failure status from REST API response : {}", restUri);
			}
		} catch (Exception e) {
			logger.error("Cannot export token from REST API response : {}", restUri, e);
		}

		return null;
	}

	public String getOrRegister(String repositoryId, String userName, String password) {
		String token = getStoredToken(userName);

		if (StringUtils.isBlank(token)) {
			return register(repositoryId, userName, password);
		} else {
			return token;
		}
	}

	public String getStoredToken(String userName) {
		Token token = tokenMap.get(userName);
		if (token != null) {
			if (token.getExpiration() < System.currentTimeMillis()) {
				logger.info("{}: Basic auth token has expired!", userName);
				return null;
			} else {
				return token.getToken();
			}
		} else {
			return null;
		}
	}

	private String getRestUri(String repositoryId) {
		return restEndpoint + "/repo/" + repositoryId + "/authtoken/";
	}

	private class Token {
		private String repositoryId;
		private String userName;
		private String token;
		private long expiration;

		public Token() {

		}

		public Token(String repositoryId, String userName, String token, long expiration) {
			super();
			this.userName = userName;
			this.token = token;
			this.expiration = expiration;
		}

		public String getRepositoryId() {
			return repositoryId;
		}

		public void setRepositoryId(String repositoryId) {
			this.repositoryId = repositoryId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public long getExpiration() {
			return expiration;
		}

		public void setExpiration(long expiration) {
			this.expiration = expiration;
		}

	}
}
