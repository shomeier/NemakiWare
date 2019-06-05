package sho.cmis.server.nemaki.itest;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.util.TypeUtils;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
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

	private Session session;

	private List<String> typeDefIds = new ArrayList(TYPE_DEFS.length);

	@Override
	public void beforeAll(ExtensionContext context) throws IOException, JSONParseException {
		if (!started) {
			started = true;
			// Your "before all tests" startup logic goes here

			session = SessionUtil.createCmisSession();
			for (String jsonTypeDef : TYPE_DEFS) {

				InputStream jsonStream = this.getClass().getResourceAsStream(TYPE_DEFS_FOLDER + jsonTypeDef);
				System.out.println("jsonStream: " + jsonStream);
				TypeDefinition typeDef = TypeUtils.readFromJSON(jsonStream);
				ObjectType type = session.createType(typeDef);
				LOG.info(MessageFormat.format("Created type with id {0}", type.getId()));
				typeDefIds.add(type.getId());
			}
			// The following line registers a callback hook when the root test context is
			// shut down
			context.getRoot().getStore(GLOBAL).put("any unique name", this);
		}
	}

	@Override
	public void close() {
		// Your "after all tests" logic goes here
		for (String typeDefId : typeDefIds) {
			session.deleteType(typeDefId);
			LOG.info(MessageFormat.format("Deleted type with id {0}", typeDefId));
		}
	}
}
