package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.problem.AbortMethod;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.InferenceContext18;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;

public abstract class NameReference extends Reference implements InvocationSite
{
    public Binding binding;
    public TypeBinding actualReceiverType;
    
    public NameReference() {
        this.bits |= 0x7;
    }
    
    @Override
    public FieldBinding fieldBinding() {
        return (FieldBinding)this.binding;
    }
    
    @Override
    public FieldBinding lastFieldBinding() {
        if ((this.bits & 0x7) == 0x1) {
            return this.fieldBinding();
        }
        return null;
    }
    
    @Override
    public InferenceContext18 freshInferenceContext(final Scope scope) {
        return null;
    }
    
    @Override
    public boolean isSuperAccess() {
        return false;
    }
    
    @Override
    public boolean isTypeAccess() {
        return this.binding == null || this.binding instanceof ReferenceBinding;
    }
    
    @Override
    public boolean isTypeReference() {
        return this.binding instanceof ReferenceBinding;
    }
    
    @Override
    public void setActualReceiverType(final ReferenceBinding receiverType) {
        if (receiverType == null) {
            return;
        }
        this.actualReceiverType = receiverType;
    }
    
    @Override
    public void setDepth(final int depth) {
        this.bits &= 0xFFFFE01F;
        if (depth > 0) {
            this.bits |= (depth & 0xFF) << 5;
        }
    }
    
    @Override
    public void setFieldIndex(final int index) {
    }
    
    public abstract String unboundReferenceErrorName();
    
    public abstract char[][] getName();
    
    protected void checkEffectiveFinality(final LocalVariableBinding localBinding, final Scope scope) {
        if ((this.bits & 0x80000) != 0x0 && !localBinding.isFinal() && !localBinding.isEffectivelyFinal()) {
            scope.problemReporter().cannotReferToNonEffectivelyFinalOuterLocal(localBinding, this);
            throw new AbortMethod(scope.referenceCompilationUnit().compilationResult, (CategorizedProblem)null);
        }
    }
}
