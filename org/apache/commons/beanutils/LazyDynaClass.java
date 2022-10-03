package org.apache.commons.beanutils;

public class LazyDynaClass extends BasicDynaClass implements MutableDynaClass
{
    protected boolean restricted;
    protected boolean returnNull;
    
    public LazyDynaClass() {
        this(null, (DynaProperty[])null);
    }
    
    public LazyDynaClass(final String name) {
        this(name, (DynaProperty[])null);
    }
    
    public LazyDynaClass(final String name, final Class<?> dynaBeanClass) {
        this(name, dynaBeanClass, null);
    }
    
    public LazyDynaClass(final String name, final DynaProperty[] properties) {
        this(name, LazyDynaBean.class, properties);
    }
    
    public LazyDynaClass(final String name, final Class<?> dynaBeanClass, final DynaProperty[] properties) {
        super(name, dynaBeanClass, properties);
        this.returnNull = false;
    }
    
    @Override
    public boolean isRestricted() {
        return this.restricted;
    }
    
    @Override
    public void setRestricted(final boolean restricted) {
        this.restricted = restricted;
    }
    
    public boolean isReturnNull() {
        return this.returnNull;
    }
    
    public void setReturnNull(final boolean returnNull) {
        this.returnNull = returnNull;
    }
    
    @Override
    public void add(final String name) {
        this.add(new DynaProperty(name));
    }
    
    @Override
    public void add(final String name, final Class<?> type) {
        if (type == null) {
            this.add(name);
        }
        else {
            this.add(new DynaProperty(name, type));
        }
    }
    
    @Override
    public void add(final String name, final Class<?> type, final boolean readable, final boolean writeable) {
        throw new UnsupportedOperationException("readable/writable properties not supported");
    }
    
    protected void add(final DynaProperty property) {
        if (property.getName() == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No new properties can be added.");
        }
        if (this.propertiesMap.get(property.getName()) != null) {
            return;
        }
        final DynaProperty[] oldProperties = this.getDynaProperties();
        final DynaProperty[] newProperties = new DynaProperty[oldProperties.length + 1];
        System.arraycopy(oldProperties, 0, newProperties, 0, oldProperties.length);
        newProperties[oldProperties.length] = property;
        this.setProperties(newProperties);
    }
    
    @Override
    public void remove(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        if (this.isRestricted()) {
            throw new IllegalStateException("DynaClass is currently restricted. No properties can be removed.");
        }
        if (this.propertiesMap.get(name) == null) {
            return;
        }
        final DynaProperty[] oldProperties = this.getDynaProperties();
        final DynaProperty[] newProperties = new DynaProperty[oldProperties.length - 1];
        int j = 0;
        for (int i = 0; i < oldProperties.length; ++i) {
            if (!name.equals(oldProperties[i].getName())) {
                newProperties[j] = oldProperties[i];
                ++j;
            }
        }
        this.setProperties(newProperties);
    }
    
    @Override
    public DynaProperty getDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        DynaProperty dynaProperty = this.propertiesMap.get(name);
        if (dynaProperty == null && !this.isReturnNull() && !this.isRestricted()) {
            dynaProperty = new DynaProperty(name);
        }
        return dynaProperty;
    }
    
    public boolean isDynaProperty(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Property name is missing.");
        }
        return this.propertiesMap.get(name) != null;
    }
}
