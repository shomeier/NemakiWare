package jp.aegif.nemaki.bjornloka.custom;

import java.util.List;

import org.ektorp.CouchDbInstance;
import org.ektorp.ViewQuery;
import org.ektorp.impl.StdCouchDbConnector;
import org.ektorp.util.Assert;

public class CustomCouchDbConnector extends StdCouchDbConnector {

	public CustomCouchDbConnector(String databaseName, CouchDbInstance dbInstance) {
		super(databaseName, dbInstance);
	}

	@Override
	public <T> List<T> queryView(final ViewQuery query, final Class<T> type) {
		Assert.notNull(query, "query may not be null");
		query.dbPath(dbURI.toString());

		CustomEmbeddedDocViewResponseHandler<T> rh = new CustomEmbeddedDocViewResponseHandler<T>(type, objectMapper,
				query.isIgnoreNotFound());

		return executeQuery(query, rh);
	}

}
