package com.sun.org.glassfish.gmbal;

import javax.management.MBeanNotificationInfo;
import javax.management.ListenerNotFoundException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.MBeanInfo;
import javax.management.AttributeList;
import javax.management.InvalidAttributeValueException;
import javax.management.Attribute;
import javax.management.ReflectionException;
import javax.management.MBeanException;
import javax.management.AttributeNotFoundException;

public class GmbalMBeanNOPImpl implements GmbalMBean
{
    @Override
    public Object getAttribute(final String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException {
        return null;
    }
    
    @Override
    public void setAttribute(final Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
    }
    
    @Override
    public AttributeList getAttributes(final String[] attributes) {
        return null;
    }
    
    @Override
    public AttributeList setAttributes(final AttributeList attributes) {
        return null;
    }
    
    @Override
    public Object invoke(final String actionName, final Object[] params, final String[] signature) throws MBeanException, ReflectionException {
        return null;
    }
    
    @Override
    public MBeanInfo getMBeanInfo() {
        return null;
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener listener, final NotificationFilter filter, final Object handback) throws ListenerNotFoundException {
    }
    
    @Override
    public void addNotificationListener(final NotificationListener listener, final NotificationFilter filter, final Object handback) throws IllegalArgumentException {
    }
    
    @Override
    public void removeNotificationListener(final NotificationListener listener) throws ListenerNotFoundException {
    }
    
    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[0];
    }
}
