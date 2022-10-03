package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.ProblemMethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;

public class JavadocMessageSend extends MessageSend
{
    public int tagSourceStart;
    public int tagSourceEnd;
    public int tagValue;
    
    public JavadocMessageSend(final char[] name, final long pos) {
        this.selector = name;
        this.nameSourcePosition = pos;
        this.sourceStart = (int)(this.nameSourcePosition >>> 32);
        this.sourceEnd = (int)this.nameSourcePosition;
        this.bits |= 0x8000;
    }
    
    public JavadocMessageSend(final char[] name, final long pos, final JavadocArgumentExpression[] arguments) {
        this(name, pos);
        this.arguments = arguments;
    }
    
    private TypeBinding internalResolveType(final Scope scope) {
        this.constant = Constant.NotAConstant;
        if (this.receiver == null) {
            this.actualReceiverType = scope.enclosingReceiverType();
        }
        else if (scope.kind == 3) {
            this.actualReceiverType = this.receiver.resolveType((ClassScope)scope);
        }
        else {
            this.actualReceiverType = this.receiver.resolveType((BlockScope)scope);
        }
        boolean hasArgsTypeVar = false;
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
                else if (!hasArgsTypeVar) {
                    hasArgsTypeVar = this.argumentTypes[i].isTypeVariable();
                }
            }
            if (this.argumentsHaveErrors) {
                return null;
            }
        }
        if (this.actualReceiverType == null) {
            return null;
        }
        this.actualReceiverType = scope.environment().convertToRawType(this.receiver.resolvedType, true);
        final ReferenceBinding enclosingType = scope.enclosingReceiverType();
        if (enclosingType != null) {
            if (enclosingType.isCompatibleWith(this.actualReceiverType)) {
                this.bits |= 0x4000;
            }
        }
        if (this.actualReceiverType.isBaseType()) {
            scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, this.argumentTypes, scope.getDeclarationModifiers());
            return null;
        }
        this.binding = scope.getMethod(this.actualReceiverType, this.selector, this.argumentTypes, this);
        if (!this.binding.isValidBinding()) {
            TypeBinding enclosingTypeBinding;
            MethodBinding methodBinding;
            for (enclosingTypeBinding = this.actualReceiverType, methodBinding = this.binding; !methodBinding.isValidBinding() && (enclosingTypeBinding.isMemberType() || enclosingTypeBinding.isLocalType()); enclosingTypeBinding = enclosingTypeBinding.enclosingType(), methodBinding = scope.getMethod(enclosingTypeBinding, this.selector, this.argumentTypes, this)) {}
            if (methodBinding.isValidBinding()) {
                this.binding = methodBinding;
            }
            else {
                enclosingTypeBinding = this.actualReceiverType;
                MethodBinding contructorBinding = this.binding;
                if (!contructorBinding.isValidBinding() && CharOperation.equals(this.selector, enclosingTypeBinding.shortReadableName())) {
                    contructorBinding = scope.getConstructor((ReferenceBinding)enclosingTypeBinding, this.argumentTypes, this);
                }
                while (!contructorBinding.isValidBinding() && (enclosingTypeBinding.isMemberType() || enclosingTypeBinding.isLocalType())) {
                    enclosingTypeBinding = enclosingTypeBinding.enclosingType();
                    if (CharOperation.equals(this.selector, enclosingTypeBinding.shortReadableName())) {
                        contructorBinding = scope.getConstructor((ReferenceBinding)enclosingTypeBinding, this.argumentTypes, this);
                    }
                }
                if (contructorBinding.isValidBinding()) {
                    this.binding = contructorBinding;
                }
            }
        }
        if (!this.binding.isValidBinding()) {
            switch (this.binding.problemId()) {
                case 3:
                case 5:
                case 6:
                case 7: {
                    final MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
                    if (closestMatch != null) {
                        this.binding = closestMatch;
                        break;
                    }
                    break;
                }
            }
        }
        if (this.binding.isValidBinding()) {
            if (hasArgsTypeVar) {
                final MethodBinding problem = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
                scope.problemReporter().javadocInvalidMethod(this, problem, scope.getDeclarationModifiers());
            }
            else if (this.binding.isVarargs()) {
                final int length2 = this.argumentTypes.length;
                if (this.binding.parameters.length != length2 || !this.argumentTypes[length2 - 1].isArrayType()) {
                    final MethodBinding problem2 = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
                    scope.problemReporter().javadocInvalidMethod(this, problem2, scope.getDeclarationModifiers());
                }
            }
            else {
                for (int length2 = this.argumentTypes.length, j = 0; j < length2; ++j) {
                    if (TypeBinding.notEquals(this.binding.parameters[j].erasure(), this.argumentTypes[j].erasure())) {
                        final MethodBinding problem3 = new ProblemMethodBinding(this.binding, this.selector, this.argumentTypes, 1);
                        scope.problemReporter().javadocInvalidMethod(this, problem3, scope.getDeclarationModifiers());
                        break;
                    }
                }
            }
            if (this.isMethodUseDeprecated(this.binding, scope, true)) {
                scope.problemReporter().javadocDeprecatedMethod(this.binding, this, scope.getDeclarationModifiers());
            }
            return this.resolvedType = this.binding.returnType;
        }
        if (this.receiver.resolvedType instanceof ProblemReferenceBinding) {
            return null;
        }
        if (this.binding.declaringClass == null) {
            if (!(this.actualReceiverType instanceof ReferenceBinding)) {
                scope.problemReporter().javadocErrorNoMethodFor(this, this.actualReceiverType, this.argumentTypes, scope.getDeclarationModifiers());
                return null;
            }
            this.binding.declaringClass = (ReferenceBinding)this.actualReceiverType;
        }
        scope.problemReporter().javadocInvalidMethod(this, this.binding, scope.getDeclarationModifiers());
        if (this.binding instanceof ProblemMethodBinding) {
            final MethodBinding closestMatch = ((ProblemMethodBinding)this.binding).closestMatch;
            if (closestMatch != null) {
                this.binding = closestMatch;
            }
        }
        return this.resolvedType = ((this.binding == null) ? null : this.binding.returnType);
    }
    
    @Override
    public boolean isSuperAccess() {
        return (this.bits & 0x4000) != 0x0;
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        if (this.receiver != null) {
            this.receiver.printExpression(0, output);
        }
        output.append('#').append(this.selector).append('(');
        if (this.arguments != null) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.arguments[i].printExpression(0, output);
            }
        }
        return output.append(')');
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
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.receiver != null) {
                this.receiver.traverse(visitor, blockScope);
            }
            if (this.arguments != null) {
                for (int argumentsLength = this.arguments.length, i = 0; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, blockScope);
                }
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.receiver != null) {
                this.receiver.traverse(visitor, scope);
            }
            if (this.arguments != null) {
                for (int argumentsLength = this.arguments.length, i = 0; i < argumentsLength; ++i) {
                    this.arguments[i].traverse(visitor, scope);
                }
            }
        }
        visitor.endVisit(this, scope);
    }
}
