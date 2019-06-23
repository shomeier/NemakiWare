package sho.cmis.server.nemaki.itest;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.util.TypeUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.junit.jupiter.api.extension.ExtendWith;

import sho.cmis.server.nemaki.itest.util.SessionUtil;

@ExtendWith({ SetUpRepository.class })
public abstract class AbstractITest {

	protected static Session session = SessionUtil.createCmisSession();;
	protected static String testFolderId = SetUpRepository.TEST_FOLDER_ID;

	protected static String createItestDocument(String parentId, String name, String content) {
		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, ItestIds.DOCUMENT_TYPE_ID);
		map.put(PropertyIds.NAME, name);

		ContentStream contentStream = new ContentStreamImpl(name, "text/plain", content);

		ObjectId objectId = session.createDocument(map, session.createObjectId(parentId), contentStream,
				VersioningState.MAJOR);

		return objectId.getId();
	}

	protected static String createItestItem(String parentId, String name) {
		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, ItestIds.ITEM_TYPE_ID);
		map.put(PropertyIds.NAME, name);

		ObjectId objectId = session.createItem(map, session.createObjectId(parentId));

		return objectId.getId();
	}

	protected static String createFolder(String parentId, String name) {

		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
		map.put(PropertyIds.PARENT_ID, parentId);
		map.put(PropertyIds.NAME, name);
		ObjectId result = session.createFolder(map, new ObjectIdImpl(parentId));
		return result.getId();
	}

	protected static TypeDefinition getTypeDefFromJson(String resourceName) throws Exception {
		InputStream jsonStream = AbstractITest.class.getResourceAsStream(resourceName);
		return TypeUtils.readFromJSON(jsonStream);
	}
}
