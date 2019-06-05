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

	private static String documentName = "testDocument";
	private static String documentContent = "testContent";
	private static String documentId;

	@BeforeAll
	public static void before() throws Exception {
		AbstractITest.before();
		documentId = createDocument(testFolderId, documentName, documentContent);
	}

	@AfterAll
	public static void after() throws Exception {
		AbstractITest.after();
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
