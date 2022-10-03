package org.apache.xmlbeans.impl.jam;

public interface JAnnotation extends JElement
{
    public static final String SINGLE_VALUE_NAME = "value";
    
    String getSimpleName();
    
    Object getProxy();
    
    JAnnotationValue[] getValues();
    
    JAnnotationValue getValue(final String p0);
    
    Object getAnnotationInstance();
}
