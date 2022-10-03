package com.me.ems.onpremise.summaryserver.summary.probedistribution;

import java.util.Hashtable;
import java.util.Enumeration;
import java.io.FileOutputStream;
import org.json.JSONObject;
import java.util.Formatter;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import javax.net.ssl.KeyManager;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;
import java.io.FileInputStream;
import java.io.File;
import org.apache.http.config.SocketConfig;
import org.apache.http.client.config.RequestConfig;
import java.net.SocketAddress;
import java.util.Properties;
import org.apache.http.client.AuthenticationStrategy;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import java.net.InetSocketAddress;
import java.net.Proxy;
import com.btr.proxy.selector.pac.PacScriptSource;
import com.btr.proxy.selector.pac.PacProxySelector;
import com.btr.proxy.selector.pac.UrlPacScriptSource;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.apache.http.impl.client.HttpClientBuilder;
import com.me.devicemanagement.framework.server.util.SecurityUtil;
import java.io.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import java.io.ByteArrayOutputStream;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import org.apache.http.util.EntityUtils;
import org.apache.catalina.connector.ClientAbortException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.apache.http.message.BasicHttpRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.me.ems.onpremise.summaryserver.summary.authentication.ProbeAuthUtil;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import java.util.List;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.Consts;
import java.util.Collections;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import java.net.URISyntaxException;
import org.apache.http.HttpEntity;
import java.io.OutputStream;
import org.apache.http.HttpResponse;
import org.apache.http.HttpRequest;
import org.apache.http.HttpHost;
import javax.net.ssl.SSLHandshakeException;
import com.me.ems.onpremise.summaryserver.common.HttpsHandlerUtil;
import java.util.HashMap;
import org.apache.http.client.utils.URIUtils;
import java.net.URI;
import java.io.InputStream;
import com.adventnet.iam.security.UploadedFileItem;
import java.util.Map;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Closeable;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import com.me.ems.onpremise.summaryserver.summary.proberegistration.ProbeUtil;
import java.util.logging.Level;
import java.util.BitSet;
import org.apache.http.message.HeaderGroup;
import org.apache.http.client.HttpClient;
import java.util.logging.Logger;

public class ForwardToProbeUtil
{
    private static Logger logger;
    private static ForwardToProbeUtil instance;
    private HttpClient proxyClient;
    private HeaderGroup ignoreHeaders;
    public static final String X_PROBE_HEADER = "X-ProbeID";
    private String clientName;
    private boolean isRetryEnabled;
    String[] skipHeaders;
    protected static final BitSet ASCII_QUERY_CHARS;
    
    public ForwardToProbeUtil(final String clientName, final boolean retryOnTimeout) {
        this(clientName);
        this.isRetryEnabled = retryOnTimeout;
        ForwardToProbeUtil.logger.log(Level.INFO, "Client {0} =s retry state {1}", new Object[] { this.clientName, this.isRetryEnabled });
    }
    
    public ForwardToProbeUtil(final String clientName) {
        this.isRetryEnabled = false;
        this.skipHeaders = new String[] { "Connection", "Keep-Alive", "Proxy-Authenticate", "Proxy-Authorization", "TE", "Trailers", "Transfer-Encoding", "Upgrade" };
        this.clientName = clientName;
        ForwardToProbeUtil.logger.log(Level.INFO, "Client {0} Created", this.clientName);
        ProbeUtil.getInstance().getAllProbeDetails();
        this.proxyClient = this.createHttpClient();
        this.ignoreHeaders = new HeaderGroup();
        for (final String header : this.skipHeaders) {
            this.ignoreHeaders.addHeader((Header)new BasicHeader(header, (String)null));
        }
    }
    
