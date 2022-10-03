package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import java.lang.annotation.Annotation;
import java.io.IOException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

public class CreatorProperty extends SettableBeanProperty
{
    private static final long serialVersionUID = 1L;
    protected final AnnotatedParameter _annotated;
    protected final JacksonInject.Value _injectableValue;
    protected SettableBeanProperty _fallbackSetter;
    protected final int _creatorIndex;
    protected boolean _ignorable;
    
    protected CreatorProperty(final PropertyName name, final JavaType type, final PropertyName wrapperName, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final JacksonInject.Value injectable, final PropertyMetadata metadata) {
        super(name, type, wrapperName, typeDeser, contextAnnotations, metadata);
        this._annotated = param;
        this._creatorIndex = index;
        this._injectableValue = injectable;
        this._fallbackSetter = null;
    }
    
    @Deprecated
    public CreatorProperty(final PropertyName name, final JavaType type, final PropertyName wrapperName, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final Object injectableValueId, final PropertyMetadata metadata) {
        this(name, type, wrapperName, typeDeser, contextAnnotations, param, index, (injectableValueId == null) ? null : JacksonInject.Value.construct(injectableValueId, (Boolean)null), metadata);
    }
    
    public static CreatorProperty construct(final PropertyName name, final JavaType type, final PropertyName wrapperName, final TypeDeserializer typeDeser, final Annotations contextAnnotations, final AnnotatedParameter param, final int index, final JacksonInject.Value injectable, final PropertyMetadata metadata) {
        return new CreatorProperty(name, type, wrapperName, typeDeser, contextAnnotations, param, index, injectable, metadata);
    }
    
    protected CreatorProperty(final CreatorProperty src, final PropertyName newName) {
        super(src, newName);
        this._annotated = src._annotated;
        this._injectableValue = src._injectableValue;
        this._fallbackSetter = src._fallbackSetter;
        this._creatorIndex = src._creatorIndex;
        this._ignorable = src._ignorable;
    }
    
    protected CreatorProperty(final CreatorProperty src, final JsonDeserializer<?> deser, final NullValueProvider nva) {
        super(src, deser, nva);
        this._annotated = src._annotated;
        this._injectableValue = src._injectableValue;
        this._fallbackSetter = src._fallbackSetter;
        this._creatorIndex = src._creatorIndex;
        this._ignorable = src._ignorable;
    }
    
    @Override
    public SettableBeanProperty withName(final PropertyName newName) {
        return new CreatorProperty(this, newName);
    }
    
    @Override
    public SettableBeanProperty withValueDeserializer(final JsonDeserializer<?> deser) {
        if (this._valueDeserializer == deser) {
            return this;
        }
        final NullValueProvider nvp = (this._valueDeserializer == this._nullProvider) ? deser : this._nullProvider;
        return new CreatorProperty(this, deser, nvp);
    }
    
    @Override
    public SettableBeanProperty withNullProvider(final NullValueProvider nva) {
        return new CreatorProperty(this, this._valueDeserializer, nva);
    }
    
    @Override
    public void fixAccess(final DeserializationConfig config) {
        if (this._fallbackSetter != null) {
            this._fallbackSetter.fixAccess(config);
        }
    }
    
    public void setFallbackSetter(final SettableBeanProperty fallbackSetter) {
        this._fallbackSetter = fallbackSetter;
    }
    
    @Override
    public void markAsIgnorable() {
        this._ignorable = true;
    }
    
    @Override
    public boolean isIgnorable() {
        return this._ignorable;
    }
    
    @Deprecated
    public Object findInjectableValue(final DeserializationContext context, final Object beanInstance) throws JsonMappingException {
        if (this._injectableValue == null) {
            context.reportBadDefinition(ClassUtil.classOf(beanInstance), String.format("Property '%s' (type %s) has no injectable value id configured", this.getName(), this.getClass().getName()));
        }
        return context.findInjectableValue(this._injectableValue.getId(), this, beanInstance);
    }
    
    @Deprecated
    public void inject(final DeserializationContext context, final Object beanInstance) throws IOException {
        this.set(beanInstance, this.findInjectableValue(context, beanInstance));
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> acls) {
        if (this._annotated == null) {
            return null;
        }
        return this._annotated.getAnnotation(acls);
    }
    
    @Override
    public AnnotatedMember getMember() {
        return this._annotated;
    }
    
    @Override
    public int getCreatorIndex() {
        return this._creatorIndex;
    }
    
    @Override
    public void deserializeAndSet(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        this._verifySetter();
        this._fallbackSetter.set(instance, this.deserialize(p, ctxt));
    }
    
    @Override
    public Object deserializeSetAndReturn(final JsonParser p, final DeserializationContext ctxt, final Object instance) throws IOException {
        this._verifySetter();
        return this._fallbackSetter.setAndReturn(instance, this.deserialize(p, ctxt));
    }
    
    @Override
    public void set(final Object instance, final Object value) throws IOException {
        this._verifySetter();
        this._fallbackSetter.set(instance, value);
    }
    
    @Override
    public Object setAndReturn(final Object instance, final Object value) throws IOException {
        this._verifySetter();
        return this._fallbackSetter.setAndReturn(instance, value);
    }
    
    @Override
    public PropertyMetadata getMetadata() {
        final PropertyMetadata md = super.getMetadata();
        if (this._fallbackSetter != null) {
            return md.withMergeInfo(this._fallbackSetter.getMetadata().getMergeInfo());
        }
        return md;
    }
    
    @Override
    public Object getInjectableValueId() {
        return (this._injectableValue == null) ? null : this._injectableValue.getId();
    }
    
    @Override
    public boolean isInjectionOnly() {
        return this._injectableValue != null && !this._injectableValue.willUseInput(true);
    }
    
    @Override
    public String toString() {
        return "[creator property, name '" + this.getName() + "'; inject id '" + this.getInjectableValueId() + "']";
    }
    
    private final void _verifySetter() throws IOException {
        if (this._fallbackSetter == null) {
            this._reportMissingSetter(null, null);
        }
    }
    
    private void _reportMissingSetter(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final String msg = "No fallback setter/field defined for creator property '" + this.getName() + "'";
        if (ctxt != null) {
            ctxt.reportBadDefinition(this.getType(), msg);
            return;
        }
        throw InvalidDefinitionException.from(p, msg, this.getType());
    }
}
