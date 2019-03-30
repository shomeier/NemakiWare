package jp.aegif.nemaki.bjornloka.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ektorp.http.HttpResponse;
import org.ektorp.http.StdResponseHandler;
import org.ektorp.util.Assert;

import java.util.List;

public class CustomEmbeddedDocViewResponseHandler<T> extends
        StdResponseHandler<List<T>> {

    private CustomQueryResultParser<T> parser;

    public CustomEmbeddedDocViewResponseHandler(Class<T> docType, ObjectMapper om) {
        Assert.notNull(om, "ObjectMapper may not be null");
        Assert.notNull(docType, "docType may not be null");
        parser = new CustomQueryResultParser<T>(docType, om);
    }

    public CustomEmbeddedDocViewResponseHandler(Class<T> docType, ObjectMapper om,
                                                boolean ignoreNotFound) {
        Assert.notNull(om, "ObjectMapper may not be null");
        Assert.notNull(docType, "docType may not be null");
        parser = new CustomQueryResultParser<T>(docType, om);
        parser.setIgnoreNotFound(ignoreNotFound);
    }

    @Override
    public List<T> success(HttpResponse hr) throws Exception {
        parser.parseResult(hr.getContent());
        return parser.getRows();
    }
}