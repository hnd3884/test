package com.zoho.security.api.wrapper;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLException;
import java.util.ArrayList;
import java.io.OutputStream;
import java.util.Iterator;
import com.zoho.security.util.MultipartUtil;
import java.io.IOException;
import java.net.Proxy;
import java.net.MalformedURLException;
import com.adventnet.iam.security.IAMSecurityException;
import com.zoho.security.api.Util;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.zoho.security.agent.MultipartFile;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;
import java.net.URL;

public final class URLWrapper
{
    private String actualURL;
    private URL url;
    private HttpURLConnection httpConnection;
    private HttpsURLConnection httpsConnection;
    private boolean isSecure;
    private int maxRedirects;
    private boolean enablePostRedirect;
    private boolean doInput;
    private boolean doOutput;
    private long ifModifiedSince;
    private boolean useCaches;
    private boolean defaultUseCaches;
    private int connectTimeout;
    private int readTimeout;
    private String requestMethod;
    private String body;
    private byte[] dataAsByteArray;
    private int chunkLength;
    private int fixedContentLength;
    private long fixedContentLengthLong;
    private boolean instanceFollowRedirects;
    private boolean isImportURL;
    private HashMap<String, List<String>> requestProperties;
    private Map<String, String> multipartParams;
    private List<MultipartFile> multipartFiles;
    private boolean allowLanAccess;
    private static final Logger LOGGER;
    
    public URLWrapper(final String urlString) {
        this(null, urlString, false);
    }
    
    public URLWrapper(final String urlString, final boolean allowLanAccess) {
        this(null, urlString, allowLanAccess);
    }
    
    public URLWrapper(final URL context, final String urlString, final boolean allowLanAccess) {
        this.isSecure = false;
        this.maxRedirects = 3;
        this.enablePostRedirect = false;
        this.doInput = true;
        this.doOutput = false;
        this.ifModifiedSince = 0L;
        this.useCaches = true;
        this.defaultUseCaches = true;
        this.connectTimeout = 10000;
        this.readTimeout = 10000;
        this.requestMethod = "GET";
        this.chunkLength = -1;
        this.fixedContentLength = -1;
        this.fixedContentLengthLong = -1L;
        this.instanceFollowRedirects = true;
        this.isImportURL = false;
        this.requestProperties = null;
        this.multipartParams = null;
        this.multipartFiles = null;
        this.allowLanAccess = false;
        try {
            this.url = new URL(context, urlString);
        }
        catch (final MalformedURLException e) {
            URLWrapper.LOGGER.log(Level.SEVERE, "Unable to parse the URL : {0} , Exception : {1}", new Object[] { Util.getMaskedUrl(urlString), e.getMessage() });
            throw new IAMSecurityException("INVALID_URL");
        }
        Util.validateURL(this.url, allowLanAccess);
        this.isSecure = "https".equals(this.url.getProtocol());
        this.actualURL = urlString;
        this.allowLanAccess = allowLanAccess;
    }
    
    @Deprecated
    public HttpURLConnection openHttpURLConnection() {
        return this.openHttpURLConnection(null);
    }
    
    @Deprecated
    public HttpURLConnection openHttpURLConnection(final Proxy proxy) {
        for (int redirects = 0; redirects < this.maxRedirects; ++redirects) {
            try {
                if (proxy == null) {
                    this.httpConnection = (HttpURLConnection)this.url.openConnection();
                }
                else {
                    this.httpConnection = (HttpURLConnection)this.url.openConnection(proxy);
                }
                this.connect();
                if (!this.followRedirect()) {
                    return this.httpConnection;
                }
                if ("POST".equals(this.requestMethod) && this.enablePostRedirect) {
                    this.requestMethod = "POST";
                }
                else {
                    this.requestMethod = "GET";
                }
            }
            catch (final IAMSecurityException secEx) {
                throw secEx;
            }
            catch (final IOException ex) {
                this.handleException(ex);
            }
        }
        URLWrapper.LOGGER.log(Level.SEVERE, "Redirect limit exceeded for this url : {0} & MaxRedirects : {1} ", new Object[] { Util.getMaskedUrl(this.actualURL), this.maxRedirects });
        throw new IAMSecurityException("URL_REDIRECT_LIMIT_EXCEEEDED");
    }
    
