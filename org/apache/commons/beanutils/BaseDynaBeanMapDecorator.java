package org.apache.commons.beanutils;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

public abstract class BaseDynaBeanMapDecorator<K> implements Map<K, Object>
{
    private final DynaBean dynaBean;
    private final boolean readOnly;
    private transient Set<K> keySet;
    
    public BaseDynaBeanMapDecorator(final DynaBean dynaBean) {
        this(dynaBean, true);
    }
    
    public BaseDynaBeanMapDecorator(final DynaBean dynaBean, final boolean readOnly) {
        if (dynaBean == null) {
            throw new IllegalArgumentException("DynaBean is null");
        }
        this.dynaBean = dynaBean;
        this.readOnly = readOnly;
    }
    
    public boolean isReadOnly() {
        return this.readOnly;
    }
    
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean containsKey(final Object key) {
        final DynaClass dynaClass = this.getDynaBean().getDynaClass();
        final DynaProperty dynaProperty = dynaClass.getDynaProperty(this.toString(key));
        return dynaProperty != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        final DynaProperty[] dynaProperties;
        final DynaProperty[] properties = dynaProperties = this.getDynaProperties();
        for (final DynaProperty propertie : dynaProperties) {
            final String key = propertie.getName();
            final Object prop = this.getDynaBean().get(key);
            if (value == null) {
                if (prop == null) {
                    return true;
                }
            }
            else if (value.equals(prop)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Set<Entry<K, Object>> entrySet() {
        final DynaProperty[] properties = this.getDynaProperties();
        final Set<Entry<K, Object>> set = new HashSet<Entry<K, Object>>(properties.length);
        for (final DynaProperty propertie : properties) {
            final K key = this.convertKey(propertie.getName());
            final Object value = this.getDynaBean().get(propertie.getName());
            set.add(new MapEntry<K>(key, value));
        }
        return Collections.unmodifiableSet((Set<? extends Entry<K, Object>>)set);
    }
    
    @Override
    public Object get(final Object key) {
        return this.getDynaBean().get(this.toString(key));
    }
    
    @Override
    public boolean isEmpty() {
        return this.getDynaProperties().length == 0;
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet != null) {
            return this.keySet;
        }
        final DynaProperty[] properties = this.getDynaProperties();
        Set<K> set = new HashSet<K>(properties.length);
        for (final DynaProperty propertie : properties) {
            set.add(this.convertKey(propertie.getName()));
        }
        set = Collections.unmodifiableSet((Set<? extends K>)set);
        final DynaClass dynaClass = this.getDynaBean().getDynaClass();
        if (!(dynaClass instanceof MutableDynaClass)) {
            this.keySet = set;
        }
        return set;
    }
    
    @Override
    public Object put(final K key, final Object value) {
        if (this.isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        final String property = this.toString(key);
        final Object previous = this.getDynaBean().get(property);
        this.getDynaBean().set(property, value);
        return previous;
    }
    
    @Override
    public void putAll(final Map<? extends K, ?> map) {
        if (this.isReadOnly()) {
            throw new UnsupportedOperationException("Map is read only");
        }
        for (final Entry<? extends K, ?> e : map.entrySet()) {
            this.put(e.getKey(), e.getValue());
        }
    }
    
    @Override
    public Object remove(final Object key) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int size() {
        return this.getDynaProperties().length;
    }
    
    @Override
    public Collection<Object> values() {
        final DynaProperty[] properties = this.getDynaProperties();
        final List<Object> values = new ArrayList<Object>(properties.length);
        for (final DynaProperty propertie : properties) {
            final String key = propertie.getName();
            final Object value = this.getDynaBean().get(key);
            values.add(value);
        }
        return Collections.unmodifiableList((List<?>)values);
    }
    
    public DynaBean getDynaBean() {
        return this.dynaBean;
    }
    
    protected abstract K convertKey(final String p0);
    
    private DynaProperty[] getDynaProperties() {
        return this.getDynaBean().getDynaClass().getDynaProperties();
    }
    
    private String toString(final Object obj) {
        return (obj == null) ? null : obj.toString();
    }
    
    private static class MapEntry<K> implements Entry<K, Object>
    {
        private final K key;
        private final Object value;
        
        MapEntry(final K key, final Object value) {
            this.key = key;
            this.value = value;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            final Entry<?, ?> e = (Entry<?, ?>)o;
            return this.key.equals(e.getKey()) && ((this.value != null) ? this.value.equals(e.getValue()) : (e.getValue() == null));
        }
        
        @Override
        public int hashCode() {
            return this.key.hashCode() + ((this.value == null) ? 0 : this.value.hashCode());
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public Object getValue() {
            return this.value;
        }
        
        @Override
        public Object setValue(final Object value) {
            throw new UnsupportedOperationException();
        }
    }
}
