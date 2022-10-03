package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.ParameterizedMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class JavadocAllocationExpression extends AllocationExpression
{
    public int tagSourceStart;
    public int tagSourceEnd;
    public int tagValue;
    public int memberStart;
    public char[][] qualification;
    
    public JavadocAllocationExpression(final int start, final int end) {
        this.sourceStart = start;
        this.sourceEnd = end;
        this.bits |= 0x8000;
    }
    
    public JavadocAllocationExpression(final long pos) {
        this((int)(pos >>> 32), (int)pos);
    }
    
    TypeBinding internalResolveType(final Scope scope) {
        this.constant = Constant.NotAConstant;
        if (this.type == null) {
            this.resolvedType = scope.enclosingSourceType();
        }
        else if (scope.kind == 3) {
            this.resolvedType = this.type.resolveType((ClassScope)scope);
        }
        else {
            this.resolvedType = this.type.resolveType((BlockScope)scope, true);
        }
        this.argumentTypes = Binding.NO_PARAMETERS;
        boolean hasTypeVarArgs = false;
        if (this.arguments != null) {
            this.argumentsHaveErrors = false;
            final int length = this.arguments.length;
            this.argumentTypes = new TypeBinding[length];
            for (int i = 0; i < length; ++i) {
                final Expression argument = this.arguments[i];
                if (scope.kind == 3) {
                    this.argumentTypes[i] = argument.resolveType((ClassScope)scope);
                }
                else {
                    this.argumentTypes[i] = argument.resolveType((BlockScope)scope);
                }
                if (this.argumentTypes[i] == null) {
                    this.argumentsHaveErrors = true;
                }
                else if (!hasTypeVarArgs) {
                    hasTypeVarArgs = this.argumentTypes[i].isTypeVariable();
                }
            }
            if (this.argumentsHaveErrors) {
                return null;
            }
        }
        if (this.resolvedType == null) {
            return null;
        }
        this.resolvedType = scope.environment().convertToRawType(this.type.resolvedType, true);
        final SourceTypeBinding enclosingType = scope.enclosingSourceType();
        if (enclosingType != null) {
            if (enclosingType.isCompatibleWith(this.resolvedType)) {
                this.bits |= 0x4000;
            }
        }
        final ReferenceBinding allocationType = (ReferenceBinding)this.resolvedType;
        this.binding = scope.getConstructor(allocationType, this.argumentTypes, this);
        if (!this.binding.isValidBinding()) {
            ReferenceBinding enclosingTypeBinding;
            MethodBinding contructorBinding;
            for (enclosingTypeBinding = allocationType, contructorBinding = this.binding; !contructorBinding.isValidBinding() && (enclosingTypeBinding.isMemberType() || enclosingTypeBinding.isLocalType()); enclosingTypeBinding = enclosingTypeBinding.enclosingType(), contructorBinding = scope.getConstructor(enclosingTypeBinding, this.argumentTypes, this)) {}
            if (contructorBinding.isValidBinding()) {
                this.binding = contructorBinding;
            }
        }
        if (!this.binding.isValidBinding()) {
            final MethodBinding methodBinding = scope.getMethod(this.resolvedType, this.resolvedType.sourceName(), this.argumentTypes, this);
            if (methodBinding.isValidBinding()) {
                this.binding = methodBinding;
            }
            else {
                if (this.binding.declaringClass == null) {
                    this.binding.declaringClass = allocationType;
                }
                scope.problemReporter().javadocInvalidConstructor(this, this.binding, scope.getDeclarationModifiers());
            }
            return this.resolvedType;
        }
        if (this.binding.isVarargs()) {
            final int length2 = this.argumentTypes.length;
            if (this.binding.parameters.length != length2 || !this.argumentTypes[length2 - 1].isArrayType()) {
                final MethodBinding problem = new ProblemMethodBinding(this.binding, this.binding.selector, this.argumentTypes, 1);
                scope.problemReporter().javadocInvalidConstructor(this, problem, scope.getDeclarationModifiers());
            }
        }
        else if (hasTypeVarArgs) {
            final MethodBinding problem2 = new ProblemMethodBinding(this.binding, this.binding.selector, this.argumentTypes, 1);
            scope.problemReporter().javadocInvalidConstructor(this, problem2, scope.getDeclarationModifiers());
        }
        else if (this.binding instanceof ParameterizedMethodBinding) {
            final ParameterizedMethodBinding paramMethodBinding = (ParameterizedMethodBinding)this.binding;
            if (paramMethodBinding.hasSubstitutedParameters()) {
                for (int length3 = this.argumentTypes.length, j = 0; j < length3; ++j) {
                    if (TypeBinding.notEquals(paramMethodBinding.parameters[j], this.argumentTypes[j]) && TypeBinding.notEquals(paramMethodBinding.parameters[j].erasure(), this.argumentTypes[j].erasure())) {
                        final MethodBinding problem3 = new ProblemMethodBinding(this.binding, this.binding.selector, this.argumentTypes, 1);
                        scope.problemReporter().javadocInvalidConstructor(this, problem3, scope.getDeclarationModifiers());
                        break;
                    }
                }
            }
        }
        else if (this.resolvedType.isMemberType()) {
            final int length2 = this.qualification.length;
            if (length2 > 1) {
                ReferenceBinding enclosingTypeBinding2 = allocationType;
                if (this.type instanceof JavadocQualifiedTypeReference && ((JavadocQualifiedTypeReference)this.type).tokens.length != length2) {
                    scope.problemReporter().javadocInvalidMemberTypeQualification(this.memberStart + 1, this.sourceEnd, scope.getDeclarationModifiers());
                }
                else {
                    int idx = length2;
                    while (idx > 0 && CharOperation.equals(this.qualification[--idx], enclosingTypeBinding2.sourceName) && (enclosingTypeBinding2 = enclosingTypeBinding2.enclosingType()) != null) {}
                    if (idx > 0 || enclosingTypeBinding2 != null) {
                        scope.problemReporter().javadocInvalidMemberTypeQualification(this.memberStart + 1, this.sourceEnd, scope.getDeclarationModifiers());
                    }
                }
            }
        }
        if (this.isMethodUseDeprecated(this.binding, scope, true)) {
            scope.problemReporter().javadocDeprecatedMethod(this.binding, this, scope.getDeclarationModifiers());
        }
        return allocationType;
    }
    
    @Override
    public boolean isSuperAccess() {
        return (this.bits & 0x4000) != 0x0;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        return this.internalResolveType(scope);
    }
    
    @Override
    public TypeBinding resolveType(final ClassScope scope) {
        return this.internalResolveType(scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                for (int i = 0, argumentsLength = this.arguments.length; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.typeArguments != null) {
                for (int i = 0, typeArgumentsLength = this.typeArguments.length; i < typeArgumentsLength; ++i) {
                    this.typeArguments[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                for (int i = 0, argumentsLength = this.arguments.length; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