    @Deprecated
    public HttpsURLConnection openHttpsURLConnection() {
        return this.openHttpsURLConnection(null);
    }
    
    @Deprecated
    public HttpsURLConnection openHttpsURLConnection(final Proxy proxy) {
        for (int redirects = 0; redirects < this.maxRedirects; ++redirects) {
            try {
                if (proxy == null) {
                    this.httpsConnection = (HttpsURLConnection)this.url.openConnection();
                }
                else {
                    this.httpsConnection = (HttpsURLConnection)this.url.openConnection(proxy);
                }
                this.connect();
                if (!this.followRedirect()) {
                    return this.httpsConnection;
                }
                if ("POST".equals(this.requestMethod) && this.enablePostRedirect) {
                    this.requestMethod = "POST";
                }
                else {
                    this.requestMethod = "GET";
                }
            }
            catch (final IAMSecurityException secEx) {
                throw secEx;
            }
            catch (final IOException ex) {
                this.handleException(ex);
            }
        }
        URLWrapper.LOGGER.log(Level.SEVERE, "Redirect limit exceeded for this url : {0} & MaxRedirects : {1} ", new Object[] { Util.getMaskedUrl(this.actualURL), this.maxRedirects });
        throw new IAMSecurityException("URL_REDIRECT_LIMIT_EXCEEEDED");
    }
    
    public HttpURLConnection openURLConnection() {
        return this.openURLConnection(null);
    }
    
    public HttpURLConnection openURLConnection(final Proxy proxy) {
        for (int redirects = 0; redirects < this.maxRedirects; ++redirects) {
            HttpURLConnection urlConnection = null;
            try {
                if (!this.isSecure) {
                    final HttpURLConnection httpConnection = (HttpURLConnection)((proxy == null) ? this.url.openConnection() : ((HttpURLConnection)this.url.openConnection(proxy)));
                    this.httpConnection = httpConnection;
                    urlConnection = httpConnection;
                }
                else {
                    final HttpsURLConnection httpsConnection = (HttpsURLConnection)((proxy == null) ? this.url.openConnection() : ((HttpsURLConnection)this.url.openConnection(proxy)));
                    this.httpsConnection = httpsConnection;
                    urlConnection = httpsConnection;
                }
                this.connect();
                if (!this.followRedirect()) {
                    return urlConnection;
                }
                if ("POST".equals(this.requestMethod) && this.enablePostRedirect) {
                    this.requestMethod = "POST";
                }
                else {
                    this.requestMethod = "GET";
                }
            }
            catch (final IAMSecurityException secEx) {
                throw secEx;
            }
            catch (final IOException ex) {
                this.handleException(ex);
            }
        }
        URLWrapper.LOGGER.log(Level.SEVERE, "Redirect limit exceeded for this url : {0} & MaxRedirects : {1} ", new Object[] { Util.getMaskedUrl(this.actualURL), this.maxRedirects });
        throw new IAMSecurityException("URL_REDIRECT_LIMIT_EXCEEEDED");
    }
    
