package org.apache.commons.beanutils;

import java.util.Locale;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.beans.IntrospectionException;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class FluentPropertyBeanIntrospector implements BeanIntrospector
{
    public static final String DEFAULT_WRITE_METHOD_PREFIX = "set";
    private final Log log;
    private final String writeMethodPrefix;
    
    public FluentPropertyBeanIntrospector(final String writePrefix) {
        this.log = LogFactory.getLog((Class)this.getClass());
        if (writePrefix == null) {
            throw new IllegalArgumentException("Prefix for write methods must not be null!");
        }
        this.writeMethodPrefix = writePrefix;
    }
    
    public FluentPropertyBeanIntrospector() {
        this("set");
    }
    
    public String getWriteMethodPrefix() {
        return this.writeMethodPrefix;
    }
    
    @Override
    public void introspect(final IntrospectionContext icontext) throws IntrospectionException {
        for (final Method m : icontext.getTargetClass().getMethods()) {
            if (m.getName().startsWith(this.getWriteMethodPrefix())) {
                final String propertyName = this.propertyName(m);
                final PropertyDescriptor pd = icontext.getPropertyDescriptor(propertyName);
                try {
                    if (pd == null) {
                        icontext.addPropertyDescriptor(this.createFluentPropertyDescritor(m, propertyName));
                    }
                    else if (pd.getWriteMethod() == null) {
                        pd.setWriteMethod(m);
                    }
                }
                catch (final IntrospectionException e) {
                    this.log.info((Object)("Error when creating PropertyDescriptor for " + m + "! Ignoring this property."));
                    this.log.debug((Object)"Exception is:", (Throwable)e);
                }
            }
        }
    }
    
    private String propertyName(final Method m) {
        final String methodName = m.getName().substring(this.getWriteMethodPrefix().length());
        return (methodName.length() > 1) ? Introspector.decapitalize(methodName) : methodName.toLowerCase(Locale.ENGLISH);
    }
    
    private PropertyDescriptor createFluentPropertyDescritor(final Method m, final String propertyName) throws IntrospectionException {
        return new PropertyDescriptor(this.propertyName(m), null, m);
    }
}
