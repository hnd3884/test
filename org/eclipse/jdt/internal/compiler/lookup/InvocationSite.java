package org.eclipse.jdt.internal.compiler.lookup;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.ExpressionContext;

public interface InvocationSite
{
    TypeBinding[] genericTypeArguments();
    
    boolean isSuperAccess();
    
    boolean isQualifiedSuper();
    
    boolean isTypeAccess();
    
    void setActualReceiverType(final ReferenceBinding p0);
    
    void setDepth(final int p0);
    
    void setFieldIndex(final int p0);
    
    int sourceEnd();
    
    int sourceStart();
    
    TypeBinding invocationTargetType();
    
    boolean receiverIsImplicitThis();
    
    boolean checkingPotentialCompatibility();
    
    void acceptPotentiallyCompatibleMethods(final MethodBinding[] p0);
    
    InferenceContext18 freshInferenceContext(final Scope p0);
    
    ExpressionContext getExpressionContext();
    
    public static class EmptyWithAstNode implements InvocationSite
    {
        ASTNode node;
        
        public EmptyWithAstNode(final ASTNode node) {
            this.node = node;
        }
        
        @Override
        public TypeBinding[] genericTypeArguments() {
            return null;
        }
        
        @Override
        public boolean isSuperAccess() {
            return false;
        }
        
        @Override
        public boolean isTypeAccess() {
            return false;
        }
        
        @Override
        public void setActualReceiverType(final ReferenceBinding receiverType) {
        }
        
        @Override
        public void setDepth(final int depth) {
        }
        
        @Override
        public void setFieldIndex(final int depth) {
        }
        
        @Override
        public int sourceEnd() {
            return this.node.sourceEnd;
        }
        
        @Override
        public int sourceStart() {
            return this.node.sourceStart;
        }
        
        @Override
        public TypeBinding invocationTargetType() {
            return null;
        }
        
        @Override
        public boolean receiverIsImplicitThis() {
            return false;
        }
        
        @Override
        public InferenceContext18 freshInferenceContext(final Scope scope) {
            return null;
        }
        
        @Override
        public ExpressionContext getExpressionContext() {
            return ExpressionContext.VANILLA_CONTEXT;
        }
        
        @Override
        public boolean isQualifiedSuper() {
            return false;
        }
        
        @Override
        public boolean checkingPotentialCompatibility() {
            return false;
        }
        
        @Override
        public void acceptPotentiallyCompatibleMethods(final MethodBinding[] methods) {
        }
    }
}
