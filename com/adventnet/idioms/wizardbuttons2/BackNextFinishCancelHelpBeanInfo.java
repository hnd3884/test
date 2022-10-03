package com.adventnet.idioms.wizardbuttons2;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class BackNextFinishCancelHelpBeanInfo extends SimpleBeanInfo
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
        (BackNextFinishCancelHelpBeanInfo.beanDescriptor = new BeanDescriptor(BackNextFinishCancelHelp.class)).setValue("isContainer", Boolean.FALSE);
        return BackNextFinishCancelHelpBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return BackNextFinishCancelHelpBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return BackNextFinishCancelHelpBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return BackNextFinishCancelHelpBeanInfo.methods;
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
                if (BackNextFinishCancelHelpBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (BackNextFinishCancelHelpBeanInfo.iconColor16 == null) {
                    BackNextFinishCancelHelpBeanInfo.iconColor16 = this.loadImage(BackNextFinishCancelHelpBeanInfo.iconNameC16);
                }
                return BackNextFinishCancelHelpBeanInfo.iconColor16;
            }
            case 2: {
                if (BackNextFinishCancelHelpBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (BackNextFinishCancelHelpBeanInfo.iconColor32 == null) {
                    BackNextFinishCancelHelpBeanInfo.iconColor32 = this.loadImage(BackNextFinishCancelHelpBeanInfo.iconNameC32);
                }
                return BackNextFinishCancelHelpBeanInfo.iconColor32;
            }
            case 3: {
                if (BackNextFinishCancelHelpBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (BackNextFinishCancelHelpBeanInfo.iconMono16 == null) {
                    BackNextFinishCancelHelpBeanInfo.iconMono16 = this.loadImage(BackNextFinishCancelHelpBeanInfo.iconNameM16);
                }
                return BackNextFinishCancelHelpBeanInfo.iconMono16;
            }
            case 4: {
                if (BackNextFinishCancelHelpBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (BackNextFinishCancelHelpBeanInfo.iconMono32 == null) {
                    BackNextFinishCancelHelpBeanInfo.iconMono32 = this.loadImage(BackNextFinishCancelHelpBeanInfo.iconNameM32);
                }
                return BackNextFinishCancelHelpBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        BackNextFinishCancelHelpBeanInfo.beanDescriptor = null;
        BackNextFinishCancelHelpBeanInfo.properties = null;
        BackNextFinishCancelHelpBeanInfo.eventSets = null;
        BackNextFinishCancelHelpBeanInfo.methods = null;
        BackNextFinishCancelHelpBeanInfo.iconColor16 = null;
        BackNextFinishCancelHelpBeanInfo.iconColor32 = null;
        BackNextFinishCancelHelpBeanInfo.iconMono16 = null;
        BackNextFinishCancelHelpBeanInfo.iconMono32 = null;
        BackNextFinishCancelHelpBeanInfo.iconNameC16 = "/com/adventnet/idioms/wizardbuttons2/b_n_c_h_f.gif";
        BackNextFinishCancelHelpBeanInfo.iconNameC32 = null;
        BackNextFinishCancelHelpBeanInfo.iconNameM16 = null;
        BackNextFinishCancelHelpBeanInfo.iconNameM32 = null;
    }
}
