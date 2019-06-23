package sho.cmis.server.nemaki.itest;

public final class ItestEnv {

	private static final String TYPE_DEFS_FOLDER = "/type-definitions";

	private static final String TYPE_DEFS_UPDATE_FOLDER = TYPE_DEFS_FOLDER + "/update";

	public static final String TYPE_DEF_DOCUMENT = TYPE_DEFS_FOLDER + "/D_itest_document.json";
	public static final String TYPE_DEF_ITEM = TYPE_DEFS_FOLDER + "/D_itest_item.json";
	public static final String TYPE_DEF_SEC_MARKER = TYPE_DEFS_FOLDER + "/S_itest_secondary_marker.json";
	public static final String TYPE_DEF_SEC_POS = TYPE_DEFS_FOLDER + "/S_itest_part_of_speech.json";

	// we make updateType tests on this type
	public static final String TYPE_DEF_UPDATE = TYPE_DEF_DOCUMENT;
	public static final String TYPE_DEF_UPDATE_ADD_PROPERTY = TYPE_DEFS_UPDATE_FOLDER
			+ "/D_itest_document_update_add_property.json";
	public static final String TYPE_DEF_UPDATE_EXISTING_PROPERTY = TYPE_DEFS_UPDATE_FOLDER
			+ "/D_itest_document_update_existing_property.json";

	public static final String[] TYPE_DEFS = { TYPE_DEF_DOCUMENT, TYPE_DEF_ITEM, TYPE_DEF_SEC_MARKER,
			TYPE_DEF_SEC_POS };
}
