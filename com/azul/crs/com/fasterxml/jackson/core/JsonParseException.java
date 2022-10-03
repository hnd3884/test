package com.azul.crs.com.fasterxml.jackson.core;

import com.azul.crs.com.fasterxml.jackson.core.util.RequestPayload;
import com.azul.crs.com.fasterxml.jackson.core.exc.StreamReadException;

public class JsonParseException extends StreamReadException
{
    private static final long serialVersionUID = 2L;
    
    @Deprecated
    public JsonParseException(final String msg, final JsonLocation loc) {
        super(msg, loc, null);
    }
    
    @Deprecated
    public JsonParseException(final String msg, final JsonLocation loc, final Throwable root) {
        super(msg, loc, root);
    }
    
    public JsonParseException(final JsonParser p, final String msg) {
        super(p, msg);
    }
    
    public JsonParseException(final JsonParser p, final String msg, final Throwable root) {
        super(p, msg, root);
    }
    
    public JsonParseException(final JsonParser p, final String msg, final JsonLocation loc) {
        super(p, msg, loc);
    }
    
    public JsonParseException(final JsonParser p, final String msg, final JsonLocation loc, final Throwable root) {
        super(msg, loc, root);
    }
    
    @Override
    public JsonParseException withParser(final JsonParser p) {
        this._processor = p;
        return this;
    }
    
    @Override
    public JsonParseException withRequestPayload(final RequestPayload p) {
        this._requestPayload = p;
        return this;
    }
    
    @Override
    public JsonParser getProcessor() {
        return super.getProcessor();
    }
    
    @Override
    public RequestPayload getRequestPayload() {
        return super.getRequestPayload();
    }
    
    @Override
    public String getRequestPayloadAsString() {
        return super.getRequestPayloadAsString();
    }
    
    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
