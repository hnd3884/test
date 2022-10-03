package com.fasterxml.jackson.dataformat.xml.deser;

import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.util.Iterator;
import java.util.HashSet;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.dataformat.xml.util.TypeUtil;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBase;
import com.fasterxml.jackson.databind.JavaType;
import java.util.Set;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;

public class WrapperHandlingDeserializer extends DelegatingDeserializer
{
    private static final long serialVersionUID = 1L;
    protected final Set<String> _namesToWrap;
    protected final JavaType _type;
    
    public WrapperHandlingDeserializer(final BeanDeserializerBase delegate) {
        this(delegate, null);
    }
    
    public WrapperHandlingDeserializer(final BeanDeserializerBase delegate, final Set<String> namesToWrap) {
        super((JsonDeserializer)delegate);
        this._namesToWrap = namesToWrap;
        this._type = delegate.getValueType();
    }
    
    protected JsonDeserializer<?> newDelegatingInstance(final JsonDeserializer<?> newDelegatee0) {
        throw new IllegalStateException("Internal error: should never get called");
    }
    
    public JsonDeserializer<?> createContextual(final DeserializationContext ctxt, final BeanProperty property) throws JsonMappingException {
        JavaType vt = this._type;
        if (vt == null) {
            vt = ctxt.constructType(this._delegatee.handledType());
        }
        final JsonDeserializer<?> del = (JsonDeserializer<?>)ctxt.handleSecondaryContextualization(this._delegatee, property, vt);
        final BeanDeserializerBase newDelegatee = this._verifyDeserType(del);
        final Iterator<SettableBeanProperty> it = newDelegatee.properties();
        HashSet<String> unwrappedNames = null;
        while (it.hasNext()) {
            final SettableBeanProperty prop = it.next();
            final JavaType type = prop.getType();
            if (!TypeUtil.isIndexedType(type)) {
                continue;
            }
            final PropertyName wrapperName = prop.getWrapperName();
            if (wrapperName != null && wrapperName != PropertyName.NO_NAME) {
                continue;
            }
            if (unwrappedNames == null) {
                unwrappedNames = new HashSet<String>();
            }
            unwrappedNames.add(prop.getName());
        }
        if (unwrappedNames == null) {
            return (JsonDeserializer<?>)newDelegatee;
        }
        return (JsonDeserializer<?>)new WrapperHandlingDeserializer(newDelegatee, unwrappedNames);
    }
    
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        this._configureParser(p);
        return this._delegatee.deserialize(p, ctxt);
    }
    
    public Object deserialize(final JsonParser p, final DeserializationContext ctxt, final Object intoValue) throws IOException {
        this._configureParser(p);
        return this._delegatee.deserialize(p, ctxt, intoValue);
    }
    
    public Object deserializeWithType(final JsonParser p, final DeserializationContext ctxt, final TypeDeserializer typeDeserializer) throws IOException {
        this._configureParser(p);
        return this._delegatee.deserializeWithType(p, ctxt, typeDeserializer);
    }
    
    protected final void _configureParser(JsonParser p) throws IOException {
        while (p instanceof JsonParserDelegate) {
            p = ((JsonParserDelegate)p).delegate();
        }
        if (p instanceof FromXmlParser) {
            ((FromXmlParser)p).addVirtualWrapping(this._namesToWrap);
        }
    }
    
    protected BeanDeserializerBase _verifyDeserType(final JsonDeserializer<?> deser) {
        if (!(deser instanceof BeanDeserializerBase)) {
            throw new IllegalArgumentException("Can not change delegate to be of type " + deser.getClass().getName());
        }
        return (BeanDeserializerBase)deser;
    }
}
