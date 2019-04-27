package jp.aegif.nemaki.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.enums.CmisVersion;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertiesImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyBooleanImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDateTimeImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyDecimalImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIdImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyIntegerImpl;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.PropertyStringImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import jp.aegif.nemaki.businesslogic.ContentService;
import jp.aegif.nemaki.businesslogic.TypeService;
import jp.aegif.nemaki.cmis.aspect.CompileService;
import jp.aegif.nemaki.cmis.aspect.type.TypeManager;
import jp.aegif.nemaki.cmis.service.RelationshipService;
import jp.aegif.nemaki.cmis.service.VersioningService;
import jp.aegif.nemaki.model.Content;
import jp.aegif.nemaki.model.Document;
import jp.aegif.nemaki.model.Folder;
import jp.aegif.nemaki.model.Relationship;
import jp.aegif.nemaki.model.VersionSeries;

@Path("/repo/{repositoryId}/bulkCheckIn")
public class BulkCheckInResource extends ResourceBase {
	private static final Log log = LogFactory.getLog(BulkCheckInResource.class);

	private ContentService contentService;
	private VersioningService versioningService;
	private RelationshipService relationshipService;
	private TypeService typeService;
	private TypeManager typeManager;
	private CompileService compileService;

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	public void setVersioningService(VersioningService versioningService) {
		this.versioningService = versioningService;
	}

	public void setRelationshipService(RelationshipService relationshipService) {
		this.relationshipService = relationshipService;
	}

