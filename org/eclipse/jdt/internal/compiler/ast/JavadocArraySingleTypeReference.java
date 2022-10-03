package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class JavadocArraySingleTypeReference extends ArrayTypeReference
{
    public JavadocArraySingleTypeReference(final char[] name, final int dim, final long pos) {
        super(name, dim, pos);
        this.bits |= 0x8000;
    }
    
    @Override
    protected void reportInvalidType(final Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
    }
    
    @Override
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
