package sho.cmis.server.nemaki.itest.accuracy.service.object;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sho.cmis.server.nemaki.itest.AbstractITest;

public class CreateDocumentAccItest extends AbstractITest {

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
	public void test_createDocument() {
		String testName = "createDocument1";
		String testContent = "testContent";
		String documentId = createItestDocument(testFolderId, testName, testContent);
		assertNotNull(documentId);

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME);
		CmisObject object = session.getObject(documentId, opCtx);
		Document document = (Document) object;
		List<Folder> parents = document.getParents();
		assertTrue(parents.size() == 1);
		Folder parentFolder = parents.get(0);
		String parentFolderId = parentFolder.getId();
		assertTrue(testFolderId.equals(parentFolderId));

		String documentName = document.getName();
		assertTrue(testName.equals(documentName));

		ContentStream contentStream = document.getContentStream();
		String contentString = new BufferedReader(new InputStreamReader(contentStream.getStream())).lines()
				.collect(Collectors.joining("\n"));
		assertTrue(testContent.equals(contentString));
	}
}
