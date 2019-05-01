package sho.cmis.server.nemaki.itest;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import sho.cmis.server.nemaki.itest.util.SessionUtil;

public abstract class AbstractITest {

	protected static Session session;
	protected static String testFolderId;

	@BeforeAll
	public static void before() throws Exception {
		session = SessionUtil.createCmisSession("itest", "admin", "admin");
		testFolderId = createTestFolder();
	}

	@AfterAll
	public static void after() throws Exception {
		Folder folder = (Folder) session.getObject(testFolderId);
		folder.deleteTree(true, UnfileObject.DELETE, true);
	}

	protected static String createTestFolder() {
		String rootFolderId = session.getRepositoryInfo().getRootFolderId();

		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
		map.put(PropertyIds.PARENT_ID, rootFolderId);
		map.put(PropertyIds.NAME, "testFolder_" + System.currentTimeMillis());
		ObjectId result = session.createFolder(map, new ObjectIdImpl(rootFolderId));
		return result.getId();
	}

	protected static String createDocument(String parentId, String name, String string) {
		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
		map.put(PropertyIds.NAME, name);

		ContentStream contentStream = new ContentStreamImpl(name, "text/plain", string);

		ObjectId objectId = session.createDocument(map, session.createObjectId(parentId), contentStream,
				VersioningState.MAJOR);

		return objectId.getId();
	}
}
