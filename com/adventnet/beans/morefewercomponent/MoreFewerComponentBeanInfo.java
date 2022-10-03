package com.adventnet.beans.morefewercomponent;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class MoreFewerComponentBeanInfo extends SimpleBeanInfo
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
        (MoreFewerComponentBeanInfo.beanDescriptor = new BeanDescriptor(MoreFewerComponent.class)).setDisplayName("MoreFewerComponent");
        MoreFewerComponentBeanInfo.beanDescriptor.setValue("isContainer", Boolean.FALSE);
        return MoreFewerComponentBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return MoreFewerComponentBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return MoreFewerComponentBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return MoreFewerComponentBeanInfo.methods;
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
                if (MoreFewerComponentBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (MoreFewerComponentBeanInfo.iconColor16 == null) {
                    MoreFewerComponentBeanInfo.iconColor16 = this.loadImage(MoreFewerComponentBeanInfo.iconNameC16);
                }
                return MoreFewerComponentBeanInfo.iconColor16;
            }
            case 2: {
                if (MoreFewerComponentBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (MoreFewerComponentBeanInfo.iconColor32 == null) {
                    MoreFewerComponentBeanInfo.iconColor32 = this.loadImage(MoreFewerComponentBeanInfo.iconNameC32);
                }
                return MoreFewerComponentBeanInfo.iconColor32;
            }
            case 3: {
                if (MoreFewerComponentBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (MoreFewerComponentBeanInfo.iconMono16 == null) {
                    MoreFewerComponentBeanInfo.iconMono16 = this.loadImage(MoreFewerComponentBeanInfo.iconNameM16);
                }
                return MoreFewerComponentBeanInfo.iconMono16;
            }
            case 4: {
                if (MoreFewerComponentBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (MoreFewerComponentBeanInfo.iconMono32 == null) {
                    MoreFewerComponentBeanInfo.iconMono32 = this.loadImage(MoreFewerComponentBeanInfo.iconNameM32);
                }
                return MoreFewerComponentBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        MoreFewerComponentBeanInfo.beanDescriptor = null;
        MoreFewerComponentBeanInfo.properties = null;
        MoreFewerComponentBeanInfo.eventSets = null;
        MoreFewerComponentBeanInfo.methods = null;
        MoreFewerComponentBeanInfo.iconColor16 = null;
        MoreFewerComponentBeanInfo.iconColor32 = null;
        MoreFewerComponentBeanInfo.iconMono16 = null;
        MoreFewerComponentBeanInfo.iconMono32 = null;
        MoreFewerComponentBeanInfo.iconNameC16 = "/com/adventnet/beans/morefewercomponent/morefewercomponent.gif";
        MoreFewerComponentBeanInfo.iconNameC32 = null;
        MoreFewerComponentBeanInfo.iconNameM16 = null;
        MoreFewerComponentBeanInfo.iconNameM32 = null;
    }
}
