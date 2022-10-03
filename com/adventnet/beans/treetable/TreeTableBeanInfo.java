package com.adventnet.beans.treetable;

import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class TreeTableBeanInfo extends SimpleBeanInfo
{
    private static final int PROPERTY_propertyDescriptors = 0;
    private static final int PROPERTY_beanDescriptor = 1;
    private static final int PROPERTY_defaultPropertyIndex = 2;
    private static final int PROPERTY_eventSetDescriptors = 3;
    private static final int PROPERTY_defaultEventIndex = 4;
    private static final int PROPERTY_additionalBeanInfo = 5;
    private static final int PROPERTY_methodDescriptors = 6;
    private static final int PROPERTY_icon = 7;
    private static final int METHOD_loadImage0 = 0;
    private static Image iconColor16;
    private static Image iconColor32;
    private static Image iconMono16;
    private static Image iconMono32;
    private static String iconNameC16;
    private static String iconNameC32;
    private static String iconNameM16;
    private static String iconNameM32;
    private static final int defaultPropertyIndex = -1;
    private static final int defaultEventIndex = -1;
    
    private static BeanDescriptor getBdescriptor() {
        return new BeanDescriptor(TreeTableBeanInfo.class, null);
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        final PropertyDescriptor[] array = new PropertyDescriptor[8];
        try {
            array[0] = new PropertyDescriptor("propertyDescriptors", TreeTableBeanInfo.class, "getPropertyDescriptors", null);
            array[1] = new PropertyDescriptor("beanDescriptor", TreeTableBeanInfo.class, "getBeanDescriptor", null);
            array[2] = new PropertyDescriptor("defaultPropertyIndex", TreeTableBeanInfo.class, "getDefaultPropertyIndex", null);
            array[3] = new PropertyDescriptor("eventSetDescriptors", TreeTableBeanInfo.class, "getEventSetDescriptors", null);
            array[4] = new PropertyDescriptor("defaultEventIndex", TreeTableBeanInfo.class, "getDefaultEventIndex", null);
            array[5] = new PropertyDescriptor("additionalBeanInfo", TreeTableBeanInfo.class, "getAdditionalBeanInfo", null);
            array[6] = new PropertyDescriptor("methodDescriptors", TreeTableBeanInfo.class, "getMethodDescriptors", null);
            array[7] = new IndexedPropertyDescriptor("icon", TreeTableBeanInfo.class, null, null, "getIcon", null);
        }
        catch (final IntrospectionException ex) {}
        return array;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return new EventSetDescriptor[0];
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        final MethodDescriptor[] array = { null };
        try {
            (array[0] = new MethodDescriptor(TreeTableBeanInfo.class.getMethod("loadImage", String.class))).setDisplayName("");
        }
        catch (final Exception ex) {}
        return array;
    }
    
    public BeanDescriptor getBeanDescriptor() {
        return getBdescriptor();
    }
    
    public PropertyDescriptor[] getPropertyDescriptors() {
        return getPdescriptor();
    }
    
    public EventSetDescriptor[] getEventSetDescriptors() {
        return getEdescriptor();
    }
    
    public MethodDescriptor[] getMethodDescriptors() {
        return getMdescriptor();
    }
    
    public int getDefaultPropertyIndex() {
        return -1;
    }
    
    public int getDefaultEventIndex() {
        return -1;
    }
    
    public Image getIcon(final int n) {
        switch (n) {
            case 1: {
                if (TreeTableBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (TreeTableBeanInfo.iconColor16 == null) {
                    TreeTableBeanInfo.iconColor16 = this.loadImage(TreeTableBeanInfo.iconNameC16);
                }
                return TreeTableBeanInfo.iconColor16;
            }
            case 2: {
                if (TreeTableBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (TreeTableBeanInfo.iconColor32 == null) {
                    TreeTableBeanInfo.iconColor32 = this.loadImage(TreeTableBeanInfo.iconNameC32);
                }
                return TreeTableBeanInfo.iconColor32;
            }
            case 3: {
                if (TreeTableBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (TreeTableBeanInfo.iconMono16 == null) {
                    TreeTableBeanInfo.iconMono16 = this.loadImage(TreeTableBeanInfo.iconNameM16);
                }
                return TreeTableBeanInfo.iconMono16;
            }
            case 4: {
                if (TreeTableBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (TreeTableBeanInfo.iconMono32 == null) {
                    TreeTableBeanInfo.iconMono32 = this.loadImage(TreeTableBeanInfo.iconNameM32);
                }
                return TreeTableBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        TreeTableBeanInfo.iconColor16 = null;
        TreeTableBeanInfo.iconColor32 = null;
        TreeTableBeanInfo.iconMono16 = null;
        TreeTableBeanInfo.iconMono32 = null;
        TreeTableBeanInfo.iconNameC16 = "/com/adventnet/beans/treetable/tree_tablecom_16.gif";
        TreeTableBeanInfo.iconNameC32 = null;
        TreeTableBeanInfo.iconNameM16 = null;
        TreeTableBeanInfo.iconNameM32 = null;
    }
}
