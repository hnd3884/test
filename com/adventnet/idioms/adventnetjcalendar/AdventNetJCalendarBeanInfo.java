package com.adventnet.idioms.adventnetjcalendar;

import java.beans.MethodDescriptor;
import java.beans.EventSetDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.BeanDescriptor;
import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class AdventNetJCalendarBeanInfo extends SimpleBeanInfo
{
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
        final BeanDescriptor beanDescriptor = new BeanDescriptor(AdventNetJCalendar.class, null);
        beanDescriptor.setValue("isContainer", Boolean.FALSE);
        return beanDescriptor;
    }
    
    private static PropertyDescriptor[] getPdescriptor() {
        return new PropertyDescriptor[0];
    }
    
    private static EventSetDescriptor[] getEdescriptor() {
        return new EventSetDescriptor[0];
    }
    
    private static MethodDescriptor[] getMdescriptor() {
        return new MethodDescriptor[0];
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
                if (AdventNetJCalendarBeanInfo.iconNameC16 == null) {
                    return null;
                }
                if (AdventNetJCalendarBeanInfo.iconColor16 == null) {
                    AdventNetJCalendarBeanInfo.iconColor16 = this.loadImage(AdventNetJCalendarBeanInfo.iconNameC16);
                }
                return AdventNetJCalendarBeanInfo.iconColor16;
            }
            case 2: {
                if (AdventNetJCalendarBeanInfo.iconNameC32 == null) {
                    return null;
                }
                if (AdventNetJCalendarBeanInfo.iconColor32 == null) {
                    AdventNetJCalendarBeanInfo.iconColor32 = this.loadImage(AdventNetJCalendarBeanInfo.iconNameC32);
                }
                return AdventNetJCalendarBeanInfo.iconColor32;
            }
            case 3: {
                if (AdventNetJCalendarBeanInfo.iconNameM16 == null) {
                    return null;
                }
                if (AdventNetJCalendarBeanInfo.iconMono16 == null) {
                    AdventNetJCalendarBeanInfo.iconMono16 = this.loadImage(AdventNetJCalendarBeanInfo.iconNameM16);
                }
                return AdventNetJCalendarBeanInfo.iconMono16;
            }
            case 4: {
                if (AdventNetJCalendarBeanInfo.iconNameM32 == null) {
                    return null;
                }
                if (AdventNetJCalendarBeanInfo.iconMono32 == null) {
                    AdventNetJCalendarBeanInfo.iconMono32 = this.loadImage(AdventNetJCalendarBeanInfo.iconNameM32);
                }
                return AdventNetJCalendarBeanInfo.iconMono32;
            }
            default: {
                return null;
            }
        }
    }
    
    static {
        AdventNetJCalendarBeanInfo.iconColor16 = null;
        AdventNetJCalendarBeanInfo.iconColor32 = null;
        AdventNetJCalendarBeanInfo.iconMono16 = null;
        AdventNetJCalendarBeanInfo.iconMono32 = null;
        AdventNetJCalendarBeanInfo.iconNameC16 = "/com/adventnet/idioms/adventnetjcalendar/calendar.gif";
        AdventNetJCalendarBeanInfo.iconNameC32 = null;
        AdventNetJCalendarBeanInfo.iconNameM16 = null;
        AdventNetJCalendarBeanInfo.iconNameM32 = null;
    }
}
