package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.istack.internal.Nullable;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;

public interface AnnotationReader<T, C, F, M>
{
    void setErrorHandler(final ErrorHandler p0);
    
     <A extends Annotation> A getFieldAnnotation(final Class<A> p0, final F p1, final Locatable p2);
    
    boolean hasFieldAnnotation(final Class<? extends Annotation> p0, final F p1);
    
    boolean hasClassAnnotation(final C p0, final Class<? extends Annotation> p1);
    
    Annotation[] getAllFieldAnnotations(final F p0, final Locatable p1);
    
     <A extends Annotation> A getMethodAnnotation(final Class<A> p0, final M p1, final M p2, final Locatable p3);
    
    boolean hasMethodAnnotation(final Class<? extends Annotation> p0, final String p1, final M p2, final M p3, final Locatable p4);
    
    Annotation[] getAllMethodAnnotations(final M p0, final Locatable p1);
    
     <A extends Annotation> A getMethodAnnotation(final Class<A> p0, final M p1, final Locatable p2);
    
    boolean hasMethodAnnotation(final Class<? extends Annotation> p0, final M p1);
    
    @Nullable
     <A extends Annotation> A getMethodParameterAnnotation(final Class<A> p0, final M p1, final int p2, final Locatable p3);
    
    @Nullable
     <A extends Annotation> A getClassAnnotation(final Class<A> p0, final C p1, final Locatable p2);
    
    @Nullable
     <A extends Annotation> A getPackageAnnotation(final Class<A> p0, final C p1, final Locatable p2);
    
    T getClassValue(final Annotation p0, final String p1);
    
    T[] getClassArrayValue(final Annotation p0, final String p1);
}
