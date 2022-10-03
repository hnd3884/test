package com.zoho.security.api;

import java.util.Collection;
import java.util.Arrays;
import java.io.InputStream;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.net.URL;
import java.util.ArrayList;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class HttpConnection
{
    private static final Logger LOGGER;
    private String url;
    private String requestMethod;
    private int connectTimeout;
    private int readTimeout;
    private boolean followRedirect;
    private int maxRedirects;
    private byte[] requestBody;
    private HashMap<String, List<String>> requestProperties;
    private boolean enablePostMethodRedirect;
    private int redirectionCount;
    private HttpURLConnection connection;
    private String actualURL;
    public static final List<String> REQ_BODY_ALLOWED_METHODS;
    
    public HttpConnection(final String url, final String method) {
        this.requestMethod = "GET";
        this.connectTimeout = 10000;
        this.readTimeout = 10000;
        this.followRedirect = true;
        this.maxRedirects = 3;
        this.requestProperties = null;
        this.enablePostMethodRedirect = false;
        this.redirectionCount = 0;
        this.connection = null;
        this.url = url;
        this.actualURL = url;
        this.requestMethod = method;
    }
    
    public HttpConnection(final String url, final String method, final int connectTimeout, final int readTimeout) {
        this(url, method);
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }
    
    public void setConnectTimeout(final int timeout) {
        this.connectTimeout = timeout;
    }
    
    public void setReadTimeout(final int timeout) {
        this.readTimeout = timeout;
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
    
    public void setFollowRedirect(final boolean redirect) {
        this.followRedirect = redirect;
    }
    
    public void setMaxRedirects(final int maxRedirects) {
        this.maxRedirects = maxRedirects;
    }
    
    public void setRequestMethod(final String method) {
        this.requestMethod = method;
    }
    
    public void setRequestBody(final String reqBody) {
        this.requestBody = reqBody.getBytes();
    }
    
    public void setRequestBody(final byte[] reqBody) {
        this.requestBody = reqBody;
    }
    
    public void setPostMethodRedirect(final boolean postRedirect) {
        this.enablePostMethodRedirect = postRedirect;
    }
    
    public void connect() throws IOException {
        (this.connection = (HttpURLConnection)new URL(this.url).openConnection()).setRequestMethod(this.requestMethod);
        this.connection.setConnectTimeout(this.connectTimeout);
        this.connection.setReadTimeout(this.readTimeout);
        this.connection.setInstanceFollowRedirects(this.followRedirect);
        this.connection.setInstanceFollowRedirects(false);
        if (this.requestProperties != null) {
            for (final Map.Entry<String, List<String>> entry : this.requestProperties.entrySet()) {
                final String headerName = entry.getKey();
                for (final String headerValue : entry.getValue()) {
                    this.connection.addRequestProperty(headerName, headerValue);
                }
            }
        }
        if (this.requestBody != null && HttpConnection.REQ_BODY_ALLOWED_METHODS.contains(this.requestMethod)) {
            this.connection.setDoOutput(true);
            this.connection.getOutputStream().write(this.requestBody);
        }
        if (this.followRedirect()) {
            if ("POST".equals(this.requestMethod) && this.enablePostMethodRedirect) {
                this.requestMethod = "POST";
            }
            else {
                this.requestMethod = "GET";
            }
            this.connect();
        }
    }
    
    private boolean followRedirect() throws IOException {
        if (!this.followRedirect) {
            return false;
        }
        final int responseCode = this.connection.getResponseCode();
        if (responseCode < 300 || responseCode > 307 || responseCode == 306 || responseCode == 304) {
            return false;
        }
        ++this.redirectionCount;
        if (this.redirectionCount > this.maxRedirects) {
            HttpConnection.LOGGER.log(Level.SEVERE, "Redirect limit exceeded for this url : {0} & MaxRedirects : {1} ", new Object[] { this.actualURL, this.maxRedirects });
            throw new IAMSecurityException("URL_REDIRECT_LIMIT_EXCEEEDED");
        }
        final String redirectStr = this.connection.getHeaderField("Location");
        if (redirectStr == null) {
            return false;
        }
        URL redirectURL;
        if (redirectStr.startsWith("http://") || redirectStr.startsWith("https://")) {
            redirectURL = new URL(redirectStr);
            if (!this.connection.getURL().getProtocol().equalsIgnoreCase(redirectURL.getProtocol())) {
                HttpConnection.LOGGER.log(Level.SEVERE, "URL redirection not allowed due to url protocol mismatch, actual url : {0}, redirect url : {1}", new Object[] { this.actualURL, redirectStr });
                throw new IAMSecurityException("INVALID_REDIRECT_URL");
            }
        }
        else {
            redirectURL = new URL(this.connection.getURL(), redirectStr);
        }
        this.disconnect();
        this.url = redirectURL.toString();
        return true;
    }
    
    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }
    
    public InputStream getInputStream() throws IOException {
        return this.connection.getInputStream();
    }
    
    public InputStream getErrorStream() throws IOException {
        return this.connection.getErrorStream();
    }
    
    public Map<String, List<String>> getHeaderFields() {
        return this.connection.getHeaderFields();
    }
    
    public void disconnect() {
        this.connection.disconnect();
    }
    
    static {
        LOGGER = Logger.getLogger(HttpConnection.class.getName());
        REQ_BODY_ALLOWED_METHODS = new ArrayList<String>(Arrays.asList("POST", "PUT", "DELETE"));
    }
}
