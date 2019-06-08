package sho.cmis.server.nemaki.itest;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.util.TypeUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParseException;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sho.cmis.server.nemaki.itest.util.SessionUtil;

public class SetUpRepository implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

	private static final Logger LOG = LoggerFactory.getLogger(SetUpRepository.class);

	private static boolean started = false;

	private static final String TYPE_DEFS_FOLDER = "/type-definitions/";

	private static final String[] TYPE_DEFS = { "itest_document.json", "itest_secondary_marker.json",
			"itest_secondary.json" };

	public static String TEST_FOLDER_ID;

	private Session session;

	private List<String> typeDefIds = new ArrayList(TYPE_DEFS.length);

	@Override
	public void beforeAll(ExtensionContext context) throws IOException, JSONParseException {
		if (!started) {
			started = true;
			// Your "before all tests" startup logic goes here

			session = SessionUtil.createCmisSession();
			createTypeDefs();
			TEST_FOLDER_ID = createTestFolder();
			// The following line registers a callback hook when the root test context is
			// shut down
			context.getRoot().getStore(GLOBAL).put("any unique name", this);
		}
	}

	@Override
	public void close() {
		// Your "after all tests" logic goes here
		deleteTypeDefs();
		deleteTestFolder();
	}

	protected void createTypeDefs() throws IOException, JSONParseException {
		for (String jsonTypeDef : TYPE_DEFS) {

			InputStream jsonStream = this.getClass().getResourceAsStream(TYPE_DEFS_FOLDER + jsonTypeDef);
			System.out.println("jsonStream: " + jsonStream);
			TypeDefinition typeDef = TypeUtils.readFromJSON(jsonStream);
			ObjectType type = session.createType(typeDef);
			LOG.info(MessageFormat.format("Created type with id {0}", type.getId()));
			typeDefIds.add(type.getId());
		}
	}

	protected void deleteTypeDefs() {
		for (String typeDefId : typeDefIds) {
			session.deleteType(typeDefId);
			LOG.info(MessageFormat.format("Deleted type with id {0}", typeDefId));
		}
	}

	protected String createTestFolder() {
		String rootFolderId = session.getRepositoryInfo().getRootFolderId();

		Map<String, Object> map = new HashMap<>();
		map.put(PropertyIds.OBJECT_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
		map.put(PropertyIds.PARENT_ID, rootFolderId);
		map.put(PropertyIds.NAME, "testFolder_" + System.currentTimeMillis());
		ObjectId result = session.createFolder(map, new ObjectIdImpl(rootFolderId));
		return result.getId();
	}

	protected void deleteTestFolder() {
		Folder folder = (Folder) session.getObject(TEST_FOLDER_ID);
		folder.deleteTree(true, UnfileObject.DELETE, true);
	}
}
