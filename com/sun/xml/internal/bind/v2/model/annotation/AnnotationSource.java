package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;

public interface AnnotationSource
{
     <A extends Annotation> A readAnnotation(final Class<A> p0);
    
    boolean hasAnnotation(final Class<? extends Annotation> p0);
}