    public void destroy() {
        if (this.proxyClient instanceof Closeable) {
            try {
                ((Closeable)this.proxyClient).close();
            }
            catch (final IOException e) {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "While destroying client object, shutting down HttpClient: ", e);
            }
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "Client {0} Destroyed", this.clientName);
    }
    
    public void setRetryState(final boolean isRetryEnabled) {
        this.isRetryEnabled = isRetryEnabled;
    }
    
    public ArrayList<String> getIgnoreHeader() {
        return new ArrayList<String>(Arrays.asList(this.skipHeaders));
    }
    
    public Map<String, Object> sendRequest(final String method, final String uri, final String queryParam, final Map<String, String> headers, final Map<String, String[]> paramMap, final Map<String, UploadedFileItem> multiFileObj, final InputStream in, final Object... options) throws URISyntaxException, IOException {
        ForwardToProbeUtil.logger.log(Level.INFO, "Client {0} sendReq Start", this.clientName);
        Map<String, Object> result = null;
        final StringBuilder targetURI = new StringBuilder(500);
        targetURI.append(uri);
        if (queryParam != null && !queryParam.isEmpty()) {
            formatAndAddQueryParam(targetURI, queryParam);
        }
        final HttpHost probeHost = URIUtils.extractHost(new URI(targetURI.toString()));
        final HttpRequest proxyRequest = this.getTargetRequestEntity(method, targetURI.toString(), queryParam, headers, paramMap, multiFileObj, in);
        if (headers != null && headers.size() > 0) {
            this.addHeaders(headers, proxyRequest);
        }
        HttpResponse proxyResponse = null;
        final OutputStream servletOutputStream = null;
        try {
            result = new HashMap<String, Object>();
            proxyResponse = this.proxyClient.execute(probeHost, proxyRequest);
            ForwardToProbeUtil.logger.log(Level.INFO, "Request forwarded " + probeHost);
            final int statusCode = proxyResponse.getStatusLine().getStatusCode();
            ForwardToProbeUtil.logger.log(Level.INFO, "Status Code {0}", statusCode);
            result.put("statusCode", statusCode);
            result.put("status", proxyResponse.getStatusLine().getReasonPhrase());
            ForwardToProbeUtil.logger.log(Level.INFO, "Copy Response started");
            result.put("responseHeader", this.getResponseHeaders(proxyResponse));
            if (statusCode == 304) {
                result.put("noBodyContent", true);
            }
            else {
                final HttpEntity entity = proxyResponse.getEntity();
                result.put("entity", entity);
                ForwardToProbeUtil.logger.log(Level.INFO, "sending Response client");
            }
        }
        catch (final SSLHandshakeException ex) {
            if (options.length == 0 && this.isRetryEnabled) {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes , rechecking certificate", ex);
                HttpsHandlerUtil.processCertificateFromServer(targetURI.toString(), null);
                this.sendRequest(method, uri, queryParam, headers, paramMap, multiFileObj, in, true);
            }
            else {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes FOR the url " + targetURI.toString(), ex);
            }
        }
        catch (final Exception e) {
            ForwardToProbeUtil.logger.log(Level.SEVERE, "Exception while handling response", e);
            throw e;
        }
        return result;
    }
    
    private Map<String, String> getResponseHeaders(final HttpResponse proxyResponse) {
        final Map<String, String> headers = new HashMap<String, String>();
        for (final Header header : proxyResponse.getAllHeaders()) {
            final String headerName = header.getName();
            if (!this.ignoreHeaders.containsHeader(headerName)) {
                final String headerValue = header.getValue();
                if (!headerName.equalsIgnoreCase("Set-Cookie")) {
                    if (!headerName.equalsIgnoreCase("Set-Cookie2")) {
                        if (!headerName.equalsIgnoreCase("Location")) {
                            headers.put(headerName, headerValue);
                        }
                    }
                }
            }
        }
        return headers;
    }
    
    private HttpRequest getTargetRequestEntity(final String method, final String targetURI, final String queryString, final Map<String, String> headers, final Map<String, String[]> paramMap, final Map<String, UploadedFileItem> multiFileObj, final InputStream in) throws UnsupportedEncodingException {
        final String contentType = headers.get("Content-Type");
        final HttpEntityEnclosingRequest eProxyRequest = (HttpEntityEnclosingRequest)new BasicHttpEntityEnclosingRequest(method, targetURI);
        List<NameValuePair> queryParams = Collections.emptyList();
        if (queryString != null) {
            queryParams = URLEncodedUtils.parse(queryString, Consts.UTF_8);
        }
        final Map<String, String[]> form = paramMap;
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        final HttpMethodParams multiParam = new HttpMethodParams();
        if (paramMap != null) {
        Label_0084:
            for (final String name : form.keySet()) {
                for (final NameValuePair queryParam : queryParams) {
                    if (name.equals(queryParam.getName())) {
                        continue Label_0084;
                    }
                }
                final String[] values = form.get(name);
                if (values.length == 1) {
                    params.add((NameValuePair)new BasicNameValuePair(name, values[0]));
                }
                else {
                    ForwardToProbeUtil.logger.log(Level.WARNING, "form data may be empty or more than one value:");
                    for (final String value : values) {
                        params.add((NameValuePair)new BasicNameValuePair(name + "[]", value));
                        ForwardToProbeUtil.logger.log(Level.WARNING, "name {0}[] -- value {1}", new String[] { name, value });
                    }
                }
            }
        }
        if (contentType != null) {
            if (contentType.contains("application/x-www-form-urlencoded")) {
                eProxyRequest.setEntity((HttpEntity)new UrlEncodedFormEntity((List)params, "UTF-8"));
            }
            else if (contentType.contains("multipart/form-data") && multiFileObj != null) {
                final MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
                meBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                meBuilder.setBoundary(contentType.substring(contentType.indexOf("=") + 1));
                for (final NameValuePair param : params) {
                    meBuilder.addPart(param.getName(), (ContentBody)new StringBody(param.getValue(), ContentType.TEXT_PLAIN));
                }
                for (final UploadedFileItem file : multiFileObj.values()) {
                    meBuilder.addBinaryBody(file.getFieldName(), file.getUploadedFile(), ContentType.DEFAULT_BINARY, file.getFileName());
                }
                eProxyRequest.setEntity(meBuilder.build());
            }
            else if (in != null) {
                long contentLength = -1L;
                final String contentLengthHeader = headers.get("Content-Length");
                if (contentLengthHeader != null) {
                    contentLength = Long.parseLong(contentLengthHeader);
                }
                eProxyRequest.setEntity((HttpEntity)new InputStreamEntity(in, contentLength));
            }
        }
        return (HttpRequest)eProxyRequest;
    }
    
    public Map<String, String> getAuthHeaderFromProbe(final Long probeId) throws Exception {
        final Map probeDetails = ProbeUtil.getInstance().getProbeDetail(probeId);
        if (probeDetails != null) {
            final Map<String, String> authHeaders = new HashMap<String, String>();
            final String probeApiKey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
            authHeaders.put("userDomain", this.getEncryptUserDomain(probeApiKey));
            authHeaders.put("ProbeAuthorization", probeApiKey);
            authHeaders.put("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
            return authHeaders;
        }
        return null;
    }
    
    private void addHeaders(final Map<String, String> headers, final HttpRequest proxyRequest) {
        for (final Map.Entry<String, String> header : headers.entrySet()) {
            if (header.getKey().equalsIgnoreCase("Content-Type") && header.getValue().equalsIgnoreCase("multipart/form-data")) {
                continue;
            }
            proxyRequest.addHeader((String)header.getKey(), (String)header.getValue());
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "Header Injection finished");
    }
    
    public void proxyRequest(final HttpServletRequest servletRequest, final HttpServletResponse servletResponse, final Long probeId, final Object... options) throws Exception {
        ForwardToProbeUtil.logger.log(Level.INFO, "Client {0} proxy request Start", this.clientName);
        HttpResponse proxyResponse = null;
        OutputStream servletOutputStream = null;
        String proxyRequestUri = "";
        try {
            final Map probeDetails = ProbeUtil.getInstance().getProbeDetail(probeId);
            if (probeDetails != null) {
                final String probeApiKey = ProbeAuthUtil.getInstance().getProbeAuthKey(probeId);
                final String method = servletRequest.getMethod();
                proxyRequestUri = this.rewriteUrlFromRequest(servletRequest, probeDetails);
                final HttpHost probeHost = URIUtils.extractHost(new URI(proxyRequestUri));
                HttpRequest proxyRequest;
                if (servletRequest.getHeader("Content-Length") != null || servletRequest.getHeader("Transfer-Encoding") != null) {
                    proxyRequest = this.newProxyRequestWithEntity(method, proxyRequestUri, servletRequest);
                }
                else {
                    proxyRequest = (HttpRequest)new BasicHttpRequest(method, proxyRequestUri);
                }
                this.addRequestHeaders(servletRequest, proxyRequest, probeApiKey, proxyRequestUri);
                proxyResponse = this.proxyClient.execute(probeHost, proxyRequest);
                ForwardToProbeUtil.logger.log(Level.INFO, "Request forwarded " + probeHost);
                final int statusCode = proxyResponse.getStatusLine().getStatusCode();
                ForwardToProbeUtil.logger.log(Level.INFO, "Status Code {0}", statusCode);
                servletResponse.setStatus(statusCode, proxyResponse.getStatusLine().getReasonPhrase());
                ForwardToProbeUtil.logger.log(Level.INFO, "Copy Response started");
                this.copyResponseHeaders(proxyResponse, servletRequest, servletResponse);
                if (statusCode == 304) {
                    servletResponse.setIntHeader("Content-Length", 0);
                }
                else {
                    final HttpEntity entity = proxyResponse.getEntity();
                    if (entity != null) {
                        servletOutputStream = (OutputStream)servletResponse.getOutputStream();
                        entity.writeTo(servletOutputStream);
                    }
                    ForwardToProbeUtil.logger.log(Level.INFO, "sending Response client");
                }
            }
        }
        catch (final SSLHandshakeException ex) {
            if (options.length == 0 && this.isRetryEnabled) {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes , rechecking certificate", ex);
                HttpsHandlerUtil.processCertificateFromServer(proxyRequestUri, probeId);
                this.proxyClient = this.createHttpClient();
                this.proxyRequest(servletRequest, servletResponse, probeId, true);
            }
            else {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "SSLHandshakeException while pushing to probes FOR the url " + proxyRequestUri, ex);
            }
        }
        catch (final SocketTimeoutException | SocketException | ClientAbortException e) {
            ForwardToProbeUtil.logger.log(Level.SEVERE, "ReadTimeOccur or Software cased error while forward request", e);
            if (options.length == 0 && this.isRetryEnabled) {
                this.proxyRequest(servletRequest, servletResponse, probeId, true);
                ForwardToProbeUtil.logger.log(Level.WARNING, "Retrying the Request one more time", e);
                return;
            }
            throw e;
        }
        catch (final Exception e2) {
            ForwardToProbeUtil.logger.log(Level.SEVERE, "Exception while handling response", e2);
            throw e2;
        }
        finally {
            if (proxyResponse != null) {
                EntityUtils.consumeQuietly(proxyResponse.getEntity());
            }
            if (servletOutputStream != null) {
                servletOutputStream.flush();
            }
        }
    }
    
    private void copyResponseHeaders(final HttpResponse proxyResponse, final HttpServletRequest servletRequest, final HttpServletResponse servletResponse) {
        for (final Header header : proxyResponse.getAllHeaders()) {
            final String headerName = header.getName();
            if (!this.ignoreHeaders.containsHeader(headerName)) {
                final String headerValue = header.getValue();
                if (!headerName.equalsIgnoreCase("Set-Cookie")) {
                    if (!headerName.equalsIgnoreCase("Set-Cookie2")) {
                        if (!headerName.equalsIgnoreCase("Location")) {
                            servletResponse.addHeader(headerName, headerValue);
                        }
                    }
                }
            }
        }
    }
    
    public String getEncryptUserDomain(final String authorization) throws Exception {
        final String domainName = ApiFactoryProvider.getAuthUtilAccessAPI().getDomainName();
        final String userName = ApiFactoryProvider.getAuthUtilAccessAPI().getLoginName();
        return ApiFactoryProvider.getCryptoAPI().encrypt(userName + "::" + domainName, authorization, (String)null);
    }
    
    private void addRequestHeaders(final HttpServletRequest servletRequest, final HttpRequest proxyRequest, final String probeApiKey, final String uri) throws Exception {
        final String acceptHeader = servletRequest.getHeader("Accept");
        final String contentDisposition = servletRequest.getHeader("Content-Disposition");
        final String contentType = servletRequest.getContentType();
        final String module = servletRequest.getHeader("Module");
        proxyRequest.addHeader("userDomain", this.getEncryptUserDomain(probeApiKey));
        proxyRequest.addHeader("ProbeAuthorization", probeApiKey);
        proxyRequest.addHeader("hsKey", ProbeAuthUtil.getInstance().getProbeHandShakekey());
        proxyRequest.addHeader("Referer", uri);
        if (acceptHeader != null) {
            proxyRequest.addHeader("Accept", acceptHeader);
        }
        if (module != null) {
            proxyRequest.addHeader("Module", module);
        }
        if (contentType != null && !contentType.contains("multipart/form-data")) {
            proxyRequest.addHeader("Content-Type", contentType);
        }
        if (contentDisposition != null) {
            proxyRequest.addHeader("Content-Disposition", contentDisposition);
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "Header Extraction & Injection finished");
    }
    
    public List<NameValuePair> extractRequestParam(final HttpServletRequest servletRequest) {
        List<NameValuePair> queryParams = Collections.emptyList();
        final String queryString = servletRequest.getQueryString();
        if (queryString != null) {
            queryParams = URLEncodedUtils.parse(queryString, Consts.UTF_8);
        }
        final Map<String, String[]> form = servletRequest.getParameterMap();
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        final HttpMethodParams multiParam = new HttpMethodParams();
    Label_0063:
        for (final String name : form.keySet()) {
            for (final NameValuePair queryParam : queryParams) {
                if (name.equals(queryParam.getName())) {
                    continue Label_0063;
                }
            }
            final String[] values = form.get(name);
            if (values.length == 1) {
                params.add((NameValuePair)new BasicNameValuePair(name, values[0]));
            }
            else {
                ForwardToProbeUtil.logger.log(Level.WARNING, "form data may be empty or more than one value:");
                for (final String value : values) {
                    params.add((NameValuePair)new BasicNameValuePair(name, value));
                    ForwardToProbeUtil.logger.log(Level.WARNING, "name {0}[] -- value {1}", new String[] { name, value });
                }
            }
        }
        return params;
    }
    
    protected HttpRequest newProxyRequestWithEntity(final String method, final String proxyRequestUri, final HttpServletRequest servletRequest) throws IOException {
        final HttpEntityEnclosingRequest eProxyRequest = (HttpEntityEnclosingRequest)new BasicHttpEntityEnclosingRequest(method, proxyRequestUri);
        final String contentType = servletRequest.getContentType();
        long contentLength = -1L;
        if (contentType != null) {
            if (contentType.contains("application/x-www-form-urlencoded")) {
                final List<NameValuePair> params = this.extractRequestParam(servletRequest);
                eProxyRequest.setEntity((HttpEntity)new UrlEncodedFormEntity((List)params, "UTF-8"));
            }
            else if (contentType.contains("multipart/form-data")) {
                final List<NameValuePair> params = this.extractRequestParam(servletRequest);
                final MultipartEntityBuilder meBuilder = MultipartEntityBuilder.create();
                meBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                meBuilder.setBoundary(contentType.substring(contentType.indexOf("=") + 1));
                final Map<String, UploadedFileItem> multiFileObj = WebclientAPIFactoryProvider.getFormFileAPI().getAllUploadedFileItem(servletRequest);
                for (final NameValuePair param : params) {
                    meBuilder.addPart(param.getName(), (ContentBody)new StringBody(param.getValue(), ContentType.TEXT_PLAIN));
                }
                for (final UploadedFileItem file : multiFileObj.values()) {
                    meBuilder.addBinaryBody(file.getFieldName(), file.getUploadedFile(), ContentType.DEFAULT_BINARY, file.getFileName());
                }
                eProxyRequest.setEntity(meBuilder.build());
            }
            else {
                final String contentLengthHeader = servletRequest.getHeader("Content-Length");
                if (contentLengthHeader != null) {
                    contentLength = Long.parseLong(contentLengthHeader);
                    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    IOUtils.copy((InputStream)servletRequest.getInputStream(), (OutputStream)baos);
                    final byte[] bytes = baos.toByteArray();
                    eProxyRequest.setEntity((HttpEntity)new InputStreamEntity((InputStream)new ByteArrayInputStream(bytes), (long)bytes.length));
                    ForwardToProbeUtil.logger.log(Level.INFO, "Content-header length " + contentLength + " byte array length " + bytes.length);
                }
                else {
                    ForwardToProbeUtil.logger.log(Level.INFO, "Content-header length is null");
                    eProxyRequest.setEntity((HttpEntity)new InputStreamEntity((InputStream)servletRequest.getInputStream()));
                }
            }
        }
        return (HttpRequest)eProxyRequest;
    }
    
    public String getHostURlFromProbeDetails(final Map probeDetails) {
        final StringBuilder uri = new StringBuilder(500);
        final String serverName = probeDetails.get("HOST");
        final String port = (int)probeDetails.get("PORT") + "";
        final String protocol = probeDetails.get("PROTOCOL");
        uri.append(protocol).append("://").append(serverName).append(":").append(port);
        return uri.toString();
    }
    
    public String rewriteUrlFromRequest(final HttpServletRequest servletRequest, final Map probeDetails) {
        final StringBuilder uri = new StringBuilder(500);
        uri.append(this.getHostURlFromProbeDetails(probeDetails));
        uri.append(SecurityUtil.getNormalizedRequestURI(servletRequest));
        ForwardToProbeUtil.logger.log(Level.INFO, "uri base: " + (Object)uri);
        final String queryString = servletRequest.getQueryString();
        formatAndAddQueryParam(uri, queryString);
        return uri.toString();
    }
    
    public static void formatAndAddQueryParam(final StringBuilder uri, String queryString) {
        ForwardToProbeUtil.logger.log(Level.INFO, "queryString : {0}", queryString);
        String fragment = null;
        if (queryString != null) {
            final int fragIdx = queryString.indexOf(35);
            if (fragIdx >= 0) {
                fragment = queryString.substring(fragIdx + 1);
                queryString = queryString.substring(0, fragIdx);
            }
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "queryString : {0}", queryString);
        if (queryString != null && queryString.length() > 0) {
            uri.append('?');
            uri.append(encodeUriQuery(queryString, false));
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "uri after queryString : " + (Object)uri);
        if (fragment != null) {
            uri.append('#');
            uri.append(encodeUriQuery(fragment, false));
        }
        ForwardToProbeUtil.logger.log(Level.INFO, "uri after fragment : " + (Object)uri);
    }
    
    protected void setProxyToClient(final HttpClientBuilder clientBuilder) {
        Properties proxyConf = null;
        try {
            proxyConf = ApiFactoryProvider.getServerSettingsAPI().getProxyConfiguration();
            String proxyHost = null;
            String proxyUser = null;
            String proxyPassword = null;
            String proxyScript = null;
            int proxyPort = 0;
            if (proxyConf != null) {
                final int proxyType = Integer.parseInt(SyMUtil.getSyMParameter("proxyType"));
                if (proxyConf.containsKey("proxyScriptEna") && ((Hashtable<K, Object>)proxyConf).get("proxyScriptEna").toString().equals("1")) {
                    proxyScript = ((Hashtable<K, String>)proxyConf).get("proxyScript");
                }
                else {
                    proxyHost = ((Hashtable<K, String>)proxyConf).get("proxyHost");
                    proxyPort = Integer.parseInt(((Hashtable<K, String>)proxyConf).get("proxyPort"));
                }
                proxyUser = ((Hashtable<K, String>)proxyConf).get("proxyUser");
                proxyPassword = ((Hashtable<K, String>)proxyConf).get("proxyPass");
                if (proxyScript != null) {
                    final PacProxySelector pacProxySelector = new PacProxySelector((PacScriptSource)new UrlPacScriptSource(proxyScript));
                    final List<Proxy> proxyList = pacProxySelector.select(new URI(proxyScript));
                    if (proxyList != null && !proxyList.isEmpty()) {
                        for (final Proxy proxy : proxyList) {
                            final SocketAddress address = proxy.address();
                            if (address != null) {
                                proxyHost = ((InetSocketAddress)address).getHostName();
                                proxyPort = ((InetSocketAddress)address).getPort();
                            }
                        }
                    }
                }
                final HttpHost proxy2 = new HttpHost(proxyHost, proxyPort, "http");
                final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
                final UsernamePasswordCredentials usernamePasswordCredentials = new UsernamePasswordCredentials(proxyUser, proxyPassword);
                credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), (Credentials)usernamePasswordCredentials);
                clientBuilder.setDefaultCredentialsProvider((CredentialsProvider)credsProvider);
                clientBuilder.setProxy(proxy2);
                clientBuilder.setProxyAuthenticationStrategy((AuthenticationStrategy)new ProxyAuthenticationStrategy());
            }
        }
        catch (final Exception e) {
            ForwardToProbeUtil.logger.log(Level.SEVERE, "Exception while client builder", e);
        }
    }
    
    protected HttpClient createHttpClient() {
        final int timeout = 120000;
        final HttpClientBuilder clientBuilder = HttpClientBuilder.create().setDefaultRequestConfig(RequestConfig.custom().setRedirectsEnabled(true).setCookieSpec("ignoreCookies").setConnectTimeout(timeout).setSocketTimeout(timeout).setConnectionRequestTimeout(timeout).build()).setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(timeout).build());
        clientBuilder.setMaxConnTotal(50);
        clientBuilder.useSystemProperties();
        final File truststoreFile = new File(ApiFactoryProvider.getUtilAccessAPI().getServerHome() + File.separator + "jre" + File.separator + "lib" + File.separator + "security" + File.separator + "jssecacerts");
        if (truststoreFile.exists()) {
            SSLContext sc = null;
            try {
                final InputStream in = new FileInputStream(truststoreFile);
                final KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
                ks.load(in, "changeit".toCharArray());
                final TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                tmf.init(ks);
                sc = SSLContext.getInstance("TLS");
                sc.init(null, tmf.getTrustManagers(), new SecureRandom());
                clientBuilder.setSSLContext(sc);
            }
            catch (final NoSuchAlgorithmException | KeyManagementException e) {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "Exception while handing adding ssl context to client", e);
            }
            catch (final Exception e2) {
                ForwardToProbeUtil.logger.log(Level.SEVERE, "Exception while handing adding ssl context to client", e2);
            }
        }
        return (HttpClient)clientBuilder.build();
    }
    
    protected static CharSequence encodeUriQuery(final CharSequence in, final boolean encodePercent) {
        StringBuilder outBuf = null;
        Formatter formatter = null;
        for (int i = 0; i < in.length(); ++i) {
            final char c = in.charAt(i);
            boolean escape = true;
            if (c < '\u0080') {
                if (ForwardToProbeUtil.ASCII_QUERY_CHARS.get(c) && (!encodePercent || c != '%')) {
                    escape = false;
                }
            }
            else if (!Character.isISOControl(c) && !Character.isSpaceChar(c)) {
                escape = false;
            }
            if (!escape) {
                if (outBuf != null) {
                    outBuf.append(c);
                }
            }
            else {
                if (outBuf == null) {
                    outBuf = new StringBuilder(in.length() + 15);
                    outBuf.append(in, 0, i);
                    formatter = new Formatter(outBuf);
                }
                formatter.format("%%%02X", (int)c);
            }
        }
        return (outBuf != null) ? outBuf : in;
    }
    
    public <T> T extractResponseFromEntity(final HttpEntity responseEntity, T out) throws Exception {
        if (responseEntity == null) {
            return null;
        }
        if (out instanceof String) {
            out = (T)EntityUtils.toString(responseEntity);
        }
        else if (out instanceof StringBuffer) {
            ((StringBuffer)out).append(EntityUtils.toString(responseEntity));
        }
        else if (out instanceof StringBuilder) {
            ((StringBuilder)out).append(EntityUtils.toString(responseEntity));
        }
        else if (out instanceof JSONObject) {
            final String response = EntityUtils.toString(responseEntity);
            out = (T)new JSONObject(response);
        }
        else if (out instanceof File) {
            final InputStream instream = responseEntity.getContent();
            long current = 0L;
            final long contentLength = responseEntity.getContentLength();
            FileOutputStream buffer = null;
            if (instream != null) {
                try {
                    buffer = new FileOutputStream((File)out);
                    final byte[] tmp = new byte[4096];
                    int l;
                    while (current < contentLength && (l = instream.read(tmp)) != -1) {
                        current += l;
                        buffer.write(tmp, 0, l);
                    }
                    buffer.flush();
                }
                finally {
                    if (buffer != null) {
                        buffer.close();
                    }
                    instream.close();
                }
            }
        }
        else if (out instanceof ByteArrayOutputStream) {
            responseEntity.writeTo((OutputStream)out);
        }
        else {
            if (!(out instanceof byte[])) {
                throw new Exception("Unsupported Return type requested");
            }
            out = (T)(Object)EntityUtils.toByteArray(responseEntity);
        }
        return out;
    }
    
    public Map<String, String> extractHeader(final HttpServletRequest request, List<String> skipHeader) {
        final Map<String, String> result = new HashMap<String, String>();
        final Enumeration<String> headerKeys = request.getHeaderNames();
        if (skipHeader == null) {
            skipHeader = new ArrayList<String>();
        }
        while (headerKeys.hasMoreElements()) {
            final String headerKey = headerKeys.nextElement();
            if (skipHeader.contains(headerKey)) {
                continue;
            }
            result.put(headerKey, request.getHeader(headerKey));
        }
        return result;
    }
    
    static {
        ForwardToProbeUtil.logger = Logger.getLogger("SummaryUIRequestForward");
        ForwardToProbeUtil.instance = null;
        final char[] c_unreserved = "_-!.~'()*".toCharArray();
        final char[] c_punct = ",;:$&+=".toCharArray();
        final char[] c_reserved = "?/[]@".toCharArray();
        ASCII_QUERY_CHARS = new BitSet(128);
        for (char c = 'a'; c <= 'z'; ++c) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c);
        }
        for (char c = 'A'; c <= 'Z'; ++c) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c);
        }
        for (char c = '0'; c <= '9'; ++c) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c);
        }
        for (final char c2 : c_unreserved) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c2);
        }
        for (final char c2 : c_punct) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c2);
        }
        for (final char c2 : c_reserved) {
            ForwardToProbeUtil.ASCII_QUERY_CHARS.set(c2);
        }
        ForwardToProbeUtil.ASCII_QUERY_CHARS.set(37);
    }
}
