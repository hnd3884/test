package sun.net.www.protocol.https;

import java.security.Permission;
import java.net.ProtocolException;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Principal;
import javax.security.cert.X509Certificate;
import javax.net.ssl.SSLPeerUnverifiedException;
import java.security.cert.Certificate;
import sun.net.util.IPAddressUtil;
import java.net.MalformedURLException;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class HttpsURLConnectionImpl extends HttpsURLConnection
{
    protected DelegateHttpsURLConnection delegate;
    
    HttpsURLConnectionImpl(final URL url, final Handler handler) throws IOException {
        this(url, null, handler);
    }
    
    static URL checkURL(final URL url) throws IOException {
        if (url != null && url.toExternalForm().indexOf(10) > -1) {
            throw new MalformedURLException("Illegal character in URL");
        }
        final String checkAuthority = IPAddressUtil.checkAuthority(url);
        if (checkAuthority != null) {
            throw new MalformedURLException(checkAuthority);
        }
        return url;
    }
    
    HttpsURLConnectionImpl(final URL url, final Proxy proxy, final Handler handler) throws IOException {
        super(checkURL(url));
        this.delegate = new DelegateHttpsURLConnection(this.url, proxy, handler, this);
    }
    
    protected HttpsURLConnectionImpl(final URL url) throws IOException {
        super(url);
    }
    
    protected void setNewClient(final URL url) throws IOException {
        this.delegate.setNewClient(url, false);
    }
    
    protected void setNewClient(final URL url, final boolean b) throws IOException {
        this.delegate.setNewClient(url, b);
    }
    
    protected void setProxiedClient(final URL url, final String s, final int n) throws IOException {
        this.delegate.setProxiedClient(url, s, n);
    }
    
    protected void setProxiedClient(final URL url, final String s, final int n, final boolean b) throws IOException {
        this.delegate.setProxiedClient(url, s, n, b);
    }
    
    @Override
    public void connect() throws IOException {
        this.delegate.connect();
    }
    
    protected boolean isConnected() {
        return this.delegate.isConnected();
    }
    
    protected void setConnected(final boolean connected) {
        this.delegate.setConnected(connected);
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
    
    public X509Certificate[] getServerCertificateChain() {
        try {
            return this.delegate.getServerCertificateChain();
        }
        catch (final SSLPeerUnverifiedException ex) {
            return null;
        }
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
    public String getHeaderField(final String s) {
        return this.delegate.getHeaderField(s);
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
    public void setRequestProperty(final String s, final String s2) {
        this.delegate.setRequestProperty(s, s2);
    }
    
    @Override
    public void addRequestProperty(final String s, final String s2) {
        this.delegate.addRequestProperty(s, s2);
    }
    
    @Override
    public int getResponseCode() throws IOException {
        return this.delegate.getResponseCode();
    }
    
    @Override
    public String getRequestProperty(final String s) {
        return this.delegate.getRequestProperty(s);
    }
    
    @Override
    public Map<String, List<String>> getRequestProperties() {
        return this.delegate.getRequestProperties();
    }
    
    @Override
    public void setInstanceFollowRedirects(final boolean instanceFollowRedirects) {
        this.delegate.setInstanceFollowRedirects(instanceFollowRedirects);
    }
    
    @Override
    public boolean getInstanceFollowRedirects() {
        return this.delegate.getInstanceFollowRedirects();
    }
    
    @Override
    public void setRequestMethod(final String requestMethod) throws ProtocolException {
        this.delegate.setRequestMethod(requestMethod);
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
    public long getHeaderFieldDate(final String s, final long n) {
        return this.delegate.getHeaderFieldDate(s, n);
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
    public int getHeaderFieldInt(final String s, final int n) {
        return this.delegate.getHeaderFieldInt(s, n);
    }
    
    @Override
    public long getHeaderFieldLong(final String s, final long n) {
        return this.delegate.getHeaderFieldLong(s, n);
    }
    
    @Override
    public Object getContent() throws IOException {
        return this.delegate.getContent();
    }
    
    @Override
    public Object getContent(final Class[] array) throws IOException {
        return this.delegate.getContent(array);
    }
    
    @Override
    public String toString() {
        return this.delegate.toString();
    }
    
    @Override
    public void setDoInput(final boolean doInput) {
        this.delegate.setDoInput(doInput);
    }
    
    @Override
    public boolean getDoInput() {
        return this.delegate.getDoInput();
    }
    
    @Override
    public void setDoOutput(final boolean doOutput) {
        this.delegate.setDoOutput(doOutput);
    }
    
    @Override
    public boolean getDoOutput() {
        return this.delegate.getDoOutput();
    }
    
    @Override
    public void setAllowUserInteraction(final boolean allowUserInteraction) {
        this.delegate.setAllowUserInteraction(allowUserInteraction);
    }
    
    @Override
    public boolean getAllowUserInteraction() {
        return this.delegate.getAllowUserInteraction();
    }
    
    @Override
    public void setUseCaches(final boolean useCaches) {
        this.delegate.setUseCaches(useCaches);
    }
    
    @Override
    public boolean getUseCaches() {
        return this.delegate.getUseCaches();
    }
    
    @Override
    public void setIfModifiedSince(final long ifModifiedSince) {
        this.delegate.setIfModifiedSince(ifModifiedSince);
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
    public void setDefaultUseCaches(final boolean defaultUseCaches) {
        this.delegate.setDefaultUseCaches(defaultUseCaches);
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.delegate.dispose();
    }
    
    @Override
    public boolean equals(final Object o) {
        return this.delegate.equals(o);
    }
    
    @Override
    public int hashCode() {
        return this.delegate.hashCode();
    }
    
    @Override
    public void setConnectTimeout(final int connectTimeout) {
        this.delegate.setConnectTimeout(connectTimeout);
    }
    
    @Override
    public int getConnectTimeout() {
        return this.delegate.getConnectTimeout();
    }
    
    @Override
    public void setReadTimeout(final int readTimeout) {
        this.delegate.setReadTimeout(readTimeout);
    }
    
    @Override
    public int getReadTimeout() {
        return this.delegate.getReadTimeout();
    }
    
    @Override
    public void setFixedLengthStreamingMode(final int fixedLengthStreamingMode) {
        this.delegate.setFixedLengthStreamingMode(fixedLengthStreamingMode);
    }
    
    @Override
    public void setFixedLengthStreamingMode(final long fixedLengthStreamingMode) {
        this.delegate.setFixedLengthStreamingMode(fixedLengthStreamingMode);
    }
    
    @Override
    public void setChunkedStreamingMode(final int chunkedStreamingMode) {
        this.delegate.setChunkedStreamingMode(chunkedStreamingMode);
    }
}
