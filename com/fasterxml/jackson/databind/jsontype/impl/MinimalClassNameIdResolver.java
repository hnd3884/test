package com.fasterxml.jackson.databind.jsontype.impl;

import java.io.IOException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.JavaType;

public class MinimalClassNameIdResolver extends ClassNameIdResolver
{
    protected final String _basePackageName;
    protected final String _basePackagePrefix;
    
    protected MinimalClassNameIdResolver(final JavaType baseType, final TypeFactory typeFactory, final PolymorphicTypeValidator ptv) {
        super(baseType, typeFactory, ptv);
        final String base = baseType.getRawClass().getName();
        final int ix = base.lastIndexOf(46);
        if (ix < 0) {
            this._basePackageName = "";
            this._basePackagePrefix = ".";
        }
        else {
            this._basePackagePrefix = base.substring(0, ix + 1);
            this._basePackageName = base.substring(0, ix);
        }
    }
    
    public static MinimalClassNameIdResolver construct(final JavaType baseType, final MapperConfig<?> config, final PolymorphicTypeValidator ptv) {
        return new MinimalClassNameIdResolver(baseType, config.getTypeFactory(), ptv);
    }
    
    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.MINIMAL_CLASS;
    }
    
    @Override
    public String idFromValue(final Object value) {
        final String n = value.getClass().getName();
        if (n.startsWith(this._basePackagePrefix)) {
            return n.substring(this._basePackagePrefix.length() - 1);
        }
        return n;
    }
    
    @Override
    protected JavaType _typeFromId(String id, final DatabindContext ctxt) throws IOException {
        if (id.startsWith(".")) {
            final StringBuilder sb = new StringBuilder(id.length() + this._basePackageName.length());
            if (this._basePackageName.length() == 0) {
                sb.append(id.substring(1));
            }
            else {
                sb.append(this._basePackageName).append(id);
            }
            id = sb.toString();
        }
        return super._typeFromId(id, ctxt);
    }
}
