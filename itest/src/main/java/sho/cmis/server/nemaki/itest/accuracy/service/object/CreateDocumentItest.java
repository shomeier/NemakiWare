package sho.cmis.server.nemaki.itest.accuracy.service.object;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.junit.jupiter.api.Test;

import sho.cmis.server.nemaki.itest.AbstractITest;

public class CreateDocumentItest extends AbstractITest {

	@Test
	public void test_createDocument() {
		String testName = "testDocument";
		String testContent = "testContent";
		String documentId = createDocument(testFolderId, testName, testContent);
		assertNotNull(documentId);

		CmisObject object = session.getObject(documentId);
		Document document = (Document) object;
		List<Folder> parents = document.getParents();
		assertTrue(parents.size() == 1);
		Folder parentFolder = parents.get(0);
		String parentFolderId = parentFolder.getId();
		assertTrue(testFolderId.equals(parentFolderId));

		assertTrue(testName.equals(document.getName()));

		ContentStream contentStream = document.getContentStream();
		String contentString = new BufferedReader(new InputStreamReader(contentStream.getStream()))
				  .lines().collect(Collectors.joining("\n"));
		assertTrue(testContent.equals(contentString));
	}

	private String createDocument(String parentId, String name, String string) {
		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
		map.put(PropertyIds.NAME, name);

		ContentStream contentStream = new ContentStreamImpl(name, "text/plain", string);

		ObjectId objectId = session.createDocument(map, session.createObjectId(parentId), contentStream,
				VersioningState.MAJOR);

		return objectId.getId();
	}
}
