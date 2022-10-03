package com.adventnet.idioms.wizardbuttons1;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class BackNextCancelHelpBeanInfo extends SimpleBeanInfo
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
        (BackNextCancelHelpBeanInfo.beanDescriptor = new BeanDescriptor(BackNextCancelHelp.class)).setValue("isContainer", Boolean.FALSE);
        return BackNextCancelHelpBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return BackNextCancelHelpBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return BackNextCancelHelpBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return BackNextCancelHelpBeanInfo.methods;
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
                if (BackNextCancelHelpBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (BackNextCancelHelpBeanInfo.iconColor16 == null) {
                    BackNextCancelHelpBeanInfo.iconColor16 = this.loadImage(BackNextCancelHelpBeanInfo.iconNameC16);
                }
                return BackNextCancelHelpBeanInfo.iconColor16;
            }
            case 2: {
                if (BackNextCancelHelpBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (BackNextCancelHelpBeanInfo.iconColor32 == null) {
                    BackNextCancelHelpBeanInfo.iconColor32 = this.loadImage(BackNextCancelHelpBeanInfo.iconNameC32);
                }
                return BackNextCancelHelpBeanInfo.iconColor32;
            }
            case 3: {
                if (BackNextCancelHelpBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (BackNextCancelHelpBeanInfo.iconMono16 == null) {
                    BackNextCancelHelpBeanInfo.iconMono16 = this.loadImage(BackNextCancelHelpBeanInfo.iconNameM16);
                }
                return BackNextCancelHelpBeanInfo.iconMono16;
            }
            case 4: {
                if (BackNextCancelHelpBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (BackNextCancelHelpBeanInfo.iconMono32 == null) {
                    BackNextCancelHelpBeanInfo.iconMono32 = this.loadImage(BackNextCancelHelpBeanInfo.iconNameM32);
                }
                return BackNextCancelHelpBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        BackNextCancelHelpBeanInfo.beanDescriptor = null;
        BackNextCancelHelpBeanInfo.properties = null;
        BackNextCancelHelpBeanInfo.eventSets = null;
        BackNextCancelHelpBeanInfo.methods = null;
        BackNextCancelHelpBeanInfo.iconColor16 = null;
        BackNextCancelHelpBeanInfo.iconColor32 = null;
        BackNextCancelHelpBeanInfo.iconMono16 = null;
        BackNextCancelHelpBeanInfo.iconMono32 = null;
        BackNextCancelHelpBeanInfo.iconNameC16 = "/com/adventnet/idioms/wizardbuttons1/b_n_c_h.gif";
        BackNextCancelHelpBeanInfo.iconNameC32 = null;
        BackNextCancelHelpBeanInfo.iconNameM16 = null;
        BackNextCancelHelpBeanInfo.iconNameM32 = null;
    }
}
