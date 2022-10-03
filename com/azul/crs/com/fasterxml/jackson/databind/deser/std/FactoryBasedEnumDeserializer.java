package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;

class FactoryBasedEnumDeserializer extends StdDeserializer<Object> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _inputType;
    protected final boolean _hasArgs;
    protected final AnnotatedMethod _factory;
    protected final JsonDeserializer<?> _deser;
    protected final ValueInstantiator _valueInstantiator;
    protected final SettableBeanProperty[] _creatorProps;
    private transient PropertyBasedCreator _propCreator;
    
    public FactoryBasedEnumDeserializer(final Class<?> cls, final AnnotatedMethod f, final JavaType paramType, final ValueInstantiator valueInstantiator, final SettableBeanProperty[] creatorProps) {
        super(cls);
        this._factory = f;
        this._hasArgs = true;
        this._inputType = (paramType.hasRawClass(String.class) ? null : paramType);
        this._deser = null;
        this._valueInstantiator = valueInstantiator;
        this._creatorProps = creatorProps;
    }
    
    public FactoryBasedEnumDeserializer(final Class<?> cls, final AnnotatedMethod f) {
        super(cls);
        this._factory = f;
        this._hasArgs = false;
        this._inputType = null;
        this._deser = null;
        this._valueInstantiator = null;
        this._creatorProps = null;
    }
    
    protected FactoryBasedEnumDeserializer(final FactoryBasedEnumDeserializer base, final JsonDeserializer<?> deser) {
        super(base._valueClass);
        this._inputType = base._inputType;
        this._factory = base._factory;
        this._hasArgs = base._hasArgs;
        this._valueInstantiator = base._valueInstantiator;
        this._creatorProps = base._creatorProps;
        this._deser = deser;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        if (this._deser == null && this._inputType != null && this._creatorProps == null) {
            return new FactoryBasedEnumDeserializer(this, ctxt.findContextualValueDeserializer(this._inputType, property));
        }
        return this;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.FALSE;
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Enum;
    }
    
    @Override
    public boolean isCachable() {
        return true;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        Object value;
        if (this._deser != null) {
            value = this._deser.deserialize(p, ctxt);
        }
        else if (this._hasArgs) {
            final JsonToken curr = p.currentToken();
            if (this._creatorProps != null) {
                if (!p.isExpectedStartObjectToken()) {
                    final JavaType targetType = this.getValueType(ctxt);
                    ctxt.reportInputMismatch(targetType, "Input mismatch reading Enum %s: properties-based `@JsonCreator` (%s) expects JSON Object (JsonToken.START_OBJECT), got JsonToken.%s", ClassUtil.getTypeDescription(targetType), this._factory, p.currentToken());
                }
                if (this._propCreator == null) {
                    this._propCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, this._creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
                }
                p.nextToken();
                return this.deserializeEnumUsingPropertyBased(p, ctxt, this._propCreator);
            }
            if (curr == JsonToken.VALUE_STRING || curr == JsonToken.FIELD_NAME) {
                value = p.getText();
            }
            else if (curr == JsonToken.VALUE_NUMBER_INT) {
                value = p.getNumberValue();
            }
            else {
                value = p.getValueAsString();
            }
        }
        else {
            p.skipChildren();
            try {
                return this._factory.call();
            }
            catch (final Exception e) {
                final Throwable t = ClassUtil.throwRootCauseIfIOE(e);
                return ctxt.handleInstantiationProblem(this._valueClass, null, t);
            }
        }
        try {
            return this._factory.callOnWith(this._valueClass, value);
        }
        catch (final Exception e) {
            final Throwable t = ClassUtil.throwRootCauseIfIOE(e);
            if (ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL) && t instanceof IllegalArgumentException) {
                return null;
            }
            return ctxt.handleInstantiationProblem(this._valueClass, value, t);
        }
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        if (this._deser == null) {
            return this.deserialize(p, ctxt);
        }
        return typeDeserializer.deserializeTypedFromAny(p, ctxt);
    }
    
    protected Object deserializeEnumUsingPropertyBased(final JsonParser p, final DeserializationContext ctxt, final PropertyBasedCreator creator) throws IOException {
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
        for (JsonToken t = p.currentToken(); t == JsonToken.FIELD_NAME; t = p.nextToken()) {
            final String propName = p.currentName();
            p.nextToken();
            final SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
            if (!buffer.readIdProperty(propName) || creatorProp != null) {
                if (creatorProp != null) {
                    buffer.assignParameter(creatorProp, this._deserializeWithErrorWrapping(p, ctxt, creatorProp));
                }
            }
        }
        return creator.build(ctxt, buffer);
    }
    
    protected final Object _deserializeWithErrorWrapping(final JsonParser p, final DeserializationContext ctxt, final SettableBeanProperty prop) throws IOException {
        try {
            return prop.deserialize(p, ctxt);
        }
        catch (final Exception e) {
            return this.wrapAndThrow(e, this.handledType(), prop.getName(), ctxt);
        }
    }
    
    protected Object wrapAndThrow(final Throwable t, final Object bean, final String fieldName, final DeserializationContext ctxt) throws IOException {
        throw JsonMappingException.wrapWithPath(this.throwOrReturnThrowable(t, ctxt), bean, fieldName);
    }
    
    private Throwable throwOrReturnThrowable(Throwable t, final DeserializationContext ctxt) throws IOException {
        t = ClassUtil.getRootCause(t);
        ClassUtil.throwIfError(t);
        final boolean wrap = ctxt == null || ctxt.isEnabled(DeserializationFeature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonProcessingException)) {
                throw (IOException)t;
            }
        }
        else if (!wrap) {
            ClassUtil.throwIfRTE(t);
        }
        return t;
    }
}
