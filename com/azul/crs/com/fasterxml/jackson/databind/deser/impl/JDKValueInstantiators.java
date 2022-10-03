package com.azul.crs.com.fasterxml.jackson.databind.deser.impl;

import java.io.IOException;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import com.azul.crs.com.fasterxml.jackson.databind.deser.std.JsonLocationInstantiator;
import com.azul.crs.com.fasterxml.jackson.core.JsonLocation;
import com.azul.crs.com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.azul.crs.com.fasterxml.jackson.databind.DeserializationConfig;

public abstract class JDKValueInstantiators
{
    public static ValueInstantiator findStdValueInstantiator(final DeserializationConfig config, final Class<?> raw) {
        if (raw == JsonLocation.class) {
            return new JsonLocationInstantiator();
        }
        if (Collection.class.isAssignableFrom(raw)) {
            if (raw == ArrayList.class) {
                return ArrayListInstantiator.INSTANCE;
            }
            if (Collections.EMPTY_SET.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_SET);
            }
            if (Collections.EMPTY_LIST.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_LIST);
            }
        }
        else if (Map.class.isAssignableFrom(raw)) {
            if (raw == LinkedHashMap.class) {
                return LinkedHashMapInstantiator.INSTANCE;
            }
            if (raw == HashMap.class) {
                return HashMapInstantiator.INSTANCE;
            }
            if (Collections.EMPTY_MAP.getClass() == raw) {
                return new ConstantValueInstantiator(Collections.EMPTY_MAP);
            }
        }
        return null;
    }
    
    private static class ArrayListInstantiator extends Base implements Serializable
    {
        private static final long serialVersionUID = 2L;
        public static final ArrayListInstantiator INSTANCE;
        
        public ArrayListInstantiator() {
            super(ArrayList.class);
        }
        
        @Override
        public boolean canInstantiate() {
            return true;
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            return new ArrayList();
        }
        
        static {
            INSTANCE = new ArrayListInstantiator();
        }
    }
    
    private static class HashMapInstantiator extends Base implements Serializable
    {
        private static final long serialVersionUID = 2L;
        public static final HashMapInstantiator INSTANCE;
        
        public HashMapInstantiator() {
            super(HashMap.class);
        }
        
        @Override
        public boolean canInstantiate() {
            return true;
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            return new HashMap();
        }
        
        static {
            INSTANCE = new HashMapInstantiator();
        }
    }
    
    private static class LinkedHashMapInstantiator extends Base implements Serializable
    {
        private static final long serialVersionUID = 2L;
        public static final LinkedHashMapInstantiator INSTANCE;
        
        public LinkedHashMapInstantiator() {
            super(LinkedHashMap.class);
        }
        
        @Override
        public boolean canInstantiate() {
            return true;
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            return new LinkedHashMap();
        }
        
        static {
            INSTANCE = new LinkedHashMapInstantiator();
        }
    }
    
    private static class ConstantValueInstantiator extends Base implements Serializable
    {
        private static final long serialVersionUID = 2L;
        protected final Object _value;
        
        public ConstantValueInstantiator(final Object value) {
            super(value.getClass());
            this._value = value;
        }
        
        @Override
        public boolean canInstantiate() {
            return true;
        }
        
        @Override
        public boolean canCreateUsingDefault() {
            return true;
        }
        
        @Override
        public Object createUsingDefault(final DeserializationContext ctxt) throws IOException {
            return this._value;
        }
    }
}
