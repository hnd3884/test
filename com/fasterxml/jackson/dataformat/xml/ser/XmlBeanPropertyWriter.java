package com.fasterxml.jackson.dataformat.xml.ser;

import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import javax.xml.namespace.QName;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;

public class XmlBeanPropertyWriter extends BeanPropertyWriter
{
    private static final long serialVersionUID = 1L;
    protected final QName _wrapperQName;
    protected final QName _wrappedQName;
    
    public XmlBeanPropertyWriter(final BeanPropertyWriter wrapped, final PropertyName wrapperName, final PropertyName wrappedName) {
        this(wrapped, wrapperName, wrappedName, null);
    }
    
    public XmlBeanPropertyWriter(final BeanPropertyWriter wrapped, final PropertyName wrapperName, final PropertyName wrappedName, final JsonSerializer<Object> serializer) {
        super(wrapped);
        this._wrapperQName = this._qname(wrapperName);
        this._wrappedQName = this._qname(wrappedName);
        if (serializer != null) {
            this.assignSerializer((JsonSerializer)serializer);
        }
    }
    
    private QName _qname(final PropertyName n) {
        String ns = n.getNamespace();
        if (ns == null) {
            ns = "";
        }
        return new QName(ns, n.getSimpleName());
    }
    
    public void serializeAsField(final Object bean, final JsonGenerator jgen, final SerializerProvider prov) throws Exception {
        final Object value = this.get(bean);
        if (value == null) {
            return;
        }
        JsonSerializer<Object> ser = (JsonSerializer<Object>)this._serializer;
        if (ser == null) {
            final Class<?> cls = value.getClass();
            final PropertySerializerMap map = this._dynamicSerializers;
            ser = (JsonSerializer<Object>)map.serializerFor((Class)cls);
            if (ser == null) {
                ser = (JsonSerializer<Object>)this._findAndAddDynamic(map, (Class)cls, prov);
            }
        }
        if (this._suppressableValue != null) {
            if (XmlBeanPropertyWriter.MARKER_FOR_EMPTY == this._suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            }
            else if (this._suppressableValue.equals(value)) {
                return;
            }
        }
        if (value == bean && this._handleSelfReference(bean, jgen, prov, (JsonSerializer)ser)) {
            return;
        }
        final ToXmlGenerator xmlGen = (jgen instanceof ToXmlGenerator) ? jgen : null;
        if (xmlGen != null) {
            xmlGen.startWrappedValue(this._wrapperQName, this._wrappedQName);
        }
        jgen.writeFieldName((SerializableString)this._name);
        if (this._typeSerializer == null) {
            ser.serialize(value, jgen, prov);
        }
        else {
            ser.serializeWithType(value, jgen, prov, this._typeSerializer);
        }
        if (xmlGen != null) {
            xmlGen.finishWrappedValue(this._wrapperQName, this._wrappedQName);
        }
    }
}
