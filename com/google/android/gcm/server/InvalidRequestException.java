package com.google.android.gcm.server;

import java.io.IOException;

public final class InvalidRequestException extends IOException
{
    private final int status;
    private final String description;
    
    public InvalidRequestException(final int status) {
        this(status, null);
    }
    
    public InvalidRequestException(final int status, final String description) {
        super(getMessage(status, description));
        this.status = status;
        this.description = description;
    }
    
    private static String getMessage(final int status, final String description) {
        final StringBuilder base = new StringBuilder("HTTP Status Code: ").append(status);
        if (description != null) {
            base.append("(").append(description).append(")");
        }
        return base.toString();
    }
    
    public int getHttpStatusCode() {
        return this.status;
    }
    
    public String getDescription() {
        return this.description;
    }
}
