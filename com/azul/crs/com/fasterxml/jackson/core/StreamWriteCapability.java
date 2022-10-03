package com.azul.crs.com.fasterxml.jackson.core;

import com.azul.crs.com.fasterxml.jackson.core.util.JacksonFeature;

public enum StreamWriteCapability implements JacksonFeature
{
    CAN_WRITE_BINARY_NATIVELY(false), 
    CAN_WRITE_FORMATTED_NUMBERS(false);
    
    private final boolean _defaultState;
    private final int _mask;
    
    private StreamWriteCapability(final boolean defaultState) {
        this._defaultState = defaultState;
        this._mask = 1 << this.ordinal();
    }
    
    @Override
    public boolean enabledByDefault() {
        return this._defaultState;
    }
    
    @Override
    public boolean enabledIn(final int flags) {
        return (flags & this._mask) != 0x0;
    }
    
    @Override
    public int getMask() {
        return this._mask;
    }
}