	public void setCompileService(CompileService compileService) {
		this.compileService = compileService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public void setTypeManager(TypeManager typeManager) {
		this.typeManager = typeManager;
	}

	@SuppressWarnings("unchecked")
	@POST
	@Path("/execute")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public String execute(MultivaluedMap<String, String> form, @Context HttpServletRequest httpRequest) {
		JSONObject result = new JSONObject();
		// JSONArray errMsg = new JSONArray();
		String repositoryId = form.get("repositoryId").get(0);
		String comment = form.get("comment").get(0);
		Boolean force = form.get("force").get(0).equals("true");
		Boolean copyRelations = form.get("copyRelations").get(0).equals("true");
		List<String> propertyIds = form.get("propertyId");
		List<String> propertyValues = form.get("propertyValue");
		List<String> objectIds = form.get("objectId");
		// List<String> changeTokens = form.get("changeToken");
		// declare properties variable
		PropertiesImpl properties = new PropertiesImpl();

		Document firstdoc = contentService.getDocument(repositoryId, objectIds.get(0));
		String typeId = firstdoc.getObjectType();
		TypeDefinition typeDef = typeManager.getTypeByQueryName(repositoryId, typeId);

		CallContext callContext = (CallContext) httpRequest.getAttribute("CallContext");
		// TODO: error checks
		if (propertyIds.size() != propertyValues.size()) {
			//
		}
		// if (objectIds.size() != changeTokens.size()){
		//
		// }
		// Properties newProperties = new Properties();

		for (int i = 0; i < propertyIds.size(); ++i) {
			String propertyName = propertyIds.get(i);
			String propertyValue = propertyValues.get(i);
			PropertyType propertyType = typeDef.getPropertyDefinitions().get(propertyName).getPropertyType();

			if (propertyType.equals(PropertyType.STRING) || propertyType.equals(PropertyType.ID)
					|| propertyType.equals(PropertyType.URI) || propertyType.equals(PropertyType.HTML)) {
				properties.addProperty(new PropertyStringImpl(propertyName, propertyValue));
			} else if (propertyType.equals(PropertyType.BOOLEAN)) {
				properties.addProperty(new PropertyBooleanImpl(propertyName, propertyValue.equals("true")));
			} else if (propertyType.equals(PropertyType.DECIMAL)) {
				properties.addProperty(new PropertyDecimalImpl(propertyName, new BigDecimal(propertyValue)));
			} else if (propertyType.equals(PropertyType.INTEGER)) {
				properties.addProperty(new PropertyIntegerImpl(propertyName, new BigInteger(propertyValue)));
			} else if (propertyType.equals(PropertyType.DATETIME)) {
				GregorianCalendar cal = new GregorianCalendar();
				DateFormat df = new SimpleDateFormat("YYYY-MM-dd'T'hh:mm:ss.sssXXX");
				ParsePosition pos = new ParsePosition(0);
				Date dt = df.parse(propertyValue, pos);
				if (pos.getErrorIndex() != -1) {
					// parse error -> skip this value
					continue;
				}
				cal.setTime(dt);
				properties.addProperty(new PropertyDateTimeImpl(propertyName, cal));
			} else {
				continue;
			}
		}

		for (int i = 0; i < objectIds.size(); ++i) {
			String objectId = objectIds.get(i);
			Document doc = contentService.getDocument(repositoryId, objectId);
			VersionSeries vs = contentService.getVersionSeries(repositoryId, doc);
			Holder<String> docIdHolder = new Holder<String>(objectId);
			typeId = doc.getObjectType();
			if (force && !vs.isVersionSeriesCheckedOut()) {
				versioningService.checkOut(callContext, repositoryId, docIdHolder, new Holder<Boolean>(true), null);
			}
			// Check in with the property set and comment
			if (!doc.isPrivateWorkingCopy()) {
				String pwcId = vs.getVersionSeriesCheckedOutId();
				docIdHolder = new Holder<String>(pwcId);
			}
			versioningService.checkIn(callContext, repositoryId, docIdHolder, true, properties, null, comment, null,
					null, null, null);
			Document newDoc = contentService.getDocumentOfLatestVersion(repositoryId, doc.getVersionSeriesId());
			// get relationships if exists
			if (copyRelations) {
				List<Relationship> relList = contentService.getRelationsipsOfObject(repositoryId, objectId,
						RelationshipDirection.SOURCE);
				// copy relationships
				for (Relationship rel : relList) {
					PropertiesImpl newProps = new PropertiesImpl();
					newProps.addProperty(new PropertyIdImpl(PropertyIds.OBJECT_TYPE_ID, rel.getObjectType()));
					newProps.addProperty(new PropertyIdImpl(PropertyIds.TARGET_ID, rel.getTargetId()));
					newProps.addProperty(new PropertyIdImpl(PropertyIds.SOURCE_ID, newDoc.getId()));
					newProps.addProperty(new PropertyStringImpl(PropertyIds.NAME, rel.getName()));
					Relationship newRel = contentService.createRelationship(callContext, repositoryId, newProps, null,
							null, null, null);
					newRel.setModifier("bulkCheckInService");
				}
			}
		}
		// todo set return messages
		return result.toJSONString();
	}

	@Path("/saveallversions")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces("application/json")
	public String saveAllVersions(@PathParam("repositoryId") String repositoryId, FormDataMultiPart multiPart,
			@FormDataParam("props") String props, @FormDataParam("parentFolderId") String parentFolderId)
			throws Exception {

		JSONArray resultArray = new JSONArray();

		try {
			JSONParser parser = new JSONParser();
			JSONObject propsJson = (JSONObject) parser.parse(props);

			JSONArray items = (JSONArray) propsJson.get("items");

			Folder parentFolder = contentService.getFolder(repositoryId, parentFolderId);
			if (parentFolder == null) {
				System.out.println("## folder not found:" + parentFolderId);
				return "";
			}

			// Fix user name as admin
			CallContext context = new FakeCallContext(repositoryId, "admin");

			Document firstDoc = null;
			Document prevDoc = null;
			VersionSeries versionSeries = null;
			for (int i = 0; i < items.size(); i++) {
				JSONObject propJson = (JSONObject) items.get(i);
				BodyPartEntity body = (BodyPartEntity) multiPart.getField("files[" + i + "]").getEntity();

				Map<String, List<FormDataBodyPart>> fields = multiPart.getFields();

				InputStream inputStream = body.getInputStream();

				File tempFile = this.saveToTempFile(inputStream);

				String fileName = multiPart.getField("files[" + i + "]").getContentDisposition().getFileName();
				String checkInComment = null;
				boolean isMajor = true;
				// prepare properties
				PropertiesImpl properties = new PropertiesImpl();

				String docName = null;
				for (Object key : propJson.keySet()) {
					// TODO cmis:createDate
					String keyStr = (String) key;
					if (keyStr.startsWith("jal") && keyStr.endsWith("Date")) {
						long v = (Long) propJson.get(key);
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(new Date(v));
						properties.addProperty(new PropertyDateTimeImpl(keyStr, cal));
					} else if (keyStr.equals("cmis:objectTypeId")) {
						String v = (String) propJson.get(key);
						properties.addProperty(new PropertyIdImpl(keyStr, v));
					} else if (keyStr.equals("checkInComment")) {
						checkInComment = (String) propJson.get(key);
					} else if (keyStr.equals("isMajor")) {
						isMajor = (Boolean) propJson.get(key);
						// System.out.println("## isMajor --> " + isMajor);
					} else {
						String v = (String) propJson.get(key);
						properties.addProperty(new PropertyStringImpl(keyStr, v));
					}

					if (keyStr.equals("cmis:name")) {
						docName = (String) propJson.get(key);
					}
				}

				ContentStream contentStream = new ContentStreamImpl(fileName, BigInteger.valueOf(tempFile.length()),
						"binary/octet-stream", new FileInputStream(tempFile));

				if (docName != null) {
					List<Content> children = contentService.getChildren(repositoryId, parentFolder.getId());
					if (CollectionUtils.isNotEmpty(children)) {
						for (Content content : children) {
							if (content instanceof Document) {
								Document doc = (Document) content;
								if (doc.getName().equals(docName)) {
									prevDoc = doc;
									break;
								}
							}
						}
					}
				}
				if (prevDoc == null) {
					// first create document
					firstDoc = contentService.createDocument(context, repositoryId, properties, parentFolder,
							contentStream, VersioningState.MAJOR, null, null, null);

					JSONObject elm = new JSONObject();
					elm.put("version", propJson.get("cmis:versionLabel"));
					elm.put("id", firstDoc.getId());
					resultArray.add(elm);

					if (items.size() > 1) {
						// create versionSeries
						versionSeries = contentService.getVersionSeries(repositoryId, firstDoc);
						prevDoc = firstDoc;
					}
				} else { // i > 0
					prevDoc = contentService.updateWithoutCheckInOut(context, repositoryId, isMajor, properties,
							contentStream, checkInComment, prevDoc, versionSeries);

					JSONObject elm = new JSONObject();
					elm.put("version", propJson.get("cmis:versionLabel"));
					elm.put("id", prevDoc.getId());
					resultArray.add(elm);

				}

				// delete temp file
				tempFile.delete();
			}

			JSONObject resultJson = new JSONObject();
			resultJson.put("item", resultArray);
			return resultJson.toString();
		} catch (Throwable t) {
			System.out.println("## catch some exception");
			t.printStackTrace();
			return "";
		} finally {

		}
	}

	private File saveToTempFile(InputStream inputStream) throws IOException {
		File tempFile = File.createTempFile("nemaki", "");
		// FileUtils.copyInputStreamToFile(inputStream, tempFile);
		Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return tempFile;
	}

	public static class FakeCallContext implements CallContext {

		private String repositoryId;
		private String userName;

		public FakeCallContext(String repositoryId, String userName) {
			this.repositoryId = repositoryId;
			this.userName = userName;
		}

		@Override
		public boolean encryptTempFiles() {
			return false;
		}

		@Override
		public Object get(String arg0) {
			return null;
		}

		@Override
		public String getBinding() {
			return CallContext.BINDING_BROWSER;
		}

		@Override
		public CmisVersion getCmisVersion() {
			return CmisVersion.CMIS_1_1;
		}

		@Override
		public BigInteger getLength() {
			return null;
		}

		@Override
		public String getLocale() {
			return null;
		}

		@Override
		public long getMaxContentSize() {
			return 0;
		}

		@Override
		public int getMemoryThreshold() {
			return 0;
		}

		@Override
		public BigInteger getOffset() {
			return null;
		}

		@Override
		public String getPassword() {
			return null;
		}

		@Override
		public String getRepositoryId() {

			return repositoryId;
		}

		@Override
		public File getTempDirectory() {
			return null;
		}

		@Override
		public String getUsername() {
			return userName;
		}

		@Override
		public boolean isObjectInfoRequired() {
			return false;
		}

	}
}
