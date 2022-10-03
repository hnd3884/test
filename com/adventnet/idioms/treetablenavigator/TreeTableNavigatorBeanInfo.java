package com.adventnet.idioms.treetablenavigator;

import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class TreeTableNavigatorBeanInfo extends SimpleBeanInfo
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
        final BeanDescriptor beanDescriptor = new BeanDescriptor(TreeTableNavigatorBeanInfo.class, null);
        final BeanDescriptor beanDescriptor2 = new BeanDescriptor(TreeTableNavigator.class);
        beanDescriptor2.setDisplayName("TreeTableNavigator");
        beanDescriptor2.setValue("isContainer", Boolean.FALSE);
        return beanDescriptor2;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        final PropertyDescriptor[] array = new PropertyDescriptor[8];
        try {
            array[0] = new PropertyDescriptor("propertyDescriptors", TreeTableNavigatorBeanInfo.class, "getPropertyDescriptors", null);
            array[1] = new PropertyDescriptor("beanDescriptor", TreeTableNavigatorBeanInfo.class, "getBeanDescriptor", null);
            array[2] = new PropertyDescriptor("defaultPropertyIndex", TreeTableNavigatorBeanInfo.class, "getDefaultPropertyIndex", null);
            array[3] = new PropertyDescriptor("eventSetDescriptors", TreeTableNavigatorBeanInfo.class, "getEventSetDescriptors", null);
            array[4] = new PropertyDescriptor("defaultEventIndex", TreeTableNavigatorBeanInfo.class, "getDefaultEventIndex", null);
            array[5] = new PropertyDescriptor("additionalBeanInfo", TreeTableNavigatorBeanInfo.class, "getAdditionalBeanInfo", null);
            array[6] = new PropertyDescriptor("methodDescriptors", TreeTableNavigatorBeanInfo.class, "getMethodDescriptors", null);
            array[7] = new IndexedPropertyDescriptor("icon", TreeTableNavigatorBeanInfo.class, null, null, "getIcon", null);
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
            (array[0] = new MethodDescriptor(TreeTableNavigatorBeanInfo.class.getMethod("loadImage", String.class))).setDisplayName("");
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
                if (TreeTableNavigatorBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (TreeTableNavigatorBeanInfo.iconColor16 == null) {
                    TreeTableNavigatorBeanInfo.iconColor16 = this.loadImage(TreeTableNavigatorBeanInfo.iconNameC16);
                }
                return TreeTableNavigatorBeanInfo.iconColor16;
            }
            case 2: {
                if (TreeTableNavigatorBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (TreeTableNavigatorBeanInfo.iconColor32 == null) {
                    TreeTableNavigatorBeanInfo.iconColor32 = this.loadImage(TreeTableNavigatorBeanInfo.iconNameC32);
                }
                return TreeTableNavigatorBeanInfo.iconColor32;
            }
            case 3: {
                if (TreeTableNavigatorBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (TreeTableNavigatorBeanInfo.iconMono16 == null) {
                    TreeTableNavigatorBeanInfo.iconMono16 = this.loadImage(TreeTableNavigatorBeanInfo.iconNameM16);
                }
                return TreeTableNavigatorBeanInfo.iconMono16;
            }
            case 4: {
                if (TreeTableNavigatorBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (TreeTableNavigatorBeanInfo.iconMono32 == null) {
                    TreeTableNavigatorBeanInfo.iconMono32 = this.loadImage(TreeTableNavigatorBeanInfo.iconNameM32);
                }
                return TreeTableNavigatorBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        TreeTableNavigatorBeanInfo.iconColor16 = null;
        TreeTableNavigatorBeanInfo.iconColor32 = null;
        TreeTableNavigatorBeanInfo.iconMono16 = null;
        TreeTableNavigatorBeanInfo.iconMono32 = null;
        TreeTableNavigatorBeanInfo.iconNameC16 = "/com/adventnet/idioms/treetablenavigator/tree_tablenav_16.gif";
        TreeTableNavigatorBeanInfo.iconNameC32 = null;
        TreeTableNavigatorBeanInfo.iconNameM16 = null;
        TreeTableNavigatorBeanInfo.iconNameM32 = null;
    }
}
