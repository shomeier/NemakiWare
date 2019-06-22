package sho.cmis.server.nemaki.itest.accuracy.service.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import sho.cmis.server.nemaki.itest.AbstractITest;
import sho.cmis.server.nemaki.itest.ItestEnv;
import sho.cmis.server.nemaki.itest.ItestIds;

public class UpdateTypeAccItest extends AbstractITest {

	@AfterEach
	public void tearDown() throws Exception {
		TypeDefinition originalType = AbstractITest.getTypeDefFromJson(ItestEnv.TYPE_DEF_UPDATE);
		session.updateType(originalType);
	}

	@Test
	public void test_updateType_addProperty() throws Exception {

		TypeDefinition typeDef = AbstractITest.getTypeDefFromJson(ItestEnv.TYPE_DEF_UPDATE_ADD_PROPERTY);
		ObjectType updatedType = session.updateType(typeDef);
		assertNotNull(updatedType);

		updatedType = session.getTypeDefinition(ItestIds.DOCUMENT_TYPE_ID);
		assertNotNull(updatedType);
		Map<String, PropertyDefinition<?>> propertyDefinitions = updatedType.getPropertyDefinitions();

		assertTrue(propertyDefinitions.containsKey(ItestIds.UPDATE_ADDED_PROPERTY_ID));
	}

	@Test
	public void test_updateType_updatedExistingProperty() throws Exception {

		TypeDefinition typeDef = AbstractITest.getTypeDefFromJson(ItestEnv.TYPE_DEF_UPDATE_EXISTING_PROPERTY);
		ObjectType updatedType = session.updateType(typeDef);
		assertNotNull(updatedType);

		updatedType = session.getTypeDefinition(ItestIds.DOCUMENT_TYPE_ID);
		assertNotNull(updatedType);
		Map<String, PropertyDefinition<?>> propertyDefinitions = updatedType.getPropertyDefinitions();

		assertTrue(propertyDefinitions.containsKey(ItestIds.UPDATE_EXISTING_PROPERTY_ID));
	}
}
