package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public interface IPolyExpression
{
    void setExpressionContext(final ExpressionContext p0);
    
    ExpressionContext getExpressionContext();
    
    void setExpectedType(final TypeBinding p0);
    
    TypeBinding invocationTargetType();
    
    boolean isPotentiallyCompatibleWith(final TypeBinding p0, final Scope p1);
    
    boolean isCompatibleWith(final TypeBinding p0, final Scope p1);
    
    boolean isBoxingCompatibleWith(final TypeBinding p0, final Scope p1);
    
    boolean sIsMoreSpecific(final TypeBinding p0, final TypeBinding p1, final Scope p2);
    
    boolean isPertinentToApplicability(final TypeBinding p0, final MethodBinding p1);
    
    boolean isPolyExpression(final MethodBinding p0);
    
    boolean isPolyExpression();
    
    boolean isFunctionalType();
    
    Expression[] getPolyExpressions();
    
    TypeBinding resolveType(final BlockScope p0);
    
    Expression resolveExpressionExpecting(final TypeBinding p0, final Scope p1, final InferenceContext18 p2);
}
