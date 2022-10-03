package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class UnionTypeReference extends TypeReference
{
    public TypeReference[] typeReferences;
    
    public UnionTypeReference(final TypeReference[] typeReferences) {
        this.bits |= 0x20000000;
        this.typeReferences = typeReferences;
        this.sourceStart = typeReferences[0].sourceStart;
        final int length = typeReferences.length;
        this.sourceEnd = typeReferences[length - 1].sourceEnd;
    }
    
    @Override
    public char[] getLastToken() {
        return null;
    }
    
    @Override
    protected TypeBinding getTypeBinding(final Scope scope) {
        return null;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope, final boolean checkBounds, final int location) {
        final int length = this.typeReferences.length;
        final TypeBinding[] allExceptionTypes = new TypeBinding[length];
        boolean hasError = false;
        for (int i = 0; i < length; ++i) {
            final TypeBinding exceptionType = this.typeReferences[i].resolveType(scope, checkBounds, location);
            if (exceptionType == null) {
                return null;
            }
            switch (exceptionType.kind()) {
                case 260: {
                    if (exceptionType.isBoundParameterizedType()) {
                        hasError = true;
                        scope.problemReporter().invalidParameterizedExceptionType(exceptionType, this.typeReferences[i]);
                        break;
                    }
                    break;
                }
                case 4100: {
                    scope.problemReporter().invalidTypeVariableAsException(exceptionType, this.typeReferences[i]);
                    hasError = true;
                    break;
                }
            }
            if (exceptionType.findSuperTypeOriginatingFrom(21, true) == null && exceptionType.isValidBinding()) {
                scope.problemReporter().cannotThrowType(this.typeReferences[i], exceptionType);
                hasError = true;
            }
            allExceptionTypes[i] = exceptionType;
            for (int j = 0; j < i; ++j) {
                if (allExceptionTypes[j].isCompatibleWith(exceptionType)) {
                    scope.problemReporter().wrongSequenceOfExceptionTypes(this.typeReferences[j], allExceptionTypes[j], exceptionType);
                    hasError = true;
                }
                else if (exceptionType.isCompatibleWith(allExceptionTypes[j])) {
                    scope.problemReporter().wrongSequenceOfExceptionTypes(this.typeReferences[i], exceptionType, allExceptionTypes[j]);
                    hasError = true;
                }
            }
        }
        if (hasError) {
            return null;
        }
        return this.resolvedType = scope.lowerUpperBound(allExceptionTypes);
    }
    
    @Override
    public char[][] getTypeName() {
        return this.typeReferences[0].getTypeName();
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            for (int length = (this.typeReferences == null) ? 0 : this.typeReferences.length, i = 0; i < length; ++i) {
                this.typeReferences[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            for (int length = (this.typeReferences == null) ? 0 : this.typeReferences.length, i = 0; i < length; ++i) {
                this.typeReferences[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        final int length = (this.typeReferences == null) ? 0 : this.typeReferences.length;
        ASTNode.printIndent(indent, output);
        for (int i = 0; i < length; ++i) {
            this.typeReferences[i].printExpression(0, output);
            if (i != length - 1) {
                output.append(" | ");
            }
        }
        return output;
    }
    
    @Override
    public boolean isUnionType() {
        return true;
    }
    
    @Override
    public TypeReference augmentTypeWithAdditionalDimensions(final int additionalDimensions, final Annotation[][] additionalAnnotations, final boolean isVarargs) {
        return this;
    }
}
