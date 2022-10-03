package org.openjsse.sun.net.www.protocol.https;

import java.security.Permission;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import org.openjsse.sun.net.util.IPAddressUtil;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpsURLConnectionImpl extends HttpsURLConnection
{
    protected DelegateHttpsURLConnection delegate;
    
    HttpsURLConnectionImpl(final URL u, final Handler handler) throws IOException {
        this(u, null, handler);
    }
    
    static URL checkURL(final URL u) throws IOException {
        if (u != null && u.toExternalForm().indexOf(10) > -1) {
            throw new MalformedURLException("Illegal character in URL");
        }
        final String s = IPAddressUtil.checkAuthority(u);
        if (s != null) {
            throw new MalformedURLException(s);
        }
        return u;
    }
    
    HttpsURLConnectionImpl(final URL u, final Proxy p, final Handler handler) throws IOException {
        super(checkURL(u));
        this.delegate = new DelegateHttpsURLConnection(this.url, p, handler, this);
    }
    
    protected HttpsURLConnectionImpl(final URL u) throws IOException {
        super(u);
    }
    
    protected void setNewClient(final URL url) throws IOException {
        this.delegate.setNewClient(url, false);
    }
    
    protected void setNewClient(final URL url, final boolean useCache) throws IOException {
        this.delegate.setNewClient(url, useCache);
    }
    
    protected void setProxiedClient(final URL url, final String proxyHost, final int proxyPort) throws IOException {
        this.delegate.setProxiedClient(url, proxyHost, proxyPort);
    }
    
    protected void setProxiedClient(final URL url, final String proxyHost, final int proxyPort, final boolean useCache) throws IOException {
        this.delegate.setProxiedClient(url, proxyHost, proxyPort, useCache);
    }
    
    @Override
    public void connect() throws IOException {
        this.delegate.connect();
    }
    
    protected boolean isConnected() {
        return this.delegate.isConnected();
    }
    
    protected void setConnected(final boolean conn) {
        this.delegate.setConnected(conn);
    }
    
    @Override
    public String getCipherSuite() {
        return this.delegate.getCipherSuite();
    }
    
    @Override
    public Certificate[] getLocalCertificates() {
        return this.delegate.getLocalCertificates();
    }
    
    @Override
    public Certificate[] getServerCertificates() throws SSLPeerUnverifiedException {
        return this.delegate.getServerCertificates();
    }
    
    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
        return this.delegate.getPeerPrincipal();
    }
    
    @Override
    public Principal getLocalPrincipal() {
        return this.delegate.getLocalPrincipal();
    }
    
    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        return this.delegate.getOutputStream();
    }
    
    @Override
    public synchronized InputStream getInputStream() throws IOException {
        return this.delegate.getInputStream();
    }
    
    @Override
    public InputStream getErrorStream() {
        return this.delegate.getErrorStream();
    }
    
    @Override
    public void disconnect() {
        this.delegate.disconnect();
    }
    
    @Override
    public boolean usingProxy() {
        return this.delegate.usingProxy();
    }
    
    @Override
    public Map<String, List<String>> getHeaderFields() {
        return this.delegate.getHeaderFields();
    }
    
    @Override
    public String getHeaderField(final String name) {
        return this.delegate.getHeaderField(name);
    }
    
    @Override
    public String getHeaderField(final int n) {
        return this.delegate.getHeaderField(n);
    }
    
    @Override
    public String getHeaderFieldKey(final int n) {
        return this.delegate.getHeaderFieldKey(n);
    }
    
    @Override
    public void setRequestProperty(final String key, final String value) {
        this.delegate.setRequestProperty(key, value);
    }
    
    @Override
    public void addRequestProperty(final String key, final String value) {
        this.delegate.addRequestProperty(key, value);
    }
    
    @Override
    public int getResponseCode() throws IOException {
        return this.delegate.getResponseCode();
    }
    
    @Override
    public String getRequestProperty(final String key) {
        return this.delegate.getRequestProperty(key);
    }
    
    @Override
    public Map<String, List<String>> getRequestProperties() {
        return this.delegate.getRequestProperties();
    }
    
    @Override
    public void setInstanceFollowRedirects(final boolean shouldFollow) {
        this.delegate.setInstanceFollowRedirects(shouldFollow);
    }
    
    @Override
    public boolean getInstanceFollowRedirects() {
        return this.delegate.getInstanceFollowRedirects();
    }
    
    @Override
    public void setRequestMethod(final String method) throws ProtocolException {
        this.delegate.setRequestMethod(method);
    }
    
    @Override
    public String getRequestMethod() {
        return this.delegate.getRequestMethod();
    }
    
    @Override
    public String getResponseMessage() throws IOException {
        return this.delegate.getResponseMessage();
    }
    
    @Override
    public long getHeaderFieldDate(final String name, final long Default) {
        return this.delegate.getHeaderFieldDate(name, Default);
    }
    
    @Override
    public Permission getPermission() throws IOException {
        return this.delegate.getPermission();
    }
    
    @Override
    public URL getURL() {
        return this.delegate.getURL();
    }
    
    @Override
    public int getContentLength() {
        return this.delegate.getContentLength();
    }
    
    @Override
    public long getContentLengthLong() {
        return this.delegate.getContentLengthLong();
    }
    
    @Override
    public String getContentType() {
        return this.delegate.getContentType();
    }
    
    @Override
    public String getContentEncoding() {
        return this.delegate.getContentEncoding();
    }
    
    @Override
    public long getExpiration() {
        return this.delegate.getExpiration();
    }
    
    @Override
    public long getDate() {
        return this.delegate.getDate();
    }
    
    @Override
    public long getLastModified() {
        return this.delegate.getLastModified();
    }
    
    @Override
    public int getHeaderFieldInt(final String name, final int Default) {
        return this.delegate.getHeaderFieldInt(name, Default);
    }
    
    @Override
    public long getHeaderFieldLong(final String name, final long Default) {
        return this.delegate.getHeaderFieldLong(name, Default);
    }
    
    @Override
    public Object getContent() throws IOException {
        return this.delegate.getContent();
    }
    
    @Override
    public Object getContent(final Class[] classes) throws IOException {
        return this.delegate.getContent(classes);
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    @Override
    public void setDoInput(final boolean doinput) {
        this.delegate.setDoInput(doinput);
    }
    
    @Override
    public boolean getDoInput() {
        return this.delegate.getDoInput();
    }
    
    @Override
    public void setDoOutput(final boolean dooutput) {
        this.delegate.setDoOutput(dooutput);
    }
    
    @Override
    public boolean getDoOutput() {
        return this.delegate.getDoOutput();
    }
    
    @Override
    public void setAllowUserInteraction(final boolean allowuserinteraction) {
        this.delegate.setAllowUserInteraction(allowuserinteraction);
    }
    
    @Override
    public boolean getAllowUserInteraction() {
        return this.delegate.getAllowUserInteraction();
    }
    
    @Override
    public void setUseCaches(final boolean usecaches) {
        this.delegate.setUseCaches(usecaches);
    }
    
    @Override
    public boolean getUseCaches() {
        return this.delegate.getUseCaches();
    }
    
    @Override
    public void setIfModifiedSince(final long ifmodifiedsince) {
        this.delegate.setIfModifiedSince(ifmodifiedsince);
    }
    
    @Override
    public long getIfModifiedSince() {
        return this.delegate.getIfModifiedSince();
    }
    
    @Override
    public boolean getDefaultUseCaches() {
        return this.delegate.getDefaultUseCaches();
    }
    
    @Override
    public void setDefaultUseCaches(final boolean defaultusecaches) {
        this.delegate.setDefaultUseCaches(defaultusecaches);
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.delegate.dispose();
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this == obj || (obj instanceof HttpsURLConnectionImpl && this.delegate.equals(((HttpsURLConnectionImpl)obj).delegate));
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public void setConnectTimeout(final int timeout) {
        this.delegate.setConnectTimeout(timeout);
    }
    
    @Override
    public int getConnectTimeout() {
        return this.delegate.getConnectTimeout();
    }
    
    @Override
    public void setReadTimeout(final int timeout) {
        this.delegate.setReadTimeout(timeout);
    }
    
    @Override
    public int getReadTimeout() {
        return this.delegate.getReadTimeout();
    }
    
    @Override
    public void setFixedLengthStreamingMode(final int contentLength) {
        this.delegate.setFixedLengthStreamingMode(contentLength);
    }
    
    @Override
    public void setFixedLengthStreamingMode(final long contentLength) {
        this.delegate.setFixedLengthStreamingMode(contentLength);
    }
    
    @Override
    public void setChunkedStreamingMode(final int chunklen) {
        this.delegate.setChunkedStreamingMode(chunklen);
    }
}
