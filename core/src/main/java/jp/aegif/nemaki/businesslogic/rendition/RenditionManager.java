package jp.aegif.nemaki.businesslogic.rendition;

import org.apache.chemistry.opencmis.commons.data.ContentStream;

public interface RenditionManager {
	public ContentStream convertToPdf(ContentStream contentStream, String documentName);

	public boolean checkConvertible(String mediatype);
}
