package sho.cmis.server.nemaki.itest.accuracy.service.object;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Item;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sho.cmis.server.nemaki.itest.AbstractITest;

public class CreateItemAccItest extends AbstractITest {

	static String subTestFolderId;

	@BeforeAll
	public static void setUp() {
		// create subfolder for create doc acc itests
		subTestFolderId = AbstractITest.createFolder(testFolderId,
				CreateItemAccItest.class.getSimpleName() + "_" + System.currentTimeMillis());
	}

	@AfterAll
	public static void tearDown() {
		Folder subTestFolder = (Folder) session.getObject(subTestFolderId);
		subTestFolder.deleteTree(true, UnfileObject.DELETE, false);
	}

	@Test
	public void test_createItem() {
		String testName = "createItem1";
		String documentId = createItestItem(testFolderId, testName);
		assertNotNull(documentId);

		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME);
		CmisObject object = session.getObject(documentId, opCtx);
		Item item = (Item) object;
		List<Folder> parents = item.getParents();
		assertTrue(parents.size() == 1);
		Folder parentFolder = parents.get(0);
		String parentFolderId = parentFolder.getId();
		assertTrue(testFolderId.equals(parentFolderId));

		String documentName = item.getName();
		assertTrue(testName.equals(documentName));
	}
}
