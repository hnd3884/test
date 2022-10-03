package com.fasterxml.jackson.core;

public enum StreamReadFeature
{
    AUTO_CLOSE_SOURCE(JsonParser.Feature.AUTO_CLOSE_SOURCE), 
    STRICT_DUPLICATE_DETECTION(JsonParser.Feature.STRICT_DUPLICATE_DETECTION), 
    IGNORE_UNDEFINED(JsonParser.Feature.IGNORE_UNDEFINED), 
    INCLUDE_SOURCE_IN_LOCATION(JsonParser.Feature.INCLUDE_SOURCE_IN_LOCATION);
    
    private final boolean _defaultState;
    private final int _mask;
    private final JsonParser.Feature _mappedFeature;
    
    private StreamReadFeature(final JsonParser.Feature mapTo) {
        this._mappedFeature = mapTo;
        this._mask = mapTo.getMask();
        this._defaultState = mapTo.enabledByDefault();
    }
    
    public static int collectDefaults() {
        int flags = 0;
        for (final StreamReadFeature f : values()) {
            if (f.enabledByDefault()) {
                flags |= f.getMask();
            }
        }
        return flags;
    }
    
    public boolean enabledByDefault() {
        return this._defaultState;
    }
    
    public boolean enabledIn(final int flags) {
        return (flags & this._mask) != 0x0;
    }
    
    public int getMask() {
        return this._mask;
    }
    
    public JsonParser.Feature mappedFeature() {
        return this._mappedFeature;
    }
}
