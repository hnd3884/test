package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import org.apache.commons.httpclient.ChunkedOutputStream;
import org.apache.commons.httpclient.ProtocolException;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.HttpException;
import java.io.IOException;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.Header;
import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public abstract class EntityEnclosingMethod extends ExpectContinueMethod
{
    public static final long CONTENT_LENGTH_AUTO = -2L;
    public static final long CONTENT_LENGTH_CHUNKED = -1L;
    private static final Log LOG;
    private InputStream requestStream;
    private String requestString;
    private RequestEntity requestEntity;
    private int repeatCount;
    private long requestContentLength;
    private boolean chunked;
    
    public EntityEnclosingMethod() {
        this.requestStream = null;
        this.requestString = null;
        this.repeatCount = 0;
        this.requestContentLength = -2L;
        this.setFollowRedirects(this.chunked = false);
    }
    
    public EntityEnclosingMethod(final String uri) {
        super(uri);
        this.requestStream = null;
        this.requestString = null;
        this.repeatCount = 0;
        this.requestContentLength = -2L;
        this.setFollowRedirects(this.chunked = false);
    }
    
    protected boolean hasRequestContent() {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.hasRequestContent()");
        return this.requestEntity != null || this.requestStream != null || this.requestString != null;
    }
    
    protected void clearRequestBody() {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.clearRequestBody()");
        this.requestStream = null;
        this.requestString = null;
        this.requestEntity = null;
    }
    
    protected byte[] generateRequestBody() {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.renerateRequestBody()");
        return null;
    }
    
    protected RequestEntity generateRequestEntity() {
        final byte[] requestBody = this.generateRequestBody();
        if (requestBody != null) {
            this.requestEntity = new ByteArrayRequestEntity(requestBody);
        }
        else if (this.requestStream != null) {
            this.requestEntity = new InputStreamRequestEntity(this.requestStream, this.requestContentLength);
            this.requestStream = null;
        }
        else if (this.requestString != null) {
            final String charset = this.getRequestCharSet();
            try {
                this.requestEntity = new StringRequestEntity(this.requestString, null, charset);
            }
            catch (final UnsupportedEncodingException e) {
                if (EntityEnclosingMethod.LOG.isWarnEnabled()) {
                    EntityEnclosingMethod.LOG.warn((Object)(charset + " not supported"));
                }
                try {
                    this.requestEntity = new StringRequestEntity(this.requestString, null, null);
                }
                catch (final UnsupportedEncodingException ex) {}
            }
        }
        return this.requestEntity;
    }
    
    public boolean getFollowRedirects() {
        return false;
    }
    
    public void setFollowRedirects(final boolean followRedirects) {
        if (followRedirects) {
            throw new IllegalArgumentException("Entity enclosing requests cannot be redirected without user intervention");
        }
        super.setFollowRedirects(false);
    }
    
    public void setRequestContentLength(final int length) {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.setRequestContentLength(int)");
        this.requestContentLength = length;
    }
    
    public String getRequestCharSet() {
        if (this.getRequestHeader("Content-Type") != null) {
            return super.getRequestCharSet();
        }
        if (this.requestEntity != null) {
            return this.getContentCharSet(new Header("Content-Type", this.requestEntity.getContentType()));
        }
        return super.getRequestCharSet();
    }
    
    public void setRequestContentLength(final long length) {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.setRequestContentLength(int)");
        this.requestContentLength = length;
    }
    
    public void setContentChunked(final boolean chunked) {
        this.chunked = chunked;
    }
    
    protected long getRequestContentLength() {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.getRequestContentLength()");
        if (!this.hasRequestContent()) {
            return 0L;
        }
        if (this.chunked) {
            return -1L;
        }
        if (this.requestEntity == null) {
            this.requestEntity = this.generateRequestEntity();
        }
        return (this.requestEntity == null) ? 0L : this.requestEntity.getContentLength();
    }
    
    protected void addRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.addRequestHeaders(HttpState, HttpConnection)");
        super.addRequestHeaders(state, conn);
        this.addContentLengthRequestHeader(state, conn);
        if (this.getRequestHeader("Content-Type") == null) {
            final RequestEntity requestEntity = this.getRequestEntity();
            if (requestEntity != null && requestEntity.getContentType() != null) {
                this.setRequestHeader("Content-Type", requestEntity.getContentType());
            }
        }
    }
    
    protected void addContentLengthRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.addContentLengthRequestHeader(HttpState, HttpConnection)");
        if (this.getRequestHeader("content-length") == null && this.getRequestHeader("Transfer-Encoding") == null) {
            final long len = this.getRequestContentLength();
            if (len < 0L) {
                if (!this.getEffectiveVersion().greaterEquals(HttpVersion.HTTP_1_1)) {
                    throw new ProtocolException(this.getEffectiveVersion() + " does not support chunk encoding");
                }
                this.addRequestHeader("Transfer-Encoding", "chunked");
            }
            else {
                this.addRequestHeader("Content-Length", String.valueOf(len));
            }
        }
    }
    
    public void setRequestBody(final InputStream body) {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.setRequestBody(InputStream)");
        this.clearRequestBody();
        this.requestStream = body;
    }
    
    public void setRequestBody(final String body) {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.setRequestBody(String)");
        this.clearRequestBody();
        this.requestString = body;
    }
    
    protected boolean writeRequestBody(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.writeRequestBody(HttpState, HttpConnection)");
        if (!this.hasRequestContent()) {
            EntityEnclosingMethod.LOG.debug((Object)"Request body has not been specified");
            return true;
        }
        if (this.requestEntity == null) {
            this.requestEntity = this.generateRequestEntity();
        }
        if (this.requestEntity == null) {
            EntityEnclosingMethod.LOG.debug((Object)"Request body is empty");
            return true;
        }
        final long contentLength = this.getRequestContentLength();
        if (this.repeatCount > 0 && !this.requestEntity.isRepeatable()) {
            throw new ProtocolException("Unbuffered entity enclosing request can not be repeated.");
        }
        ++this.repeatCount;
        OutputStream outstream = conn.getRequestOutputStream();
        if (contentLength < 0L) {
            outstream = new ChunkedOutputStream(outstream);
        }
        this.requestEntity.writeRequest(outstream);
        if (outstream instanceof ChunkedOutputStream) {
            ((ChunkedOutputStream)outstream).finish();
        }
        outstream.flush();
        EntityEnclosingMethod.LOG.debug((Object)"Request body sent");
        return true;
    }
    
    public void recycle() {
        EntityEnclosingMethod.LOG.trace((Object)"enter EntityEnclosingMethod.recycle()");
        this.clearRequestBody();
        this.requestContentLength = -2L;
        this.repeatCount = 0;
        this.chunked = false;
        super.recycle();
    }
    
    public RequestEntity getRequestEntity() {
        return this.generateRequestEntity();
    }
    
    public void setRequestEntity(final RequestEntity requestEntity) {
        this.clearRequestBody();
        this.requestEntity = requestEntity;
    }
    
    static {
        LOG = LogFactory.getLog(EntityEnclosingMethod.class);
    }
}
