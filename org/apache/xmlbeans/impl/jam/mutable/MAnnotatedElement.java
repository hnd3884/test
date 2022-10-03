package org.apache.xmlbeans.impl.jam.mutable;

import org.apache.xmlbeans.impl.jam.JAnnotatedElement;

public interface MAnnotatedElement extends MElement, JAnnotatedElement
{
    MAnnotation findOrCreateAnnotation(final String p0);
    
    MAnnotation[] getMutableAnnotations();
    
    MAnnotation getMutableAnnotation(final String p0);
    
    MAnnotation addLiteralAnnotation(final String p0);
    
    MComment getMutableComment();
    
    MComment createComment();
    
    void removeComment();
}
