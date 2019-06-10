# Initialization and Reindex
## API
Accessing the following URL initializes/sets back the last change token. NO INDEXING IS DONE
</br>
`http://localhost:8983/solr/admin/cores?core=nemaki&action=init&repositoryId=<repoId>`

Accessing the following URL executes full reindexing AND initializes the last changeToken.
</br>
`http://localhost:8983/solr/admin/cores?core=nemaki&action=index&tracking=FULL&repositoryId=<repoId>`