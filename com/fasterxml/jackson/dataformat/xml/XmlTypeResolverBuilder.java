package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.databind.jsontype.impl.MinimalClassNameIdResolver;
import java.io.IOException;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

public class XmlTypeResolverBuilder extends StdTypeResolverBuilder
{
    public StdTypeResolverBuilder init(final JsonTypeInfo.Id idType, final TypeIdResolver idRes) {
        super.init(idType, idRes);
        if (this._typeProperty != null) {
            this._typeProperty = StaxUtil.sanitizeXmlTypeName(this._typeProperty);
        }
        return this;
    }
    
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = StaxUtil.sanitizeXmlTypeName(typeIdPropName);
        return this;
    }
    
    protected TypeIdResolver idResolver(final MapperConfig<?> config, final JavaType baseType, final PolymorphicTypeValidator subtypeValidator, final Collection<NamedType> subtypes, final boolean forSer, final boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        switch (this._idType) {
            case CLASS: {
                return (TypeIdResolver)new XmlClassNameIdResolver(baseType, config.getTypeFactory(), this.subTypeValidator((MapperConfig)config));
            }
            case MINIMAL_CLASS: {
                return (TypeIdResolver)new XmlMinimalClassNameIdResolver(baseType, config.getTypeFactory(), this.subTypeValidator((MapperConfig)config));
            }
            default: {
                return super.idResolver((MapperConfig)config, baseType, subtypeValidator, (Collection)subtypes, forSer, forDeser);
            }
        }
    }
    
    protected static String encodeXmlClassName(String className) {
        int ix = className.lastIndexOf(36);
        if (ix >= 0) {
            final StringBuilder sb = new StringBuilder(className);
            do {
                sb.replace(ix, ix + 1, "..");
                ix = className.lastIndexOf(36, ix - 1);
            } while (ix >= 0);
            className = sb.toString();
        }
        return className;
    }
    
    protected static String decodeXmlClassName(String className) {
        int ix = className.lastIndexOf("..");
        if (ix >= 0) {
            final StringBuilder sb = new StringBuilder(className);
            do {
                sb.replace(ix, ix + 2, "$");
                ix = className.lastIndexOf("..", ix - 1);
            } while (ix >= 0);
            className = sb.toString();
        }
        return className;
    }
    
    protected static class XmlClassNameIdResolver extends ClassNameIdResolver
    {
        public XmlClassNameIdResolver(final JavaType baseType, final TypeFactory typeFactory, final PolymorphicTypeValidator ptv) {
            super(baseType, typeFactory, ptv);
        }
        
        public String idFromValue(final Object value) {
            return XmlTypeResolverBuilder.encodeXmlClassName(super.idFromValue(value));
        }
        
        public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
            return super.typeFromId(context, XmlTypeResolverBuilder.decodeXmlClassName(id));
        }
    }
    
    protected static class XmlMinimalClassNameIdResolver extends MinimalClassNameIdResolver
    {
        public XmlMinimalClassNameIdResolver(final JavaType baseType, final TypeFactory typeFactory, final PolymorphicTypeValidator ptv) {
            super(baseType, typeFactory, ptv);
        }
        
        public String idFromValue(final Object value) {
            return XmlTypeResolverBuilder.encodeXmlClassName(super.idFromValue(value));
        }
        
        public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
            return super.typeFromId(context, XmlTypeResolverBuilder.decodeXmlClassName(id));
        }
    }
}
