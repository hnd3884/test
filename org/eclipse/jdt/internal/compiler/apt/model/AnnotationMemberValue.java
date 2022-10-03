package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class AnnotationMemberValue extends AnnotationValueImpl
{
    private final MethodBinding _methodBinding;
    
    public AnnotationMemberValue(final BaseProcessingEnvImpl env, final Object value, final MethodBinding methodBinding) {
        super(env, value, methodBinding.returnType);
        this._methodBinding = methodBinding;
    }
    
    public MethodBinding getMethodBinding() {
        return this._methodBinding;
    }
}
