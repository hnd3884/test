package com.azul.crs.com.fasterxml.jackson.databind.exc;

import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;

public class InvalidTypeIdException extends MismatchedInputException
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _baseType;
    protected final String _typeId;
    
    public InvalidTypeIdException(final JsonParser p, final String msg, final JavaType baseType, final String typeId) {
        super(p, msg);
        this._baseType = baseType;
        this._typeId = typeId;
    }
    
    public static InvalidTypeIdException from(final JsonParser p, final String msg, final JavaType baseType, final String typeId) {
        return new InvalidTypeIdException(p, msg, baseType, typeId);
    }
    
    public JavaType getBaseType() {
        return this._baseType;
    }
    
    public String getTypeId() {
        return this._typeId;
    }
}