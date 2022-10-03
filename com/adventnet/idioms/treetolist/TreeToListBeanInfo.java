package com.adventnet.idioms.treetolist;

import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.IntrospectionException;
import java.beans.IndexedPropertyDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class TreeToListBeanInfo extends SimpleBeanInfo
{
    private static final int PROPERTY_propertyDescriptors = 0;
    private static final int PROPERTY_methodDescriptors = 1;
    private static final int PROPERTY_additionalBeanInfo = 2;
    private static final int PROPERTY_eventSetDescriptors = 3;
    private static final int PROPERTY_beanDescriptor = 4;
    private static final int PROPERTY_defaultPropertyIndex = 5;
    private static final int PROPERTY_defaultEventIndex = 6;
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
        final BeanDescriptor beanDescriptor = new BeanDescriptor(TreeToListBeanInfo.class, null);
        final BeanDescriptor beanDescriptor2 = new BeanDescriptor(TreeToList.class);
        beanDescriptor2.setDisplayName("TreeToList");
        beanDescriptor2.setValue("isContainer", Boolean.FALSE);
        return beanDescriptor2;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        final PropertyDescriptor[] array = new PropertyDescriptor[8];
        try {
            array[0] = new PropertyDescriptor("propertyDescriptors", TreeToListBeanInfo.class, "getPropertyDescriptors", null);
            array[1] = new PropertyDescriptor("methodDescriptors", TreeToListBeanInfo.class, "getMethodDescriptors", null);
            array[2] = new PropertyDescriptor("additionalBeanInfo", TreeToListBeanInfo.class, "getAdditionalBeanInfo", null);
            array[3] = new PropertyDescriptor("eventSetDescriptors", TreeToListBeanInfo.class, "getEventSetDescriptors", null);
            array[4] = new PropertyDescriptor("beanDescriptor", TreeToListBeanInfo.class, "getBeanDescriptor", null);
            array[5] = new PropertyDescriptor("defaultPropertyIndex", TreeToListBeanInfo.class, "getDefaultPropertyIndex", null);
            array[6] = new PropertyDescriptor("defaultEventIndex", TreeToListBeanInfo.class, "getDefaultEventIndex", null);
            array[7] = new IndexedPropertyDescriptor("icon", TreeToListBeanInfo.class, null, null, "getIcon", null);
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
            (array[0] = new MethodDescriptor(TreeToListBeanInfo.class.getMethod("loadImage", String.class))).setDisplayName("");
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
                if (TreeToListBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (TreeToListBeanInfo.iconColor16 == null) {
                    TreeToListBeanInfo.iconColor16 = this.loadImage(TreeToListBeanInfo.iconNameC16);
                }
                return TreeToListBeanInfo.iconColor16;
            }
            case 2: {
                if (TreeToListBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (TreeToListBeanInfo.iconColor32 == null) {
                    TreeToListBeanInfo.iconColor32 = this.loadImage(TreeToListBeanInfo.iconNameC32);
                }
                return TreeToListBeanInfo.iconColor32;
            }
            case 3: {
                if (TreeToListBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (TreeToListBeanInfo.iconMono16 == null) {
                    TreeToListBeanInfo.iconMono16 = this.loadImage(TreeToListBeanInfo.iconNameM16);
                }
                return TreeToListBeanInfo.iconMono16;
            }
            case 4: {
                if (TreeToListBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (TreeToListBeanInfo.iconMono32 == null) {
                    TreeToListBeanInfo.iconMono32 = this.loadImage(TreeToListBeanInfo.iconNameM32);
                }
                return TreeToListBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        TreeToListBeanInfo.iconColor16 = null;
        TreeToListBeanInfo.iconColor32 = null;
        TreeToListBeanInfo.iconMono16 = null;
        TreeToListBeanInfo.iconMono32 = null;
        TreeToListBeanInfo.iconNameC16 = "/com/adventnet/idioms/treetolist/treetolist.gif";
        TreeToListBeanInfo.iconNameC32 = null;
        TreeToListBeanInfo.iconNameM16 = null;
        TreeToListBeanInfo.iconNameM32 = null;
    }
}
