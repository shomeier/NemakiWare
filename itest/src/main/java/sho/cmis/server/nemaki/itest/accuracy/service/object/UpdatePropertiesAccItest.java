package sho.cmis.server.nemaki.itest.accuracy.service.object;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.SecondaryType;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sho.cmis.server.nemaki.itest.AbstractITest;
import sho.cmis.server.nemaki.itest.ItestIds;

public class UpdatePropertiesAccItest extends AbstractITest {

	static String subTestFolderId;

	@BeforeAll
	public static void setUp() {
		// create subfolder for create doc acc itests
		subTestFolderId = AbstractITest.createFolder(testFolderId,
				CreateDocumentAccItest.class.getSimpleName() + "_" + System.currentTimeMillis());
	}

	@AfterAll
	public static void tearDown() {
		Folder subTestFolder = (Folder) session.getObject(subTestFolderId);
		subTestFolder.deleteTree(true, UnfileObject.DELETE, false);
	}

	@Test
	public void test_updateProperties_multiValue() {
		String testPropertyValue = "testValue";

		String documentId = createItestDocument(testFolderId, "updateProperties1", testFolderId);

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.CHANGE_TOKEN,
				ItestIds.MULTI_VALUE_PROPERTY_ID);
		Document document = (Document) session.getObject(documentId, opCtx);

		Map<String, Object> props = new HashMap<>();
		props.put(ItestIds.MULTI_VALUE_PROPERTY_ID, Collections.singletonList(testPropertyValue));
		ObjectId updatedDocumentId = document.updateProperties(props, true);

		CmisObject updatedDocument = session.getObject(updatedDocumentId, opCtx);

		Property<Object> property = updatedDocument.getProperty(ItestIds.MULTI_VALUE_PROPERTY_ID);
		String firstValue = (String) property.getFirstValue();
		assertEquals(firstValue, testPropertyValue);
	}

	@Test
	public void test_updateProperties_secondary() {
		String testPartOfSpeech = "adverb";

		String documentId = createItestDocument(testFolderId, "updateProperties2", testFolderId);

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.CHANGE_TOKEN,
				PropertyIds.SECONDARY_OBJECT_TYPE_IDS, ItestIds.PART_OF_SPEECH_SECONDARY_PROPERTY_ID);
		Document document = (Document) session.getObject(documentId, opCtx);

		ObjectId updatedDocumentId = document.updateProperties(Collections.EMPTY_MAP,
				Collections.singletonList(ItestIds.SECONDARY_TYPE_ID), Collections.EMPTY_LIST, true);

		CmisObject object = session.getObject(updatedDocumentId, opCtx);
		List<SecondaryType> secondaryTypes = object.getSecondaryTypes();
		assertEquals(1, secondaryTypes.size());
		assertEquals(ItestIds.SECONDARY_TYPE_ID, secondaryTypes.get(0).getId());

		Map<String, Object> props = new HashMap<>();
		props.put(ItestIds.PART_OF_SPEECH_SECONDARY_PROPERTY_ID, testPartOfSpeech);

		updatedDocumentId = object.updateProperties(props, true);
		object = session.getObject(updatedDocumentId, opCtx);
		Property<Object> property = object.getProperty(ItestIds.PART_OF_SPEECH_SECONDARY_PROPERTY_ID);
		String partOfSpeech = (String) property.getFirstValue();
		assertEquals(testPartOfSpeech, partOfSpeech);
	}
}
