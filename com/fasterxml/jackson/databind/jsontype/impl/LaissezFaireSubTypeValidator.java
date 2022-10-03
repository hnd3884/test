package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;

public final class LaissezFaireSubTypeValidator extends Base
{
    private static final long serialVersionUID = 1L;
    public static final LaissezFaireSubTypeValidator instance;
    
    @Override
    public Validity validateBaseType(final MapperConfig<?> ctxt, final JavaType baseType) {
        return Validity.INDETERMINATE;
    }
    
    @Override
    public Validity validateSubClassName(final MapperConfig<?> ctxt, final JavaType baseType, final String subClassName) {
        return Validity.ALLOWED;
    }
    
    @Override
    public Validity validateSubType(final MapperConfig<?> ctxt, final JavaType baseType, final JavaType subType) {
        return Validity.ALLOWED;
    }
    
    static {
        instance = new LaissezFaireSubTypeValidator();
    }
}
