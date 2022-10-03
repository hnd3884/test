package com.azul.crs.com.fasterxml.jackson.core.exc;

import com.azul.crs.com.fasterxml.jackson.core.JsonLocation;
import com.azul.crs.com.fasterxml.jackson.core.util.RequestPayload;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;

public abstract class StreamReadException extends JsonProcessingException
{
    static final long serialVersionUID = 1L;
    protected transient JsonParser _processor;
    protected RequestPayload _requestPayload;
    
    public StreamReadException(final JsonParser p, final String msg) {
        super(msg, (p == null) ? null : p.getCurrentLocation());
        this._processor = p;
    }
    
    public StreamReadException(final JsonParser p, final String msg, final Throwable root) {
        super(msg, (p == null) ? null : p.getCurrentLocation(), root);
        this._processor = p;
    }
    
    public StreamReadException(final JsonParser p, final String msg, final JsonLocation loc) {
        super(msg, loc, null);
        this._processor = p;
    }
    
    protected StreamReadException(final String msg, final JsonLocation loc, final Throwable rootCause) {
        super(msg);
        if (rootCause != null) {
            this.initCause(rootCause);
        }
        this._location = loc;
    }
    
    public abstract StreamReadException withParser(final JsonParser p0);
    
    public abstract StreamReadException withRequestPayload(final RequestPayload p0);
    
    @Override
    public JsonParser getProcessor() {
        return this._processor;
    }
    
    public RequestPayload getRequestPayload() {
        return this._requestPayload;
    }
    
    public String getRequestPayloadAsString() {
        return (this._requestPayload != null) ? this._requestPayload.toString() : null;
    }
    
    @Override
    public String getMessage() {
        String msg = super.getMessage();
        if (this._requestPayload != null) {
            msg = msg + "\nRequest payload : " + this._requestPayload.toString();
        }
        return msg;
    }
}
