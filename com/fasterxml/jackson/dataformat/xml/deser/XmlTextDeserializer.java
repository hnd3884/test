package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;

public class XmlTextDeserializer extends DelegatingDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final int _xmlTextPropertyIndex;
    protected final SettableBeanProperty _xmlTextProperty;
    protected final ValueInstantiator _valueInstantiator;
    
    public XmlTextDeserializer(final BeanDeserializerBase delegate, final SettableBeanProperty prop) {
        super((JsonDeserializer)delegate);
        this._xmlTextProperty = prop;
        this._xmlTextPropertyIndex = prop.getPropertyIndex();
        this._valueInstantiator = delegate.getValueInstantiator();
    }
    
    public XmlTextDeserializer(final BeanDeserializerBase delegate, final int textPropIndex) {
        super((JsonDeserializer)delegate);
        this._xmlTextPropertyIndex = textPropIndex;
        this._valueInstantiator = delegate.getValueInstantiator();
        this._xmlTextProperty = delegate.findProperty(textPropIndex);
    }
    
    protected JsonDeserializer<?> newDelegatingInstance(final JsonDeserializer<?> newDelegatee0) {
        throw new IllegalStateException("Internal error: should never get called");
    }
    
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        return (JsonDeserializer<?>)new XmlTextDeserializer(this._verifyDeserType((JsonDeserializer<?>)this._delegatee), this._xmlTextPropertyIndex);
    }
    
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            final Object bean = this._valueInstantiator.createUsingDefault(ctxt);
            this._xmlTextProperty.deserializeAndSet(p, ctxt, bean);
            return bean;
        }
        return this._delegatee.deserialize(p, ctxt);
    }
    
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object bean) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_STRING) {
            this._xmlTextProperty.deserializeAndSet(p, ctxt, bean);
            return bean;
        }
        return this._delegatee.deserialize(p, ctxt, bean);
    }
    
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        return this._delegatee.deserializeWithType(p, ctxt, typeDeserializer);
    }
    
    protected BeanDeserializerBase _verifyDeserType(final JsonDeserializer<?> deser) {
        if (!(deser instanceof BeanDeserializerBase)) {
            throw new IllegalArgumentException("Can not change delegate to be of type " + deser.getClass().getName());
        }
        return (BeanDeserializerBase)deser;
    }
}
