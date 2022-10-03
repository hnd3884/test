package com.google.api.client.googleapis.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.google.common.base.StandardSystemProperty;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import java.io.OutputStream;
import java.io.InputStream;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpEncoding;
import com.google.api.client.http.GZipEncoding;
import java.util.Map;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.googleapis.MethodOverride;
import java.io.IOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.UriTemplate;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.googleapis.GoogleUtils;
import com.google.api.client.util.Preconditions;
import com.google.api.client.googleapis.media.MediaHttpDownloader;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpContent;
import com.google.api.client.util.GenericData;

public abstract class AbstractGoogleClientRequest<T> extends GenericData
{
    public static final String USER_AGENT_SUFFIX = "Google-API-Java-Client";
    private static final String API_VERSION_HEADER = "X-Goog-Api-Client";
    private final AbstractGoogleClient abstractGoogleClient;
    private final String requestMethod;
    private final String uriTemplate;
    private final HttpContent httpContent;
    private HttpHeaders requestHeaders;
    private HttpHeaders lastResponseHeaders;
    private int lastStatusCode;
    private String lastStatusMessage;
    private boolean disableGZipContent;
    private boolean returnRawInputStream;
    private Class<T> responseClass;
    private MediaHttpUploader uploader;
    private MediaHttpDownloader downloader;
    
    protected AbstractGoogleClientRequest(final AbstractGoogleClient abstractGoogleClient, final String requestMethod, final String uriTemplate, final HttpContent httpContent, final Class<T> responseClass) {
        this.requestHeaders = new HttpHeaders();
        this.lastStatusCode = -1;
        this.responseClass = (Class)Preconditions.checkNotNull((Object)responseClass);
        this.abstractGoogleClient = (AbstractGoogleClient)Preconditions.checkNotNull((Object)abstractGoogleClient);
        this.requestMethod = (String)Preconditions.checkNotNull((Object)requestMethod);
        this.uriTemplate = (String)Preconditions.checkNotNull((Object)uriTemplate);
        this.httpContent = httpContent;
        final String applicationName = abstractGoogleClient.getApplicationName();
        if (applicationName != null) {
            this.requestHeaders.setUserAgent(applicationName + " " + "Google-API-Java-Client" + "/" + GoogleUtils.VERSION);
        }
        else {
            this.requestHeaders.setUserAgent("Google-API-Java-Client/" + GoogleUtils.VERSION);
        }
        this.requestHeaders.set("X-Goog-Api-Client", (Object)ApiClientVersion.DEFAULT_VERSION);
    }
    
    public final boolean getDisableGZipContent() {
        return this.disableGZipContent;
    }
    
    public final boolean getReturnRawInputSteam() {
        return this.returnRawInputStream;
    }
    
    public AbstractGoogleClientRequest<T> setDisableGZipContent(final boolean disableGZipContent) {
        this.disableGZipContent = disableGZipContent;
        return this;
    }
    
    public AbstractGoogleClientRequest<T> setReturnRawInputStream(final boolean returnRawInputStream) {
        this.returnRawInputStream = returnRawInputStream;
        return this;
    }
    
    public final String getRequestMethod() {
        return this.requestMethod;
    }
    
    public final String getUriTemplate() {
        return this.uriTemplate;
    }
    
    public final HttpContent getHttpContent() {
        return this.httpContent;
    }
    
    public AbstractGoogleClient getAbstractGoogleClient() {
        return this.abstractGoogleClient;
    }
    
    public final HttpHeaders getRequestHeaders() {
        return this.requestHeaders;
    }
    
    public AbstractGoogleClientRequest<T> setRequestHeaders(final HttpHeaders headers) {
        this.requestHeaders = headers;
        return this;
    }
    
    public final HttpHeaders getLastResponseHeaders() {
        return this.lastResponseHeaders;
    }
    
    public final int getLastStatusCode() {
        return this.lastStatusCode;
    }
    
    public final String getLastStatusMessage() {
        return this.lastStatusMessage;
    }
    
    public final Class<T> getResponseClass() {
        return this.responseClass;
    }
    
    public final MediaHttpUploader getMediaHttpUploader() {
        return this.uploader;
    }
    
