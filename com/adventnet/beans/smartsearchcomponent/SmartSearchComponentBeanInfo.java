package com.adventnet.beans.smartsearchcomponent;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class SmartSearchComponentBeanInfo extends SimpleBeanInfo
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
        (SmartSearchComponentBeanInfo.beanDescriptor = new BeanDescriptor(SmartSearchComponent.class)).setDisplayName("SmartSearchComponent");
        SmartSearchComponentBeanInfo.beanDescriptor.setValue("isContainer", Boolean.FALSE);
        return SmartSearchComponentBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return SmartSearchComponentBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return SmartSearchComponentBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return SmartSearchComponentBeanInfo.methods;
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
                if (SmartSearchComponentBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (SmartSearchComponentBeanInfo.iconColor16 == null) {
                    SmartSearchComponentBeanInfo.iconColor16 = this.loadImage(SmartSearchComponentBeanInfo.iconNameC16);
                }
                return SmartSearchComponentBeanInfo.iconColor16;
            }
            case 2: {
                if (SmartSearchComponentBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (SmartSearchComponentBeanInfo.iconColor32 == null) {
                    SmartSearchComponentBeanInfo.iconColor32 = this.loadImage(SmartSearchComponentBeanInfo.iconNameC32);
                }
                return SmartSearchComponentBeanInfo.iconColor32;
            }
            case 3: {
                if (SmartSearchComponentBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (SmartSearchComponentBeanInfo.iconMono16 == null) {
                    SmartSearchComponentBeanInfo.iconMono16 = this.loadImage(SmartSearchComponentBeanInfo.iconNameM16);
                }
                return SmartSearchComponentBeanInfo.iconMono16;
            }
            case 4: {
                if (SmartSearchComponentBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (SmartSearchComponentBeanInfo.iconMono32 == null) {
                    SmartSearchComponentBeanInfo.iconMono32 = this.loadImage(SmartSearchComponentBeanInfo.iconNameM32);
                }
                return SmartSearchComponentBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        SmartSearchComponentBeanInfo.beanDescriptor = null;
        SmartSearchComponentBeanInfo.properties = null;
        SmartSearchComponentBeanInfo.eventSets = null;
        SmartSearchComponentBeanInfo.methods = null;
        SmartSearchComponentBeanInfo.iconColor16 = null;
        SmartSearchComponentBeanInfo.iconColor32 = null;
        SmartSearchComponentBeanInfo.iconMono16 = null;
        SmartSearchComponentBeanInfo.iconMono32 = null;
        SmartSearchComponentBeanInfo.iconNameC16 = "/com/adventnet/beans/smartsearchcomponent/smartsearchcomponent.gif";
        SmartSearchComponentBeanInfo.iconNameC32 = null;
        SmartSearchComponentBeanInfo.iconNameM16 = null;
        SmartSearchComponentBeanInfo.iconNameM32 = null;
    }
}
