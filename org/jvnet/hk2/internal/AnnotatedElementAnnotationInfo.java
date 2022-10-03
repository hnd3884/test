package org.jvnet.hk2.internal;

import java.lang.annotation.Annotation;

class AnnotatedElementAnnotationInfo
{
    final Annotation[] elementAnnotations;
    final Annotation[][] paramAnnotations;
    final boolean hasParams;
    final boolean isConstructor;
    
    AnnotatedElementAnnotationInfo(final Annotation[] elementAnnotation, final boolean hasParams, final Annotation[][] paramAnnotation, final boolean isConstructor) {
        this.elementAnnotations = elementAnnotation;
        this.hasParams = hasParams;
        this.paramAnnotations = paramAnnotation;
        this.isConstructor = isConstructor;
    }
    
    SoftAnnotatedElementAnnotationInfo soften() {
        return new SoftAnnotatedElementAnnotationInfo(this.elementAnnotations, this.hasParams, this.paramAnnotations, this.isConstructor);
    }
}
