package com.azul.crs.com.fasterxml.jackson.databind.cfg;

import java.util.Arrays;
import java.io.Serializable;

public class CoercionConfig implements Serializable
{
    private static final long serialVersionUID = 1L;
    private static final int INPUT_SHAPE_COUNT;
    protected Boolean _acceptBlankAsEmpty;
    protected final CoercionAction[] _coercionsByShape;
    
    public CoercionConfig() {
        this._coercionsByShape = new CoercionAction[CoercionConfig.INPUT_SHAPE_COUNT];
        this._acceptBlankAsEmpty = false;
    }
    
    protected CoercionConfig(final CoercionConfig src) {
        this._acceptBlankAsEmpty = src._acceptBlankAsEmpty;
        this._coercionsByShape = Arrays.copyOf(src._coercionsByShape, src._coercionsByShape.length);
    }
    
    public CoercionAction findAction(final CoercionInputShape shape) {
        return this._coercionsByShape[shape.ordinal()];
    }
    
    public Boolean getAcceptBlankAsEmpty() {
        return this._acceptBlankAsEmpty;
    }
    
    static {
        INPUT_SHAPE_COUNT = CoercionInputShape.values().length;
    }
}
