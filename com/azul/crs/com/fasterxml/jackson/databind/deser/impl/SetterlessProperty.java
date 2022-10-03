package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.PropertyName;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.util.Annotations;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import java.lang.reflect.Method;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;

public final class SetterlessProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedMethod _annotated;
    protected final Method _getter;
    
    public SetterlessProperty(final BeanPropertyDefinition propDef, final JavaType type, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedMethod method) {
        super(propDef, type, typeDeser, contextAnnotations);
        this._annotated = method;
        this._getter = method.getAnnotated();
    }
    
    protected SetterlessProperty(final SetterlessProperty src, final JsonDeserializer<?> deser, final NullValueProvider nva) {
        super(src, deser, nva);
        this._annotated = src._annotated;
        this._getter = src._getter;
    }
    
    protected SetterlessProperty(final SetterlessProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._getter = src._getter;
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new SetterlessProperty(this, newName);
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        final NullValueProvider nvp = (this._valueDeserializer == this._nullProvider) ? deser : this._nullProvider;
        return new SetterlessProperty(this, deser, nvp);
    }
    
    @Override
    public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
        return new SetterlessProperty(this, this._valueDeserializer, nva);
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        this._annotated.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        return this._annotated.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._annotated;
    }
    
    @Override
    public final void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return;
        }
        if (this._valueTypeDeserializer != null) {
            ctxt.reportBadDefinition(this.getType(), String.format("Problem deserializing 'setterless' property (\"%s\"): no way to handle typed deser with setterless yet", this.getName()));
        }
        Object toModify;
        try {
            toModify = this._getter.invoke(instance, (Object[])null);
        }
        catch (final Exception e) {
            this._throwAsIOE(p, e);
            return;
        }
        if (toModify == null) {
            ctxt.reportBadDefinition(this.getType(), String.format("Problem deserializing 'setterless' property '%s': get method returned null", this.getName()));
        }
        this._valueDeserializer.deserialize(p, ctxt, toModify);
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        this.deserializeAndSet(p, ctxt, instance);
        return instance;
    }
    
    @Override
    public final void set(final Object instance, final Object value) throws IOException {
        throw new UnsupportedOperationException("Should never call `set()` on setterless property ('" + this.getName() + "')");
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        this.set(instance, value);
        return instance;
    }
}
