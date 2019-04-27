package jp.aegif.nemaki.bjornloka.dump;

import java.io.File;

import jp.aegif.nemaki.bjornloka.custom.CustomEktorpFactory;
import jp.aegif.nemaki.bjornloka.proxy.EktorpProxy;

public class DumpEktorp extends DumpAction {

	public DumpEktorp(String url, String repositoryId, File file, boolean omitTimestamp) {
		super(url, repositoryId, file, omitTimestamp);
	}

	@Override
	public String dump() {
		EktorpProxy proxy = CustomEktorpFactory.getInstance().createProxy(url, repositoryId);
		String actionResult = action(proxy, file, omitTimestamp);
		return actionResult;
	}
}