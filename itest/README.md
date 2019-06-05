##### NemakiWare CMIS Server Integration Tests

#### Itest Repository

http://localhost:8080/core/browser/itest?cmisselector=repositoryInfo

#### Prerequisites

Indexing must be done immediately and not cron controlled:

1. Core settings in _core/src/main/webapp/WEB-INF/classes/nemakiware.properties_:</br>
_solr.indexing.force=true_ (force indexing immediately)
2. Solr settings in _solr/solr/conf/nemakisolr.properties_: </br>
_solr.tracking.cron.enabled=false_ (disable cron scheduled indexing)

#### Reset itest database

1. Run _load-itest-empty-forced_ from /launch folder in bjornloka project
2. Reindex Solr by calling:</br>
<code>http://localhost:8983/solr/admin/cores?core=nemaki&action=init&repositoryId=itest</code>