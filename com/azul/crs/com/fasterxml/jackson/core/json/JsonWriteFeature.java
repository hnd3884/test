package com.azul.crs.com.fasterxml.jackson.core.json;

import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.core.FormatFeature;

public enum JsonWriteFeature implements FormatFeature
{
    QUOTE_FIELD_NAMES(true, JsonGenerator.Feature.QUOTE_FIELD_NAMES), 
    WRITE_NAN_AS_STRINGS(true, JsonGenerator.Feature.QUOTE_NON_NUMERIC_NUMBERS), 
    WRITE_NUMBERS_AS_STRINGS(false, JsonGenerator.Feature.WRITE_NUMBERS_AS_STRINGS), 
    ESCAPE_NON_ASCII(false, JsonGenerator.Feature.ESCAPE_NON_ASCII);
    
    private final boolean _defaultState;
    private final int _mask;
    private final JsonGenerator.Feature _mappedFeature;
    
    public static int collectDefaults() {
        int flags = 0;
        for (final JsonWriteFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }
    
    private JsonWriteFeature(final boolean defaultState, final JsonGenerator.Feature mapTo) {
        this._defaultState = defaultState;
        this._mask = 1 << this.ordinal();
        this._mappedFeature = mapTo;
    }
    
    @Override
    public boolean enabledByDefault() {
        return this._defaultState;
    }
    
    @Override
    public int getMask() {
        return this._mask;
    }
    
    @Override
    public boolean enabledIn(final int flags) {
        return (flags & this._mask) != 0x0;
    }
    
    public JsonGenerator.Feature mappedFeature() {
        return this._mappedFeature;
    }
}
