package com.google.api.client.http;

import java.nio.charset.StandardCharsets;
import java.nio.charset.Charset;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import com.google.api.client.util.IOUtils;
import java.io.OutputStream;
import java.io.EOFException;
import com.google.api.client.util.LoggingInputStream;
import java.util.zip.GZIPInputStream;
import java.util.Locale;
import com.google.api.client.util.Preconditions;
import java.io.IOException;
import java.util.logging.Logger;
import com.google.api.client.util.StringUtils;
import java.util.logging.Level;
import java.io.InputStream;

public final class HttpResponse
{
    private InputStream content;
    private final String contentEncoding;
    private final String contentType;
    private final HttpMediaType mediaType;
    LowLevelHttpResponse response;
    private final int statusCode;
    private final String statusMessage;
    private final HttpRequest request;
    private final boolean returnRawInputStream;
    private static final String CONTENT_ENCODING_GZIP = "gzip";
    private static final String CONTENT_ENCODING_XGZIP = "x-gzip";
    private int contentLoggingLimit;
    private boolean loggingEnabled;
    private boolean contentRead;
    
    HttpResponse(final HttpRequest request, final LowLevelHttpResponse response) throws IOException {
        this.request = request;
        this.returnRawInputStream = request.getResponseReturnRawInputStream();
        this.contentLoggingLimit = request.getContentLoggingLimit();
        this.loggingEnabled = request.isLoggingEnabled();
        this.response = response;
        this.contentEncoding = response.getContentEncoding();
        final int code = response.getStatusCode();
        this.statusCode = ((code < 0) ? 0 : code);
        final String message = response.getReasonPhrase();
        this.statusMessage = message;
        final Logger logger = HttpTransport.LOGGER;
        final boolean loggable = this.loggingEnabled && logger.isLoggable(Level.CONFIG);
        StringBuilder logbuf = null;
        if (loggable) {
            logbuf = new StringBuilder();
            logbuf.append("-------------- RESPONSE --------------").append(StringUtils.LINE_SEPARATOR);
            final String statusLine = response.getStatusLine();
            if (statusLine != null) {
                logbuf.append(statusLine);
            }
            else {
                logbuf.append(this.statusCode);
                if (message != null) {
                    logbuf.append(' ').append(message);
                }
            }
            logbuf.append(StringUtils.LINE_SEPARATOR);
        }
        request.getResponseHeaders().fromHttpResponse(response, loggable ? logbuf : null);
        String contentType = response.getContentType();
        if (contentType == null) {
            contentType = request.getResponseHeaders().getContentType();
        }
        this.contentType = contentType;
        this.mediaType = parseMediaType(contentType);
        if (loggable) {
            logger.config(logbuf.toString());
        }
    }
    
    private static HttpMediaType parseMediaType(final String contentType) {
        if (contentType == null) {
            return null;
        }
        try {
            return new HttpMediaType(contentType);
        }
        catch (final IllegalArgumentException e) {
            return null;
        }
    }
    
    public int getContentLoggingLimit() {
        return this.contentLoggingLimit;
    }
    
    public HttpResponse setContentLoggingLimit(final int contentLoggingLimit) {
        Preconditions.checkArgument(contentLoggingLimit >= 0, (Object)"The content logging limit must be non-negative.");
        this.contentLoggingLimit = contentLoggingLimit;
        return this;
    }
    
    public boolean isLoggingEnabled() {
        return this.loggingEnabled;
    }
    
    public HttpResponse setLoggingEnabled(final boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
        return this;
    }
    
    public String getContentEncoding() {
        return this.contentEncoding;
    }
    
    public String getContentType() {
        return this.contentType;
    }
    
    public HttpMediaType getMediaType() {
        return this.mediaType;
    }
    
    public HttpHeaders getHeaders() {
        return this.request.getResponseHeaders();
    }
    
    public boolean isSuccessStatusCode() {
        return HttpStatusCodes.isSuccess(this.statusCode);
    }
    
    public int getStatusCode() {
        return this.statusCode;
    }
    
    public String getStatusMessage() {
        return this.statusMessage;
    }
    
    public HttpTransport getTransport() {
        return this.request.getTransport();
    }
    
    public HttpRequest getRequest() {
        return this.request;
    }
    
    public InputStream getContent() throws IOException {
        if (!this.contentRead) {
            InputStream lowLevelResponseContent = this.response.getContent();
            if (lowLevelResponseContent != null) {
                boolean contentProcessed = false;
                try {
                    if (!this.returnRawInputStream && this.contentEncoding != null) {
                        final String contentEncoding = this.contentEncoding.trim().toLowerCase(Locale.ENGLISH);
                        if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
                            lowLevelResponseContent = new GZIPInputStream(new ConsumingInputStream(lowLevelResponseContent));
                        }
                    }
                    final Logger logger = HttpTransport.LOGGER;
                    if (this.loggingEnabled && logger.isLoggable(Level.CONFIG)) {
                        lowLevelResponseContent = new LoggingInputStream(lowLevelResponseContent, logger, Level.CONFIG, this.contentLoggingLimit);
                    }
                    this.content = lowLevelResponseContent;
                    contentProcessed = true;
                }
                catch (final EOFException ex) {}
                finally {
                    if (!contentProcessed) {
                        lowLevelResponseContent.close();
                    }
                }
            }
            this.contentRead = true;
        }
        return this.content;
    }
    
    public void download(final OutputStream outputStream) throws IOException {
        final InputStream inputStream = this.getContent();
        IOUtils.copy(inputStream, outputStream);
    }
    
    public void ignore() throws IOException {
        if (this.response == null) {
            return;
        }
        try (final InputStream lowLevelResponseContent = this.response.getContent()) {}
    }
    
    public void disconnect() throws IOException {
        this.response.disconnect();
        this.ignore();
    }
    
    public <T> T parseAs(final Class<T> dataClass) throws IOException {
        if (!this.hasMessageBody()) {
            return null;
        }
        return this.request.getParser().parseAndClose(this.getContent(), this.getContentCharset(), dataClass);
    }
    
    private boolean hasMessageBody() throws IOException {
        final int statusCode = this.getStatusCode();
        if (this.getRequest().getRequestMethod().equals("HEAD") || statusCode / 100 == 1 || statusCode == 204 || statusCode == 304) {
            this.ignore();
            return false;
        }
        return true;
    }
    
    public Object parseAs(final Type dataType) throws IOException {
        if (!this.hasMessageBody()) {
            return null;
        }
        return this.request.getParser().parseAndClose(this.getContent(), this.getContentCharset(), dataType);
    }
    
    public String parseAsString() throws IOException {
        final InputStream content = this.getContent();
        if (content == null) {
            return "";
        }
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(content, out);
        return out.toString(this.getContentCharset().name());
    }
    
    public Charset getContentCharset() {
        if (this.mediaType != null) {
            if (this.mediaType.getCharsetParameter() != null) {
                return this.mediaType.getCharsetParameter();
            }
            if ("application".equals(this.mediaType.getType()) && "json".equals(this.mediaType.getSubType())) {
                return StandardCharsets.UTF_8;
            }
        }
        return StandardCharsets.ISO_8859_1;
    }
}
