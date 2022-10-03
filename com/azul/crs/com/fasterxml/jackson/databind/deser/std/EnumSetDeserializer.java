package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.util.AccessPattern;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.databind.deser.impl.NullsConstantProvider;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.EnumSet;

public class EnumSetDeserializer extends StdDeserializer<EnumSet<?>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JavaType _enumType;
    protected JsonDeserializer<Enum<?>> _enumDeserializer;
    protected final NullValueProvider _nullProvider;
    protected final boolean _skipNullValues;
    protected final Boolean _unwrapSingle;
    
    public EnumSetDeserializer(final JavaType enumType, final JsonDeserializer<?> deser) {
        super(EnumSet.class);
        this._enumType = enumType;
        if (!enumType.isEnumType()) {
            throw new IllegalArgumentException("Type " + enumType + " not Java Enum type");
        }
        this._enumDeserializer = (JsonDeserializer<Enum<?>>)deser;
        this._unwrapSingle = null;
        this._nullProvider = null;
        this._skipNullValues = false;
    }
    
    @Deprecated
    protected EnumSetDeserializer(final EnumSetDeserializer base, final JsonDeserializer<?> deser, final Boolean unwrapSingle) {
        this(base, deser, base._nullProvider, unwrapSingle);
    }
    
    protected EnumSetDeserializer(final EnumSetDeserializer base, final JsonDeserializer<?> deser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(base);
        this._enumType = base._enumType;
        this._enumDeserializer = (JsonDeserializer<Enum<?>>)deser;
        this._nullProvider = nuller;
        this._skipNullValues = NullsConstantProvider.isSkipper(nuller);
        this._unwrapSingle = unwrapSingle;
    }
    
    public EnumSetDeserializer withDeserializer(final JsonDeserializer<?> deser) {
        if (this._enumDeserializer == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, this._nullProvider, this._unwrapSingle);
    }
    
    @Deprecated
    public EnumSetDeserializer withResolved(final JsonDeserializer<?> deser, final Boolean unwrapSingle) {
        return this.withResolved(deser, this._nullProvider, unwrapSingle);
    }
    
    public EnumSetDeserializer withResolved(final JsonDeserializer<?> deser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        if (Objects.equals(this._unwrapSingle, unwrapSingle) && this._enumDeserializer == deser && this._nullProvider == deser) {
            return this;
        }
        return new EnumSetDeserializer(this, deser, nuller, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._enumType.getValueHandler() == null;
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }
    
    @Override
    public Boolean supportsUpdate(final DeserializationConfig config) {
        return Boolean.TRUE;
    }
    
    @Override
    public Object getEmptyValue(final DeserializationContext ctxt) throws JsonMappingException {
        return this.constructSet();
    }
    
    @Override
    public AccessPattern getEmptyAccessPattern() {
        return AccessPattern.DYNAMIC;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, EnumSet.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        JsonDeserializer<?> deser = this._enumDeserializer;
        if (deser == null) {
            deser = ctxt.findContextualValueDeserializer(this._enumType, property);
        }
        else {
            deser = ctxt.handleSecondaryContextualization(deser, property, this._enumType);
        }
        return this.withResolved(deser, this.findContentNullProvider(ctxt, property, deser), unwrapSingle);
    }
    
    @Override
    public EnumSet<?> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final EnumSet result = this.constructSet();
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        return this._deserialize(p, ctxt, result);
    }
    
    @Override
    public EnumSet<?> deserialize(final JsonParser p, final DeserializationContext ctxt, final EnumSet<?> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        return this._deserialize(p, ctxt, result);
    }
    
    protected final EnumSet<?> _deserialize(final JsonParser p, final DeserializationContext ctxt, final EnumSet result) throws IOException {
        try {
            JsonToken t;
            while ((t = p.nextToken()) != JsonToken.END_ARRAY) {
                Enum<?> value;
                if (t == JsonToken.VALUE_NULL) {
                    if (this._skipNullValues) {
                        continue;
                    }
                    value = (Enum)this._nullProvider.getNullValue(ctxt);
                }
                else {
                    value = this._enumDeserializer.deserialize(p, ctxt);
                }
                if (value != null) {
                    result.add(value);
                }
            }
        }
        catch (final Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    private EnumSet constructSet() {
        return EnumSet.noneOf(this._enumType.getRawClass());
    }
    
    protected EnumSet<?> handleNonArray(final JsonParser p, final DeserializationContext ctxt, final EnumSet result) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (!canWrap) {
            return (EnumSet)ctxt.handleUnexpectedToken(EnumSet.class, p);
        }
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return (EnumSet)ctxt.handleUnexpectedToken(this._enumType, p);
        }
        try {
            final Enum<?> value = this._enumDeserializer.deserialize(p, ctxt);
            if (value != null) {
                result.add(value);
            }
        }
        catch (final Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
        return result;
    }
}