    protected final void initializeMediaUpload(final AbstractInputStreamContent mediaContent) {
        final HttpRequestFactory requestFactory = this.abstractGoogleClient.getRequestFactory();
        (this.uploader = new MediaHttpUploader(mediaContent, requestFactory.getTransport(), requestFactory.getInitializer())).setInitiationRequestMethod(this.requestMethod);
        if (this.httpContent != null) {
            this.uploader.setMetadata(this.httpContent);
        }
    }
    
    public final MediaHttpDownloader getMediaHttpDownloader() {
        return this.downloader;
    }
    
    protected final void initializeMediaDownload() {
        final HttpRequestFactory requestFactory = this.abstractGoogleClient.getRequestFactory();
        this.downloader = new MediaHttpDownloader(requestFactory.getTransport(), requestFactory.getInitializer());
    }
    
    public GenericUrl buildHttpRequestUrl() {
        return new GenericUrl(UriTemplate.expand(this.abstractGoogleClient.getBaseUrl(), this.uriTemplate, (Object)this, true));
    }
    
    public HttpRequest buildHttpRequest() throws IOException {
        return this.buildHttpRequest(false);
    }
    
    protected HttpRequest buildHttpRequestUsingHead() throws IOException {
        return this.buildHttpRequest(true);
    }
    
    private HttpRequest buildHttpRequest(final boolean usingHead) throws IOException {
        Preconditions.checkArgument(this.uploader == null);
        Preconditions.checkArgument(!usingHead || this.requestMethod.equals("GET"));
        final String requestMethodToUse = usingHead ? "HEAD" : this.requestMethod;
        final HttpRequest httpRequest = this.getAbstractGoogleClient().getRequestFactory().buildRequest(requestMethodToUse, this.buildHttpRequestUrl(), this.httpContent);
        new MethodOverride().intercept(httpRequest);
        httpRequest.setParser(this.getAbstractGoogleClient().getObjectParser());
        if (this.httpContent == null && (this.requestMethod.equals("POST") || this.requestMethod.equals("PUT") || this.requestMethod.equals("PATCH"))) {
            httpRequest.setContent((HttpContent)new EmptyContent());
        }
        httpRequest.getHeaders().putAll((Map)this.requestHeaders);
        if (!this.disableGZipContent) {
            httpRequest.setEncoding((HttpEncoding)new GZipEncoding());
        }
        httpRequest.setResponseReturnRawInputStream(this.returnRawInputStream);
        final HttpResponseInterceptor responseInterceptor = httpRequest.getResponseInterceptor();
        httpRequest.setResponseInterceptor((HttpResponseInterceptor)new HttpResponseInterceptor() {
            public void interceptResponse(final HttpResponse response) throws IOException {
                if (responseInterceptor != null) {
                    responseInterceptor.interceptResponse(response);
                }
                if (!response.isSuccessStatusCode() && httpRequest.getThrowExceptionOnExecuteError()) {
                    throw AbstractGoogleClientRequest.this.newExceptionOnError(response);
                }
            }
        });
        return httpRequest;
    }
    
    public HttpResponse executeUnparsed() throws IOException {
        return this.executeUnparsed(false);
    }
    
    protected HttpResponse executeMedia() throws IOException {
        this.set("alt", "media");
        return this.executeUnparsed();
    }
    
    protected HttpResponse executeUsingHead() throws IOException {
        Preconditions.checkArgument(this.uploader == null);
        final HttpResponse response = this.executeUnparsed(true);
        response.ignore();
        return response;
    }
    
    private HttpResponse executeUnparsed(final boolean usingHead) throws IOException {
        HttpResponse response;
        if (this.uploader == null) {
            response = this.buildHttpRequest(usingHead).execute();
        }
        else {
            final GenericUrl httpRequestUrl = this.buildHttpRequestUrl();
            final HttpRequest httpRequest = this.getAbstractGoogleClient().getRequestFactory().buildRequest(this.requestMethod, httpRequestUrl, this.httpContent);
            final boolean throwExceptionOnExecuteError = httpRequest.getThrowExceptionOnExecuteError();
            response = this.uploader.setInitiationHeaders(this.requestHeaders).setDisableGZipContent(this.disableGZipContent).upload(httpRequestUrl);
            response.getRequest().setParser(this.getAbstractGoogleClient().getObjectParser());
            if (throwExceptionOnExecuteError && !response.isSuccessStatusCode()) {
                throw this.newExceptionOnError(response);
            }
        }
        this.lastResponseHeaders = response.getHeaders();
        this.lastStatusCode = response.getStatusCode();
        this.lastStatusMessage = response.getStatusMessage();
        return response;
    }
    
