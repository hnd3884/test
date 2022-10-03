package com.adventnet.beans.ipaddressfield;

import java.beans.BeanDescriptor;
import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;

public class IpAddressComponentBeanInfo extends SimpleBeanInfo
{
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
        final BeanDescriptor beanDescriptor = new BeanDescriptor(IpAddressComponent.class, null);
        beanDescriptor.setValue("isContainer", Boolean.FALSE);
        return beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return IpAddressComponentBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return IpAddressComponentBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return IpAddressComponentBeanInfo.methods;
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
                if (IpAddressComponentBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (IpAddressComponentBeanInfo.iconColor16 == null) {
                    IpAddressComponentBeanInfo.iconColor16 = this.loadImage(IpAddressComponentBeanInfo.iconNameC16);
                }
                return IpAddressComponentBeanInfo.iconColor16;
            }
            case 2: {
                if (IpAddressComponentBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (IpAddressComponentBeanInfo.iconColor32 == null) {
                    IpAddressComponentBeanInfo.iconColor32 = this.loadImage(IpAddressComponentBeanInfo.iconNameC32);
                }
                return IpAddressComponentBeanInfo.iconColor32;
            }
            case 3: {
                if (IpAddressComponentBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (IpAddressComponentBeanInfo.iconMono16 == null) {
                    IpAddressComponentBeanInfo.iconMono16 = this.loadImage(IpAddressComponentBeanInfo.iconNameM16);
                }
                return IpAddressComponentBeanInfo.iconMono16;
            }
            case 4: {
                if (IpAddressComponentBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (IpAddressComponentBeanInfo.iconMono32 == null) {
                    IpAddressComponentBeanInfo.iconMono32 = this.loadImage(IpAddressComponentBeanInfo.iconNameM32);
                }
                return IpAddressComponentBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        IpAddressComponentBeanInfo.properties = null;
        IpAddressComponentBeanInfo.eventSets = null;
        IpAddressComponentBeanInfo.methods = null;
        IpAddressComponentBeanInfo.iconColor16 = null;
        IpAddressComponentBeanInfo.iconColor32 = null;
        IpAddressComponentBeanInfo.iconMono16 = null;
        IpAddressComponentBeanInfo.iconMono32 = null;
        IpAddressComponentBeanInfo.iconNameC16 = "/com/adventnet/beans/ipaddressfield/IpAddressField.png";
        IpAddressComponentBeanInfo.iconNameC32 = null;
        IpAddressComponentBeanInfo.iconNameM16 = null;
        IpAddressComponentBeanInfo.iconNameM32 = null;
    }
}
