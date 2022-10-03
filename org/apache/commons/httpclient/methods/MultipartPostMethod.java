package org.apache.commons.httpclient.methods;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import org.apache.commons.httpclient.HttpException;
import java.io.IOException;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import java.io.FileNotFoundException;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import java.io.File;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;

public class MultipartPostMethod extends ExpectContinueMethod
{
    public static final String MULTIPART_FORM_CONTENT_TYPE = "multipart/form-data";
    private static final Log LOG;
    private final List parameters;
    
    public MultipartPostMethod() {
        this.parameters = new ArrayList();
    }
    
    public MultipartPostMethod(final String uri) {
        super(uri);
        this.parameters = new ArrayList();
    }
    
    protected boolean hasRequestContent() {
        return true;
    }
    
    public String getName() {
        return "POST";
    }
    
    public void addParameter(final String parameterName, final String parameterValue) {
        MultipartPostMethod.LOG.trace((Object)"enter addParameter(String parameterName, String parameterValue)");
        final Part param = new StringPart(parameterName, parameterValue);
        this.parameters.add(param);
    }
    
    public void addParameter(final String parameterName, final File parameterFile) throws FileNotFoundException {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.addParameter(String parameterName, File parameterFile)");
        final Part param = new FilePart(parameterName, parameterFile);
        this.parameters.add(param);
    }
    
    public void addParameter(final String parameterName, final String fileName, final File parameterFile) throws FileNotFoundException {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.addParameter(String parameterName, String fileName, File parameterFile)");
        final Part param = new FilePart(parameterName, fileName, parameterFile);
        this.parameters.add(param);
    }
    
    public void addPart(final Part part) {
        MultipartPostMethod.LOG.trace((Object)"enter addPart(Part part)");
        this.parameters.add(part);
    }
    
    public Part[] getParts() {
        return this.parameters.toArray(new Part[this.parameters.size()]);
    }
    
    protected void addContentLengthRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        MultipartPostMethod.LOG.trace((Object)"enter EntityEnclosingMethod.addContentLengthRequestHeader(HttpState, HttpConnection)");
        if (this.getRequestHeader("Content-Length") == null) {
            final long len = this.getRequestContentLength();
            this.addRequestHeader("Content-Length", String.valueOf(len));
        }
        this.removeRequestHeader("Transfer-Encoding");
    }
    
    protected void addContentTypeRequestHeader(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        MultipartPostMethod.LOG.trace((Object)"enter EntityEnclosingMethod.addContentTypeRequestHeader(HttpState, HttpConnection)");
        if (!this.parameters.isEmpty()) {
            final StringBuffer buffer = new StringBuffer("multipart/form-data");
            if (Part.getBoundary() != null) {
                buffer.append("; boundary=");
                buffer.append(Part.getBoundary());
            }
            this.setRequestHeader("Content-Type", buffer.toString());
        }
    }
    
    protected void addRequestHeaders(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.addRequestHeaders(HttpState state, HttpConnection conn)");
        super.addRequestHeaders(state, conn);
        this.addContentLengthRequestHeader(state, conn);
        this.addContentTypeRequestHeader(state, conn);
    }
    
    protected boolean writeRequestBody(final HttpState state, final HttpConnection conn) throws IOException, HttpException {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.writeRequestBody(HttpState state, HttpConnection conn)");
        final OutputStream out = conn.getRequestOutputStream();
        Part.sendParts(out, this.getParts());
        return true;
    }
    
    protected long getRequestContentLength() throws IOException {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.getRequestContentLength()");
        return Part.getLengthOfParts(this.getParts());
    }
    
    public void recycle() {
        MultipartPostMethod.LOG.trace((Object)"enter MultipartPostMethod.recycle()");
        super.recycle();
        this.parameters.clear();
    }
    
    static {
        LOG = LogFactory.getLog(MultipartPostMethod.class);
    }
}
