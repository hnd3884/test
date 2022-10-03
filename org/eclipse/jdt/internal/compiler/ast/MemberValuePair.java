package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ElementValuePair;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;

public class MemberValuePair extends ASTNode
{
    public char[] name;
    public Expression value;
    public MethodBinding binding;
    public ElementValuePair compilerElementPair;
    
    public MemberValuePair(final char[] token, final int sourceStart, final int sourceEnd, final Expression value) {
        this.compilerElementPair = null;
        this.name = token;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.value = value;
        if (value instanceof ArrayInitializer) {
            value.bits |= 0x1;
        }
    }
    
    @Override
    public StringBuffer print(final int indent, final StringBuffer output) {
        output.append(this.name).append(" = ");
        this.value.print(0, output);
        return output;
    }
    
    public void resolveTypeExpecting(final BlockScope scope, final TypeBinding requiredType) {
        if (this.value == null) {
            this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
            return;
        }
        if (requiredType == null) {
            if (this.value instanceof ArrayInitializer) {
                this.value.resolveTypeExpecting(scope, null);
            }
            else {
                this.value.resolveType(scope);
            }
            this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
            return;
        }
        this.value.setExpectedType(requiredType);
        TypeBinding valueType;
        if (this.value instanceof ArrayInitializer) {
            final ArrayInitializer initializer = (ArrayInitializer)this.value;
            valueType = initializer.resolveTypeExpecting(scope, this.binding.returnType);
        }
        else if (this.value instanceof ArrayAllocationExpression) {
            scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
            this.value.resolveType(scope);
            valueType = null;
        }
        else {
            valueType = this.value.resolveType(scope);
            final ASTVisitor visitor = new ASTVisitor() {
                @Override
                public boolean visit(final SingleNameReference reference, final BlockScope scop) {
                    if (reference.binding instanceof LocalVariableBinding) {
                        ((LocalVariableBinding)reference.binding).useFlag = 1;
                    }
                    return true;
                }
            };
            this.value.traverse(visitor, scope);
        }
        this.compilerElementPair = new ElementValuePair(this.name, this.value, this.binding);
        if (valueType == null) {
            return;
        }
        final TypeBinding leafType = requiredType.leafComponentType();
        final boolean[] shouldExit = { false };
        final Runnable check = new Runnable() {
            @Override
            public void run() {
                if (!MemberValuePair.this.value.isConstantValueOfTypeAssignableToType(valueType, requiredType) && !valueType.isCompatibleWith(requiredType)) {
                    if (!requiredType.isArrayType() || requiredType.dimensions() != 1 || (!MemberValuePair.this.value.isConstantValueOfTypeAssignableToType(valueType, leafType) && !valueType.isCompatibleWith(leafType))) {
                        if (leafType.isAnnotationType() && !valueType.isAnnotationType()) {
                            scope.problemReporter().annotationValueMustBeAnnotation(MemberValuePair.this.binding.declaringClass, MemberValuePair.this.name, MemberValuePair.this.value, leafType);
                        }
                        else {
                            scope.problemReporter().typeMismatchError(valueType, requiredType, MemberValuePair.this.value, null);
                        }
                        shouldExit[0] = true;
                    }
                }
                else {
                    scope.compilationUnitScope().recordTypeConversion(requiredType.leafComponentType(), valueType.leafComponentType());
                    MemberValuePair.this.value.computeConversion(scope, requiredType, valueType);
                }
            }
        };
        if (!scope.deferCheck(check)) {
            check.run();
            if (shouldExit[0]) {
                return;
            }
        }
        switch (leafType.erasure().id) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11: {
                if (this.value instanceof ArrayInitializer) {
                    final ArrayInitializer initializer2 = (ArrayInitializer)this.value;
                    final Expression[] expressions = initializer2.expressions;
                    if (expressions != null) {
                        for (int i = 0, max = expressions.length; i < max; ++i) {
                            final Expression expression = expressions[i];
                            if (expression.resolvedType != null) {
                                if (expression.constant == Constant.NotAConstant) {
                                    scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, expressions[i], false);
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    if (this.value.constant != Constant.NotAConstant) {
                        break;
                    }
                    if (valueType.isArrayType()) {
                        scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
                        break;
                    }
                    scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, false);
                    break;
                }
                break;
            }
            case 16: {
                if (this.value instanceof ArrayInitializer) {
                    final ArrayInitializer initializer2 = (ArrayInitializer)this.value;
                    final Expression[] expressions = initializer2.expressions;
                    if (expressions != null) {
                        for (int i = 0, max = expressions.length; i < max; ++i) {
                            final Expression currentExpression = expressions[i];
                            if (!(currentExpression instanceof ClassLiteralAccess)) {
                                scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, currentExpression);
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    if (!(this.value instanceof ClassLiteralAccess)) {
                        scope.problemReporter().annotationValueMustBeClassLiteral(this.binding.declaringClass, this.name, this.value);
                        break;
                    }
                    break;
                }
                break;
            }
            default: {
                if (leafType.isEnum()) {
                    if (this.value instanceof NullLiteral) {
                        scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                        break;
                    }
                    if (this.value instanceof ArrayInitializer) {
                        final ArrayInitializer initializer2 = (ArrayInitializer)this.value;
                        final Expression[] expressions = initializer2.expressions;
                        if (expressions != null) {
                            for (int i = 0, max = expressions.length; i < max; ++i) {
                                final Expression currentExpression = expressions[i];
                                if (currentExpression instanceof NullLiteral) {
                                    scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
                                }
                                else if (currentExpression instanceof NameReference) {
                                    final NameReference nameReference = (NameReference)currentExpression;
                                    final Binding nameReferenceBinding = nameReference.binding;
                                    if (nameReferenceBinding.kind() == 1) {
                                        final FieldBinding fieldBinding = (FieldBinding)nameReferenceBinding;
                                        if (!fieldBinding.declaringClass.isEnum()) {
                                            scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, currentExpression, true);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        break;
                    }
                    else {
                        if (!(this.value instanceof NameReference)) {
                            scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                            break;
                        }
                        final NameReference nameReference2 = (NameReference)this.value;
                        final Binding nameReferenceBinding2 = nameReference2.binding;
                        if (nameReferenceBinding2.kind() != 1) {
                            break;
                        }
                        final FieldBinding fieldBinding2 = (FieldBinding)nameReferenceBinding2;
                        if (fieldBinding2.declaringClass.isEnum()) {
                            break;
                        }
                        if (!fieldBinding2.type.isArrayType()) {
                            scope.problemReporter().annotationValueMustBeConstant(this.binding.declaringClass, this.name, this.value, true);
                            break;
                        }
                        scope.problemReporter().annotationValueMustBeArrayInitializer(this.binding.declaringClass, this.name, this.value);
                        break;
                    }
                }
                else {
                    if (!leafType.isAnnotationType()) {
                        break;
                    }
                    if (!valueType.leafComponentType().isAnnotationType()) {
                        scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
                        break;
                    }
                    if (this.value instanceof ArrayInitializer) {
                        final ArrayInitializer initializer2 = (ArrayInitializer)this.value;
                        final Expression[] expressions = initializer2.expressions;
                        if (expressions != null) {
                            for (int i = 0, max = expressions.length; i < max; ++i) {
                                final Expression currentExpression = expressions[i];
                                if (currentExpression instanceof NullLiteral || !(currentExpression instanceof Annotation)) {
                                    scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, currentExpression, leafType);
                                }
                            }
                            break;
                        }
                        break;
                    }
                    else {
                        if (!(this.value instanceof Annotation)) {
                            scope.problemReporter().annotationValueMustBeAnnotation(this.binding.declaringClass, this.name, this.value, leafType);
                            break;
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope) && this.value != null) {
            this.value.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
    
    public void traverse(final ASTVisitor visitor, final ClassScope scope) {
        if (visitor.visit(this, scope) && this.value != null) {
            this.value.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
