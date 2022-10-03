package com.sun.beans.finder;

import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.lang.reflect.Method;
import java.beans.BeanInfo;

public final class BeanInfoFinder extends InstanceFinder<BeanInfo>
{
    private static final String DEFAULT = "sun.beans.infos";
    private static final String DEFAULT_NEW = "com.sun.beans.infos";
    
    public BeanInfoFinder() {
        super(BeanInfo.class, true, "BeanInfo", new String[] { "sun.beans.infos" });
    }
    
    private static boolean isValid(final Class<?> clazz, final Method method) {
        return method != null && method.getDeclaringClass().isAssignableFrom(clazz);
    }
    
    @Override
    protected BeanInfo instantiate(final Class<?> clazz, String s, final String s2) {
        if ("sun.beans.infos".equals(s)) {
            s = "com.sun.beans.infos";
        }
        final BeanInfo beanInfo = (!"com.sun.beans.infos".equals(s) || "ComponentBeanInfo".equals(s2)) ? super.instantiate(clazz, s, s2) : null;
        if (beanInfo != null) {
            final BeanDescriptor beanDescriptor = beanInfo.getBeanDescriptor();
            if (beanDescriptor != null) {
                if (clazz.equals(beanDescriptor.getBeanClass())) {
                    return beanInfo;
                }
            }
            else {
                final PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
                if (propertyDescriptors != null) {
                    for (final PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        Method method = propertyDescriptor.getReadMethod();
                        if (method == null) {
                            method = propertyDescriptor.getWriteMethod();
                        }
                        if (isValid(clazz, method)) {
                            return beanInfo;
                        }
                    }
                }
                else {
                    final MethodDescriptor[] methodDescriptors = beanInfo.getMethodDescriptors();
                    if (methodDescriptors != null) {
                        final MethodDescriptor[] array2 = methodDescriptors;
                        for (int length2 = array2.length, j = 0; j < length2; ++j) {
                            if (isValid(clazz, array2[j].getMethod())) {
                                return beanInfo;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
