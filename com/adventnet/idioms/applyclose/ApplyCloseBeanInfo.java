package com.adventnet.idioms.applyclose;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class ApplyCloseBeanInfo extends SimpleBeanInfo
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
        (ApplyCloseBeanInfo.beanDescriptor = new BeanDescriptor(ApplyClose.class)).setValue("isContainer", Boolean.FALSE);
        return ApplyCloseBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return ApplyCloseBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return ApplyCloseBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return ApplyCloseBeanInfo.methods;
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
                if (ApplyCloseBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (ApplyCloseBeanInfo.iconColor16 == null) {
                    ApplyCloseBeanInfo.iconColor16 = this.loadImage(ApplyCloseBeanInfo.iconNameC16);
                }
                return ApplyCloseBeanInfo.iconColor16;
            }
            case 2: {
                if (ApplyCloseBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (ApplyCloseBeanInfo.iconColor32 == null) {
                    ApplyCloseBeanInfo.iconColor32 = this.loadImage(ApplyCloseBeanInfo.iconNameC32);
                }
                return ApplyCloseBeanInfo.iconColor32;
            }
            case 3: {
                if (ApplyCloseBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (ApplyCloseBeanInfo.iconMono16 == null) {
                    ApplyCloseBeanInfo.iconMono16 = this.loadImage(ApplyCloseBeanInfo.iconNameM16);
                }
                return ApplyCloseBeanInfo.iconMono16;
            }
            case 4: {
                if (ApplyCloseBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (ApplyCloseBeanInfo.iconMono32 == null) {
                    ApplyCloseBeanInfo.iconMono32 = this.loadImage(ApplyCloseBeanInfo.iconNameM32);
                }
                return ApplyCloseBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        ApplyCloseBeanInfo.beanDescriptor = null;
        ApplyCloseBeanInfo.properties = null;
        ApplyCloseBeanInfo.eventSets = null;
        ApplyCloseBeanInfo.methods = null;
        ApplyCloseBeanInfo.iconColor16 = null;
        ApplyCloseBeanInfo.iconColor32 = null;
        ApplyCloseBeanInfo.iconMono16 = null;
        ApplyCloseBeanInfo.iconMono32 = null;
        ApplyCloseBeanInfo.iconNameC16 = "/com/adventnet/idioms/applyclose/a_c.gif";
        ApplyCloseBeanInfo.iconNameC32 = null;
        ApplyCloseBeanInfo.iconNameM16 = null;
        ApplyCloseBeanInfo.iconNameM32 = null;
    }
}
