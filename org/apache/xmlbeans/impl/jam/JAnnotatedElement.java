package org.apache.xmlbeans.impl.jam;

public interface JAnnotatedElement extends JElement
{
    JAnnotation[] getAnnotations();
    
    JAnnotation getAnnotation(final Class p0);
    
    @Deprecated
    Object getAnnotationProxy(final Class p0);
    
    JAnnotation getAnnotation(final String p0);
    
    JAnnotationValue getAnnotationValue(final String p0);
    
    JComment getComment();
    
    @Deprecated
    JAnnotation[] getAllJavadocTags();
}
