package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class JavadocReturnStatement extends ReturnStatement
{
    public JavadocReturnStatement(final int s, final int e) {
        super(null, s, e);
        this.bits |= 0x48000;
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final MethodScope methodScope = scope.methodScope();
        MethodBinding methodBinding = null;
        final TypeBinding methodType = (methodScope.referenceContext instanceof AbstractMethodDeclaration) ? (((methodBinding = ((AbstractMethodDeclaration)methodScope.referenceContext).binding) == null) ? null : methodBinding.returnType) : TypeBinding.VOID;
        if (methodType == null || methodType == TypeBinding.VOID) {
            scope.problemReporter().javadocUnexpectedTag(this.sourceStart, this.sourceEnd);
        }
        else if ((this.bits & 0x40000) != 0x0) {
            scope.problemReporter().javadocEmptyReturnTag(this.sourceStart, this.sourceEnd, scope.getDeclarationModifiers());
        }
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("return");
        if ((this.bits & 0x40000) == 0x0) {
            output.append(' ').append(" <not empty>");
        }
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
