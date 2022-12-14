package com.google.api.client.http;

import com.google.api.client.util.Preconditions;
import com.google.api.client.util.StringUtils;
import java.io.IOException;

public class HttpResponseException extends IOException
{
    private static final long serialVersionUID = -1875819453475890043L;
    private final int statusCode;
    private final String statusMessage;
    private final transient HttpHeaders headers;
    private final String content;
    
    public HttpResponseException(final HttpResponse response) {
        this(new Builder(response));
    }
    
    protected HttpResponseException(final Builder builder) {
        super(builder.message);
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.content = builder.content;
    }
    
    public final boolean isSuccessStatusCode() {
        return HttpStatusCodes.isSuccess(this.statusCode);
    }
    
    public final int getStatusCode() {
        return this.statusCode;
    }
    
    public final String getStatusMessage() {
        return this.statusMessage;
    }
    
    public HttpHeaders getHeaders() {
        return this.headers;
    }
    
    public final String getContent() {
        return this.content;
    }
    
    public static StringBuilder computeMessageBuffer(final HttpResponse response) {
        final StringBuilder builder = new StringBuilder();
        final int statusCode = response.getStatusCode();
        if (statusCode != 0) {
            builder.append(statusCode);
        }
        final String statusMessage = response.getStatusMessage();
        if (statusMessage != null) {
            if (statusCode != 0) {
                builder.append(' ');
            }
            builder.append(statusMessage);
        }
        final HttpRequest request = response.getRequest();
        if (request != null) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            final String requestMethod = request.getRequestMethod();
            if (requestMethod != null) {
                builder.append(requestMethod).append(' ');
            }
            builder.append(request.getUrl());
        }
        return builder;
    }
    
    public static class Builder
    {
        int statusCode;
        String statusMessage;
        HttpHeaders headers;
        String content;
        String message;
        
        public Builder(final int statusCode, final String statusMessage, final HttpHeaders headers) {
            this.setStatusCode(statusCode);
            this.setStatusMessage(statusMessage);
            this.setHeaders(headers);
        }
        
        public Builder(final HttpResponse response) {
            this(response.getStatusCode(), response.getStatusMessage(), response.getHeaders());
            try {
                this.content = response.parseAsString();
                if (this.content.length() == 0) {
                    this.content = null;
                }
            }
            catch (final IOException exception) {
                exception.printStackTrace();
            }
            catch (final IllegalArgumentException exception2) {
                exception2.printStackTrace();
            }
            final StringBuilder builder = HttpResponseException.computeMessageBuffer(response);
            if (this.content != null) {
                builder.append(StringUtils.LINE_SEPARATOR).append(this.content);
            }
            this.message = builder.toString();
        }
        
        public final String getMessage() {
            return this.message;
        }
        
        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }
        
        public final int getStatusCode() {
            return this.statusCode;
        }
        
        public Builder setStatusCode(final int statusCode) {
            Preconditions.checkArgument(statusCode >= 0);
            this.statusCode = statusCode;
            return this;
        }
        
        public final String getStatusMessage() {
            return this.statusMessage;
        }
        
        public Builder setStatusMessage(final String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }
        
        public HttpHeaders getHeaders() {
            return this.headers;
        }
        
        public Builder setHeaders(final HttpHeaders headers) {
            this.headers = Preconditions.checkNotNull(headers);
            return this;
        }
        
        public final String getContent() {
            return this.content;
        }
        
        public Builder setContent(final String content) {
            this.content = content;
            return this;
        }
        
        public HttpResponseException build() {
            return new HttpResponseException(this);
        }
    }
}
