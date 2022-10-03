package com.sun.beans.infos;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.awt.Component;
import java.beans.SimpleBeanInfo;

public class ComponentBeanInfo extends SimpleBeanInfo
{
    private static final Class<Component> beanClass;
    
    @Override
    public PropertyDescriptor[] getPropertyDescriptors() {
        try {
            final PropertyDescriptor propertyDescriptor = new PropertyDescriptor("name", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor2 = new PropertyDescriptor("background", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor3 = new PropertyDescriptor("foreground", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor4 = new PropertyDescriptor("font", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor5 = new PropertyDescriptor("enabled", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor6 = new PropertyDescriptor("visible", ComponentBeanInfo.beanClass);
            final PropertyDescriptor propertyDescriptor7 = new PropertyDescriptor("focusable", ComponentBeanInfo.beanClass);
            propertyDescriptor5.setExpert(true);
            propertyDescriptor6.setHidden(true);
            propertyDescriptor2.setBound(true);
            propertyDescriptor3.setBound(true);
            propertyDescriptor4.setBound(true);
            propertyDescriptor7.setBound(true);
            return new PropertyDescriptor[] { propertyDescriptor, propertyDescriptor2, propertyDescriptor3, propertyDescriptor4, propertyDescriptor5, propertyDescriptor6, propertyDescriptor7 };
        }
        catch (final IntrospectionException ex) {
            throw new Error(ex.toString());
        }
    }
    
    static {
        beanClass = Component.class;
    }
}
