package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationException;
import java.lang.annotation.Annotation;
import com.sun.xml.internal.bind.v2.model.core.ErrorHandler;

public abstract class AbstractInlineAnnotationReaderImpl<T, C, F, M> implements AnnotationReader<T, C, F, M>
{
    private ErrorHandler errorHandler;
    
    @Override
    public void setErrorHandler(final ErrorHandler errorHandler) {
        if (errorHandler == null) {
            throw new IllegalArgumentException();
        }
        this.errorHandler = errorHandler;
    }
    
    public final ErrorHandler getErrorHandler() {
        assert this.errorHandler != null : "error handler must be set before use";
        return this.errorHandler;
    }
    
    @Override
    public final <A extends Annotation> A getMethodAnnotation(final Class<A> annotation, final M getter, final M setter, final Locatable srcPos) {
        final A a1 = (A)((getter == null) ? null : this.getMethodAnnotation(annotation, getter, srcPos));
        final A a2 = (A)((setter == null) ? null : this.getMethodAnnotation(annotation, setter, srcPos));
        if (a1 == null) {
            if (a2 == null) {
                return null;
            }
            return a2;
        }
        else {
            if (a2 == null) {
                return a1;
            }
            this.getErrorHandler().error(new IllegalAnnotationException(Messages.DUPLICATE_ANNOTATIONS.format(annotation.getName(), this.fullName(getter), this.fullName(setter)), a1, a2));
            return a1;
        }
    }
    
    @Override
    public boolean hasMethodAnnotation(final Class<? extends Annotation> annotation, final String propertyName, final M getter, final M setter, final Locatable srcPos) {
        final boolean x = getter != null && this.hasMethodAnnotation(annotation, getter);
        final boolean y = setter != null && this.hasMethodAnnotation(annotation, setter);
        if (x && y) {
            this.getMethodAnnotation(annotation, getter, setter, srcPos);
        }
        return x || y;
    }
    
    protected abstract String fullName(final M p0);
}
