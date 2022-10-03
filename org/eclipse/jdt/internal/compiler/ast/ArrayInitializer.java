package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;

public class ArrayInitializer extends Expression
{
    public Expression[] expressions;
    public ArrayBinding binding;
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.expressions != null) {
            final CompilerOptions compilerOptions = currentScope.compilerOptions();
            final boolean analyseResources = compilerOptions.analyseResourceLeaks;
            final boolean evalNullTypeAnnotations = currentScope.environment().usesNullTypeAnnotations();
            for (int i = 0, max = this.expressions.length; i < max; ++i) {
                flowInfo = this.expressions[i].analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
                if (analyseResources && FakedTrackingVariable.isAnyCloseable(this.expressions[i].resolvedType)) {
                    flowInfo = FakedTrackingVariable.markPassedToOutside(currentScope, this.expressions[i], flowInfo, flowContext, false);
                }
                if (evalNullTypeAnnotations) {
                    this.checkAgainstNullTypeAnnotation(currentScope, this.binding.elementsType(), this.expressions[i], flowContext, flowInfo);
                }
            }
        }
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        this.generateCode(null, null, currentScope, codeStream, valueRequired);
    }
    
    public void generateCode(final TypeReference typeReference, final ArrayAllocationExpression allocationExpression, final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        final int expressionLength = (this.expressions == null) ? 0 : this.expressions.length;
        codeStream.generateInlinedValue(expressionLength);
        codeStream.newArray(typeReference, allocationExpression, this.binding);
        if (this.expressions != null) {
            final int elementsTypeID = (this.binding.dimensions > 1) ? -1 : this.binding.leafComponentType.id;
            for (int i = 0; i < expressionLength; ++i) {
                final Expression expr;
                if ((expr = this.expressions[i]).constant != Constant.NotAConstant) {
                    switch (elementsTypeID) {
                        case 2:
                        case 3:
                        case 4:
                        case 7:
                        case 10: {
                            if (expr.constant.longValue() != 0L) {
                                codeStream.dup();
                                codeStream.generateInlinedValue(i);
                                expr.generateCode(currentScope, codeStream, true);
                                codeStream.arrayAtPut(elementsTypeID, false);
                                break;
                            }
                            break;
                        }
                        case 8:
                        case 9: {
                            final double constantValue = expr.constant.doubleValue();
                            if (constantValue == -0.0 || constantValue != 0.0) {
                                codeStream.dup();
                                codeStream.generateInlinedValue(i);
                                expr.generateCode(currentScope, codeStream, true);
                                codeStream.arrayAtPut(elementsTypeID, false);
                                break;
                            }
                            break;
                        }
                        case 5: {
                            if (expr.constant.booleanValue()) {
                                codeStream.dup();
                                codeStream.generateInlinedValue(i);
                                expr.generateCode(currentScope, codeStream, true);
                                codeStream.arrayAtPut(elementsTypeID, false);
                                break;
                            }
                            break;
                        }
                        default: {
                            if (!(expr instanceof NullLiteral)) {
                                codeStream.dup();
                                codeStream.generateInlinedValue(i);
                                expr.generateCode(currentScope, codeStream, true);
                                codeStream.arrayAtPut(elementsTypeID, false);
                                break;
                            }
                            break;
                        }
                    }
                }
                else if (!(expr instanceof NullLiteral)) {
                    codeStream.dup();
                    codeStream.generateInlinedValue(i);
                    expr.generateCode(currentScope, codeStream, true);
                    codeStream.arrayAtPut(elementsTypeID, false);
                }
            }
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        else {
            codeStream.pop();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printExpression(final int indent, final StringBuffer output) {
        output.append('{');
        if (this.expressions != null) {
            int j = 20;
            for (int i = 0; i < this.expressions.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.expressions[i].printExpression(0, output);
                if (--j == 0) {
                    output.append('\n');
                    ASTNode.printIndent(indent + 1, output);
                    j = 20;
                }
            }
        }
        return output.append('}');
    }
    
    @Override
    public TypeBinding resolveTypeExpecting(final BlockScope scope, final TypeBinding expectedType) {
        this.constant = Constant.NotAConstant;
        if (!(expectedType instanceof ArrayBinding)) {
            TypeBinding leafElementType = null;
            int dim = 1;
            if (this.expressions == null) {
                leafElementType = scope.getJavaLangObject();
            }
            else {
                Expression expression;
                for (expression = this.expressions[0]; expression != null && expression instanceof ArrayInitializer; expression = ((ArrayInitializer)expression).expressions[0]) {
                    ++dim;
                    final Expression[] subExprs = ((ArrayInitializer)expression).expressions;
                    if (subExprs == null) {
                        leafElementType = scope.getJavaLangObject();
                        expression = null;
                        break;
                    }
                }
                if (expression != null) {
                    leafElementType = expression.resolveType(scope);
                }
                for (int i = 1, length = this.expressions.length; i < length; ++i) {
                    expression = this.expressions[i];
                    if (expression != null) {
                        expression.resolveType(scope);
                    }
                }
            }
            if (leafElementType != null) {
                this.resolvedType = scope.createArrayType(leafElementType, dim);
                if (expectedType != null) {
                    scope.problemReporter().typeMismatchError(this.resolvedType, expectedType, this, null);
                }
            }
            return null;
        }
        if ((this.bits & 0x1) == 0x0) {
            final TypeBinding leafComponentType = expectedType.leafComponentType();
            if (!leafComponentType.isReifiable()) {
                scope.problemReporter().illegalGenericArray(leafComponentType, this);
            }
        }
        final ArrayBinding arrayBinding = (ArrayBinding)expectedType;
        this.binding = arrayBinding;
        this.resolvedType = arrayBinding;
        if (this.expressions == null) {
            return this.binding;
        }
        final TypeBinding elementType = this.binding.elementsType();
        for (int j = 0, length2 = this.expressions.length; j < length2; ++j) {
            final Expression expression2 = this.expressions[j];
            expression2.setExpressionContext(ExpressionContext.ASSIGNMENT_CONTEXT);
            expression2.setExpectedType(elementType);
            final TypeBinding expressionType = (expression2 instanceof ArrayInitializer) ? expression2.resolveTypeExpecting(scope, elementType) : expression2.resolveType(scope);
            if (expressionType != null) {
                if (TypeBinding.notEquals(elementType, expressionType)) {
                    scope.compilationUnitScope().recordTypeConversion(elementType, expressionType);
                }
                if (expression2.isConstantValueOfTypeAssignableToType(expressionType, elementType) || expressionType.isCompatibleWith(elementType)) {
                    expression2.computeConversion(scope, elementType, expressionType);
                }
                else if (this.isBoxingCompatible(expressionType, elementType, expression2, scope)) {
                    expression2.computeConversion(scope, elementType, expressionType);
                }
                else {
                    scope.problemReporter().typeMismatchError(expressionType, elementType, expression2, null);
                }
            }
        }
        return this.binding;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope) && this.expressions != null) {
            for (int expressionsLength = this.expressions.length, i = 0; i < expressionsLength; ++i) {
                this.expressions[i].traverse(visitor, scope);
            }
        }
        visitor.endVisit(this, scope);
    }
}
