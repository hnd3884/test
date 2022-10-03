package com.zoho.clustering.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import javax.net.ssl.SSLException;
import java.io.OutputStream;
import java.util.Iterator;
import javax.net.ssl.HostnameVerifier;
import java.util.logging.Level;
import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

public class HttpMethod
{
    private static Logger logger;
    private URL urlObj;
    private String requestMethod;
    private int connTimeoutMillis;
    private int readTimeoutMillis;
    private HttpURLConnection conn;
    private int httpStatus;
    private String errResponseStr;
    private String hostnameVerifierClassName;
    private String postParam;
    private Map<String, String> requestHeaderMap;
    
    public static void main(final String[] args) throws Exception {
        final HttpMethod meth = new HttpMethod(args[0]);
        final int sc = meth.execute();
        System.out.println(sc);
    }
    
    public HttpMethod(final String url) {
        try {
            this.urlObj = new URL(url);
        }
        catch (final MalformedURLException exp) {
            throw new IllegalArgumentException(exp);
        }
        this.requestMethod = "GET";
        this.connTimeoutMillis = 1000;
        this.readTimeoutMillis = 1000;
    }
    
    public String getRequestMethod() {
        return this.requestMethod;
    }
    
    public URL getURL() {
        return this.urlObj;
    }
    
    public int getConnectionTimeout() {
        return this.connTimeoutMillis;
    }
    
    public int getReadTimeout() {
        return this.readTimeoutMillis;
    }
    
    public void setRequestMethod(final String requestMethod) {
        this.requestMethod = requestMethod;
    }
    
    public void setConnectionTimeout(final int connTimeoutMillis) {
        this.connTimeoutMillis = connTimeoutMillis;
    }
    
    public void setReadTimeout(final int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
    
    public void setPostParam(final String requestparam) {
        this.postParam = requestparam;
    }
    
    public void setRequestHeader(final Map<String, String> requestHeader) {
        this.requestHeaderMap = requestHeader;
    }
    
    public int execute() throws IOException {
        return this.executeInternal(true);
    }
    
    private int executeInternal(final boolean protolMismatchCheck) throws IOException {
        (this.conn = (HttpURLConnection)this.urlObj.openConnection()).setRequestMethod(this.requestMethod);
        if (this.requestHeaderMap != null) {
            for (final String key : this.requestHeaderMap.keySet()) {
                this.conn.setRequestProperty(key, this.requestHeaderMap.get(key));
            }
        }
        if (this.conn.getRequestMethod().equals("POST")) {
            this.conn.setDoOutput(true);
            try (final OutputStream os = this.conn.getOutputStream()) {
                os.write(this.postParam.getBytes());
                os.flush();
            }
        }
        this.conn.setConnectTimeout(this.connTimeoutMillis);
        if (this.readTimeoutMillis != 0) {
            this.conn.setReadTimeout(this.readTimeoutMillis);
        }
        if (this.conn instanceof HttpsURLConnection && this.hostnameVerifierClassName != null) {
            HttpMethod.logger.log(Level.FINER, "HostnameVerifier class is set to {0}", this.hostnameVerifierClassName);
            try {
                final HostnameVerifier hostnameVerifier = (HostnameVerifier)Thread.currentThread().getContextClassLoader().loadClass(this.hostnameVerifierClassName).newInstance();
                ((HttpsURLConnection)this.conn).setHostnameVerifier(hostnameVerifier);
            }
            catch (final InstantiationException e) {
                e.printStackTrace();
            }
            catch (final IllegalAccessException e2) {
                e2.printStackTrace();
            }
            catch (final ClassNotFoundException e3) {
                e3.printStackTrace();
            }
        }
        try {
            this.httpStatus = this.conn.getResponseCode();
        }
        catch (final IOException ioExp) {
            if (!protolMismatchCheck) {
                throw ioExp;
            }
            ioExp.printStackTrace();
            this.checkForProtocolMismatch(ioExp);
        }
        if (this.httpStatus != 200) {
            try {
                this.errResponseStr = getAsString(this.conn.getErrorStream());
            }
            catch (final IOException ignored) {
                ignored.printStackTrace();
            }
        }
        return this.httpStatus;
    }
    
    private void checkForProtocolMismatch(final IOException ioExp) throws RuntimeException, IOException {
        final String protocol = this.urlObj.getProtocol();
        final String urlStr = this.urlObj.toString();
        if ("https".equals(protocol)) {
            if (ioExp.getMessage().contains("HTTPS hostname wrong")) {
                throw new RuntimeException("SSL problem while accessing the URL [" + urlStr + "]. Maybe the host/domain name in URL doesn't match with host/domain name in the server-certificate.", ioExp);
            }
            if (ioExp instanceof SSLException) {
                throw new RuntimeException("SSL problem while accessing the URL [" + urlStr + "]. Maybe the server-certificate is not added to the trust-store. Please check.", ioExp);
            }
            if (!(ioExp instanceof SocketTimeoutException) && !(ioExp instanceof SocketException)) {
                throw ioExp;
            }
            final HttpMethod httpReq = new HttpMethod(this.urlObj.toString().replaceFirst("https", "http"));
            try {
                httpReq.executeInternal(false);
                throw new RuntimeException("Maybe the server doesn't support 'https' access. Correct the URL [" + urlStr + "].", ioExp);
            }
            catch (final IOException ignored) {
                throw ioExp;
            }
        }
        if ("http".equals(protocol) && ioExp instanceof SocketException) {
            final HttpMethod httpsReq = new HttpMethod(this.urlObj.toString().replaceFirst("http", "https"));
            try {
                httpsReq.executeInternal(false);
                throw new RuntimeException("Maybe the server doesn't support 'http' access. Correct the URL [" + urlStr + "].", ioExp);
            }
            catch (final SSLException exp) {
                throw new RuntimeException("Maybe the server doesn't support 'http' access. Correct the URL [" + urlStr + "].", ioExp);
            }
            catch (final IOException ignored) {
                throw ioExp;
            }
        }
        throw ioExp;
    }
    
    public InputStream getResponseAsStream() throws IOException {
        if (this.httpStatus != 200) {
            throw new IllegalStateException("Not applicable. Http status is [" + this.httpStatus + "]");
        }
        return this.conn.getInputStream();
    }
    
    public String getResponseAsString() throws IOException {
        return getAsString(this.getResponseAsStream());
    }
    
    public String getErrorResponseAsString() {
        return this.errResponseStr;
    }
    
    private static String getAsString(final InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        BufferedReader bin = null;
        final StringBuilder buff = new StringBuilder();
        try {
            bin = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = bin.readLine()) != null) {
                if (line.length() > 0) {
                    buff.append(line).append("\n");
                }
            }
            return buff.toString();
        }
        finally {
            bin.close();
        }
    }
    
    public void setHostnameVerifierClassName(final String hostnameVerifierClassName) {
        this.hostnameVerifierClassName = hostnameVerifierClassName;
    }
    
    static {
        HttpMethod.logger = Logger.getLogger(HttpMethod.class.getName());
    }
}