    private void connect() throws IOException {
        final HttpURLConnection connection = this.isSecure ? this.httpsConnection : this.httpConnection;
        connection.setInstanceFollowRedirects(false);
        connection.setDoInput(this.doInput);
        connection.setDoOutput(this.doOutput);
        connection.setConnectTimeout(this.connectTimeout);
        connection.setReadTimeout(this.readTimeout);
        connection.setUseCaches(this.useCaches);
        connection.setRequestMethod(this.requestMethod);
        connection.setIfModifiedSince(this.ifModifiedSince);
        connection.setDefaultUseCaches(this.defaultUseCaches);
        if (this.fixedContentLength != -1) {
            connection.setFixedLengthStreamingMode(this.fixedContentLength);
        }
        if (this.fixedContentLengthLong != -1L) {
            connection.setFixedLengthStreamingMode(this.fixedContentLengthLong);
        }
        if (this.chunkLength != -1) {
            connection.setChunkedStreamingMode(this.chunkLength);
        }
        if (this.requestProperties != null) {
            for (final Map.Entry<String, List<String>> entry : this.requestProperties.entrySet()) {
                final String headerName = entry.getKey();
                for (final String headerValue : entry.getValue()) {
                    connection.addRequestProperty(headerName, headerValue);
                }
            }
        }
        connection.setRequestProperty("ZSEC_USER_IMPORT_URL", "true");
        if ("POST".equals(this.requestMethod) || "PUT".equals(this.requestMethod)) {
            if (this.body != null || this.dataAsByteArray != null) {
                final byte[] bytes = (this.body != null) ? this.body.getBytes("UTF-8") : this.dataAsByteArray;
                connection.setDoOutput(true);
                final OutputStream os = connection.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
            else if (this.multipartParams != null || this.multipartFiles != null) {
                MultipartUtil.appendMultipartData(connection, this.multipartParams, this.multipartFiles);
            }
        }
        else {
            connection.connect();
        }
    }
    
    private boolean followRedirect() throws IOException {
        if (!this.isImportURL && !this.instanceFollowRedirects) {
            return false;
        }
        final HttpURLConnection connection = this.isSecure ? this.httpsConnection : this.httpConnection;
        final int responseCode = connection.getResponseCode();
        if (responseCode < 300 || responseCode > 307 || responseCode == 306 || responseCode == 304) {
            return false;
        }
        final String redirectStr = connection.getHeaderField("Location");
        if (redirectStr == null) {
            return false;
        }
        if (this.isImportURL && !this.instanceFollowRedirects) {
            URLWrapper.LOGGER.log(Level.SEVERE, "Error code- DISALLOWED_URL_REDIRECT, error msg- URL redirection is disallowed for import-url \"{0}\" and detected redirect-url is \"{1}\".", new Object[] { Util.getMaskedUrl(this.actualURL), Util.getMaskedUrl(redirectStr) });
            throw new IAMSecurityException("DISALLOWED_URL_REDIRECT");
        }
        URLWrapper redirectURL;
        if (redirectStr.startsWith("http://") || redirectStr.startsWith("https://")) {
            redirectURL = new URLWrapper(redirectStr, this.allowLanAccess);
            if (!this.isImportURL && !this.url.getProtocol().equalsIgnoreCase(redirectURL.url.getProtocol())) {
                return false;
            }
        }
        else {
            redirectURL = new URLWrapper(this.url, redirectStr, this.allowLanAccess);
        }
        this.isSecure = (this.isImportURL ? redirectURL.isSecure : this.isSecure);
        this.url = redirectURL.url;
        return true;
    }
    
    public void isImportURL(final boolean isImportURL) {
        this.isImportURL = isImportURL;
    }
    
    public boolean isImportURL() {
        return this.isImportURL;
    }
    
    public void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }
    
    public String getRequestMethod() {
        return this.requestMethod;
    }
    
    public int getMaxRedirects() {
        return this.maxRedirects;
    }
    
    public void setMaxRedirects(final int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }
    
    public boolean getDoInput() {
        return this.doInput;
    }
    
    public void setDoInput(final boolean doInput) {
        this.doInput = doInput;
    }
    
    public boolean getDoOutput() {
        return this.doOutput;
    }
    
    public void setDoOutput(final boolean doOutput) {
        this.doOutput = doOutput;
    }
    
    public long getIfModifiedSince() {
        return this.ifModifiedSince;
    }
    
    public void setIfModifiedSince(final long ifModifiedSince) {
        this.ifModifiedSince = ifModifiedSince;
    }
    
    public boolean getUseCaches() {
        return this.useCaches;
    }
    
    public void setUseCaches(final boolean useCaches) {
        this.useCaches = useCaches;
    }
    
    public boolean getDefaultUseCaches() {
        return this.defaultUseCaches;
    }
    
