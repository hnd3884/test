package com.adventnet.idioms.addmodifydelete;

import java.awt.Image;
import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.beans.SimpleBeanInfo;

public class AddModifyDeleteBeanInfo extends SimpleBeanInfo
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
        (AddModifyDeleteBeanInfo.beanDescriptor = new BeanDescriptor(AddModifyDelete.class)).setValue("isContainer", Boolean.FALSE);
        return AddModifyDeleteBeanInfo.beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return AddModifyDeleteBeanInfo.properties;
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return AddModifyDeleteBeanInfo.eventSets;
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return AddModifyDeleteBeanInfo.methods;
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
                if (AddModifyDeleteBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (AddModifyDeleteBeanInfo.iconColor16 == null) {
                    AddModifyDeleteBeanInfo.iconColor16 = this.loadImage(AddModifyDeleteBeanInfo.iconNameC16);
                }
                return AddModifyDeleteBeanInfo.iconColor16;
            }
            case 2: {
                if (AddModifyDeleteBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (AddModifyDeleteBeanInfo.iconColor32 == null) {
                    AddModifyDeleteBeanInfo.iconColor32 = this.loadImage(AddModifyDeleteBeanInfo.iconNameC32);
                }
                return AddModifyDeleteBeanInfo.iconColor32;
            }
            case 3: {
                if (AddModifyDeleteBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (AddModifyDeleteBeanInfo.iconMono16 == null) {
                    AddModifyDeleteBeanInfo.iconMono16 = this.loadImage(AddModifyDeleteBeanInfo.iconNameM16);
                }
                return AddModifyDeleteBeanInfo.iconMono16;
            }
            case 4: {
                if (AddModifyDeleteBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (AddModifyDeleteBeanInfo.iconMono32 == null) {
                    AddModifyDeleteBeanInfo.iconMono32 = this.loadImage(AddModifyDeleteBeanInfo.iconNameM32);
                }
                return AddModifyDeleteBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        AddModifyDeleteBeanInfo.beanDescriptor = null;
        AddModifyDeleteBeanInfo.properties = null;
        AddModifyDeleteBeanInfo.eventSets = null;
        AddModifyDeleteBeanInfo.methods = null;
        AddModifyDeleteBeanInfo.iconColor16 = null;
        AddModifyDeleteBeanInfo.iconColor32 = null;
        AddModifyDeleteBeanInfo.iconMono16 = null;
        AddModifyDeleteBeanInfo.iconMono32 = null;
        AddModifyDeleteBeanInfo.iconNameC16 = "/com/adventnet/idioms/addmodifydelete/a_m_d.gif";
        AddModifyDeleteBeanInfo.iconNameC32 = null;
        AddModifyDeleteBeanInfo.iconNameM16 = null;
        AddModifyDeleteBeanInfo.iconNameM32 = null;
    }
}
