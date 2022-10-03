package com.adventnet.beans.xtable;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class XTableBeanInfo extends SimpleBeanInfo
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
        return XTableBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return XTableBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return XTableBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return XTableBeanInfo.methods;
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
                if (XTableBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (XTableBeanInfo.iconColor16 == null) {
                    XTableBeanInfo.iconColor16 = this.loadImage(XTableBeanInfo.iconNameC16);
                }
                return XTableBeanInfo.iconColor16;
            }
            case 2: {
                if (XTableBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (XTableBeanInfo.iconColor32 == null) {
                    XTableBeanInfo.iconColor32 = this.loadImage(XTableBeanInfo.iconNameC32);
                }
                return XTableBeanInfo.iconColor32;
            }
            case 3: {
                if (XTableBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (XTableBeanInfo.iconMono16 == null) {
                    XTableBeanInfo.iconMono16 = this.loadImage(XTableBeanInfo.iconNameM16);
                }
                return XTableBeanInfo.iconMono16;
            }
            case 4: {
                if (XTableBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (XTableBeanInfo.iconMono32 == null) {
                    XTableBeanInfo.iconMono32 = this.loadImage(XTableBeanInfo.iconNameM32);
                }
                return XTableBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        XTableBeanInfo.beanDescriptor = null;
        XTableBeanInfo.properties = null;
        XTableBeanInfo.eventSets = null;
        XTableBeanInfo.methods = null;
        XTableBeanInfo.iconColor16 = null;
        XTableBeanInfo.iconColor32 = null;
        XTableBeanInfo.iconMono16 = null;
        XTableBeanInfo.iconMono32 = null;
        XTableBeanInfo.iconNameC16 = "/com/adventnet/beans/xtable/xtable.gif";
        XTableBeanInfo.iconNameC32 = null;
        XTableBeanInfo.iconNameM16 = null;
        XTableBeanInfo.iconNameM32 = null;
    }
}
