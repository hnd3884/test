package org.apache.commons.beanutils;

import java.util.HashMap;
import java.beans.IntrospectionException;
import java.lang.reflect.Method;
import java.util.Map;
import java.beans.PropertyDescriptor;

class BeanIntrospectionData
{
    private final PropertyDescriptor[] descriptors;
    private final Map<String, String> writeMethodNames;
    
    public BeanIntrospectionData(final PropertyDescriptor[] descs) {
        this(descs, setUpWriteMethodNames(descs));
    }
    
    BeanIntrospectionData(final PropertyDescriptor[] descs, final Map<String, String> writeMethNames) {
        this.descriptors = descs;
        this.writeMethodNames = writeMethNames;
    }
    
    public PropertyDescriptor[] getDescriptors() {
        return this.descriptors;
    }
    
    public PropertyDescriptor getDescriptor(final String name) {
        for (final PropertyDescriptor pd : this.getDescriptors()) {
            if (name.equals(pd.getName())) {
                return pd;
            }
        }
        return null;
    }
    
    public Method getWriteMethod(final Class<?> beanCls, final PropertyDescriptor desc) {
        Method method = desc.getWriteMethod();
        if (method == null) {
            final String methodName = this.writeMethodNames.get(desc.getName());
            if (methodName != null) {
                method = MethodUtils.getAccessibleMethod(beanCls, methodName, desc.getPropertyType());
                if (method != null) {
                    try {
                        desc.setWriteMethod(method);
                    }
                    catch (final IntrospectionException ex) {}
                }
            }
        }
        return method;
    }
    
    private static Map<String, String> setUpWriteMethodNames(final PropertyDescriptor[] descs) {
        final Map<String, String> methods = new HashMap<String, String>();
        for (final PropertyDescriptor pd : descs) {
            final Method method = pd.getWriteMethod();
            if (method != null) {
                methods.put(pd.getName(), method.getName());
            }
        }
        return methods;
    }
}
