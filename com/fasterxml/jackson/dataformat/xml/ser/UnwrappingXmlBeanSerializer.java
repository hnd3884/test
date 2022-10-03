package com.fasterxml.jackson.dataformat.xml.ser;

import com.fasterxml.jackson.core.JsonGenerationException;
import java.io.IOException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import java.util.Set;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.util.NameTransformer;

public class UnwrappingXmlBeanSerializer extends XmlBeanSerializerBase
{
    private static final long serialVersionUID = 1L;
    protected final NameTransformer _nameTransformer;
    
    public UnwrappingXmlBeanSerializer(final XmlBeanSerializerBase src, final NameTransformer transformer) {
        super(src, transformer);
        this._nameTransformer = transformer;
    }
    
    public UnwrappingXmlBeanSerializer(final UnwrappingXmlBeanSerializer src, final ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
        this._nameTransformer = src._nameTransformer;
    }
    
    public UnwrappingXmlBeanSerializer(final UnwrappingXmlBeanSerializer src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src, objectIdWriter, filterId);
        this._nameTransformer = src._nameTransformer;
    }
    
    protected UnwrappingXmlBeanSerializer(final UnwrappingXmlBeanSerializer src, final Set<String> toIgnore) {
        super(src, toIgnore);
        this._nameTransformer = src._nameTransformer;
    }
    
    protected UnwrappingXmlBeanSerializer(final UnwrappingXmlBeanSerializer src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(src, properties, filteredProperties);
        this._nameTransformer = src._nameTransformer;
    }
    
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer transformer) {
        return (JsonSerializer<Object>)new UnwrappingXmlBeanSerializer(this, transformer);
    }
    
    public boolean isUnwrappingSerializer() {
        return true;
    }
    
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new UnwrappingXmlBeanSerializer(this, objectIdWriter);
    }
    
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new UnwrappingXmlBeanSerializer(this, this._objectIdWriter, filterId);
    }
    
    protected BeanSerializerBase withIgnorals(final Set<String> toIgnore) {
        return new UnwrappingXmlBeanSerializer(this, toIgnore);
    }
    
    protected BeanSerializerBase withProperties(final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        return new UnwrappingXmlBeanSerializer(this, properties, filteredProperties);
    }
    
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }
    
    public final void serialize(final Object bean, final JsonGenerator jgen, final SerializerProvider provider) throws IOException, JsonGenerationException {
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, jgen, provider, false);
            return;
        }
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        }
        else {
            this.serializeFields(bean, jgen, provider);
        }
    }
    
    public String toString() {
        return "UnwrappingXmlBeanSerializer for " + this.handledType().getName();
    }
}
