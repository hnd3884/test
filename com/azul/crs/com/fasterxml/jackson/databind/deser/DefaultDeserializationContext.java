package com.azul.crs.com.fasterxml.jackson.databind.deser;

import com.azul.crs.com.fasterxml.jackson.databind.PropertyName;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.Annotated;
import java.util.Map;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import java.util.Iterator;
import java.util.ArrayList;
import com.azul.crs.com.fasterxml.jackson.databind.InjectableValues;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdResolver;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.azul.crs.com.fasterxml.jackson.annotation.ObjectIdGenerator;
import java.util.LinkedHashMap;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;

public abstract class DefaultDeserializationContext extends DeserializationContext implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected transient LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId> _objectIds;
    private List<ObjectIdResolver> _objectIdResolvers;
    
    protected DefaultDeserializationContext(final DeserializerFactory df, final DeserializerCache cache) {
        super(df, cache);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src, final DeserializationConfig config, final JsonParser p, final InjectableValues values) {
        super(src, config, p, values);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src, final DeserializationConfig config) {
        super(src, config);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src, final DeserializerFactory factory) {
        super(src, factory);
    }
    
    protected DefaultDeserializationContext(final DefaultDeserializationContext src) {
        super(src);
    }
    
    public DefaultDeserializationContext copy() {
        throw new IllegalStateException("DefaultDeserializationContext sub-class not overriding copy()");
    }
    
    @Override
    public ReadableObjectId findObjectId(final Object id, final ObjectIdGenerator<?> gen, final ObjectIdResolver resolverType) {
        if (id == null) {
            return null;
        }
        final ObjectIdGenerator.IdKey key = gen.key(id);
        if (this._objectIds == null) {
            this._objectIds = new LinkedHashMap<ObjectIdGenerator.IdKey, ReadableObjectId>();
        }
        else {
            final ReadableObjectId entry = this._objectIds.get(key);
            if (entry != null) {
                return entry;
            }
        }
        ObjectIdResolver resolver = null;
        if (this._objectIdResolvers == null) {
            this._objectIdResolvers = new ArrayList<ObjectIdResolver>(8);
        }
        else {
            for (final ObjectIdResolver res : this._objectIdResolvers) {
                if (res.canUseFor(resolverType)) {
                    resolver = res;
                    break;
                }
            }
        }
        if (resolver == null) {
            resolver = resolverType.newForDeserialization(this);
            this._objectIdResolvers.add(resolver);
        }
        final ReadableObjectId entry2 = this.createReadableObjectId(key);
        entry2.setResolver(resolver);
        this._objectIds.put(key, entry2);
        return entry2;
    }
    
    protected ReadableObjectId createReadableObjectId(final ObjectIdGenerator.IdKey key) {
        return new ReadableObjectId(key);
    }
    
    @Override
    public void checkUnresolvedObjectId() throws UnresolvedForwardReference {
        if (this._objectIds == null) {
            return;
        }
        if (!this.isEnabled(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS)) {
            return;
        }
        UnresolvedForwardReference exception = null;
        for (final Map.Entry<ObjectIdGenerator.IdKey, ReadableObjectId> entry : this._objectIds.entrySet()) {
            final ReadableObjectId roid = entry.getValue();
            if (!roid.hasReferringProperties()) {
                continue;
            }
            if (this.tryToResolveUnresolvedObjectId(roid)) {
                continue;
            }
            if (exception == null) {
                exception = new UnresolvedForwardReference(this.getParser(), "Unresolved forward references for: ");
            }
            final Object key = roid.getKey().key;
            final Iterator<ReadableObjectId.Referring> iterator = roid.referringProperties();
            while (iterator.hasNext()) {
                final ReadableObjectId.Referring referring = iterator.next();
                exception.addUnresolvedId(key, referring.getBeanType(), referring.getLocation());
            }
        }
        if (exception != null) {
            throw exception;
        }
    }
    
    protected boolean tryToResolveUnresolvedObjectId(final ReadableObjectId roid) {
        return roid.tryToResolveUnresolved(this);
    }
    
    @Override
    public JsonDeserializer<Object> deserializerInstance(final Annotated ann, final Object deserDef) throws JsonMappingException {
        if (deserDef == null) {
            return null;
        }
        JsonDeserializer<?> deser;
        if (deserDef instanceof JsonDeserializer) {
            deser = (JsonDeserializer)deserDef;
        }
        else {
            if (!(deserDef instanceof Class)) {
                throw new IllegalStateException("AnnotationIntrospector returned deserializer definition of type " + deserDef.getClass().getName() + "; expected type JsonDeserializer or Class<JsonDeserializer> instead");
            }
            final Class<?> deserClass = (Class<?>)deserDef;
            if (deserClass == JsonDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!JsonDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<JsonDeserializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = ((hi == null) ? null : hi.deserializerInstance(this._config, ann, deserClass));
            if (deser == null) {
                deser = ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
        }
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public final KeyDeserializer keyDeserializerInstance(final Annotated ann, final Object deserDef) throws JsonMappingException {
        if (deserDef == null) {
            return null;
        }
        KeyDeserializer deser;
        if (deserDef instanceof KeyDeserializer) {
            deser = (KeyDeserializer)deserDef;
        }
        else {
            if (!(deserDef instanceof Class)) {
                throw new IllegalStateException("AnnotationIntrospector returned key deserializer definition of type " + deserDef.getClass().getName() + "; expected type KeyDeserializer or Class<KeyDeserializer> instead");
            }
            final Class<?> deserClass = (Class<?>)deserDef;
            if (deserClass == KeyDeserializer.None.class || ClassUtil.isBogusClass(deserClass)) {
                return null;
            }
            if (!KeyDeserializer.class.isAssignableFrom(deserClass)) {
                throw new IllegalStateException("AnnotationIntrospector returned Class " + deserClass.getName() + "; expected Class<KeyDeserializer>");
            }
            final HandlerInstantiator hi = this._config.getHandlerInstantiator();
            deser = ((hi == null) ? null : hi.keyDeserializerInstance(this._config, ann, deserClass));
            if (deser == null) {
                deser = ClassUtil.createInstance(deserClass, this._config.canOverrideAccessModifiers());
            }
        }
        if (deser instanceof ResolvableDeserializer) {
            ((ResolvableDeserializer)deser).resolve(this);
        }
        return deser;
    }
    
    public abstract DefaultDeserializationContext with(final DeserializerFactory p0);
    
    public abstract DefaultDeserializationContext createInstance(final DeserializationConfig p0, final JsonParser p1, final InjectableValues p2);
    
    public abstract DefaultDeserializationContext createDummyInstance(final DeserializationConfig p0);
    
    public Object readRootValue(final JsonParser p, final JavaType valueType, final JsonDeserializer<Object> deser, final Object valueToUpdate) throws IOException {
        if (this._config.useRootWrapping()) {
            return this._unwrapAndDeserialize(p, valueType, deser, valueToUpdate);
        }
        if (valueToUpdate == null) {
            return deser.deserialize(p, this);
        }
        return deser.deserialize(p, this, valueToUpdate);
    }
    
    protected Object _unwrapAndDeserialize(final JsonParser p, final JavaType rootType, final JsonDeserializer<Object> deser, final Object valueToUpdate) throws IOException {
        final PropertyName expRootName = this._config.findRootName(rootType);
        final String expSimpleName = expRootName.getSimpleName();
        if (p.currentToken() != JsonToken.START_OBJECT) {
            this.reportWrongTokenException(rootType, JsonToken.START_OBJECT, "Current token not START_OBJECT (needed to unwrap root name %s), but %s", ClassUtil.name(expSimpleName), p.currentToken());
        }
        if (p.nextToken() != JsonToken.FIELD_NAME) {
            this.reportWrongTokenException(rootType, JsonToken.FIELD_NAME, "Current token not FIELD_NAME (to contain expected root name %s), but %s", ClassUtil.name(expSimpleName), p.currentToken());
        }
        final String actualName = p.currentName();
        if (!expSimpleName.equals(actualName)) {
            this.reportPropertyInputMismatch(rootType, actualName, "Root name (%s) does not match expected (%s) for type %s", ClassUtil.name(actualName), ClassUtil.name(expSimpleName), ClassUtil.getTypeDescription(rootType));
        }
        p.nextToken();
        Object result;
        if (valueToUpdate == null) {
            result = deser.deserialize(p, this);
        }
        else {
            result = deser.deserialize(p, this, valueToUpdate);
        }
        if (p.nextToken() != JsonToken.END_OBJECT) {
            this.reportWrongTokenException(rootType, JsonToken.END_OBJECT, "Current token not END_OBJECT (to match wrapper object with root name %s), but %s", ClassUtil.name(expSimpleName), p.currentToken());
        }
        return result;
    }
    
    public static final class Impl extends DefaultDeserializationContext
    {
        private static final long serialVersionUID = 1L;
        
        public Impl(final DeserializerFactory df) {
            super(df, null);
        }
        
        private Impl(final Impl src, final DeserializationConfig config, final JsonParser p, final InjectableValues values) {
            super(src, config, p, values);
        }
        
        private Impl(final Impl src) {
            super(src);
        }
        
        private Impl(final Impl src, final DeserializerFactory factory) {
            super(src, factory);
        }
        
        private Impl(final Impl src, final DeserializationConfig config) {
            super(src, config);
        }
        
        @Override
        public DefaultDeserializationContext copy() {
            ClassUtil.verifyMustOverride(Impl.class, this, "copy");
            return new Impl(this);
        }
        
        @Override
        public DefaultDeserializationContext createInstance(final DeserializationConfig config, final JsonParser p, final InjectableValues values) {
            return new Impl(this, config, p, values);
        }
        
        @Override
        public DefaultDeserializationContext createDummyInstance(final DeserializationConfig config) {
            return new Impl(this, config);
        }
        
        @Override
        public DefaultDeserializationContext with(final DeserializerFactory factory) {
            return new Impl(this, factory);
        }
    }
}
