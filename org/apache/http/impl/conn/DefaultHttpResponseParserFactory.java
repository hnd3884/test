package org.apache.http.impl.conn;

import org.apache.http.io.HttpMessageParser;
import org.apache.http.config.MessageConstraints;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.message.BasicLineParser;
import org.apache.http.HttpResponseFactory;
import org.apache.http.message.LineParser;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.annotation.Contract;
import org.apache.http.HttpResponse;
import org.apache.http.io.HttpMessageParserFactory;

@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class DefaultHttpResponseParserFactory implements HttpMessageParserFactory<HttpResponse>
{
    public static final DefaultHttpResponseParserFactory INSTANCE;
    private final LineParser lineParser;
    private final HttpResponseFactory responseFactory;
    
    public DefaultHttpResponseParserFactory(final LineParser lineParser, final HttpResponseFactory responseFactory) {
        this.lineParser = (LineParser)((lineParser != null) ? lineParser : BasicLineParser.INSTANCE);
        this.responseFactory = (HttpResponseFactory)((responseFactory != null) ? responseFactory : DefaultHttpResponseFactory.INSTANCE);
    }
    
    public DefaultHttpResponseParserFactory(final HttpResponseFactory responseFactory) {
        this(null, responseFactory);
    }
    
    public DefaultHttpResponseParserFactory() {
        this(null, null);
    }
    
    public HttpMessageParser<HttpResponse> create(final SessionInputBuffer buffer, final MessageConstraints constraints) {
        return (HttpMessageParser<HttpResponse>)new DefaultHttpResponseParser(buffer, this.lineParser, this.responseFactory, constraints);
    }
    
    static {
        INSTANCE = new DefaultHttpResponseParserFactory();
    }
}
