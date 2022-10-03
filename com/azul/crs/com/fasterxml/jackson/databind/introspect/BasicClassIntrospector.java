package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import com.azul.crs.com.fasterxml.jackson.databind.type.SimpleType;
import com.azul.crs.com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.SerializationConfig;
import java.io.Serializable;

public class BasicClassIntrospector extends ClassIntrospector implements Serializable
{
    private static final long serialVersionUID = 2L;
    private static final Class<?> CLS_OBJECT;
    private static final Class<?> CLS_STRING;
    private static final Class<?> CLS_JSON_NODE;
    protected static final BasicBeanDescription STRING_DESC;
    protected static final BasicBeanDescription BOOLEAN_DESC;
    protected static final BasicBeanDescription INT_DESC;
    protected static final BasicBeanDescription LONG_DESC;
    protected static final BasicBeanDescription OBJECT_DESC;
    
    @Override
    public ClassIntrospector copy() {
        return new BasicClassIntrospector();
    }
    
    @Override
    public BasicBeanDescription forSerialization(final SerializationConfig config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(config, type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forSerialization(this.collectProperties(config, type, r, true));
            }
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserialization(final DeserializationConfig config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(config, type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(this.collectProperties(config, type, r, false));
            }
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDeserializationWithBuilder(final DeserializationConfig config, final JavaType builderType, final MixInResolver r, final BeanDescription valueTypeDesc) {
        return BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(config, builderType, r, valueTypeDesc, false));
    }
    
    @Deprecated
    @Override
    public BasicBeanDescription forDeserializationWithBuilder(final DeserializationConfig config, final JavaType type, final MixInResolver r) {
        return BasicBeanDescription.forDeserialization(this.collectPropertiesWithBuilder(config, type, r, null, false));
    }
    
    @Override
    public BasicBeanDescription forCreation(final DeserializationConfig config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(config, type);
        if (desc == null) {
            desc = this._findStdJdkCollectionDesc(config, type);
            if (desc == null) {
                desc = BasicBeanDescription.forDeserialization(this.collectProperties(config, type, r, false));
            }
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forClassAnnotations(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(config, type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedClass(config, type, r));
        }
        return desc;
    }
    
    @Override
    public BasicBeanDescription forDirectClassAnnotations(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        BasicBeanDescription desc = this._findStdTypeDesc(config, type);
        if (desc == null) {
            desc = BasicBeanDescription.forOtherUse(config, type, this._resolveAnnotatedWithoutSuperTypes(config, type, r));
        }
        return desc;
    }
    
    protected POJOPropertiesCollector collectProperties(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final boolean forSerialization) {
        final AnnotatedClass classDef = this._resolveAnnotatedClass(config, type, r);
        final AccessorNamingStrategy accNaming = type.isRecordType() ? config.getAccessorNaming().forRecord(config, classDef) : config.getAccessorNaming().forPOJO(config, classDef);
        return this.constructPropertyCollector(config, classDef, type, forSerialization, accNaming);
    }
    
    protected POJOPropertiesCollector collectPropertiesWithBuilder(final MapperConfig<?> config, final JavaType type, final MixInResolver r, final BeanDescription valueTypeDesc, final boolean forSerialization) {
        final AnnotatedClass builderClassDef = this._resolveAnnotatedClass(config, type, r);
        final AccessorNamingStrategy accNaming = config.getAccessorNaming().forBuilder(config, builderClassDef, valueTypeDesc);
        return this.constructPropertyCollector(config, builderClassDef, type, forSerialization, accNaming);
    }
    
    protected POJOPropertiesCollector constructPropertyCollector(final MapperConfig<?> config, final AnnotatedClass classDef, final JavaType type, final boolean forSerialization, final AccessorNamingStrategy accNaming) {
        return new POJOPropertiesCollector(config, forSerialization, type, classDef, accNaming);
    }
    
    protected BasicBeanDescription _findStdTypeDesc(final MapperConfig<?> config, final JavaType type) {
        final Class<?> cls = type.getRawClass();
        if (cls.isPrimitive()) {
            if (cls == Integer.TYPE) {
                return BasicClassIntrospector.INT_DESC;
            }
            if (cls == Long.TYPE) {
                return BasicClassIntrospector.LONG_DESC;
            }
            if (cls == Boolean.TYPE) {
                return BasicClassIntrospector.BOOLEAN_DESC;
            }
        }
        else if (ClassUtil.isJDKClass(cls)) {
            if (cls == BasicClassIntrospector.CLS_OBJECT) {
                return BasicClassIntrospector.OBJECT_DESC;
            }
            if (cls == BasicClassIntrospector.CLS_STRING) {
                return BasicClassIntrospector.STRING_DESC;
            }
            if (cls == Integer.class) {
                return BasicClassIntrospector.INT_DESC;
            }
            if (cls == Long.class) {
                return BasicClassIntrospector.LONG_DESC;
            }
            if (cls == Boolean.class) {
                return BasicClassIntrospector.BOOLEAN_DESC;
            }
        }
        else if (BasicClassIntrospector.CLS_JSON_NODE.isAssignableFrom(cls)) {
            return BasicBeanDescription.forOtherUse(config, type, AnnotatedClassResolver.createPrimordial(cls));
        }
        return null;
    }
    
    protected boolean _isStdJDKCollection(final JavaType type) {
        if (!type.isContainerType() || type.isArrayType()) {
            return false;
        }
        final Class<?> raw = type.getRawClass();
        return ClassUtil.isJDKClass(raw) && (Collection.class.isAssignableFrom(raw) || Map.class.isAssignableFrom(raw));
    }
    
    protected BasicBeanDescription _findStdJdkCollectionDesc(final MapperConfig<?> cfg, final JavaType type) {
        if (this._isStdJDKCollection(type)) {
            return BasicBeanDescription.forOtherUse(cfg, type, this._resolveAnnotatedClass(cfg, type, cfg));
        }
        return null;
    }
    
    protected AnnotatedClass _resolveAnnotatedClass(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        return AnnotatedClassResolver.resolve(config, type, r);
    }
    
    protected AnnotatedClass _resolveAnnotatedWithoutSuperTypes(final MapperConfig<?> config, final JavaType type, final MixInResolver r) {
        return AnnotatedClassResolver.resolveWithoutSuperTypes(config, type, r);
    }
    
    static {
        CLS_OBJECT = Object.class;
        CLS_STRING = String.class;
        CLS_JSON_NODE = JsonNode.class;
        STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), AnnotatedClassResolver.createPrimordial(BasicClassIntrospector.CLS_STRING));
        BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), AnnotatedClassResolver.createPrimordial(Boolean.TYPE));
        INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), AnnotatedClassResolver.createPrimordial(Integer.TYPE));
        LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), AnnotatedClassResolver.createPrimordial(Long.TYPE));
        OBJECT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Object.class), AnnotatedClassResolver.createPrimordial(BasicClassIntrospector.CLS_OBJECT));
    }
}