    protected IOException newExceptionOnError(final HttpResponse response) {
        return (IOException)new HttpResponseException(response);
    }
    
    public T execute() throws IOException {
        return (T)this.executeUnparsed().parseAs((Class)this.responseClass);
    }
    
    public InputStream executeAsInputStream() throws IOException {
        return this.executeUnparsed().getContent();
    }
    
    protected InputStream executeMediaAsInputStream() throws IOException {
        return this.executeMedia().getContent();
    }
    
    public void executeAndDownloadTo(final OutputStream outputStream) throws IOException {
        this.executeUnparsed().download(outputStream);
    }
    
    protected void executeMediaAndDownloadTo(final OutputStream outputStream) throws IOException {
        if (this.downloader == null) {
            this.executeMedia().download(outputStream);
        }
        else {
            this.downloader.download(this.buildHttpRequestUrl(), this.requestHeaders, outputStream);
        }
    }
    
    public final <E> void queue(final BatchRequest batchRequest, final Class<E> errorClass, final BatchCallback<T, E> callback) throws IOException {
        Preconditions.checkArgument(this.uploader == null, (Object)"Batching media requests is not supported");
        batchRequest.queue(this.buildHttpRequest(), this.getResponseClass(), errorClass, callback);
    }
    
    public AbstractGoogleClientRequest<T> set(final String fieldName, final Object value) {
        return (AbstractGoogleClientRequest)super.set(fieldName, value);
    }
    
    protected final void checkRequiredParameter(final Object value, final String name) {
        Preconditions.checkArgument(this.abstractGoogleClient.getSuppressRequiredParameterChecks() || value != null, "Required parameter %s must be specified", new Object[] { name });
    }
    
    static class ApiClientVersion
    {
        static final String DEFAULT_VERSION;
        private final String versionString;
        
        ApiClientVersion() {
            this(getJavaVersion(), StandardSystemProperty.OS_NAME.value(), StandardSystemProperty.OS_VERSION.value(), GoogleUtils.VERSION);
        }
        
        ApiClientVersion(final String javaVersion, final String osName, final String osVersion, final String clientVersion) {
            final StringBuilder sb = new StringBuilder("gl-java/");
            sb.append(formatSemver(javaVersion));
            sb.append(" gdcl/");
            sb.append(formatSemver(clientVersion));
            if (osName != null && osVersion != null) {
                sb.append(" ");
                sb.append(formatName(osName));
                sb.append("/");
                sb.append(formatSemver(osVersion));
            }
            this.versionString = sb.toString();
        }
        
        @Override
        public String toString() {
            return this.versionString;
        }
        
        private static String getJavaVersion() {
            final String version = System.getProperty("java.version");
            if (version == null) {
                return null;
            }
            final String formatted = formatSemver(version, null);
            if (formatted != null) {
                return formatted;
            }
            final Matcher m = Pattern.compile("^(\\d+)[^\\d]?").matcher(version);
            if (m.find()) {
                return m.group(1) + ".0.0";
            }
            return null;
        }
        
        private static String formatName(final String name) {
            return name.toLowerCase().replaceAll("[^\\w\\d\\-]", "-");
        }
        
        private static String formatSemver(final String version) {
            return formatSemver(version, version);
        }
        
        private static String formatSemver(final String version, final String defaultValue) {
            if (version == null) {
                return null;
            }
            final Matcher m = Pattern.compile("(\\d+\\.\\d+\\.\\d+).*").matcher(version);
            if (m.find()) {
                return m.group(1);
            }
            return defaultValue;
        }
        
        static {
            DEFAULT_VERSION = new ApiClientVersion().toString();
        }
    }
}
