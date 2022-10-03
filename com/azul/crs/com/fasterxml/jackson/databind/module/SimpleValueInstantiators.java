package com.azul.crs.com.fasterxml.jackson.databind.module;

import com.azul.crs.com.fasterxml.jackson.databind.BeanDescription;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.type.ClassKey;
import java.util.HashMap;
import java.io.Serializable;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiators;

public class SimpleValueInstantiators extends ValueInstantiators.Base implements Serializable
{
    private static final long serialVersionUID = -8929386427526115130L;
    protected HashMap<ClassKey, ValueInstantiator> _classMappings;
    
    public SimpleValueInstantiators() {
        this._classMappings = new HashMap<ClassKey, ValueInstantiator>();
    }
    
    public SimpleValueInstantiators addValueInstantiator(final Class<?> forType, final ValueInstantiator inst) {
        this._classMappings.put(new ClassKey(forType), inst);
        return this;
    }
    
    @Override
    public ValueInstantiator findValueInstantiator(final DeserializationConfig config, final BeanDescription beanDesc, final ValueInstantiator defaultInstantiator) {
        final ValueInstantiator inst = this._classMappings.get(new ClassKey(beanDesc.getBeanClass()));
        return (inst == null) ? defaultInstantiator : inst;
    }
}
