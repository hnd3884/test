package com.azul.crs.com.fasterxml.jackson.databind.ser.impl;

import com.azul.crs.com.fasterxml.jackson.databind.SerializationFeature;
import com.azul.crs.com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.SerializerProvider;
import com.azul.crs.com.fasterxml.jackson.core.JsonGenerator;
import com.azul.crs.com.fasterxml.jackson.databind.JsonSerializer;
import com.azul.crs.com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import java.util.Set;
import com.azul.crs.com.fasterxml.jackson.databind.util.NameTransformer;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class UnwrappingBeanSerializer extends BeanSerializerBase implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final NameTransformer _nameTransformer;
    
    public UnwrappingBeanSerializer(final BeanSerializerBase src, final NameTransformer transformer) {
        super(src, transformer);
        this._nameTransformer = transformer;
    }
    
    public UnwrappingBeanSerializer(final UnwrappingBeanSerializer src, final ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
        this._nameTransformer = src._nameTransformer;
    }
    
    public UnwrappingBeanSerializer(final UnwrappingBeanSerializer src, final ObjectIdWriter objectIdWriter, final Object filterId) {
        super(src, objectIdWriter, filterId);
        this._nameTransformer = src._nameTransformer;
    }
    
    protected UnwrappingBeanSerializer(final UnwrappingBeanSerializer src, final Set<String> toIgnore) {
        this(src, toIgnore, null);
    }
    
    protected UnwrappingBeanSerializer(final UnwrappingBeanSerializer src, final Set<String> toIgnore, final Set<String> toInclude) {
        super(src, toIgnore, toInclude);
        this._nameTransformer = src._nameTransformer;
    }
    
    protected UnwrappingBeanSerializer(final UnwrappingBeanSerializer src, final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        super(src, properties, filteredProperties);
        this._nameTransformer = src._nameTransformer;
    }
    
    @Override
    public JsonSerializer<Object> unwrappingSerializer(final NameTransformer transformer) {
        return new UnwrappingBeanSerializer(this, transformer);
    }
    
    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }
    
    @Override
    public BeanSerializerBase withObjectIdWriter(final ObjectIdWriter objectIdWriter) {
        return new UnwrappingBeanSerializer(this, objectIdWriter);
    }
    
    @Override
    public BeanSerializerBase withFilterId(final Object filterId) {
        return new UnwrappingBeanSerializer(this, this._objectIdWriter, filterId);
    }
    
    @Override
    protected BeanSerializerBase withByNameInclusion(final Set<String> toIgnore, final Set<String> toInclude) {
        return new UnwrappingBeanSerializer(this, toIgnore, toInclude);
    }
    
    @Override
    protected BeanSerializerBase withProperties(final BeanPropertyWriter[] properties, final BeanPropertyWriter[] filteredProperties) {
        return new UnwrappingBeanSerializer(this, properties, filteredProperties);
    }
    
    @Override
    protected BeanSerializerBase asArraySerializer() {
        return this;
    }
    
    @Override
    public final void serialize(final Object bean, final JsonGenerator gen, final SerializerProvider provider) throws IOException {
        gen.setCurrentValue(bean);
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, gen, provider, false);
            return;
        }
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
        }
        else {
            this.serializeFields(bean, gen, provider);
        }
    }
    
    @Override
    public void serializeWithType(final Object bean, final JsonGenerator gen, final SerializerProvider provider, final TypeSerializer typeSer) throws IOException {
        if (provider.isEnabled(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS)) {
            provider.reportBadDefinition(this.handledType(), "Unwrapped property requires use of type information: cannot serialize without disabling `SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS`");
        }
        gen.setCurrentValue(bean);
        if (this._objectIdWriter != null) {
            this._serializeWithObjectId(bean, gen, provider, typeSer);
            return;
        }
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, gen, provider);
        }
        else {
            this.serializeFields(bean, gen, provider);
        }
    }
    
    @Override
    public String toString() {
        return "UnwrappingBeanSerializer for " + this.handledType().getName();
    }
}
