package java.beans;

import java.awt.Image;

public interface BeanInfo
{
    public static final int ICON_COLOR_16x16 = 1;
    public static final int ICON_COLOR_32x32 = 2;
    public static final int ICON_MONO_16x16 = 3;
    public static final int ICON_MONO_32x32 = 4;
    
    BeanDescriptor getBeanDescriptor();
    
    EventSetDescriptor[] getEventSetDescriptors();
    
    int getDefaultEventIndex();
    
    PropertyDescriptor[] getPropertyDescriptors();
    
    int getDefaultPropertyIndex();
    
    MethodDescriptor[] getMethodDescriptors();
    
    BeanInfo[] getAdditionalBeanInfo();
    
    Image getIcon(final int p0);
}
