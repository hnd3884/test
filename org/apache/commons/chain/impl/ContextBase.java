package org.apache.commons.chain.impl;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.Collection;
import java.util.Set;
import java.util.Iterator;
import java.beans.PropertyDescriptor;
import java.util.Map;
import org.apache.commons.chain.Context;
import java.util.HashMap;

public class ContextBase extends HashMap implements Context
{
    private transient Map descriptors;
    private transient PropertyDescriptor[] pd;
    private static Object singleton;
    private static Object[] zeroParams;
    
    public ContextBase() {
        this.descriptors = null;
        this.pd = null;
        this.initialize();
    }
    
    public ContextBase(final Map map) {
        super(map);
        this.descriptors = null;
        this.pd = null;
        this.initialize();
        this.putAll(map);
    }
    
    public void clear() {
        if (this.descriptors == null) {
            super.clear();
        }
        else {
            final Iterator keys = this.keySet().iterator();
            while (keys.hasNext()) {
                final Object key = keys.next();
                if (!this.descriptors.containsKey(key)) {
                    keys.remove();
                }
            }
        }
    }
    
    public boolean containsValue(final Object value) {
        if (this.descriptors == null) {
            return super.containsValue(value);
        }
        if (super.containsValue(value)) {
            return true;
        }
        for (int i = 0; i < this.pd.length; ++i) {
            if (this.pd[i].getReadMethod() != null) {
                final Object prop = this.readProperty(this.pd[i]);
                if (value == null) {
                    if (prop == null) {
                        return true;
                    }
                }
                else if (value.equals(prop)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Set entrySet() {
        return new EntrySetImpl();
    }
    
    public Object get(final Object key) {
        if (this.descriptors == null) {
            return super.get(key);
        }
        if (key != null) {
            final PropertyDescriptor descriptor = this.descriptors.get(key);
            if (descriptor != null) {
                if (descriptor.getReadMethod() != null) {
                    return this.readProperty(descriptor);
                }
                return null;
            }
        }
        return super.get(key);
    }
    
    public boolean isEmpty() {
        if (this.descriptors == null) {
            return super.isEmpty();
        }
        return super.size() <= this.descriptors.size();
    }
    
    public Set keySet() {
        return super.keySet();
    }
    
    public Object put(final Object key, final Object value) {
        if (this.descriptors == null) {
            return super.put(key, value);
        }
        if (key != null) {
            final PropertyDescriptor descriptor = this.descriptors.get(key);
            if (descriptor != null) {
                Object previous = null;
                if (descriptor.getReadMethod() != null) {
                    previous = this.readProperty(descriptor);
                }
                this.writeProperty(descriptor, value);
                return previous;
            }
        }
        return super.put(key, value);
    }
    
    public void putAll(final Map map) {
        final Iterator pairs = map.entrySet().iterator();
        while (pairs.hasNext()) {
            final Map.Entry pair = pairs.next();
            this.put(pair.getKey(), pair.getValue());
        }
    }
    
    public Object remove(final Object key) {
        if (this.descriptors == null) {
            return super.remove(key);
        }
        if (key != null) {
            final PropertyDescriptor descriptor = this.descriptors.get(key);
            if (descriptor != null) {
                throw new UnsupportedOperationException("Local property '" + key + "' cannot be removed");
            }
        }
        return super.remove(key);
    }
    
    public Collection values() {
        return new ValuesImpl();
    }
    
    private Iterator entriesIterator() {
        return new EntrySetIterator();
    }
    
    private Map.Entry entry(final Object key) {
        if (this.containsKey(key)) {
            return new MapEntryImpl(key, this.get(key));
        }
        return null;
    }
    
    private void initialize() {
        try {
            this.pd = Introspector.getBeanInfo(this.getClass()).getPropertyDescriptors();
        }
        catch (final IntrospectionException e) {
            this.pd = new PropertyDescriptor[0];
        }
        for (int i = 0; i < this.pd.length; ++i) {
            final String name = this.pd[i].getName();
            if (!"class".equals(name) && !"empty".equals(name)) {
                if (this.descriptors == null) {
                    this.descriptors = new HashMap(this.pd.length - 2);
                }
                this.descriptors.put(name, this.pd[i]);
                super.put(name, ContextBase.singleton);
            }
        }
    }
    
    private Object readProperty(final PropertyDescriptor descriptor) {
        try {
            final Method method = descriptor.getReadMethod();
            if (method == null) {
                throw new UnsupportedOperationException("Property '" + descriptor.getName() + "' is not readable");
            }
            return method.invoke(this, ContextBase.zeroParams);
        }
        catch (final Exception e) {
            throw new UnsupportedOperationException("Exception reading property '" + descriptor.getName() + "': " + e.getMessage());
        }
    }
    
    private boolean remove(final Map.Entry entry) {
        final Map.Entry actual = this.entry(entry.getKey());
        if (actual == null) {
            return false;
        }
        if (!entry.equals(actual)) {
            return false;
        }
        this.remove(entry.getKey());
        return true;
    }
    
    private Iterator valuesIterator() {
        return new ValuesIterator();
    }
    
    private void writeProperty(final PropertyDescriptor descriptor, final Object value) {
        try {
            final Method method = descriptor.getWriteMethod();
            if (method == null) {
                throw new UnsupportedOperationException("Property '" + descriptor.getName() + "' is not writeable");
            }
            method.invoke(this, value);
        }
        catch (final Exception e) {
            throw new UnsupportedOperationException("Exception writing property '" + descriptor.getName() + "': " + e.getMessage());
        }
    }
    
    static {
        ContextBase.singleton = new Serializable() {
            public boolean equals(final Object object) {
                return false;
            }
        };
        ContextBase.zeroParams = new Object[0];
    }
    
    private class EntrySetImpl extends AbstractSet
    {
        public void clear() {
            ContextBase.this.clear();
        }
        
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            final Map.Entry actual = ContextBase.this.entry(entry.getKey());
            return actual != null && actual.equals(entry);
        }
        
        public boolean isEmpty() {
            return ContextBase.this.isEmpty();
        }
        
        public Iterator iterator() {
            return ContextBase.this.entriesIterator();
        }
        
        public boolean remove(final Object obj) {
            return obj instanceof Map.Entry && ContextBase.this.remove((Map.Entry)obj);
        }
        
        public int size() {
            return ContextBase.this.size();
        }
    }
    
    private class EntrySetIterator implements Iterator
    {
        private Map.Entry entry;
        private Iterator keys;
        
        private EntrySetIterator() {
            this.entry = null;
            this.keys = ContextBase.this.keySet().iterator();
        }
        
        public boolean hasNext() {
            return this.keys.hasNext();
        }
        
        public Object next() {
            return this.entry = ContextBase.this.entry(this.keys.next());
        }
        
        public void remove() {
            ContextBase.this.remove(this.entry);
        }
    }
    
    private class MapEntryImpl implements Map.Entry
    {
        private Object key;
        private Object value;
        
        MapEntryImpl(final Object key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        public boolean equals(final Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            if (this.key == null) {
                return entry.getKey() == null;
            }
            if (!this.key.equals(entry.getKey())) {
                return false;
            }
            if (this.value == null) {
                return entry.getValue() == null;
            }
            return this.value.equals(entry.getValue());
        }
        
        public Object getKey() {
            return this.key;
        }
        
        public Object getValue() {
            return this.value;
        }
        
        public int hashCode() {
            return ((this.key == null) ? 0 : this.key.hashCode()) ^ ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        public Object setValue(final Object value) {
            final Object previous = this.value;
            ContextBase.this.put(this.key, value);
            this.value = value;
            return previous;
        }
        
        public String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
    
    private class ValuesImpl extends AbstractCollection
    {
        public void clear() {
            ContextBase.this.clear();
        }
        
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            return ContextBase.this.containsValue(entry.getValue());
        }
        
        public boolean isEmpty() {
            return ContextBase.this.isEmpty();
        }
        
        public Iterator iterator() {
            return ContextBase.this.valuesIterator();
        }
        
        public boolean remove(final Object obj) {
            return obj instanceof Map.Entry && ContextBase.this.remove((Map.Entry)obj);
        }
        
        public int size() {
            return ContextBase.this.size();
        }
    }
    
    private class ValuesIterator implements Iterator
    {
        private Map.Entry entry;
        private Iterator keys;
        
        private ValuesIterator() {
            this.entry = null;
            this.keys = ContextBase.this.keySet().iterator();
        }
        
        public boolean hasNext() {
            return this.keys.hasNext();
        }
        
        public Object next() {
            this.entry = ContextBase.this.entry(this.keys.next());
            return this.entry.getValue();
        }
        
        public void remove() {
            ContextBase.this.remove(this.entry);
        }
    }
}
