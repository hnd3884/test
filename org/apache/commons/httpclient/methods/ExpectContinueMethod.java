package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.httpclient.HttpException;
import java.io.IOException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.logging.Log;
import org.apache.commons.httpclient.HttpMethodBase;

public abstract class ExpectContinueMethod extends HttpMethodBase
{
    private static final Log LOG;
    
    public ExpectContinueMethod() {
    }
    
    public ExpectContinueMethod(final String uri) {
        super(uri);
    }
    
    public boolean getUseExpectHeader() {
        return this.getParams().getBooleanParameter("http.protocol.expect-continue", false);
    }
    
    public void setUseExpectHeader(final boolean value) {
        this.getParams().setBooleanParameter("http.protocol.expect-continue", value);
    }
    
    protected abstract boolean hasRequestContent();
    
    protected void addRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        ExpectContinueMethod.LOG.trace((Object)"enter ExpectContinueMethod.addRequestHeaders(HttpState, HttpConnection)");
        super.addRequestHeaders(state, conn);
        final boolean headerPresent = this.getRequestHeader("Expect") != null;
        if (this.getParams().isParameterTrue("http.protocol.expect-continue") && this.getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1) && this.hasRequestContent()) {
            if (!headerPresent) {
                this.setRequestHeader("Expect", "100-continue");
            }
        }
        else if (headerPresent) {
            this.removeRequestHeader("Expect");
        }
    }
    
    static {
        LOG = LogFactory.getLog(ExpectContinueMethod.class);
    }
}
