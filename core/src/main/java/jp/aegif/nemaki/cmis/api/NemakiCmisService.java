package jp.aegif.nemaki.cmis.api;

import org.apache.chemistry.opencmis.commons.server.ProgressControlCmisService;
import org.apache.chemistry.opencmis.server.support.wrapper.CallContextAwareCmisService;

public interface NemakiCmisService extends CallContextAwareCmisService, ProgressControlCmisService {

}
