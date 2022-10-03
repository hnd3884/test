package org.eclipse.jdt.internal.compiler.lookup;

public class AnnotationHolder
{
    AnnotationBinding[] annotations;
    
    static AnnotationHolder storeAnnotations(final AnnotationBinding[] annotations, AnnotationBinding[][] parameterAnnotations, final Object defaultValue, final LookupEnvironment optionalEnv) {
        if (parameterAnnotations != null) {
            boolean isEmpty = true;
            for (int i = parameterAnnotations.length; isEmpty && --i >= 0; isEmpty = false) {
                if (parameterAnnotations[i] != null && parameterAnnotations[i].length > 0) {}
            }
            if (isEmpty) {
                parameterAnnotations = null;
            }
        }
        if (defaultValue != null) {
            return new AnnotationMethodHolder(annotations, parameterAnnotations, defaultValue, optionalEnv);
        }
        if (parameterAnnotations != null) {
            return new MethodHolder(annotations, parameterAnnotations);
        }
        return new AnnotationHolder().setAnnotations(annotations);
    }
    
    AnnotationBinding[] getAnnotations() {
        return this.annotations;
    }
    
    Object getDefaultValue() {
        return null;
    }
    
    public AnnotationBinding[][] getParameterAnnotations() {
        return null;
    }
    
    AnnotationBinding[] getParameterAnnotations(final int paramIndex) {
        return Binding.NO_ANNOTATIONS;
    }
    
    AnnotationHolder setAnnotations(final AnnotationBinding[] annotations) {
        this.annotations = annotations;
        if (annotations == null || annotations.length == 0) {
            return null;
        }
        return this;
    }
    
    static class MethodHolder extends AnnotationHolder
    {
        AnnotationBinding[][] parameterAnnotations;
        
        MethodHolder(final AnnotationBinding[] annotations, final AnnotationBinding[][] parameterAnnotations) {
            this.setAnnotations(annotations);
            this.parameterAnnotations = parameterAnnotations;
        }
        
        @Override
        public AnnotationBinding[][] getParameterAnnotations() {
            return this.parameterAnnotations;
        }
        
        @Override
        AnnotationBinding[] getParameterAnnotations(final int paramIndex) {
            final AnnotationBinding[] result = (AnnotationBinding[])((this.parameterAnnotations == null) ? null : this.parameterAnnotations[paramIndex]);
            return (result == null) ? Binding.NO_ANNOTATIONS : result;
        }
        
        @Override
        AnnotationHolder setAnnotations(final AnnotationBinding[] annotations) {
            this.annotations = ((annotations == null || annotations.length == 0) ? Binding.NO_ANNOTATIONS : annotations);
            return this;
        }
    }
    
    static class AnnotationMethodHolder extends MethodHolder
    {
        Object defaultValue;
        LookupEnvironment env;
        
        AnnotationMethodHolder(final AnnotationBinding[] annotations, final AnnotationBinding[][] parameterAnnotations, final Object defaultValue, final LookupEnvironment optionalEnv) {
            super(annotations, parameterAnnotations);
            this.defaultValue = defaultValue;
            this.env = optionalEnv;
        }
        
        @Override
        Object getDefaultValue() {
            if (this.defaultValue instanceof UnresolvedReferenceBinding) {
                if (this.env == null) {
                    throw new IllegalStateException();
                }
                this.defaultValue = ((UnresolvedReferenceBinding)this.defaultValue).resolve(this.env, false);
            }
            return this.defaultValue;
        }
    }
}
