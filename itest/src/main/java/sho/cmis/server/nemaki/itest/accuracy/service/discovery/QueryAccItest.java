package sho.cmis.server.nemaki.itest.accuracy.service.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
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
import sho.cmis.server.nemaki.itest.ItestIds;

public class QueryAccItest extends AbstractITest {

	private static final Logger LOG = LoggerFactory.getLogger(QueryAccItest.class);

	private static String documentName = "testDocument";
	private static String documentContent = "testContent";
	private static String documentId;

	@BeforeAll
	public static void before() throws Exception {
//		AbstractITest.before();
		documentId = createItestDocument(testFolderId, documentName, documentContent);
	}

	@AfterAll
	public static void after() throws Exception {
//		AbstractITest.after();
		session.delete(session.createObjectId(documentId));

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

	@Test
	public void test_query_secondaryObjects() {

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME,
				PropertyIds.SECONDARY_OBJECT_TYPE_IDS, PropertyIds.CHANGE_TOKEN,
				ItestIds.PART_OF_SPEECH_SECONDARY_PROPERTY_ID);
		opCtx.setIncludeAllowableActions(true);

		CmisObject object = session.getObject(documentId, opCtx);

		Map<String, String> props = Collections.singletonMap(ItestIds.PART_OF_SPEECH_SECONDARY_PROPERTY_ID, "adverb");
		ObjectId updatedObjectId = object.updateProperties(props,
				Collections.singletonList(ItestIds.PART_OF_SPEECH_SECONDARY_TYPE_ID), null, true);

		//@// @formatter:off
		String statement =
				"SELECT P.itest:part_of_speech, D.cmis:objectId " +
						"FROM itest:document AS D " +
						"JOIN itest:part_of_speech AS P ON D.cmis:objectId = P.cmis:objectId " +
						"WHERE P.itest:part_of_speech = 'adverb'" +
						"ORDER BY P.itest:part_of_speech";
		// @formatter:on
		ItemIterable<QueryResult> query = session.query(statement, false, opCtx);
		assertEquals(1, query.getTotalNumItems());
		QueryResult result = query.iterator().next();
		PropertyData<Object> propId = result.getPropertyById(PropertyIds.OBJECT_ID);
		assertEquals(updatedObjectId.getId(), propId.getFirstValue());
	}
}
