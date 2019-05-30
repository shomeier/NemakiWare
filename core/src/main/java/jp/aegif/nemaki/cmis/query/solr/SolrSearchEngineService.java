package jp.aegif.nemaki.cmis.query.solr;

import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jp.aegif.nemaki.cmis.api.SearchEngineService;
import jp.aegif.nemaki.cmis.aspect.query.solr.SolrUtil;

@Component
public class SolrSearchEngineService implements SearchEngineService {

	@Autowired
	private SolrUtil sorlUtil;

	@Override
	public void updateIndex(CallContext callContext) {
		System.out.println("SolrUtil is: " + sorlUtil);

	}

}
