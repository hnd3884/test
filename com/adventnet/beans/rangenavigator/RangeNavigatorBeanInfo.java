package com.adventnet.beans.rangenavigator;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class RangeNavigatorBeanInfo extends SimpleBeanInfo
{
    private static final Class beanClass;
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
        (RangeNavigatorBeanInfo.beanDescriptor = new BeanDescriptor(RangeNavigator.class)).setValue("isContainer", Boolean.FALSE);
        return RangeNavigatorBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return RangeNavigatorBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return RangeNavigatorBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return RangeNavigatorBeanInfo.methods;
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
                if (RangeNavigatorBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (RangeNavigatorBeanInfo.iconColor16 == null) {
                    RangeNavigatorBeanInfo.iconColor16 = this.loadImage(RangeNavigatorBeanInfo.iconNameC16);
                }
                return RangeNavigatorBeanInfo.iconColor16;
            }
            case 2: {
                if (RangeNavigatorBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (RangeNavigatorBeanInfo.iconColor32 == null) {
                    RangeNavigatorBeanInfo.iconColor32 = this.loadImage(RangeNavigatorBeanInfo.iconNameC32);
                }
                return RangeNavigatorBeanInfo.iconColor32;
            }
            case 3: {
                if (RangeNavigatorBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (RangeNavigatorBeanInfo.iconMono16 == null) {
                    RangeNavigatorBeanInfo.iconMono16 = this.loadImage(RangeNavigatorBeanInfo.iconNameM16);
                }
                return RangeNavigatorBeanInfo.iconMono16;
            }
            case 4: {
                if (RangeNavigatorBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (RangeNavigatorBeanInfo.iconMono32 == null) {
                    RangeNavigatorBeanInfo.iconMono32 = this.loadImage(RangeNavigatorBeanInfo.iconNameM32);
                }
                return RangeNavigatorBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        beanClass = RangeNavigator.class;
        RangeNavigatorBeanInfo.beanDescriptor = null;
        RangeNavigatorBeanInfo.properties = null;
        RangeNavigatorBeanInfo.eventSets = null;
        RangeNavigatorBeanInfo.methods = null;
        RangeNavigatorBeanInfo.iconColor16 = null;
        RangeNavigatorBeanInfo.iconColor32 = null;
        RangeNavigatorBeanInfo.iconMono16 = null;
        RangeNavigatorBeanInfo.iconMono32 = null;
        RangeNavigatorBeanInfo.iconNameC16 = "/com/adventnet/beans/rangenavigator/rangenavigator.gif";
        RangeNavigatorBeanInfo.iconNameC32 = null;
        RangeNavigatorBeanInfo.iconNameM16 = null;
        RangeNavigatorBeanInfo.iconNameM32 = null;
    }
}
