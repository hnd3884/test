package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class JavadocSingleNameReference extends SingleNameReference
{
    public int tagSourceStart;
    public int tagSourceEnd;
    
    public JavadocSingleNameReference(final char[] source, final long pos, final int tagStart, final int tagEnd) {
        super(source, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= 0x8000;
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        this.resolve(scope, true, scope.compilerOptions().reportUnusedParameterIncludeDocCommentReference);
    }
    
    public void resolve(final BlockScope scope, final boolean warn, final boolean considerParamRefAsUsage) {
        final LocalVariableBinding variableBinding = scope.findVariable(this.token);
        if (variableBinding != null && variableBinding.isValidBinding() && (variableBinding.tagBits & 0x400L) != 0x0L) {
            this.binding = variableBinding;
            if (considerParamRefAsUsage) {
                variableBinding.useFlag = 1;
            }
            return;
        }
        if (warn) {
            try {
                final MethodScope methScope = (MethodScope)scope;
                scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, methScope.referenceMethod().modifiers);
            }
            catch (final Exception ex) {
                scope.problemReporter().javadocUndeclaredParamTagName(this.token, this.sourceStart, this.sourceEnd, -1);
            }
        }
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
