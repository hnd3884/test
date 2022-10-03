package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;

public class JavadocQualifiedTypeReference extends QualifiedTypeReference
{
    public int tagSourceStart;
    public int tagSourceEnd;
    public PackageBinding packageBinding;
    
    public JavadocQualifiedTypeReference(final char[][] sources, final long[] pos, final int tagStart, final int tagEnd) {
        super(sources, pos);
        this.tagSourceStart = tagStart;
        this.tagSourceEnd = tagEnd;
        this.bits |= 0x8000;
    }
    
    private TypeBinding internalResolveType(final Scope scope, final boolean checkBounds) {
        this.constant = Constant.NotAConstant;
        if (this.resolvedType != null) {
            return this.resolvedType.isValidBinding() ? this.resolvedType : this.resolvedType.closestMatch();
        }
        final TypeBinding typeBinding = this.getTypeBinding(scope);
        this.resolvedType = typeBinding;
        final TypeBinding type = typeBinding;
        if (type == null) {
            return null;
        }
        if (!type.isValidBinding()) {
            final Binding binding = scope.getTypeOrPackage(this.tokens);
            if (binding instanceof PackageBinding) {
                this.packageBinding = (PackageBinding)binding;
            }
            else {
                this.reportInvalidType(scope);
            }
            return null;
        }
        if (type.isGenericType() || type.isParameterizedType()) {
            this.resolvedType = scope.environment().convertToRawType(type, true);
        }
        return this.resolvedType;
    }
    
    @Override
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers());
    }
    
    @Override
    protected void reportDeprecatedType(final TypeBinding type, final Scope scope, final int index) {
        scope.problemReporter().javadocDeprecatedType(type, this, scope.getDeclarationModifiers(), index);
    }
    
    @Override
    protected void reportInvalidType(final Scope scope) {
        scope.problemReporter().javadocInvalidType(this, this.resolvedType, scope.getDeclarationModifiers());
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope blockScope, final boolean checkBounds, final int location) {
        return this.internalResolveType(blockScope, checkBounds);
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope classScope, final int location) {
        return this.internalResolveType(classScope, false);
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
