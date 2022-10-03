package com.sun.xml.internal.ws.spi.db;

import javax.xml.namespace.QName;
import java.util.Map;

public abstract class WrapperAccessor
{
    protected Map<Object, PropertySetter> propertySetters;
    protected Map<Object, PropertyGetter> propertyGetters;
    protected boolean elementLocalNameCollision;
    
    protected PropertySetter getPropertySetter(final QName name) {
        final Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
        return this.propertySetters.get(key);
    }
    
    protected PropertyGetter getPropertyGetter(final QName name) {
        final Object key = this.elementLocalNameCollision ? name : name.getLocalPart();
        return this.propertyGetters.get(key);
    }
    
    public PropertyAccessor getPropertyAccessor(final String ns, final String name) {
        final QName n = new QName(ns, name);
        final PropertySetter setter = this.getPropertySetter(n);
        final PropertyGetter getter = this.getPropertyGetter(n);
        return new PropertyAccessor() {
            @Override
            public Object get(final Object bean) throws DatabindingException {
                return getter.get(bean);
            }
            
            @Override
            public void set(final Object bean, final Object value) throws DatabindingException {
                setter.set(bean, value);
            }
        };
    }
}
