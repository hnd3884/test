package com.azul.crs.client;

public class Response<T>
{
    private int code;
    private T payload;
    private String message;
    private String error;
    
    public Response<T> code(final int code) {
        this.code = code;
        return this;
    }
    
    public Response<T> message(final String error) {
        this.message = error;
        return this;
    }
    
    public Response<T> payload(final T payload) {
        this.payload = payload;
        return this;
    }
    
    public Response<T> error(final String error) {
        this.error = error;
        return this;
    }
    
    public int getCode() {
        return this.code;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public T getPayload() {
        return this.payload;
    }
    
    public String getError() {
        return this.error;
    }
    
    @Override
    public String toString() {
        return "code=" + this.code + ", message=" + this.message + ", payload=" + this.payload + ", error=" + this.error;
    }
    
    public String errorString() {
        return "code=" + this.code + ", cause=" + ((this.error != null) ? this.error : this.message);
    }
    
    public boolean successful() {
        return this.code / 100 == 2;
    }
    
    public boolean canRetry() {
        switch (this.code) {
            case 502:
            case 503:
            case 504: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
}
