# Initialization and Reindex
## API
Accessing the following URL executes full reindexing.
The last changeToken is also initialized.
</br>
`http://localhost:8983/solr/admin/cores?core=nemaki&action=init&repositoryId=<repoId>`

Accessing the following URL executes full reindexing from the last changeToken.
</br>
`http://localhost:8983/solr/admin/cores?core=nemaki&action=index&tracking=FULL&repositoryId=<repoId>`