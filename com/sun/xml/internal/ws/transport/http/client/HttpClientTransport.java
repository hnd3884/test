package com.sun.xml.internal.ws.transport.http.client;

import java.io.FilterOutputStream;
import javax.net.ssl.SSLSession;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;
import java.util.Iterator;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.WebServiceException;
import java.io.FilterInputStream;
import com.sun.xml.internal.ws.transport.Headers;
import com.sun.istack.internal.Nullable;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.sun.xml.internal.ws.client.ClientTransportException;
import com.sun.xml.internal.ws.resources.ClientMessages;
import java.util.zip.GZIPOutputStream;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.EndpointAddress;
import java.net.HttpURLConnection;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class HttpClientTransport
{
    private static final byte[] THROW_AWAY_BUFFER;
    int statusCode;
    String statusMessage;
    int contentLength;
    private final Map<String, List<String>> reqHeaders;
    private Map<String, List<String>> respHeaders;
    private OutputStream outputStream;
    private boolean https;
    private HttpURLConnection httpConnection;
    private final EndpointAddress endpoint;
    private final Packet context;
    private final Integer chunkSize;
    
    public HttpClientTransport(@NotNull final Packet packet, @NotNull final Map<String, List<String>> reqHeaders) {
        this.respHeaders = null;
        this.httpConnection = null;
        this.endpoint = packet.endpointAddress;
        this.context = packet;
        this.reqHeaders = reqHeaders;
        this.chunkSize = this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
    }
    
    OutputStream getOutput() {
        try {
            this.createHttpConnection();
            if (this.requiresOutputStream()) {
                this.outputStream = this.httpConnection.getOutputStream();
                if (this.chunkSize != null) {
                    this.outputStream = new WSChunkedOuputStream(this.outputStream, this.chunkSize);
                }
                final List<String> contentEncoding = this.reqHeaders.get("Content-Encoding");
                if (contentEncoding != null && contentEncoding.get(0).contains("gzip")) {
                    this.outputStream = new GZIPOutputStream(this.outputStream);
                }
            }
            this.httpConnection.connect();
        }
        catch (final Exception ex) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_CLIENT_FAILED(ex), ex);
        }
        return this.outputStream;
    }
    
    void closeOutput() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.close();
            this.outputStream = null;
        }
    }
    
    @Nullable
    InputStream getInput() {
        InputStream in;
        try {
            in = this.readResponse();
            if (in != null) {
                final String contentEncoding = this.httpConnection.getContentEncoding();
                if (contentEncoding != null && contentEncoding.contains("gzip")) {
                    in = new GZIPInputStream(in);
                }
            }
        }
        catch (final IOException e) {
            throw new ClientTransportException(ClientMessages.localizableHTTP_STATUS_CODE(this.statusCode, this.statusMessage), e);
        }
        return in;
    }
    
    public Map<String, List<String>> getHeaders() {
        if (this.respHeaders != null) {
            return this.respHeaders;
        }
        (this.respHeaders = new Headers()).putAll(this.httpConnection.getHeaderFields());
        return this.respHeaders;
    }
    
    @Nullable
    protected InputStream readResponse() {
        InputStream is;
        try {
            is = this.httpConnection.getInputStream();
        }
        catch (final IOException ioe) {
            is = this.httpConnection.getErrorStream();
        }
        if (is == null) {
            return is;
        }
        final InputStream temp = is;
        return new FilterInputStream(temp) {
            boolean closed;
            
            @Override
            public void close() throws IOException {
                if (!this.closed) {
                    this.closed = true;
                    while (temp.read(HttpClientTransport.THROW_AWAY_BUFFER) != -1) {}
                    super.close();
                }
            }
        };
    }
    
    protected void readResponseCodeAndMessage() {
        try {
            this.statusCode = this.httpConnection.getResponseCode();
            this.statusMessage = this.httpConnection.getResponseMessage();
            this.contentLength = this.httpConnection.getContentLength();
        }
        catch (final IOException ioe) {
            throw new WebServiceException(ioe);
        }
    }
    
    protected HttpURLConnection openConnection(final Packet packet) {
        return null;
    }
    
    protected boolean checkHTTPS(final HttpURLConnection connection) {
        if (connection instanceof HttpsURLConnection) {
            final String verificationProperty = this.context.invocationProperties.get("com.sun.xml.internal.ws.client.http.HostnameVerificationProperty");
            if (verificationProperty != null && verificationProperty.equalsIgnoreCase("true")) {
                ((HttpsURLConnection)connection).setHostnameVerifier(new HttpClientVerifier());
            }
            final HostnameVerifier verifier = this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.hostname.verifier");
            if (verifier != null) {
                ((HttpsURLConnection)connection).setHostnameVerifier(verifier);
            }
            final SSLSocketFactory sslSocketFactory = this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory");
            if (sslSocketFactory != null) {
                ((HttpsURLConnection)connection).setSSLSocketFactory(sslSocketFactory);
            }
            return true;
        }
        return false;
    }
    
    private void createHttpConnection() throws IOException {
        this.httpConnection = this.openConnection(this.context);
        if (this.httpConnection == null) {
            this.httpConnection = (HttpURLConnection)this.endpoint.openConnection();
        }
        final String scheme = this.endpoint.getURI().getScheme();
        if (scheme.equals("https")) {
            this.https = true;
        }
        if (this.checkHTTPS(this.httpConnection)) {
            this.https = true;
        }
        this.httpConnection.setAllowUserInteraction(true);
        this.httpConnection.setDoOutput(true);
        this.httpConnection.setDoInput(true);
        final String requestMethod = this.context.invocationProperties.get("javax.xml.ws.http.request.method");
        final String method = (requestMethod != null) ? requestMethod : "POST";
        this.httpConnection.setRequestMethod(method);
        final Integer reqTimeout = this.context.invocationProperties.get("com.sun.xml.internal.ws.request.timeout");
        if (reqTimeout != null) {
            this.httpConnection.setReadTimeout(reqTimeout);
        }
        final Integer connectTimeout = this.context.invocationProperties.get("com.sun.xml.internal.ws.connect.timeout");
        if (connectTimeout != null) {
            this.httpConnection.setConnectTimeout(connectTimeout);
        }
        final Integer chunkSize = this.context.invocationProperties.get("com.sun.xml.internal.ws.transport.http.client.streaming.chunk.size");
        if (chunkSize != null) {
            this.httpConnection.setChunkedStreamingMode(chunkSize);
        }
        for (final Map.Entry<String, List<String>> entry : this.reqHeaders.entrySet()) {
            if ("Content-Length".equals(entry.getKey())) {
                continue;
            }
            for (final String value : entry.getValue()) {
                this.httpConnection.addRequestProperty(entry.getKey(), value);
            }
        }
    }
    
    boolean isSecure() {
        return this.https;
    }
    
    protected void setStatusCode(final int statusCode) {
        this.statusCode = statusCode;
    }
    
    private boolean requiresOutputStream() {
        return !this.httpConnection.getRequestMethod().equalsIgnoreCase("GET") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("HEAD") && !this.httpConnection.getRequestMethod().equalsIgnoreCase("DELETE");
    }
    
    @Nullable
    String getContentType() {
        return this.httpConnection.getContentType();
    }
    
    public int getContentLength() {
        return this.httpConnection.getContentLength();
    }
    
    static {
        THROW_AWAY_BUFFER = new byte[8192];
        try {
            JAXBContext.newInstance(new Class[0]).createUnmarshaller();
        }
        catch (final JAXBException ex) {}
    }
    
    private static class HttpClientVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(final String s, final SSLSession sslSession) {
            return true;
        }
    }
    
    private static class LocalhostHttpClientVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(final String s, final SSLSession sslSession) {
            return "localhost".equalsIgnoreCase(s) || "127.0.0.1".equals(s);
        }
    }
    
    private static final class WSChunkedOuputStream extends FilterOutputStream
    {
        final int chunkSize;
        
        WSChunkedOuputStream(final OutputStream actual, final int chunkSize) {
            super(actual);
            this.chunkSize = chunkSize;
        }
        
        @Override
        public void write(final byte[] b, int off, int len) throws IOException {
            while (len > 0) {
                final int sent = (len > this.chunkSize) ? this.chunkSize : len;
                this.out.write(b, off, sent);
                len -= sent;
                off += sent;
            }
        }
    }
}
