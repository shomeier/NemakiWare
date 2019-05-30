package jp.aegif.nemaki.cmis.api;

public interface NemakiCmisServiceWrapper extends NemakiCmisService {

	NemakiCmisService getWrappedService();

	void setWrappedService(NemakiCmisService service);
}
