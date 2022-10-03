package com.adventnet.idioms.okcancelapplyhelp;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class OkCancelApplyHelpBeanInfo extends SimpleBeanInfo
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
        (OkCancelApplyHelpBeanInfo.beanDescriptor = new BeanDescriptor(OkCancelApplyHelp.class)).setValue("isContainer", Boolean.FALSE);
        return OkCancelApplyHelpBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return OkCancelApplyHelpBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return OkCancelApplyHelpBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return OkCancelApplyHelpBeanInfo.methods;
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
                if (OkCancelApplyHelpBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (OkCancelApplyHelpBeanInfo.iconColor16 == null) {
                    OkCancelApplyHelpBeanInfo.iconColor16 = this.loadImage(OkCancelApplyHelpBeanInfo.iconNameC16);
                }
                return OkCancelApplyHelpBeanInfo.iconColor16;
            }
            case 2: {
                if (OkCancelApplyHelpBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (OkCancelApplyHelpBeanInfo.iconColor32 == null) {
                    OkCancelApplyHelpBeanInfo.iconColor32 = this.loadImage(OkCancelApplyHelpBeanInfo.iconNameC32);
                }
                return OkCancelApplyHelpBeanInfo.iconColor32;
            }
            case 3: {
                if (OkCancelApplyHelpBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (OkCancelApplyHelpBeanInfo.iconMono16 == null) {
                    OkCancelApplyHelpBeanInfo.iconMono16 = this.loadImage(OkCancelApplyHelpBeanInfo.iconNameM16);
                }
                return OkCancelApplyHelpBeanInfo.iconMono16;
            }
            case 4: {
                if (OkCancelApplyHelpBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (OkCancelApplyHelpBeanInfo.iconMono32 == null) {
                    OkCancelApplyHelpBeanInfo.iconMono32 = this.loadImage(OkCancelApplyHelpBeanInfo.iconNameM32);
                }
                return OkCancelApplyHelpBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        OkCancelApplyHelpBeanInfo.beanDescriptor = null;
        OkCancelApplyHelpBeanInfo.properties = null;
        OkCancelApplyHelpBeanInfo.eventSets = null;
        OkCancelApplyHelpBeanInfo.methods = null;
        OkCancelApplyHelpBeanInfo.iconColor16 = null;
        OkCancelApplyHelpBeanInfo.iconColor32 = null;
        OkCancelApplyHelpBeanInfo.iconMono16 = null;
        OkCancelApplyHelpBeanInfo.iconMono32 = null;
        OkCancelApplyHelpBeanInfo.iconNameC16 = "/com/adventnet/idioms/okcancelapplyhelp/o_c_a_h.gif";
        OkCancelApplyHelpBeanInfo.iconNameC32 = null;
        OkCancelApplyHelpBeanInfo.iconNameM16 = null;
        OkCancelApplyHelpBeanInfo.iconNameM32 = null;
    }
}
