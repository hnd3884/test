package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyValueBuffer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.ObjectIdReader;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.MapperFeature;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.PropertyBasedCreator;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.EnumMap;

public class EnumMapDeserializer extends ContainerDeserializerBase<EnumMap<?, ?>> implements ContextualDeserializer, ResolvableDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final Class<?> _enumClass;
    protected KeyDeserializer _keyDeserializer;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected PropertyBasedCreator _propertyBasedCreator;
    
    public EnumMapDeserializer(final JavaType mapType, final ValueInstantiator valueInst, final KeyDeserializer keyDeser, final JsonDeserializer<?> valueDeser, final TypeDeserializer vtd, final NullValueProvider nuller) {
        super(mapType, nuller, null);
        this._enumClass = mapType.getKeyType().getRawClass();
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = (JsonDeserializer<Object>)valueDeser;
        this._valueTypeDeserializer = vtd;
        this._valueInstantiator = valueInst;
    }
    
    protected EnumMapDeserializer(final EnumMapDeserializer base, final KeyDeserializer keyDeser, final JsonDeserializer<?> valueDeser, final TypeDeserializer vtd, final NullValueProvider nuller) {
        super(base, nuller, base._unwrapSingle);
        this._enumClass = base._enumClass;
        this._keyDeserializer = keyDeser;
        this._valueDeserializer = (JsonDeserializer<Object>)valueDeser;
        this._valueTypeDeserializer = vtd;
        this._valueInstantiator = base._valueInstantiator;
        this._delegateDeserializer = base._delegateDeserializer;
        this._propertyBasedCreator = base._propertyBasedCreator;
    }
    
    @Deprecated
    public EnumMapDeserializer(final JavaType mapType, final KeyDeserializer keyDeser, final JsonDeserializer<?> valueDeser, final TypeDeserializer vtd) {
        this(mapType, null, keyDeser, valueDeser, vtd, null);
    }
    
    public EnumMapDeserializer withResolved(final KeyDeserializer keyDeserializer, final JsonDeserializer<?> valueDeserializer, final TypeDeserializer valueTypeDeser, final NullValueProvider nuller) {
        if (keyDeserializer == this._keyDeserializer && nuller == this._nullProvider && valueDeserializer == this._valueDeserializer && valueTypeDeser == this._valueTypeDeserializer) {
            return this;
        }
        return new EnumMapDeserializer(this, keyDeserializer, valueDeserializer, valueTypeDeser, nuller);
    }
    
    @Override
    public void resolve(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator != null) {
            if (this._valueInstantiator.canCreateUsingDelegate()) {
                final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
            }
            else if (this._valueInstantiator.canCreateUsingArrayDelegate()) {
                final JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
                if (delegateType == null) {
                    ctxt.reportBadDefinition(this._containerType, String.format("Invalid delegate-creator definition for %s: value instantiator (%s) returned true for 'canCreateUsingArrayDelegate()', but null for 'getArrayDelegateType()'", this._containerType, this._valueInstantiator.getClass().getName()));
                }
                this._delegateDeserializer = this.findDeserializer(ctxt, delegateType, null);
            }
            else if (this._valueInstantiator.canCreateFromObjectWith()) {
                final SettableBeanProperty[] creatorProps = this._valueInstantiator.getFromObjectArguments(ctxt.getConfig());
                this._propertyBasedCreator = PropertyBasedCreator.construct(ctxt, this._valueInstantiator, creatorProps, ctxt.isEnabled(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES));
            }
        }
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        KeyDeserializer keyDeser = this._keyDeserializer;
        if (keyDeser == null) {
            keyDeser = ctxt.findKeyDeserializer(this._containerType.getKeyType(), property);
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        final JavaType vt = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = ctxt.findContextualValueDeserializer(vt, property);
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, vt);
        }
        TypeDeserializer vtd = this._valueTypeDeserializer;
        if (vtd != null) {
            vtd = vtd.forProperty(property);
        }
        return this.withResolved(keyDeser, valueDeser, vtd, this.findContentNullProvider(ctxt, property, valueDeser));
    }
    
    @Override
    public boolean isCachable() {
        return this._valueDeserializer == null && this._keyDeserializer == null && this._valueTypeDeserializer == null;
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Map;
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        return this._valueDeserializer;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this.constructMap(ctxt);
    }
    
    @Override
    public EnumMap<?, ?> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingProperties(p, ctxt);
        }
        if (this._delegateDeserializer != null) {
            return (EnumMap)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        switch (p.currentTokenId()) {
            case 1:
            case 2:
            case 5: {
                return this.deserialize(p, ctxt, (EnumMap)this.constructMap(ctxt));
            }
            case 6: {
                return this._deserializeFromString(p, ctxt);
            }
            case 3: {
                return this._deserializeFromArray(p, ctxt);
            }
            default: {
                return (EnumMap)ctxt.handleUnexpectedToken(this.getValueType(ctxt), p);
            }
        }
    }
    
    @Override
    public EnumMap<?, ?> deserialize(final JsonParser p, final DeserializationContext ctxt, final EnumMap result) throws IOException {
        p.setCurrentValue(result);
        final JsonDeserializer<Object> valueDes = this._valueDeserializer;
        final TypeDeserializer typeDeser = this._valueTypeDeserializer;
        String keyStr;
        if (p.isExpectedStartObjectToken()) {
            keyStr = p.nextFieldName();
        }
        else {
            final JsonToken t = p.currentToken();
            if (t != JsonToken.FIELD_NAME) {
                if (t == JsonToken.END_OBJECT) {
                    return result;
                }
                ctxt.reportWrongTokenException(this, JsonToken.FIELD_NAME, null, new Object[0]);
            }
            keyStr = p.currentName();
        }
        while (keyStr != null) {
            final Enum<?> key = (Enum<?>)this._keyDeserializer.deserializeKey(keyStr, ctxt);
            final JsonToken t2 = p.nextToken();
            Label_0244: {
                if (key == null) {
                    if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                        return (EnumMap)ctxt.handleWeirdStringValue(this._enumClass, keyStr, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
                    }
                    p.skipChildren();
                }
                else {
                    Object value;
                    try {
                        if (t2 == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                break Label_0244;
                            }
                            value = this._nullProvider.getNullValue(ctxt);
                        }
                        else if (typeDeser == null) {
                            value = valueDes.deserialize(p, ctxt);
                        }
                        else {
                            value = valueDes.deserializeWithType(p, ctxt, typeDeser);
                        }
                    }
                    catch (final Exception e) {
                        return this.wrapAndThrow(e, result, keyStr);
                    }
                    result.put(key, value);
                }
            }
            keyStr = p.nextFieldName();
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromObject(p, ctxt);
    }
    
    protected EnumMap<?, ?> constructMap(final DeserializationContext ctxt) throws JsonMappingException {
        if (this._valueInstantiator == null) {
            return new EnumMap<Object, Object>(this._enumClass);
        }
        try {
            if (!this._valueInstantiator.canCreateUsingDefault()) {
                return (EnumMap)ctxt.handleMissingInstantiator(this.handledType(), this.getValueInstantiator(), null, "no default constructor found", new Object[0]);
            }
            return (EnumMap)this._valueInstantiator.createUsingDefault(ctxt);
        }
        catch (final IOException e) {
            return ClassUtil.throwAsMappingException(ctxt, e);
        }
    }
    
    public EnumMap<?, ?> _deserializeUsingProperties(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final PropertyBasedCreator creator = this._propertyBasedCreator;
        final PropertyValueBuffer buffer = creator.startBuilding(p, ctxt, null);
        String keyName;
        if (p.isExpectedStartObjectToken()) {
            keyName = p.nextFieldName();
        }
        else if (p.hasToken(JsonToken.FIELD_NAME)) {
            keyName = p.currentName();
        }
        else {
            keyName = null;
        }
        while (keyName != null) {
            final JsonToken t = p.nextToken();
            final SettableBeanProperty prop = creator.findCreatorProperty(keyName);
            Label_0318: {
                if (prop != null) {
                    if (buffer.assignParameter(prop, prop.deserialize(p, ctxt))) {
                        p.nextToken();
                        EnumMap<?, ?> result;
                        try {
                            result = (EnumMap)creator.build(ctxt, buffer);
                        }
                        catch (final Exception e) {
                            return this.wrapAndThrow(e, this._containerType.getRawClass(), keyName);
                        }
                        return this.deserialize(p, ctxt, (EnumMap)result);
                    }
                }
                else {
                    final Enum<?> key = (Enum<?>)this._keyDeserializer.deserializeKey(keyName, ctxt);
                    if (key == null) {
                        if (!ctxt.isEnabled(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)) {
                            return (EnumMap)ctxt.handleWeirdStringValue(this._enumClass, keyName, "value not one of declared Enum instance names for %s", this._containerType.getKeyType());
                        }
                        p.nextToken();
                        p.skipChildren();
                    }
                    else {
                        Object value;
                        try {
                            if (t == JsonToken.VALUE_NULL) {
                                if (this._skipNullValues) {
                                    break Label_0318;
                                }
                                value = this._nullProvider.getNullValue(ctxt);
                            }
                            else if (this._valueTypeDeserializer == null) {
                                value = this._valueDeserializer.deserialize(p, ctxt);
                            }
                            else {
                                value = this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
                            }
                        }
                        catch (final Exception e2) {
                            ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e2, this._containerType.getRawClass(), keyName);
                            return null;
                        }
                        buffer.bufferMapProperty(key, value);
                    }
                }
            }
            keyName = p.nextFieldName();
        }
        try {
            return (EnumMap)creator.build(ctxt, buffer);
        }
        catch (final Exception e3) {
            ((ContainerDeserializerBase<Object>)this).wrapAndThrow(e3, this._containerType.getRawClass(), keyName);
            return null;
        }
    }
}
