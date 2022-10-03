package com.azul.crs.client;

import sun.security.validator.ValidatorException;
import javax.net.ssl.SSLHandshakeException;
import java.net.UnknownHostException;
import java.io.IOException;

public class Result<T>
{
    private Response<T> response;
    private IOException exception;
    
    public Result(final Response<T> response) {
        this.response = response;
    }
    
    public Result(final IOException exception) {
        this.exception = exception;
    }
    
    public boolean hasException() {
        return this.exception != null;
    }
    
    public boolean hasResponse() {
        return this.response != null;
    }
    
    public Response<T> getResponse() {
        return this.response;
    }
    
    public IOException getException() {
        return this.exception;
    }
    
    public boolean successful() {
        return this.hasResponse() && this.response.successful();
    }
    
    public boolean canRetry() {
        if (this.hasResponse()) {
            return this.response.canRetry();
        }
        return !(this.exception instanceof UnknownHostException) && (!(this.exception instanceof SSLHandshakeException) || this.exception.getCause() == null || !(this.exception.getCause() instanceof ValidatorException));
    }
    
    public String errorString() {
        return this.hasResponse() ? this.response.errorString() : this.exception.getMessage();
    }
    
    @Override
    public String toString() {
        return "Result{response=" + this.response + ", exception=" + this.exception + '}';
    }
}
