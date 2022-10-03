package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JClass;
import org.apache.xmlbeans.impl.jam.JAnnotation;

public interface MAnnotation extends JAnnotation, MElement
{
    void setAnnotationInstance(final Object p0);
    
    void setSimpleValue(final String p0, final Object p1, final JClass p2);
    
    MAnnotation createNestedValue(final String p0, final String p1);
    
    MAnnotation[] createNestedValueArray(final String p0, final String p1, final int p2);
}
