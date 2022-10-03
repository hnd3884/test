package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.CatchParameterBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.InvocationSite;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;

public class Argument extends LocalDeclaration
{
    private static final char[] SET;
    
    static {
        SET = "set".toCharArray();
    }
    
    public Argument(final char[] name, final long posNom, final TypeReference tr, final int modifiers) {
        super(name, (int)(posNom >>> 32), (int)posNom);
        this.declarationSourceEnd = (int)posNom;
        this.modifiers = modifiers;
        this.type = tr;
        if (tr != null) {
            this.bits |= (tr.bits & 0x100000);
        }
        this.bits |= 0x40000004;
    }
    
    public Argument(final char[] name, final long posNom, final TypeReference tr, final int modifiers, final boolean typeElided) {
        super(name, (int)(posNom >>> 32), (int)posNom);
        this.declarationSourceEnd = (int)posNom;
        this.modifiers = modifiers;
        this.type = tr;
        if (tr != null) {
            this.bits |= (tr.bits & 0x100000);
        }
        this.bits |= 0x40000006;
    }
    
    @Override
    public boolean isRecoveredFromLoneIdentifier() {
        return false;
    }
    
    public TypeBinding createBinding(final MethodScope scope, final TypeBinding typeBinding) {
        if (this.binding == null) {
            this.binding = new LocalVariableBinding(this, typeBinding, this.modifiers, scope);
        }
        else if (!this.binding.type.isValidBinding()) {
            final AbstractMethodDeclaration methodDecl = scope.referenceMethod();
            if (methodDecl != null) {
                final MethodBinding methodBinding = methodDecl.binding;
                if (methodBinding != null) {
                    final MethodBinding methodBinding2 = methodBinding;
                    methodBinding2.tagBits |= 0x200L;
                }
            }
        }
        if ((this.binding.tagBits & 0x200000000L) == 0x0L) {
            ASTNode.resolveAnnotations(scope, this.annotations, this.binding, true);
            if (scope.compilerOptions().sourceLevel >= 3407872L) {
                Annotation.isTypeUseCompatible(this.type, scope, this.annotations);
                scope.validateNullAnnotation(this.binding.tagBits, this.type, this.annotations);
            }
        }
        this.binding.declaration = this;
        return this.binding.type;
    }
    
    public TypeBinding bind(final MethodScope scope, final TypeBinding typeBinding, final boolean used) {
        final TypeBinding newTypeBinding = this.createBinding(scope, typeBinding);
        final Binding existingVariable = scope.getBinding(this.name, 3, this, false);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            final boolean localExists = existingVariable instanceof LocalVariableBinding;
            if (localExists && this.hiddenVariableDepth == 0) {
                if ((this.bits & 0x200000) != 0x0 && scope.isLambdaSubscope()) {
                    scope.problemReporter().lambdaRedeclaresArgument(this);
                }
                else {
                    scope.problemReporter().redefineArgument(this);
                }
            }
            else {
                boolean isSpecialArgument = false;
                if (existingVariable instanceof FieldBinding) {
                    if (scope.isInsideConstructor()) {
                        isSpecialArgument = true;
                    }
                    else {
                        final AbstractMethodDeclaration methodDecl = scope.referenceMethod();
                        if (methodDecl != null && CharOperation.prefixEquals(Argument.SET, methodDecl.selector)) {
                            isSpecialArgument = true;
                        }
                    }
                }
                scope.problemReporter().localVariableHiding(this, existingVariable, isSpecialArgument);
            }
        }
        scope.addLocalVariable(this.binding);
        this.binding.useFlag = (used ? 1 : 0);
        return newTypeBinding;
    }
    
    @Override
    public int getKind() {
        return ((this.bits & 0x4) != 0x0) ? 5 : 4;
    }
    
    @Override
    public boolean isArgument() {
        return true;
    }
    
    public boolean isVarArgs() {
        return this.type != null && (this.type.bits & 0x4000) != 0x0;
    }
    
    public boolean hasElidedType() {
        return (this.bits & 0x2) != 0x0;
    }
    
    public boolean hasNullTypeAnnotation(final TypeReference.AnnotationPosition position) {
        return TypeReference.containsNullAnnotation(this.annotations) || (this.type != null && this.type.hasNullTypeAnnotation(position));
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        ASTNode.printModifiers(this.modifiers, output);
        if (this.annotations != null) {
            ASTNode.printAnnotations(this.annotations, output);
            output.append(' ');
        }
        if (this.type == null) {
            output.append("<no type> ");
        }
        else {
            this.type.print(0, output).append(' ');
        }
        return output.append(this.name);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        return this.print(indent, output).append(';');
    }
    
    public TypeBinding resolveForCatch(final BlockScope scope) {
        final TypeBinding exceptionType = this.type.resolveType(scope, true);
        boolean hasError;
        if (exceptionType == null) {
            hasError = true;
        }
        else {
            hasError = false;
            switch (exceptionType.kind()) {
                case 260: {
                    if (exceptionType.isBoundParameterizedType()) {
                        hasError = true;
                        scope.problemReporter().invalidParameterizedExceptionType(exceptionType, this);
                        break;
                    }
                    break;
                }
                case 4100: {
                    scope.problemReporter().invalidTypeVariableAsException(exceptionType, this);
                    hasError = true;
                    break;
                }
            }
            if (exceptionType.findSuperTypeOriginatingFrom(21, true) == null && exceptionType.isValidBinding()) {
                scope.problemReporter().cannotThrowType(this.type, exceptionType);
                hasError = true;
            }
        }
        final Binding existingVariable = scope.getBinding(this.name, 3, this, false);
        if (existingVariable != null && existingVariable.isValidBinding()) {
            if (existingVariable instanceof LocalVariableBinding && this.hiddenVariableDepth == 0) {
                scope.problemReporter().redefineArgument(this);
            }
            else {
                scope.problemReporter().localVariableHiding(this, existingVariable, false);
            }
        }
        if ((this.type.bits & 0x20000000) != 0x0) {
            this.binding = new CatchParameterBinding(this, exceptionType, this.modifiers | 0x10, false);
            final LocalVariableBinding binding = this.binding;
            binding.tagBits |= 0x1000L;
        }
        else {
            this.binding = new CatchParameterBinding(this, exceptionType, this.modifiers, false);
        }
        ASTNode.resolveAnnotations(scope, this.annotations, this.binding, true);
        Annotation.isTypeUseCompatible(this.type, scope, this.annotations);
        if (scope.compilerOptions().isAnnotationBasedNullAnalysisEnabled && (this.type.hasNullTypeAnnotation(TypeReference.AnnotationPosition.ANY) || TypeReference.containsNullAnnotation(this.annotations))) {
            scope.problemReporter().nullAnnotationUnsupportedLocation(this.type);
        }
        scope.addLocalVariable(this.binding);
        this.binding.setConstant(Constant.NotAConstant);
        if (hasError) {
            return null;
        }
        return exceptionType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.annotations != null) {
                for (int annotationsLength = this.annotations.length, i = 0; i < annotationsLength; ++i) {
                    this.annotations[i].traverse(visitor, scope);
                }
            }
            if (this.type != null) {
                this.type.traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
