package com.azul.crs.client;

import java.net.UnknownHostException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;

public class CRSException extends IOException
{
    public static final int REASON_NO_ENDPOINT = -1;
    public static final int AUTHENTICATION_FAILURE = -2;
    public static final int REASON_GENERIC = -3;
    public static final int REASON_INTERNAL_ERROR = -4;
    private static final String MESSAGE_NO_ENDPOINT = "No CRS endpoint found.\nPlease specify via command line arguments or verify if your DNS has CRS record provisioned";
    private static final String MESSAGE_ENDPOINT_AUTHENTICATION_FAILED = "CRS endpoint authentication error.\nPlease ensure you have proper endpoint address specified in command line or your DNS settings.";
    private static final String MESSAGE_ENDPOINT_ADDRESS = "\n API endpoint address configured: ";
    private final int reason;
    private final Result result;
    private final Client client;
    
    public CRSException(final int reason) {
        this(null, reason, null, null);
    }
    
    public CRSException(final int reason, final String message, final Throwable cause) {
        super(message, cause);
        this.client = null;
        this.reason = reason;
        this.result = null;
    }
    
    public CRSException(final Client client, final int reason, final String message, final Result result) {
        super(message);
        this.client = client;
        this.reason = reason;
        this.result = result;
        if (result.hasException()) {
            this.initCause(result.getException());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder message = new StringBuilder();
        switch (this.reason) {
            case -1: {
                message.append("No CRS endpoint found.\nPlease specify via command line arguments or verify if your DNS has CRS record provisioned");
                break;
            }
            case -2: {
                if (this.getCause() != null) {
                    if (this.getCause() instanceof SSLHandshakeException) {
                        message.append("CRS endpoint authentication error.\nPlease ensure you have proper endpoint address specified in command line or your DNS settings.");
                    }
                    else if (this.getCause() instanceof UnknownHostException) {
                        message.append("No CRS endpoint found.\nPlease specify via command line arguments or verify if your DNS has CRS record provisioned");
                    }
                    else {
                        message.append(this.getMessage()).append(this.result.errorString());
                    }
                }
                else {
                    message.append(this.getMessage());
                    if (this.result != null) {
                        message.append(this.result.errorString());
                    }
                }
                if (this.client != null && this.client.getRestAPI() != null) {
                    message.append("\n API endpoint address configured: ").append(this.client.getRestAPI());
                    break;
                }
                break;
            }
            default: {
                message = new StringBuilder(this.getMessage());
                if (this.getCause() != null) {
                    message.append("\nCaused by: ").append(this.getCause().toString());
                    break;
                }
                break;
            }
        }
        return message.toString();
    }
}
