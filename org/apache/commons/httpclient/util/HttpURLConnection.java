package org.apache.commons.httpclient.util;

import org.apache.commons.logging.LogFactory;
import java.io.OutputStream;
import java.security.Permission;
import java.net.ProtocolException;
import org.apache.commons.httpclient.Header;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.logging.Log;

public class HttpURLConnection extends java.net.HttpURLConnection
{
    private static final Log LOG;
    private HttpMethod method;
    private URL url;
    
    public HttpURLConnection(final HttpMethod method, final URL url) {
        super(url);
        this.method = method;
        this.url = url;
    }
    
    protected HttpURLConnection(final URL url) {
        super(url);
        throw new RuntimeException("An HTTP URL connection can only be constructed from a HttpMethod class");
    }
    
    public InputStream getInputStream() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getInputStream()");
        return this.method.getResponseBodyAsStream();
    }
    
    public InputStream getErrorStream() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getErrorStream()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void disconnect() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.disconnect()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void connect() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.connect()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean usingProxy() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.usingProxy()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public String getRequestMethod() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getRequestMethod()");
        return this.method.getName();
    }
    
    public int getResponseCode() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getResponseCode()");
        return this.method.getStatusCode();
    }
    
    public String getResponseMessage() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getResponseMessage()");
        return this.method.getStatusText();
    }
    
    public String getHeaderField(final String name) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getHeaderField(String)");
        final Header[] headers = this.method.getResponseHeaders();
        for (int i = headers.length - 1; i >= 0; --i) {
            if (headers[i].getName().equalsIgnoreCase(name)) {
                return headers[i].getValue();
            }
        }
        return null;
    }
    
    public String getHeaderFieldKey(final int keyPosition) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getHeaderFieldKey(int)");
        if (keyPosition == 0) {
            return null;
        }
        final Header[] headers = this.method.getResponseHeaders();
        if (keyPosition < 0 || keyPosition > headers.length) {
            return null;
        }
        return headers[keyPosition - 1].getName();
    }
    
    public String getHeaderField(final int position) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getHeaderField(int)");
        if (position == 0) {
            return this.method.getStatusLine().toString();
        }
        final Header[] headers = this.method.getResponseHeaders();
        if (position < 0 || position > headers.length) {
            return null;
        }
        return headers[position - 1].getValue();
    }
    
    public URL getURL() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getURL()");
        return this.url;
    }
    
    public void setInstanceFollowRedirects(final boolean isFollowingRedirects) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setInstanceFollowRedirects(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean getInstanceFollowRedirects() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getInstanceFollowRedirects()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setRequestMethod(final String method) throws ProtocolException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setRequestMethod(String)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public Permission getPermission() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getPermission()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public Object getContent() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getContent()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public Object getContent(final Class[] classes) throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getContent(Class[])");
        throw new RuntimeException("Not implemented yet");
    }
    
    public OutputStream getOutputStream() throws IOException {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getOutputStream()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public void setDoInput(final boolean isInput) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setDoInput()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean getDoInput() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getDoInput()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setDoOutput(final boolean isOutput) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setDoOutput()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean getDoOutput() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getDoOutput()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setAllowUserInteraction(final boolean isAllowInteraction) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setAllowUserInteraction(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean getAllowUserInteraction() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getAllowUserInteraction()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setUseCaches(final boolean isUsingCaches) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setUseCaches(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public boolean getUseCaches() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getUseCaches()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setIfModifiedSince(final long modificationDate) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setIfModifiedSince(long)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public long getIfModifiedSince() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getIfmodifiedSince()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public boolean getDefaultUseCaches() {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getDefaultUseCaches()");
        throw new RuntimeException("Not implemented yet");
    }
    
    public void setDefaultUseCaches(final boolean isUsingCaches) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setDefaultUseCaches(boolean)");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public void setRequestProperty(final String key, final String value) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.setRequestProperty()");
        throw new RuntimeException("This class can only be used with alreadyretrieved data");
    }
    
    public String getRequestProperty(final String key) {
        HttpURLConnection.LOG.trace((Object)"enter HttpURLConnection.getRequestProperty()");
        throw new RuntimeException("Not implemented yet");
    }
    
    static {
        LOG = LogFactory.getLog(HttpURLConnection.class);
    }
}
