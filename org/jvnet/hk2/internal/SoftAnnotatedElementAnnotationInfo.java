package org.jvnet.hk2.internal;

import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.ref.SoftReference;

class SoftAnnotatedElementAnnotationInfo
{
    private final SoftReference<Annotation[]> elementAnnotationsReference;
    private final SoftReference<Annotation[][]> paramAnnotationsReference;
    private final boolean hasParams;
    private final boolean isConstructor;
    
    SoftAnnotatedElementAnnotationInfo(final Annotation[] elementAnnotation, final boolean hasParams, final Annotation[][] paramAnnotation, final boolean isConstructor) {
        this.elementAnnotationsReference = new SoftReference<Annotation[]>(elementAnnotation);
        this.hasParams = hasParams;
        this.paramAnnotationsReference = new SoftReference<Annotation[][]>(paramAnnotation);
        this.isConstructor = isConstructor;
    }
    
    AnnotatedElementAnnotationInfo harden(final AnnotatedElement ae) {
        final Annotation[] hardenedElementAnnotations = this.elementAnnotationsReference.get();
        final Annotation[][] hardenedParamAnnotations = this.paramAnnotationsReference.get();
        if (!Utilities.USE_SOFT_REFERENCE || hardenedElementAnnotations == null || hardenedParamAnnotations == null) {
            return Utilities.computeAEAI(ae);
        }
        return new AnnotatedElementAnnotationInfo(hardenedElementAnnotations, this.hasParams, hardenedParamAnnotations, this.isConstructor);
    }
}
