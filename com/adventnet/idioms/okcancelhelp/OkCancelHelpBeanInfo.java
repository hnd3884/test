package com.adventnet.idioms.okcancelhelp;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class OkCancelHelpBeanInfo extends SimpleBeanInfo
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
        (OkCancelHelpBeanInfo.beanDescriptor = new BeanDescriptor(OkCancelHelp.class)).setValue("isContainer", Boolean.FALSE);
        return OkCancelHelpBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return OkCancelHelpBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return OkCancelHelpBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return OkCancelHelpBeanInfo.methods;
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
                if (OkCancelHelpBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (OkCancelHelpBeanInfo.iconColor16 == null) {
                    OkCancelHelpBeanInfo.iconColor16 = this.loadImage(OkCancelHelpBeanInfo.iconNameC16);
                }
                return OkCancelHelpBeanInfo.iconColor16;
            }
            case 2: {
                if (OkCancelHelpBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (OkCancelHelpBeanInfo.iconColor32 == null) {
                    OkCancelHelpBeanInfo.iconColor32 = this.loadImage(OkCancelHelpBeanInfo.iconNameC32);
                }
                return OkCancelHelpBeanInfo.iconColor32;
            }
            case 3: {
                if (OkCancelHelpBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (OkCancelHelpBeanInfo.iconMono16 == null) {
                    OkCancelHelpBeanInfo.iconMono16 = this.loadImage(OkCancelHelpBeanInfo.iconNameM16);
                }
                return OkCancelHelpBeanInfo.iconMono16;
            }
            case 4: {
                if (OkCancelHelpBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (OkCancelHelpBeanInfo.iconMono32 == null) {
                    OkCancelHelpBeanInfo.iconMono32 = this.loadImage(OkCancelHelpBeanInfo.iconNameM32);
                }
                return OkCancelHelpBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        OkCancelHelpBeanInfo.beanDescriptor = null;
        OkCancelHelpBeanInfo.properties = null;
        OkCancelHelpBeanInfo.eventSets = null;
        OkCancelHelpBeanInfo.methods = null;
        OkCancelHelpBeanInfo.iconColor16 = null;
        OkCancelHelpBeanInfo.iconColor32 = null;
        OkCancelHelpBeanInfo.iconMono16 = null;
        OkCancelHelpBeanInfo.iconMono32 = null;
        OkCancelHelpBeanInfo.iconNameC16 = "/com/adventnet/idioms/okcancelhelp/o_c_h.gif";
        OkCancelHelpBeanInfo.iconNameC32 = null;
        OkCancelHelpBeanInfo.iconNameM16 = null;
        OkCancelHelpBeanInfo.iconNameM32 = null;
    }
}
