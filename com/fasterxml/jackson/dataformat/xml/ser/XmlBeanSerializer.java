package com.fasterxml.jackson.dataformat.xml.ser;

import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class XmlBeanSerializer extends XmlBeanSerializerBase
{
    private static final long serialVersionUID = 1L;
    
    public XmlBeanSerializer(final BeanSerializerBase src) {
        super(src);
    }
    
    public XmlBeanSerializer(final XmlBeanSerializerBase src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src, objectIdWriter, filterId);
    }
    
    public XmlBeanSerializer(final XmlBeanSerializerBase src, final ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }
    
    public XmlBeanSerializer(final XmlBeanSerializerBase src, final Set<String> toIgnore) {
        super(src, toIgnore);
    }
    
    protected XmlBeanSerializer(final XmlBeanSerializerBase src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(src, properties, filteredProperties);
    }
    
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer unwrapper) {
        return (JsonSerializer<Object>)new UnwrappingXmlBeanSerializer(this, unwrapper);
    }
    
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new XmlBeanSerializer(this, objectIdWriter, this._propertyFilterId);
    }
    
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new XmlBeanSerializer(this, this._objectIdWriter, filterId);
    }
    
    protected BeanSerializerBase withIgnorals(final Set<String> toIgnore) {
        return new XmlBeanSerializer(this, toIgnore);
    }
    
    protected BeanSerializerBase withProperties(final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        return new XmlBeanSerializer(this, properties, filteredProperties);
    }
    
    protected BeanSerializerBase asArraySerializer() {
        if (this._objectIdWriter == null && this._anyGetterWriter == null && this._propertyFilterId == null) {
            return (BeanSerializerBase)new BeanAsArraySerializer((BeanSerializerBase)this);
        }
        return this;
    }
    
    public void serialize(final Object bean, final JsonGenerator g, final SerializerProvider provider) throws IOException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, g, provider, true);
            return;
        }
        g.writeStartObject();
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, g, provider);
        }
        else {
            this.serializeFields(bean, g, provider);
        }
        g.writeEndObject();
    }
    
    public String toString() {
        return "XmlBeanSerializer for " + this.handledType().getName();
    }
}
