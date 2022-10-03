package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import java.io.Serializable;

public class MutableCoercionConfig extends CoercionConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public MutableCoercionConfig() {
    }
    
    protected MutableCoercionConfig(final MutableCoercionConfig src) {
        super(src);
    }
    
    public MutableCoercionConfig copy() {
        return new MutableCoercionConfig(this);
    }
    
    public MutableCoercionConfig setCoercion(final CoercionInputShape shape, final CoercionAction action) {
        this._coercionsByShape[shape.ordinal()] = action;
        return this;
    }
    
    public MutableCoercionConfig setAcceptBlankAsEmpty(final Boolean state) {
        this._acceptBlankAsEmpty = state;
        return this;
    }
}
