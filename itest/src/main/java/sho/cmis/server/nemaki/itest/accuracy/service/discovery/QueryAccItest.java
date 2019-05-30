package sho.cmis.server.nemaki.itest.accuracy.service.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.PropertyData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sho.cmis.server.nemaki.itest.AbstractITest;

public class QueryAccItest extends AbstractITest {

	private static final Logger LOG = LoggerFactory.getLogger(QueryAccItest.class);

	private static String URL_INIT = "http://localhost:8983/solr/admin/cores?core=nemaki&action=init&repositoryId=itest";
	private static String URL_REINDEX_FROM_LAST_TOKEN = "http://localhost:8983/solr/admin/cores?core=nemaki&action=index&tracking=DELTA&repositoryId=itest";
	private static String URL_REINDEX_FULL = "http://localhost:8983/solr/admin/cores?core=nemaki&action=index&tracking=FULL&repositoryId=itest";

	private static String documentName = "testDocument";
	private static String documentContent = "testContent";
	private static String documentId;

	@BeforeAll
	public static void before() throws Exception {
		AbstractITest.before();
		documentId = createDocument(testFolderId, documentName, documentContent);
		// add two secondary types
//		List<String> secondaryTypes = new ArrayList<String>();
//		secondaryTypes.add("S:itest:secondary");
//		secondaryTypes.add("S:itest:secondary_marker");
//
//		Map<String, Object> props = new HashMap<String, Object>();
//		props.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, secondaryTypes);
//
//		// set secondary type properties
////		props.put("lingo:language", "en");
//
//		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.CHANGE_TOKEN);
//		CmisObject object = session.getObject(documentId, opCtx);
//		CmisObject newObject = object.updateProperties(props);

//		URL url = new URL(URL_REINDEX_FULL);
//		HttpURLConnection con = (HttpURLConnection) url.openConnection();
//		con.setRequestMethod("GET");
//		String contentString = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
//				.collect(Collectors.joining("\n"));
//		LOG.info(contentString);
	}

	@AfterAll
	public static void after() throws Exception {
		AbstractITest.after();

//		URL url = new URL(URL_INIT);
//		HttpURLConnection con = (HttpURLConnection) url.openConnection();
//		con.setRequestMethod("GET");
//		String contentString = new BufferedReader(new InputStreamReader(con.getInputStream())).lines()
//				.collect(Collectors.joining("\n"));
//		LOG.info(contentString);
	}

	@Test
	public void test_query_equalsCmisName() {

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME);
		opCtx.setIncludeAllowableActions(true);
		String statement = "SELECT cmis:name FROM cmis:document WHERE cmis:name='" + documentName + "'";
		ItemIterable<QueryResult> query = session.query(statement, false, opCtx);
		assertEquals(1, query.getTotalNumItems());
		QueryResult result = query.iterator().next();
		PropertyData<Object> propName = result.getPropertyById(PropertyIds.NAME);
		assertEquals(documentName, propName.getFirstValue());
	}
}
