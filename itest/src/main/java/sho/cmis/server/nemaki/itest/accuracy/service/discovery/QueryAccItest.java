package sho.cmis.server.nemaki.itest.accuracy.service.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sho.cmis.server.nemaki.itest.AbstractITest;

public class QueryAccItest extends AbstractITest {

	private static final Logger LOG = LoggerFactory.getLogger(QueryAccItest.class);

	private static String URL_REINDEX = "http://localhost:8983/solr/admin/cores?core=nemaki&action=init?repositoryId=itest";
	private static String URL_REINDEX_FROM_LAST_TOKEN = "http://localhost:8983/solr/admin/cores?core=nemaki&action=index&tracking=FULL?repositoryId=itest";

	private static String documentName = "testDocument";
	private static String documentContent = "testContent";
	private static String documentId;

	@BeforeAll
	public static void before() throws Exception {
		AbstractITest.before();
		documentId = createDocument(testFolderId, documentName, documentContent);

		URL url = new URL(URL_REINDEX_FROM_LAST_TOKEN);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		String contentString = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
				.collect(Collectors.joining("\n"));
		LOG.info(contentString);
		Thread.sleep(10000);
	}

	@Test
	public void test_query() {

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME);
		String statement = "SELECT cmis:name FROM cmis:document WHERE cmis:name='" + documentName + "'";
		ItemIterable<QueryResult> query = session.query(statement, false, opCtx);
		assertEquals(1, query.getTotalNumItems());
		QueryResult result = query.iterator().next();
		PropertyData<Object> retName = result.getPropertyById(PropertyIds.NAME);
		assertTrue(documentName.equals(retName));
	}
}
