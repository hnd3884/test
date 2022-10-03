package org.apache.commons.beanutils;

import java.lang.reflect.Method;
import java.util.List;
import java.beans.IndexedPropertyDescriptor;
import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class DefaultBeanIntrospector implements BeanIntrospector
{
    public static final BeanIntrospector INSTANCE;
    private static final Class<?>[] EMPTY_CLASS_PARAMETERS;
    private static final Class<?>[] LIST_CLASS_PARAMETER;
    private final Log log;
    
    private DefaultBeanIntrospector() {
        this.log = LogFactory.getLog((Class)this.getClass());
    }
    
    @Override
    public void introspect(final IntrospectionContext icontext) {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(icontext.getTargetClass());
        }
        catch (final IntrospectionException e) {
            this.log.error((Object)("Error when inspecting class " + icontext.getTargetClass()), (Throwable)e);
            return;
        }
        PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();
        if (descriptors == null) {
            descriptors = new PropertyDescriptor[0];
        }
        this.handleIndexedPropertyDescriptors(icontext.getTargetClass(), descriptors);
        icontext.addPropertyDescriptors(descriptors);
    }
    
    private void handleIndexedPropertyDescriptors(final Class<?> beanClass, final PropertyDescriptor[] descriptors) {
        for (final PropertyDescriptor pd : descriptors) {
            if (pd instanceof IndexedPropertyDescriptor) {
                final IndexedPropertyDescriptor descriptor = (IndexedPropertyDescriptor)pd;
                final String propName = descriptor.getName().substring(0, 1).toUpperCase() + descriptor.getName().substring(1);
                if (descriptor.getReadMethod() == null) {
                    final String methodName = (descriptor.getIndexedReadMethod() != null) ? descriptor.getIndexedReadMethod().getName() : ("get" + propName);
                    final Method readMethod = MethodUtils.getMatchingAccessibleMethod(beanClass, methodName, DefaultBeanIntrospector.EMPTY_CLASS_PARAMETERS);
                    if (readMethod != null) {
                        try {
                            descriptor.setReadMethod(readMethod);
                        }
                        catch (final Exception e) {
                            this.log.error((Object)"Error setting indexed property read method", (Throwable)e);
                        }
                    }
                }
                if (descriptor.getWriteMethod() == null) {
                    final String methodName = (descriptor.getIndexedWriteMethod() != null) ? descriptor.getIndexedWriteMethod().getName() : ("set" + propName);
                    Method writeMethod = MethodUtils.getMatchingAccessibleMethod(beanClass, methodName, DefaultBeanIntrospector.LIST_CLASS_PARAMETER);
                    if (writeMethod == null) {
                        for (final Method m : beanClass.getMethods()) {
                            if (m.getName().equals(methodName)) {
                                final Class<?>[] parameterTypes = m.getParameterTypes();
                                if (parameterTypes.length == 1 && List.class.isAssignableFrom(parameterTypes[0])) {
                                    writeMethod = m;
                                    break;
                                }
                            }
                        }
                    }
                    if (writeMethod != null) {
                        try {
                            descriptor.setWriteMethod(writeMethod);
                        }
                        catch (final Exception e) {
                            this.log.error((Object)"Error setting indexed property write method", (Throwable)e);
                        }
                    }
                }
            }
        }
    }
    
    static {
        INSTANCE = new DefaultBeanIntrospector();
        EMPTY_CLASS_PARAMETERS = new Class[0];
        LIST_CLASS_PARAMETER = new Class[] { List.class };
    }
}
