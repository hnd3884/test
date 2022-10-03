package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import com.azul.crs.com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonSetter;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonInclude;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;

public class MutableConfigOverride extends ConfigOverride implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public MutableConfigOverride() {
    }
    
    protected MutableConfigOverride(final MutableConfigOverride src) {
        super(src);
    }
    
    public MutableConfigOverride copy() {
        return new MutableConfigOverride(this);
    }
    
    public MutableConfigOverride setFormat(final JsonFormat.Value v) {
        this._format = v;
        return this;
    }
    
    public MutableConfigOverride setInclude(final JsonInclude.Value v) {
        this._include = v;
        return this;
    }
    
    public MutableConfigOverride setIncludeAsProperty(final JsonInclude.Value v) {
        this._includeAsProperty = v;
        return this;
    }
    
    public MutableConfigOverride setIgnorals(final JsonIgnoreProperties.Value v) {
        this._ignorals = v;
        return this;
    }
    
    public MutableConfigOverride setIsIgnoredType(final Boolean v) {
        this._isIgnoredType = v;
        return this;
    }
    
    public MutableConfigOverride setSetterInfo(final JsonSetter.Value v) {
        this._setterInfo = v;
        return this;
    }
    
    public MutableConfigOverride setVisibility(final JsonAutoDetect.Value v) {
        this._visibility = v;
        return this;
    }
    
    public MutableConfigOverride setMergeable(final Boolean v) {
        this._mergeable = v;
        return this;
    }
}
