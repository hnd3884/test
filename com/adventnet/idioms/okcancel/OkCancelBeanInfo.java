package com.adventnet.idioms.okcancel;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class OkCancelBeanInfo extends SimpleBeanInfo
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
        (OkCancelBeanInfo.beanDescriptor = new BeanDescriptor(OkCancel.class)).setValue("isContainer", Boolean.FALSE);
        return OkCancelBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return OkCancelBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return OkCancelBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return OkCancelBeanInfo.methods;
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
                if (OkCancelBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (OkCancelBeanInfo.iconColor16 == null) {
                    OkCancelBeanInfo.iconColor16 = this.loadImage(OkCancelBeanInfo.iconNameC16);
                }
                return OkCancelBeanInfo.iconColor16;
            }
            case 2: {
                if (OkCancelBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (OkCancelBeanInfo.iconColor32 == null) {
                    OkCancelBeanInfo.iconColor32 = this.loadImage(OkCancelBeanInfo.iconNameC32);
                }
                return OkCancelBeanInfo.iconColor32;
            }
            case 3: {
                if (OkCancelBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (OkCancelBeanInfo.iconMono16 == null) {
                    OkCancelBeanInfo.iconMono16 = this.loadImage(OkCancelBeanInfo.iconNameM16);
                }
                return OkCancelBeanInfo.iconMono16;
            }
            case 4: {
                if (OkCancelBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (OkCancelBeanInfo.iconMono32 == null) {
                    OkCancelBeanInfo.iconMono32 = this.loadImage(OkCancelBeanInfo.iconNameM32);
                }
                return OkCancelBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        OkCancelBeanInfo.beanDescriptor = null;
        OkCancelBeanInfo.properties = null;
        OkCancelBeanInfo.eventSets = null;
        OkCancelBeanInfo.methods = null;
        OkCancelBeanInfo.iconColor16 = null;
        OkCancelBeanInfo.iconColor32 = null;
        OkCancelBeanInfo.iconMono16 = null;
        OkCancelBeanInfo.iconMono32 = null;
        OkCancelBeanInfo.iconNameC16 = "/com/adventnet/idioms/okcancel/o_c.gif";
        OkCancelBeanInfo.iconNameC32 = null;
        OkCancelBeanInfo.iconNameM16 = null;
        OkCancelBeanInfo.iconNameM32 = null;
    }
}
