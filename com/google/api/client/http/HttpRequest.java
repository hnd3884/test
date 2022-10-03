package com.google.api.client.http;

import java.util.Properties;
import java.util.concurrent.Executors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.Executor;
import java.io.InputStream;
import io.opencensus.common.Scope;
import com.google.api.client.util.StreamingContent;
import java.util.logging.Logger;
import io.opencensus.trace.Span;
import java.io.IOException;
import io.opencensus.trace.AttributeValue;
import com.google.api.client.util.LoggingStreamingContent;
import com.google.api.client.util.StringUtils;
import java.util.logging.Level;
import com.google.api.client.util.Preconditions;
import io.opencensus.trace.Tracer;
import com.google.api.client.util.Sleeper;
import com.google.api.client.util.ObjectParser;
import com.google.api.client.util.Beta;

public final class HttpRequest
{
    public static final String VERSION;
    public static final String USER_AGENT_SUFFIX;
    public static final int DEFAULT_NUMBER_OF_RETRIES = 10;
    private HttpExecuteInterceptor executeInterceptor;
    private HttpHeaders headers;
    private HttpHeaders responseHeaders;
    private int numRetries;
    private int contentLoggingLimit;
    private boolean loggingEnabled;
    private boolean curlLoggingEnabled;
    private HttpContent content;
    private final HttpTransport transport;
    private String requestMethod;
    private GenericUrl url;
    private int connectTimeout;
    private int readTimeout;
    private int writeTimeout;
    private HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler;
    @Beta
    private HttpIOExceptionHandler ioExceptionHandler;
    private HttpResponseInterceptor responseInterceptor;
    private ObjectParser objectParser;
    private HttpEncoding encoding;
    @Deprecated
    @Beta
    private BackOffPolicy backOffPolicy;
    private boolean followRedirects;
    private boolean useRawRedirectUrls;
    private boolean throwExceptionOnExecuteError;
    @Deprecated
    @Beta
    private boolean retryOnExecuteIOException;
    private boolean suppressUserAgentSuffix;
    private Sleeper sleeper;
    private final Tracer tracer;
    private boolean responseReturnRawInputStream;
    
    HttpRequest(final HttpTransport transport, final String requestMethod) {
        this.headers = new HttpHeaders();
        this.responseHeaders = new HttpHeaders();
        this.numRetries = 10;
        this.contentLoggingLimit = 16384;
        this.loggingEnabled = true;
        this.curlLoggingEnabled = true;
        this.connectTimeout = 20000;
        this.readTimeout = 20000;
        this.writeTimeout = 0;
        this.followRedirects = true;
        this.useRawRedirectUrls = false;
        this.throwExceptionOnExecuteError = true;
        this.retryOnExecuteIOException = false;
        this.sleeper = Sleeper.DEFAULT;
        this.tracer = OpenCensusUtils.getTracer();
        this.responseReturnRawInputStream = false;
        this.transport = transport;
        this.setRequestMethod(requestMethod);
    }
    
    public HttpTransport getTransport() {
        return this.transport;
    }
    
    public String getRequestMethod() {
        return this.requestMethod;
    }
    
    public HttpRequest setRequestMethod(final String requestMethod) {
        Preconditions.checkArgument(requestMethod == null || HttpMediaType.matchesToken(requestMethod));
        this.requestMethod = requestMethod;
        return this;
    }
    
    public GenericUrl getUrl() {
        return this.url;
    }
    
    public HttpRequest setUrl(final GenericUrl url) {
        this.url = Preconditions.checkNotNull(url);
        return this;
    }
    
    public HttpContent getContent() {
        return this.content;
    }
    
    public HttpRequest setContent(final HttpContent content) {
        this.content = content;
        return this;
    }
    
    public HttpEncoding getEncoding() {
        return this.encoding;
    }
    
    public HttpRequest setEncoding(final HttpEncoding encoding) {
        this.encoding = encoding;
        return this;
    }
    
    @Deprecated
    @Beta
    public BackOffPolicy getBackOffPolicy() {
        return this.backOffPolicy;
    }
    
    @Deprecated
    @Beta
    public HttpRequest setBackOffPolicy(final BackOffPolicy backOffPolicy) {
        this.backOffPolicy = backOffPolicy;
        return this;
    }
    
