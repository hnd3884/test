package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public class JavadocSingleTypeReference extends SingleTypeReference
{
    public int tagSourceStart;
    public int tagSourceEnd;
    public PackageBinding packageBinding;
    
    public JavadocSingleTypeReference(final char[] source, final long pos, final int tagStart, final int tagEnd) {
        super(source, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= 0x8000;
    }
    
    @Override
    protected TypeBinding internalResolveType(final Scope scope, final int location) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            if (this.resolvedType.isValidBinding()) {
                return this.resolvedType;
            }
            switch (this.resolvedType.problemId()) {
                case 1:
                case 2:
                case 5: {
                    final TypeBinding type = this.resolvedType.closestMatch();
                    return type;
                }
                default: {
                    return null;
                }
            }
        }
        else {
            this.resolvedType = this.getTypeBinding(scope);
            if (this.resolvedType == null) {
                return null;
            }
            if (!this.resolvedType.isValidBinding()) {
                final char[][] tokens = { this.token };
                final Binding binding = scope.getTypeOrPackage(tokens);
                if (binding instanceof PackageBinding) {
                    this.packageBinding = (PackageBinding)binding;
                }
                else {
                    if (this.resolvedType.problemId() == 7) {
                        final TypeBinding closestMatch = this.resolvedType.closestMatch();
                        if (closestMatch != null && closestMatch.isTypeVariable()) {
                            return this.resolvedType = closestMatch;
                        }
                    }
                    this.reportInvalidType(scope);
                }
                return null;
            }
            if (this.isTypeUseDeprecated(this.resolvedType, scope)) {
                this.reportDeprecatedType(this.resolvedType, scope);
            }
            if (this.resolvedType.isGenericType() || this.resolvedType.isParameterizedType()) {
                this.resolvedType = scope.environment().convertToRawType(this.resolvedType, true);
            }
            return this.resolvedType;
        }
    }
    
    @Override
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }
    
    @Override
    protected void reportInvalidType(final Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
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
