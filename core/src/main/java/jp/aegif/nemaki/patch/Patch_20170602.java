package jp.aegif.nemaki.patch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component
public class Patch_20170602 extends AbstractNemakiPatch {
	private static final Log log = LogFactory.getLog(Patch_20170602.class);

	@Override
	public String getName() {
		return "patch_20170602";
	}

	@Override
	protected void applySystemPatch() {
	}

	@Override
	protected void applyPerRepositoryPatch(String repositoryId) {
		String changesByObjectIdViewCode = "function(doc) { if (doc.type == 'change')  emit(doc.objectId, doc) }";
		patchUtil.addView(repositoryId, "changesByObjectId", changesByObjectIdViewCode);
	}

}
