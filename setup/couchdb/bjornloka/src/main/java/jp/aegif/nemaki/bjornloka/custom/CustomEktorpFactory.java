package jp.aegif.nemaki.bjornloka.custom;

import jp.aegif.nemaki.bjornloka.proxy.CouchFactory;
import jp.aegif.nemaki.bjornloka.proxy.EktorpFactory;
import jp.aegif.nemaki.bjornloka.proxy.EktorpProxy;
import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.http.HttpClient;
import org.ektorp.http.StdHttpClient;
import org.ektorp.impl.StdCouchDbInstance;

import java.net.MalformedURLException;

public class CustomEktorpFactory implements CouchFactory {
    private static final CustomEktorpFactory instance = new CustomEktorpFactory();
    private CustomEktorpFactory(){

    }

    public static CustomEktorpFactory getInstance(){
        return CustomEktorpFactory.instance;
    }

    @Override
    public EktorpProxy createProxy(String url, String repositoryId){
        CouchDbInstance dbInstance = createCouchDbInstance(url);
        CouchDbConnector connector = createConnector(dbInstance, repositoryId);

        return new EktorpProxy(dbInstance, connector);
    }

    private static CouchDbInstance createCouchDbInstance(String url){
        HttpClient httpClient;
        try {
            httpClient = new StdHttpClient.Builder().url(url)
                    .maxConnections(1000).socketTimeout(100000000).build();


            return new StdCouchDbInstance(httpClient);
        } catch (MalformedURLException e) {
            System.err.println("URL is not well-formed!: " + url);
            e.printStackTrace();
        }

        return null;
    }

    private static CouchDbConnector createConnector(CouchDbInstance dbInstance, String repositoryId){
        CustomCouchDbConnector connector = new CustomCouchDbConnector(repositoryId, dbInstance);
        return connector;
    }

    @Override
    public boolean initRepository(String url, String repositoryId, boolean force) {
        CouchDbInstance dbInstance = createCouchDbInstance(url);

        //Initialize database
        if(dbInstance.checkIfDbExists(repositoryId)){
            if(!force){
                //System.err.println("Repository already exist. Do nothing.");
                return false;
            }
            dbInstance.deleteDatabase(repositoryId);
        }
        dbInstance.createDatabase(repositoryId);

        return true;
    }
}