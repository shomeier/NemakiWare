package jp.aegif.nemaki.util.constant;

public enum CmisService {

	// @formatter:off
	GET_REPOSITORY_INFOS("getRepositoryInfos"),
	GET_REPOSITORY_INFO("getRepositoryInfo"),
	GET_TYPE_CHILDREN("getTypeChildren"),
	GET_TYPE_DESCENDANTS("getTypeDescendants"),
	GET_TYPE_DEFINITION("getTypeDefinition"),
	CREATE_TYPE("createType"),
	UPDATE_TYPE("updateType"),
	DELETE_TYPE("deleteType"),
	GET_CHILDREN("getChildren"),
	GET_DESCENDANTS("getDescendants"),
	GET_FOLDER_TREE("getFolderTree"),
	GET_OBJECT_PARENTS("getObjectParents"),
	GET_FOLDER_PARENT("getFolderParent"),
	GET_CHECKED_OUT_DOCS("getCheckedOutDocs"),
	CREATE_DOCUMENT("createDocument"),
	CREATE_DOCUMENT_FROM_SOURCE("createDocumentFromSource"),
	CREATE_FOLDER("createFolder"),
	CREATE_RELATIONSHIP("createRelationship"),
	CREATE_POLICY("createPolicy"),
	CREATE_ITEM("createItem"),
	GET_ALLOWABLE_ACTIONS("getAllowableActions"),
	GET_OBJECT("getObject"),
	GET_PROPERTIES("getProperties"),
	GET_RENDITIONS("getRenditions"),
	GET_OBJECT_BY_PATH("getObjectByPath"),
	GET_CONTENT_STREAM("getContentStream"),
	UPDATE_PROPERTIES("updateProperties"),
	BULK_UPDATE_PROPERTIES("bulkUpdateProperties"),
	MOVE_OBJECT("moveObject"),
	DELETE_OBJECT("deleteObject"),
	DELETE_TREE("deleteTree"),
	SET_CONTENT_STREAM("setContentStream"),
	DELETE_CONTENT_STREAM("deleteContentStream"),
	APPEND_CONTENT_STREAM("appendContentStream"),
	CHECK_OUT("checkOut"),
	CANCEL_CHECK_OUT("cancelCheckOut"),
	CHECK_IN("checkIn"),
	GET_OBJECT_OF_LATEST_VERSION("getObjectOfLatestVersion"),
	GET_PROPERTIES_OF_LATEST_VERSION("getPropertiesOfLatestVersion"),
	GET_ALL_VERSIONS("getAllVersions"),
	QUERY("query"),
	GET_CONTENT_CHANGES("getContentChanges"),
	ADD_OBJECT_TO_FOLDER("addObjectToFolder"),
	REMOVE_OBJECT_FROM_FOLDER("removeObjectFromFolder"),
	GET_OBJECT_RELATIONSHIPS("getObjectRelationships"),
	GET_ACL("getAcl"),
	APPLY_ACL("applyAcl"),
	APPLY_POLICY("applyPolicy"),
	REMOVE_POLICY("removePolicy"),
	GET_APPLIED_POLICIES("getAppliedPolicies"),
	CREATE("create"),
	DELETE_OBJECT_OR_CANCEL_CHECKOUT("deleteObjectOrCancelCheckOut"),
	GET_OBJECT_INFO("getObjectInfo");
	// @formatter:on

	private final String value;

	CmisService(String value) {
		this.value = value;
	}

	public String value() {
		return value;
	}

	public static CmisService fromValue(String value) {
		for (CmisService cs : CmisService.values()) {
			if (cs.value.equals(value)) {
				return cs;
			}
		}
		throw new IllegalArgumentException(value);
	}
}
