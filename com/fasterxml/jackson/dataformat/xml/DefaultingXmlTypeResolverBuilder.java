package com.fasterxml.jackson.dataformat.xml;

import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import java.util.Collection;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.dataformat.xml.util.StaxUtil;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import java.io.Serializable;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DefaultingXmlTypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    public DefaultingXmlTypeResolverBuilder(final ObjectMapper.DefaultTyping t, final PolymorphicTypeValidator ptv) {
        super(t, ptv);
    }
    
    public StdTypeResolverBuilder init(final JsonTypeInfo.Id idType, final TypeIdResolver idRes) {
        super.init(idType, idRes);
        if (this._typeProperty != null) {
            this._typeProperty = StaxUtil.sanitizeXmlTypeName(this._typeProperty);
        }
        return (StdTypeResolverBuilder)this;
    }
    
    public StdTypeResolverBuilder typeProperty(String typeIdPropName) {
        if (typeIdPropName == null || typeIdPropName.length() == 0) {
            typeIdPropName = this._idType.getDefaultPropertyName();
        }
        this._typeProperty = StaxUtil.sanitizeXmlTypeName(typeIdPropName);
        return (StdTypeResolverBuilder)this;
    }
    
    protected TypeIdResolver idResolver(final MapperConfig<?> config, final JavaType baseType, final PolymorphicTypeValidator subtypeValidator, final Collection<NamedType> subtypes, final boolean forSer, final boolean forDeser) {
        if (this._customIdResolver != null) {
            return this._customIdResolver;
        }
        switch (this._idType) {
            case CLASS: {
                return (TypeIdResolver)new XmlTypeResolverBuilder.XmlClassNameIdResolver(baseType, config.getTypeFactory(), this.subTypeValidator((MapperConfig)config));
            }
            case MINIMAL_CLASS: {
                return (TypeIdResolver)new XmlTypeResolverBuilder.XmlMinimalClassNameIdResolver(baseType, config.getTypeFactory(), this.subTypeValidator((MapperConfig)config));
            }
            default: {
                return super.idResolver((MapperConfig)config, baseType, subtypeValidator, (Collection)subtypes, forSer, forDeser);
            }
        }
    }
}
