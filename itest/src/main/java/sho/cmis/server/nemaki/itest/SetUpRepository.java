package sho.cmis.server.nemaki.itest;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.runtime.ObjectIdImpl;
import org.apache.chemistry.opencmis.client.util.OperationContextUtils;
import org.apache.chemistry.opencmis.client.util.TypeUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.json.parser.JSONParseException;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sho.cmis.server.nemaki.itest.util.SessionUtil;

public class SetUpRepository implements BeforeAllCallback, ExtensionContext.Store.CloseableResource {

	private static final Logger LOG = LoggerFactory.getLogger(SetUpRepository.class);

	private static boolean started = false;

	public static String TEST_FOLDER_ID;

	private Session session = SessionUtil.createCmisSession();

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		if (!started) {
			started = true;
			// Your "before all tests" startup logic goes here
			cleanRepo();

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
		// deleteTypeDefs();
		// cleanRepo();
		// deleteTestFolder();
	}

	protected void createTypeDefs() throws IOException, JSONParseException {
		for (String jsonTypeDef : ItestEnv.TYPE_DEFS) {

			InputStream jsonStream = this.getClass().getResourceAsStream(jsonTypeDef);
			TypeDefinition typeDef = TypeUtils.readFromJSON(jsonStream);
			ObjectType type = session.createType(typeDef);
			LOG.info(MessageFormat.format("Created type with id {0}", type.getId()));
			// typeDefIds.add(type.getId());
		}
	}

	protected void deleteTypeDefs() throws Exception {
		for (String jsonTypeDef : ItestEnv.TYPE_DEFS) {

			InputStream jsonStream = this.getClass().getResourceAsStream(jsonTypeDef);
			TypeDefinition typeDef = TypeUtils.readFromJSON(jsonStream);

			try {
				session.getTypeDefinition(typeDef.getId());
				session.deleteType(typeDef.getId());
				LOG.info(MessageFormat.format("Deleted type with id {0}", typeDef.getId()));
			} catch (CmisObjectNotFoundException e) {
				continue;
			}
		}

		// ItemIterable<ObjectType> docChildren =
		// session.getTypeChildren("cmis:document", false);
		// for (ObjectType docType : docChildren) {
		// session.deleteType(docType.getId());
		// }
		// ItemIterable<ObjectType> itemChildren = session.getTypeChildren("cmis:item",
		// false);
		// for (ObjectType itemType : itemChildren) {
		// session.deleteType(itemType.getId());
		// }
		// ItemIterable<ObjectType> folChildren = session.getTypeChildren("cmis:folder",
		// false);
		// for (ObjectType folType : folChildren) {
		// session.deleteType(folType.getId());
		// }
		// ItemIterable<ObjectType> secChildren =
		// session.getTypeChildren("cmis:secondary", false);
		// for (ObjectType secType : secChildren) {
		// session.deleteType(secType.getId());
		// }
	}

	protected void cleanRepo() throws Exception {
		OperationContext opCtx = OperationContextUtils.createMinimumOperationContext(PropertyIds.NAME);
		opCtx.setIncludeAllowableActions(true);
		Folder rootFolder = session.getRootFolder(opCtx);
		ItemIterable<CmisObject> children = rootFolder.getChildren(opCtx);
		for (CmisObject cmisObject : children) {
			if (cmisObject instanceof Folder) {
				Folder folder = ((Folder) cmisObject);
				if (!folder.getName().startsWith(".")) {
					folder.deleteTree(true, UnfileObject.DELETE, true);
				}
			} else {
				cmisObject.delete(true);
			}
		}

		deleteTypeDefs();
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