    public void setDefaultUseCaches(final boolean defaultUseCaches) {
        this.defaultUseCaches = defaultUseCaches;
    }
    
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    public void setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }
    
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    public void setReadTimeout(final int readTimeout) {
        this.readTimeout = readTimeout;
    }
    
    public String getPostData() {
        return this.body;
    }
    
    public void setPostData(final String postData) {
        this.body = postData;
    }
    
    public void setPostData(final byte[] postData) {
        this.dataAsByteArray = postData;
    }
    
    public HashMap<String, List<String>> getRequestProperties() {
        return this.requestProperties;
    }
    
    public void setRequestProperty(final String key, final String value) {
        if (this.requestProperties == null) {
            this.requestProperties = new HashMap<String, List<String>>();
        }
        final List<String> headerValues = new ArrayList<String>();
        headerValues.add(value);
        this.requestProperties.put(key, headerValues);
    }
    
    public void addRequestProperty(final String key, final String value) {
        if (this.requestProperties == null) {
            this.requestProperties = new HashMap<String, List<String>>();
        }
        if (this.requestProperties.containsKey(key)) {
            this.requestProperties.get(key).add(value);
        }
        else {
            final List<String> headerValues = new ArrayList<String>();
            headerValues.add(value);
            this.requestProperties.put(key, headerValues);
        }
    }
    
    public void setChunkedStreamingMode(final int chunkLen) {
        this.chunkLength = chunkLen;
    }
    
    public void setFixedLengthStreamingMode(final int contentLength) {
        this.fixedContentLength = contentLength;
    }
    
    public void setFixedLengthStreamingMode(final long contentLength) {
        this.fixedContentLengthLong = contentLength;
    }
    
    public void setInstanceFollowRedirects(final boolean followRedirects) {
        this.instanceFollowRedirects = followRedirects;
    }
    
    public boolean getInstanceFollowRedirects() {
        return this.instanceFollowRedirects;
    }
    
    public void setEnablePostRedirection(final boolean enablePostRedirect) {
        this.enablePostRedirect = enablePostRedirect;
    }
    
    public boolean isEnabledPostRedirection() {
        return this.enablePostRedirect;
    }
    
    public String getURL() {
        return this.actualURL;
    }
    
    public boolean isAllowedLanAccess() {
        return this.allowLanAccess;
    }
    
    public void setMultipartParams(final Map<String, String> params) {
        this.multipartParams = params;
    }
    
    public Map<String, String> getMultipartParams() {
        return this.multipartParams;
    }
    
    public List<MultipartFile> getMultipartFiles() {
        return this.multipartFiles;
    }
    
    public void setMultipartFiles(final List<MultipartFile> files) {
        this.multipartFiles = files;
    }
    
    private void handleException(final IOException ex) {
        if (!this.isImportURL) {
            URLWrapper.LOGGER.log(Level.SEVERE, "URLWrapper Error :: Unable to make connection to the URL : {0}, Actual URL : {1}, Exception Message : {2}", new Object[] { Util.getMaskedUrl(this.url.toString()), Util.getMaskedUrl(this.actualURL), ex.getMessage() });
            throw new IAMSecurityException("URL_CONNECTION_FAILED");
        }
        if (ex instanceof SSLException) {
            URLWrapper.LOGGER.log(Level.WARNING, "SSL handshake failed for the import URL: \"{0}\" & Exception Message: \"{1}\"", new Object[] { Util.getMaskedUrl(this.actualURL), ex.getMessage() });
            final String errorCode = (ex instanceof SSLHandshakeException) ? getSSLHandshakeErrorCode(ex.getMessage()) : "IMPORT_URL_SSL_HANDSHAKE_FAILED";
            final IAMSecurityException secEx = new IAMSecurityException(errorCode);
            secEx.initCause(ex);
            throw secEx;
        }
        URLWrapper.LOGGER.log(Level.WARNING, "Import from the URL \"{0}\" failed & Exception Message : \"{1}\"", new Object[] { Util.getMaskedUrl(this.actualURL), ex.getMessage() });
        throw new IAMSecurityException("UNABLE_TO_IMPORT");
    }
    
    private static String getSSLHandshakeErrorCode(final String exMessage) {
        String errorCode = "IMPORT_URL_CERTIFICATE_";
        if (exMessage == null) {
            return errorCode + "NOT_VALID";
        }
        final String message = exMessage.substring(exMessage.lastIndexOf(":") + 1).trim();
        if ("unable to find valid certification path to requested target".equalsIgnoreCase(message)) {
            errorCode += "NOT_TRUSTED";
        }
        else if ("timestamp check failed".equalsIgnoreCase(message) || "validity check failed".equalsIgnoreCase(message)) {
            errorCode += "EXPIRED";
        }
        else if (message.startsWith("No subject alternative") || message.startsWith("No name matching")) {
            errorCode += "NAME_MISMATCH";
        }
        else {
            errorCode += "NOT_VALID";
        }
        return errorCode;
    }
    
    static {
        LOGGER = Logger.getLogger(URLWrapper.class.getName());
    }
}
