package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedGenericMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;

public interface Invocation extends InvocationSite
{
    Expression[] arguments();
    
    MethodBinding binding();
    
    void registerInferenceContext(final ParameterizedGenericMethodBinding p0, final InferenceContext18 p1);
    
    InferenceContext18 getInferenceContext(final ParameterizedMethodBinding p0);
    
    void cleanUpInferenceContexts();
    
    void registerResult(final TypeBinding p0, final MethodBinding p1);
}
