package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class SuperReference extends ThisReference
{
    public SuperReference(final int sourceStart, final int sourceEnd) {
        super(sourceStart, sourceEnd);
    }
    
    public static ExplicitConstructorCall implicitSuperConstructorCall() {
        return new ExplicitConstructorCall(1);
    }
    
    @Override
    public boolean isImplicitThis() {
        return false;
    }
    
    @Override
    public boolean isSuper() {
        return true;
    }
    
    @Override
    public boolean isUnqualifiedSuper() {
        return true;
    }
    
    @Override
    public boolean isThis() {
        return false;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return output.append("super");
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        final ReferenceBinding enclosingReceiverType = scope.enclosingReceiverType();
        if (!this.checkAccess(scope, enclosingReceiverType)) {
            return null;
        }
        if (enclosingReceiverType.id == 1) {
            scope.problemReporter().cannotUseSuperInJavaLangObject(this);
            return null;
        }
        return this.resolvedType = enclosingReceiverType.superclass();
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
}
