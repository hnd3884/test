package com.adventnet.idioms.tablenavigator;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class TableNavigatorBeanInfo extends SimpleBeanInfo
{
    private static BeanDescriptor beanDescriptor;
    private static PropertyDescriptor[] properties;
    private static EventSetDescriptor[] eventSets;
    private static MethodDescriptor[] methods;
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
        (TableNavigatorBeanInfo.beanDescriptor = new BeanDescriptor(TableNavigator.class)).setDisplayName("TableNavigator");
        TableNavigatorBeanInfo.beanDescriptor.setValue("isContainer", Boolean.FALSE);
        return TableNavigatorBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return TableNavigatorBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return TableNavigatorBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return TableNavigatorBeanInfo.methods;
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
                if (TableNavigatorBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (TableNavigatorBeanInfo.iconColor16 == null) {
                    TableNavigatorBeanInfo.iconColor16 = this.loadImage(TableNavigatorBeanInfo.iconNameC16);
                }
                return TableNavigatorBeanInfo.iconColor16;
            }
            case 2: {
                if (TableNavigatorBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (TableNavigatorBeanInfo.iconColor32 == null) {
                    TableNavigatorBeanInfo.iconColor32 = this.loadImage(TableNavigatorBeanInfo.iconNameC32);
                }
                return TableNavigatorBeanInfo.iconColor32;
            }
            case 3: {
                if (TableNavigatorBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (TableNavigatorBeanInfo.iconMono16 == null) {
                    TableNavigatorBeanInfo.iconMono16 = this.loadImage(TableNavigatorBeanInfo.iconNameM16);
                }
                return TableNavigatorBeanInfo.iconMono16;
            }
            case 4: {
                if (TableNavigatorBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (TableNavigatorBeanInfo.iconMono32 == null) {
                    TableNavigatorBeanInfo.iconMono32 = this.loadImage(TableNavigatorBeanInfo.iconNameM32);
                }
                return TableNavigatorBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        TableNavigatorBeanInfo.beanDescriptor = null;
        TableNavigatorBeanInfo.properties = null;
        TableNavigatorBeanInfo.eventSets = null;
        TableNavigatorBeanInfo.methods = null;
        TableNavigatorBeanInfo.iconColor16 = null;
        TableNavigatorBeanInfo.iconColor32 = null;
        TableNavigatorBeanInfo.iconMono16 = null;
        TableNavigatorBeanInfo.iconMono32 = null;
        TableNavigatorBeanInfo.iconNameC16 = "/com/adventnet/idioms/tablenavigator/tablenavigator.gif";
        TableNavigatorBeanInfo.iconNameC32 = null;
        TableNavigatorBeanInfo.iconNameM16 = null;
        TableNavigatorBeanInfo.iconNameM32 = null;
    }
}
