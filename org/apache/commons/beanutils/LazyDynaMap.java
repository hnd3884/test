package org.apache.commons.beanutils;

import java.util.Iterator;
import java.util.Map;

public class LazyDynaMap extends LazyDynaBean implements MutableDynaClass
{
    protected String name;
    protected boolean restricted;
    protected boolean returnNull;
    
    public LazyDynaMap() {
        this(null, (Map<String, Object>)null);
    }
    
    public LazyDynaMap(final String name) {
        this(name, (Map<String, Object>)null);
    }
    
    public LazyDynaMap(final Map<String, Object> values) {
        this(null, values);
    }
    
    public LazyDynaMap(final String name, final Map<String, Object> values) {
        this.returnNull = false;
        this.name = ((name == null) ? "LazyDynaMap" : name);
        this.values = ((values == null) ? this.newMap() : values);
        this.dynaClass = this;
    }
    
    public LazyDynaMap(final DynaProperty[] properties) {
        this(null, properties);
    }
    
    public LazyDynaMap(final String name, final DynaProperty[] properties) {
        this(name, (Map<String, Object>)null);
        if (properties != null) {
            for (final DynaProperty propertie : properties) {
                this.add(propertie);
            }
        }
    }
    
    public LazyDynaMap(final DynaClass dynaClass) {
        this(dynaClass.getName(), dynaClass.getDynaProperties());
    }
    
    public void setMap(final Map<String, Object> values) {
        this.values = values;
    }
    
    @Override
    public Map<String, Object> getMap() {
        return this.values;
    }
    
    @Override
    public void set(final String name, final Object value) {
        if (this.isRestricted() && !this.values.containsKey(name)) {
            throw new IllegalArgumentException("Invalid property name '" + name + "' (DynaClass is restricted)");
        }
        this.values.put(name, value);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (!this.values.containsKey(name) && this.isReturnNull()) {
            return null;
        }
        final Object value = this.values.get(name);
        if (value == null) {
            return new DynaProperty(name);
        }
        return new DynaProperty(name, value.getClass());
    }
    
    @Override
    public DynaProperty[] getDynaProperties() {
        int i = 0;
        final DynaProperty[] properties = new DynaProperty[this.values.size()];
        for (final Map.Entry<String, Object> e : this.values.entrySet()) {
            final String name = e.getKey();
            final Object value = this.values.get(name);
            properties[i++] = new DynaProperty(name, (value == null) ? null : value.getClass());
        }
        return properties;
    }
    
    @Override
    public DynaBean newInstance() {
        Map<String, Object> newMap = null;
        try {
            final Map<String, Object> temp = newMap = (Map)this.getMap().getClass().newInstance();
        }
        catch (final Exception ex) {
            newMap = this.newMap();
        }
        final LazyDynaMap lazyMap = new LazyDynaMap(newMap);
        final DynaProperty[] properties = this.getDynaProperties();
        if (properties != null) {
            for (final DynaProperty propertie : properties) {
                lazyMap.add(propertie);
            }
        }
        return lazyMap;
    }
    
    @Override
    public boolean isRestricted() {
        return this.restricted;
    }
    
    @Override
    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }
    
    @Override
    public void add(final String name) {
        this.add(name, null);
    }
    
    @Override
    public void add(final String name, final Class<?> type) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }
        final Object value = this.values.get(name);
        if (value == null) {
            this.values.put(name, (type == null) ? null : this.createProperty(name, type));
        }
    }
    
    @Override
    public void add(final String name, final Class<?> type, final boolean readable, final boolean writeable) {
        throw new UnsupportedOperationException("readable/writable properties not supported");
    }
    
    protected void add(final DynaProperty property) {
        this.add(property.getName(), property.getType());
    }
    
    @Override
    public void remove(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }
        if (this.values.containsKey(name)) {
            this.values.remove(name);
        }
    }
    
    public boolean isReturnNull() {
        return this.returnNull;
    }
    
    public void setReturnNull(final boolean returnNull) {
        this.returnNull = returnNull;
    }
    
    @Override
    protected boolean isDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        return this.values.containsKey(name);
    }
}
