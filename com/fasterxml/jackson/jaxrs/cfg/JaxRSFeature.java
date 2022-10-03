package com.fasterxml.jackson.jaxrs.cfg;

import com.fasterxml.jackson.databind.cfg.ConfigFeature;

public enum JaxRSFeature implements ConfigFeature
{
    ALLOW_EMPTY_INPUT(true), 
    ADD_NO_SNIFF_HEADER(false), 
    DYNAMIC_OBJECT_MAPPER_LOOKUP(false), 
    CACHE_ENDPOINT_READERS(true), 
    CACHE_ENDPOINT_WRITERS(true);
    
    private final boolean _defaultState;
    
    private JaxRSFeature(final boolean defaultState) {
        this._defaultState = defaultState;
    }
    
    public static int collectDefaults() {
        int flags = 0;
        for (final JaxRSFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }
    
    public boolean enabledByDefault() {
        return this._defaultState;
    }
    
    public int getMask() {
        return 1 << this.ordinal();
    }
    
    public boolean enabledIn(final int flags) {
        return (flags & this.getMask()) != 0x0;
    }
}