    public int getContentLoggingLimit() {
        return this.contentLoggingLimit;
    }
    
    public HttpRequest setContentLoggingLimit(final int contentLoggingLimit) {
        Preconditions.checkArgument(contentLoggingLimit >= 0, (Object)"The content logging limit must be non-negative.");
        this.contentLoggingLimit = contentLoggingLimit;
        return this;
    }
    
    public boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }
    
    public HttpRequest setLoggingEnabled(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }
    
    public boolean isCurlLoggingEnabled() {
        return this.curlLoggingEnabled;
    }
    
    public HttpRequest setCurlLoggingEnabled(final boolean curlLoggingEnabled) {
        this.curlLoggingEnabled = curlLoggingEnabled;
        return this;
    }
    
    public int getConnectTimeout() {
        return this.connectTimeout;
    }
    
    public HttpRequest setConnectTimeout(final int connectTimeout) {
        Preconditions.checkArgument(connectTimeout >= 0);
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    public int getReadTimeout() {
        return this.readTimeout;
    }
    
    public HttpRequest setReadTimeout(final int readTimeout) {
        Preconditions.checkArgument(readTimeout >= 0);
        this.readTimeout = readTimeout;
        return this;
    }
    
    public int getWriteTimeout() {
        return this.writeTimeout;
    }
    
    public HttpRequest setWriteTimeout(final int writeTimeout) {
        Preconditions.checkArgument(writeTimeout >= 0);
        this.writeTimeout = writeTimeout;
        return this;
    }
    
    public HttpHeaders getHeaders() {
        return this.headers;
    }
    
    public HttpRequest setHeaders(final HttpHeaders headers) {
        this.headers = Preconditions.checkNotNull(headers);
        return this;
    }
    
    public HttpHeaders getResponseHeaders() {
        return this.responseHeaders;
    }
    
    public HttpRequest setResponseHeaders(final HttpHeaders responseHeaders) {
        this.responseHeaders = Preconditions.checkNotNull(responseHeaders);
        return this;
    }
    
    public HttpExecuteInterceptor getInterceptor() {
        return this.executeInterceptor;
    }
    
    public HttpRequest setInterceptor(final HttpExecuteInterceptor interceptor) {
        this.executeInterceptor = interceptor;
        return this;
    }
    
    public HttpUnsuccessfulResponseHandler getUnsuccessfulResponseHandler() {
        return this.unsuccessfulResponseHandler;
    }
    
    public HttpRequest setUnsuccessfulResponseHandler(final HttpUnsuccessfulResponseHandler unsuccessfulResponseHandler) {
        this.unsuccessfulResponseHandler = unsuccessfulResponseHandler;
        return this;
    }
    
    @Beta
    public HttpIOExceptionHandler getIOExceptionHandler() {
        return this.ioExceptionHandler;
    }
    
    @Beta
    public HttpRequest setIOExceptionHandler(final HttpIOExceptionHandler ioExceptionHandler) {
        this.ioExceptionHandler = ioExceptionHandler;
        return this;
    }
    
    public HttpResponseInterceptor getResponseInterceptor() {
        return this.responseInterceptor;
    }
    
    public HttpRequest setResponseInterceptor(final HttpResponseInterceptor responseInterceptor) {
        this.responseInterceptor = responseInterceptor;
        return this;
    }
    
    public int getNumberOfRetries() {
        return this.numRetries;
    }
    
    public HttpRequest setNumberOfRetries(final int numRetries) {
        Preconditions.checkArgument(numRetries >= 0);
        this.numRetries = numRetries;
        return this;
    }
    
    public HttpRequest setParser(final ObjectParser parser) {
        this.objectParser = parser;
        return this;
    }
    
    public final ObjectParser getParser() {
        return this.objectParser;
    }
    
    public boolean getFollowRedirects() {
        return this.followRedirects;
    }
    
    public HttpRequest setFollowRedirects(final boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }
    
    public boolean getUseRawRedirectUrls() {
        return this.useRawRedirectUrls;
    }
    
    public HttpRequest setUseRawRedirectUrls(final boolean useRawRedirectUrls) {
        this.useRawRedirectUrls = useRawRedirectUrls;
        return this;
    }
    
    public boolean getThrowExceptionOnExecuteError() {
        return this.throwExceptionOnExecuteError;
    }
    
    public HttpRequest setThrowExceptionOnExecuteError(final boolean throwExceptionOnExecuteError) {
        this.throwExceptionOnExecuteError = throwExceptionOnExecuteError;
        return this;
    }
    
    @Deprecated
    @Beta
    public boolean getRetryOnExecuteIOException() {
        return this.retryOnExecuteIOException;
    }
    
    @Deprecated
    @Beta
    public HttpRequest setRetryOnExecuteIOException(final boolean retryOnExecuteIOException) {
        this.retryOnExecuteIOException = retryOnExecuteIOException;
        return this;
    }
    
    public boolean getSuppressUserAgentSuffix() {
        return this.suppressUserAgentSuffix;
    }
    
    public HttpRequest setSuppressUserAgentSuffix(final boolean suppressUserAgentSuffix) {
        this.suppressUserAgentSuffix = suppressUserAgentSuffix;
        return this;
    }
    
    public boolean getResponseReturnRawInputStream() {
        return this.responseReturnRawInputStream;
    }
    
    public HttpRequest setResponseReturnRawInputStream(final boolean responseReturnRawInputStream) {
        this.responseReturnRawInputStream = responseReturnRawInputStream;
        return this;
    }
    
    public HttpResponse execute() throws IOException {
        boolean retryRequest = false;
        Preconditions.checkArgument(this.numRetries >= 0);
        int retriesRemaining = this.numRetries;
        if (this.backOffPolicy != null) {
            this.backOffPolicy.reset();
        }
        HttpResponse response = null;
        Preconditions.checkNotNull(this.requestMethod);
        Preconditions.checkNotNull(this.url);
        final Span span = this.tracer.spanBuilder(OpenCensusUtils.SPAN_NAME_HTTP_REQUEST_EXECUTE).setRecordEvents(OpenCensusUtils.isRecordEvent()).startSpan();
        IOException executeException;
        do {
            span.addAnnotation("retry #" + (this.numRetries - retriesRemaining));
            if (response != null) {
                response.ignore();
            }
            response = null;
            executeException = null;
            if (this.executeInterceptor != null) {
                this.executeInterceptor.intercept(this);
            }
            final String urlString = this.url.build();
            addSpanAttribute(span, "http.method", this.requestMethod);
            addSpanAttribute(span, "http.host", this.url.getHost());
            addSpanAttribute(span, "http.path", this.url.getRawPath());
            addSpanAttribute(span, "http.url", urlString);
            final LowLevelHttpRequest lowLevelHttpRequest = this.transport.buildRequest(this.requestMethod, urlString);
            final Logger logger = HttpTransport.LOGGER;
            final boolean loggable = this.loggingEnabled && logger.isLoggable(Level.CONFIG);
            StringBuilder logbuf = null;
            StringBuilder curlbuf = null;
            if (loggable) {
                logbuf = new StringBuilder();
                logbuf.append("-------------- REQUEST  --------------").append(StringUtils.LINE_SEPARATOR);
                logbuf.append(this.requestMethod).append(' ').append(urlString).append(StringUtils.LINE_SEPARATOR);
                if (this.curlLoggingEnabled) {
                    curlbuf = new StringBuilder("curl -v --compressed");
                    if (!this.requestMethod.equals("GET")) {
                        curlbuf.append(" -X ").append(this.requestMethod);
                    }
                }
            }
            final String originalUserAgent = this.headers.getUserAgent();
            if (!this.suppressUserAgentSuffix) {
                if (originalUserAgent == null) {
                    this.headers.setUserAgent(HttpRequest.USER_AGENT_SUFFIX);
                    addSpanAttribute(span, "http.user_agent", HttpRequest.USER_AGENT_SUFFIX);
                }
                else {
                    final String newUserAgent = originalUserAgent + " " + HttpRequest.USER_AGENT_SUFFIX;
                    this.headers.setUserAgent(newUserAgent);
                    addSpanAttribute(span, "http.user_agent", newUserAgent);
                }
            }
            OpenCensusUtils.propagateTracingContext(span, this.headers);
            HttpHeaders.serializeHeaders(this.headers, logbuf, curlbuf, logger, lowLevelHttpRequest);
            if (!this.suppressUserAgentSuffix) {
                this.headers.setUserAgent(originalUserAgent);
            }
            StreamingContent streamingContent = this.content;
            final boolean contentRetrySupported = streamingContent == null || this.content.retrySupported();
            if (streamingContent != null) {
                long contentLength = -1L;
                final String contentType = this.content.getType();
                if (loggable) {
                    streamingContent = new LoggingStreamingContent(streamingContent, HttpTransport.LOGGER, Level.CONFIG, this.contentLoggingLimit);
                }
                String contentEncoding;
                if (this.encoding == null) {
                    contentEncoding = null;
                    contentLength = this.content.getLength();
                }
                else {
                    contentEncoding = this.encoding.getName();
                    streamingContent = new HttpEncodingStreamingContent(streamingContent, this.encoding);
                }
                if (loggable) {
                    if (contentType != null) {
                        final String header = "Content-Type: " + contentType;
                        logbuf.append(header).append(StringUtils.LINE_SEPARATOR);
                        if (curlbuf != null) {
                            curlbuf.append(" -H '" + header + "'");
                        }
                    }
                    if (contentEncoding != null) {
                        final String header = "Content-Encoding: " + contentEncoding;
                        logbuf.append(header).append(StringUtils.LINE_SEPARATOR);
                        if (curlbuf != null) {
                            curlbuf.append(" -H '" + header + "'");
                        }
                    }
                    if (contentLength >= 0L) {
                        final String header = "Content-Length: " + contentLength;
                        logbuf.append(header).append(StringUtils.LINE_SEPARATOR);
                    }
                }
                if (curlbuf != null) {
                    curlbuf.append(" -d '@-'");
                }
                lowLevelHttpRequest.setContentType(contentType);
                lowLevelHttpRequest.setContentEncoding(contentEncoding);
                lowLevelHttpRequest.setContentLength(contentLength);
                lowLevelHttpRequest.setStreamingContent(streamingContent);
            }
            if (loggable) {
                logger.config(logbuf.toString());
                if (curlbuf != null) {
                    curlbuf.append(" -- '");
                    curlbuf.append(urlString.replaceAll("'", "'\"'\"'"));
                    curlbuf.append("'");
                    if (streamingContent != null) {
                        curlbuf.append(" << $$$");
                    }
                    logger.config(curlbuf.toString());
                }
            }
            retryRequest = (contentRetrySupported && retriesRemaining > 0);
            lowLevelHttpRequest.setTimeout(this.connectTimeout, this.readTimeout);
            lowLevelHttpRequest.setWriteTimeout(this.writeTimeout);
            final Scope ws = this.tracer.withSpan(span);
            OpenCensusUtils.recordSentMessageEvent(span, lowLevelHttpRequest.getContentLength());
            try {
                final LowLevelHttpResponse lowLevelHttpResponse = lowLevelHttpRequest.execute();
                if (lowLevelHttpResponse != null) {
                    OpenCensusUtils.recordReceivedMessageEvent(span, lowLevelHttpResponse.getContentLength());
                    span.putAttribute("http.status_code", AttributeValue.longAttributeValue((long)lowLevelHttpResponse.getStatusCode()));
                }
                boolean responseConstructed = false;
                try {
                    response = new HttpResponse(this, lowLevelHttpResponse);
                    responseConstructed = true;
                }
                finally {
                    if (!responseConstructed) {
                        try (final InputStream lowLevelContent = lowLevelHttpResponse.getContent()) {}
                    }
                }
            }
            catch (final IOException e) {
                if (!this.retryOnExecuteIOException && (this.ioExceptionHandler == null || !this.ioExceptionHandler.handleIOException(this, retryRequest))) {
                    span.end(OpenCensusUtils.getEndSpanOptions(null));
                    throw e;
                }
                executeException = e;
                if (loggable) {
                    logger.log(Level.WARNING, "exception thrown while executing request", e);
                }
            }
            finally {
                ws.close();
            }
            boolean responseProcessed = false;
            try {
                if (response != null && !response.isSuccessStatusCode()) {
                    boolean errorHandled = false;
                    if (this.unsuccessfulResponseHandler != null) {
                        errorHandled = this.unsuccessfulResponseHandler.handleResponse(this, response, retryRequest);
                    }
                    if (!errorHandled) {
                        if (this.handleRedirect(response.getStatusCode(), response.getHeaders())) {
                            errorHandled = true;
                        }
                        else if (retryRequest && this.backOffPolicy != null && this.backOffPolicy.isBackOffRequired(response.getStatusCode())) {
                            final long backOffTime = this.backOffPolicy.getNextBackOffMillis();
                            if (backOffTime != -1L) {
                                try {
                                    this.sleeper.sleep(backOffTime);
                                }
                                catch (final InterruptedException ex) {}
                                errorHandled = true;
                            }
                        }
                    }
                    retryRequest &= errorHandled;
                    if (retryRequest) {
                        response.ignore();
                    }
                }
                else {
                    retryRequest &= (response == null);
                }
                --retriesRemaining;
                responseProcessed = true;
            }
            finally {
                if (response != null && !responseProcessed) {
                    response.disconnect();
                }
            }
        } while (retryRequest);
        span.end(OpenCensusUtils.getEndSpanOptions((response == null) ? null : Integer.valueOf(response.getStatusCode())));
        if (response == null) {
            throw executeException;
        }
        if (this.responseInterceptor != null) {
            this.responseInterceptor.interceptResponse(response);
        }
        if (this.throwExceptionOnExecuteError && !response.isSuccessStatusCode()) {
            try {
                throw new HttpResponseException(response);
            }
            finally {
                response.disconnect();
            }
        }
        return response;
    }
    
    @Beta
    public Future<HttpResponse> executeAsync(final Executor executor) {
        final FutureTask<HttpResponse> future = new FutureTask<HttpResponse>(new Callable<HttpResponse>() {
            @Override
            public HttpResponse call() throws Exception {
                return HttpRequest.this.execute();
            }
        });
        executor.execute(future);
        return future;
    }
    
    @Beta
    public Future<HttpResponse> executeAsync() {
        return this.executeAsync(Executors.newFixedThreadPool(1, new ThreadFactoryBuilder().setDaemon(true).build()));
    }
    
    public boolean handleRedirect(final int statusCode, final HttpHeaders responseHeaders) {
        final String redirectLocation = responseHeaders.getLocation();
        if (this.getFollowRedirects() && HttpStatusCodes.isRedirect(statusCode) && redirectLocation != null) {
            this.setUrl(new GenericUrl(this.url.toURL(redirectLocation), this.useRawRedirectUrls));
            if (statusCode == 303) {
                this.setRequestMethod("GET");
                this.setContent(null);
            }
            this.headers.setAuthorization((String)null);
            this.headers.setIfMatch(null);
            this.headers.setIfNoneMatch(null);
            this.headers.setIfModifiedSince(null);
            this.headers.setIfUnmodifiedSince(null);
            this.headers.setIfRange(null);
            return true;
        }
        return false;
    }
    
    public Sleeper getSleeper() {
        return this.sleeper;
    }
    
    public HttpRequest setSleeper(final Sleeper sleeper) {
        this.sleeper = Preconditions.checkNotNull(sleeper);
        return this;
    }
    
    private static void addSpanAttribute(final Span span, final String key, final String value) {
        if (value != null) {
            span.putAttribute(key, AttributeValue.stringAttributeValue(value));
        }
    }
    
    private static String getVersion() {
        String version = "unknown-version";
        try (final InputStream inputStream = HttpRequest.class.getResourceAsStream("/com/google/api/client/http/google-http-client.properties")) {
            if (inputStream != null) {
                final Properties properties = new Properties();
                properties.load(inputStream);
                version = properties.getProperty("google-http-client.version");
            }
        }
        catch (final IOException ex) {}
        return version;
    }
    
    static {
        VERSION = getVersion();
        USER_AGENT_SUFFIX = "Google-HTTP-Java-Client/" + HttpRequest.VERSION + " (gzip)";
    }
}
