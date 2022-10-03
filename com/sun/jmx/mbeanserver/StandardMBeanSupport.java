package com.sun.jmx.mbeanserver;

import javax.management.MBeanInfo;
import javax.management.ObjectName;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import java.lang.reflect.Method;

public class StandardMBeanSupport extends MBeanSupport<Method>
{
    public <T> StandardMBeanSupport(final T t, final Class<T> clazz) throws NotCompliantMBeanException {
        super(t, clazz);
    }
    
    @Override
    MBeanIntrospector<Method> getMBeanIntrospector() {
        return StandardMBeanIntrospector.getInstance();
    }
    
    @Override
    Object getCookie() {
        return null;
    }
    
    @Override
    public void register(final MBeanServer mBeanServer, final ObjectName objectName) {
    }
    
    @Override
    public void unregister() {
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        final MBeanInfo mBeanInfo = super.getMBeanInfo();
        if (StandardMBeanIntrospector.isDefinitelyImmutableInfo(this.getResource().getClass())) {
            return mBeanInfo;
        }
        return new MBeanInfo(mBeanInfo.getClassName(), mBeanInfo.getDescription(), mBeanInfo.getAttributes(), mBeanInfo.getConstructors(), mBeanInfo.getOperations(), MBeanIntrospector.findNotifications(this.getResource()), mBeanInfo.getDescriptor());
    }
}
