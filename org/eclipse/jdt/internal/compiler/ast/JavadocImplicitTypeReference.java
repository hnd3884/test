package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class JavadocImplicitTypeReference extends TypeReference
{
    public char[] token;
    
    public JavadocImplicitTypeReference(final char[] name, final int pos) {
        this.token = name;
        this.sourceStart = pos;
        this.sourceEnd = pos;
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        return null;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        this.constant = Constant.NotAConstant;
        return this.resolvedType = scope.enclosingReceiverType();
    }
    
    @Override
    public char[] getLastToken() {
        return this.token;
    }
    
    @Override
    public char[][] getTypeName() {
        if (this.token != null) {
            final char[][] tokens = { this.token };
            return tokens;
        }
        return null;
    }
    
    @Override
    public boolean isThis() {
        return true;
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
                case 2: {
                    final TypeBinding type = this.resolvedType.closestMatch();
                    return type;
                }
                default: {
                    return null;
                }
            }
        }
        else {
            final TypeBinding typeBinding = this.getTypeBinding(scope);
            this.resolvedType = typeBinding;
            TypeBinding type2 = typeBinding;
            if (type2 == null) {
                return null;
            }
            final boolean hasError;
            if (hasError = !type2.isValidBinding()) {
                this.reportInvalidType(scope);
                switch (type2.problemId()) {
                    case 1:
                    case 2: {
                        type2 = type2.closestMatch();
                        if (type2 == null) {
                            return null;
                        }
                        break;
                    }
                    default: {
                        return null;
                    }
                }
            }
            if (type2.isArrayType() && ((ArrayBinding)type2).leafComponentType == TypeBinding.VOID) {
                scope.problemReporter().cannotAllocateVoidArray(this);
                return null;
            }
            if (this.isTypeUseDeprecated(type2, scope)) {
                this.reportDeprecatedType(type2, scope);
            }
            if (type2.isGenericType() || type2.isParameterizedType()) {
                type2 = scope.environment().convertToRawType(type2, true);
            }
            if (hasError) {
                return type2;
            }
            return this.resolvedType = type2;
        }
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
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        return new StringBuffer();
    }
}
