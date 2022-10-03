package com.azul.crs.com.fasterxml.jackson.databind.deser.std;

import com.azul.crs.com.fasterxml.jackson.core.JsonProcessingException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.azul.crs.com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.core.JsonParser;
import com.azul.crs.com.fasterxml.jackson.databind.JsonMappingException;
import com.azul.crs.com.fasterxml.jackson.databind.introspect.AnnotatedWithParams;
import com.azul.crs.com.fasterxml.jackson.annotation.JsonFormat;
import com.azul.crs.com.fasterxml.jackson.databind.BeanProperty;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import com.azul.crs.com.fasterxml.jackson.databind.type.LogicalType;
import java.util.Objects;
import com.azul.crs.com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import java.util.Collection;

@JacksonStdImpl
public final class StringCollectionDeserializer extends ContainerDeserializerBase<Collection<String>> implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final JsonDeserializer<String> _valueDeserializer;
    protected final ValueInstantiator _valueInstantiator;
    protected final JsonDeserializer<Object> _delegateDeserializer;
    
    public StringCollectionDeserializer(final JavaType collectionType, final JsonDeserializer<?> valueDeser, final ValueInstantiator valueInstantiator) {
        this(collectionType, valueInstantiator, null, valueDeser, valueDeser, null);
    }
    
    protected StringCollectionDeserializer(final JavaType collectionType, final ValueInstantiator valueInstantiator, final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        super(collectionType, nuller, unwrapSingle);
        this._valueDeserializer = (JsonDeserializer<String>)valueDeser;
        this._valueInstantiator = valueInstantiator;
        this._delegateDeserializer = (JsonDeserializer<Object>)delegateDeser;
    }
    
    protected StringCollectionDeserializer withResolved(final JsonDeserializer<?> delegateDeser, final JsonDeserializer<?> valueDeser, final NullValueProvider nuller, final Boolean unwrapSingle) {
        if (Objects.equals(this._unwrapSingle, unwrapSingle) && this._nullProvider == nuller && this._valueDeserializer == valueDeser && this._delegateDeserializer == delegateDeser) {
            return this;
        }
        return new StringCollectionDeserializer(this._containerType, this._valueInstantiator, delegateDeser, valueDeser, nuller, unwrapSingle);
    }
    
    @Override
    public boolean isCachable() {
        return this._valueDeserializer == null && this._delegateDeserializer == null;
    }
    
    @Override
    public LogicalType logicalType() {
        return LogicalType.Collection;
    }
    
    @Override
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JsonDeserializer<Object> delegate = null;
        if (this._valueInstantiator != null) {
            AnnotatedWithParams delegateCreator = this._valueInstantiator.getArrayDelegateCreator();
            if (delegateCreator != null) {
                final JavaType delegateType = this._valueInstantiator.getArrayDelegateType(ctxt.getConfig());
                delegate = this.findDeserializer(ctxt, delegateType, property);
            }
            else if ((delegateCreator = this._valueInstantiator.getDelegateCreator()) != null) {
                final JavaType delegateType = this._valueInstantiator.getDelegateType(ctxt.getConfig());
                delegate = this.findDeserializer(ctxt, delegateType, property);
            }
        }
        JsonDeserializer<?> valueDeser = this._valueDeserializer;
        final JavaType valueType = this._containerType.getContentType();
        if (valueDeser == null) {
            valueDeser = this.findConvertingContentDeserializer(ctxt, property, valueDeser);
            if (valueDeser == null) {
                valueDeser = ctxt.findContextualValueDeserializer(valueType, property);
            }
        }
        else {
            valueDeser = ctxt.handleSecondaryContextualization(valueDeser, property, valueType);
        }
        final Boolean unwrapSingle = this.findFormatFeature(ctxt, property, Collection.class, JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        final NullValueProvider nuller = this.findContentNullProvider(ctxt, property, valueDeser);
        if (this.isDefaultDeserializer(valueDeser)) {
            valueDeser = null;
        }
        return this.withResolved(delegate, valueDeser, nuller, unwrapSingle);
    }
    
    @Override
    public JsonDeserializer<Object> getContentDeserializer() {
        final JsonDeserializer<?> deser = this._valueDeserializer;
        return (JsonDeserializer<Object>)deser;
    }
    
    @Override
    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (this._delegateDeserializer != null) {
            return (Collection)this._valueInstantiator.createUsingDelegate(ctxt, this._delegateDeserializer.deserialize(p, ctxt));
        }
        final Collection<String> result = (Collection<String>)this._valueInstantiator.createUsingDefault(ctxt);
        return this.deserialize(p, ctxt, result);
    }
    
    @Override
    public Collection<String> deserialize(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        if (!p.isExpectedStartArrayToken()) {
            return this.handleNonArray(p, ctxt, result);
        }
        if (this._valueDeserializer != null) {
            return this.deserializeUsingCustom(p, ctxt, result, this._valueDeserializer);
        }
        Label_0034: {
            break Label_0034;
            try {
                while (true) {
                    String value = p.nextTextValue();
                    if (value != null) {
                        result.add(value);
                    }
                    else {
                        final JsonToken t = p.currentToken();
                        if (t == JsonToken.END_ARRAY) {
                            break;
                        }
                        if (t == JsonToken.VALUE_NULL) {
                            if (this._skipNullValues) {
                                continue;
                            }
                            value = (String)this._nullProvider.getNullValue(ctxt);
                        }
                        else {
                            value = this._parseString(p, ctxt);
                        }
                        result.add(value);
                    }
                }
            }
            catch (final Exception e) {
                throw JsonMappingException.wrapWithPath(e, result, result.size());
            }
        }
        return result;
    }
    
    private Collection<String> deserializeUsingCustom(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result, final JsonDeserializer<String> deser) throws IOException {
        try {
            while (true) {
                String value;
                if (p.nextTextValue() == null) {
                    final JsonToken t = p.currentToken();
                    if (t == JsonToken.END_ARRAY) {
                        break;
                    }
                    if (t == JsonToken.VALUE_NULL) {
                        if (this._skipNullValues) {
                            continue;
                        }
                        value = (String)this._nullProvider.getNullValue(ctxt);
                    }
                    else {
                        value = deser.deserialize(p, ctxt);
                    }
                }
                else {
                    value = deser.deserialize(p, ctxt);
                }
                result.add(value);
            }
        }
        catch (final Exception e) {
            throw JsonMappingException.wrapWithPath(e, result, result.size());
        }
        return result;
    }
    
    @Override
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return typeDeserializer.deserializeTypedFromArray(p, ctxt);
    }
    
    private final Collection<String> handleNonArray(final JsonParser p, final DeserializationContext ctxt, final Collection<String> result) throws IOException {
        final boolean canWrap = this._unwrapSingle == Boolean.TRUE || (this._unwrapSingle == null && ctxt.isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
        if (canWrap) {
            final JsonDeserializer<String> valueDes = this._valueDeserializer;
            final JsonToken t = p.currentToken();
            String value;
            if (t == JsonToken.VALUE_NULL) {
                if (this._skipNullValues) {
                    return result;
                }
                value = (String)this._nullProvider.getNullValue(ctxt);
            }
            else {
                try {
                    value = ((valueDes == null) ? this._parseString(p, ctxt) : valueDes.deserialize(p, ctxt));
                }
                catch (final Exception e) {
                    throw JsonMappingException.wrapWithPath(e, result, result.size());
                }
            }
            result.add(value);
            return result;
        }
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return this._deserializeFromString(p, ctxt);
        }
        return (Collection)ctxt.handleUnexpectedToken(this._containerType, p);
    }
}
