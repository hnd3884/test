package com.azul.crs.com.fasterxml.jackson.databind.module;

import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.KeyDeserializer;
import com.azul.crs.com.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.deser.KeyDeserializers;

public class SimpleKeyDeserializers implements KeyDeserializers, Serializable
{
    private static final long serialVersionUID = 1L;
    protected HashMap<ClassKey, KeyDeserializer> _classMappings;
    
    public SimpleKeyDeserializers() {
        this._classMappings = null;
    }
    
    public SimpleKeyDeserializers addDeserializer(final Class<?> forClass, final KeyDeserializer deser) {
        if (this._classMappings == null) {
            this._classMappings = new HashMap<ClassKey, KeyDeserializer>();
        }
        this._classMappings.put(new ClassKey(forClass), deser);
        return this;
    }
    
    @Override
    public KeyDeserializer findKeyDeserializer(final JavaType type, final DeserializationConfig config, final BeanDescription beanDesc) {
        if (this._classMappings == null) {
            return null;
        }
        return this._classMappings.get(new ClassKey(type.getRawClass()));
    }
}
