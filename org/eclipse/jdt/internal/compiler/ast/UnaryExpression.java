package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class UnaryExpression extends OperatorExpression
{
    public Expression expression;
    public Constant optimizedBooleanConstant;
    
    public UnaryExpression(final Expression expression, final int operator) {
        this.expression = expression;
        this.bits |= operator << 6;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.expression.checkNPE(currentScope, flowContext, flowInfo);
        if ((this.bits & 0xFC0) >> 6 == 11) {
            flowContext.tagBits ^= 0x4;
            flowInfo = this.expression.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            flowContext.tagBits ^= 0x4;
            return flowInfo;
        }
        return this.expression.analyseCode(currentScope, flowContext, flowInfo);
    }
    
    @Override
    public Constant optimizedBooleanConstant() {
        return (this.optimizedBooleanConstant == null) ? this.constant : this.optimizedBooleanConstant;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        if (this.constant != Constant.NotAConstant) {
            if (valueRequired) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        switch ((this.bits & 0xFC0) >> 6) {
            case 11: {
                switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                    case 5: {
                        final BranchLabel falseLabel;
                        this.expression.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel = new BranchLabel(codeStream), valueRequired);
                        if (!valueRequired) {
                            falseLabel.place();
                            break;
                        }
                        codeStream.iconst_0();
                        if (falseLabel.forwardReferenceCount() > 0) {
                            final BranchLabel endifLabel;
                            codeStream.goto_(endifLabel = new BranchLabel(codeStream));
                            codeStream.decrStackSize(1);
                            falseLabel.place();
                            codeStream.iconst_1();
                            endifLabel.place();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 12: {
                switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        this.expression.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.iconst_m1();
                            codeStream.ixor();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.expression.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ldc2_w(-1L);
                            codeStream.lxor();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 13: {
                if (this.constant != Constant.NotAConstant) {
                    if (valueRequired) {
                        switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                            case 10: {
                                codeStream.generateInlinedValue(this.constant.intValue() * -1);
                                break;
                            }
                            case 9: {
                                codeStream.generateInlinedValue(this.constant.floatValue() * -1.0f);
                                break;
                            }
                            case 7: {
                                codeStream.generateInlinedValue(this.constant.longValue() * -1L);
                                break;
                            }
                            case 8: {
                                codeStream.generateInlinedValue(this.constant.doubleValue() * -1.0);
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                else {
                    this.expression.generateCode(currentScope, codeStream, valueRequired);
                    if (valueRequired) {
                        switch ((this.expression.implicitConversion & 0xFF) >> 4) {
                            case 10: {
                                codeStream.ineg();
                                break;
                            }
                            case 9: {
                                codeStream.fneg();
                                break;
                            }
                            case 7: {
                                codeStream.lneg();
                                break;
                            }
                            case 8: {
                                codeStream.dneg();
                                break;
                            }
                        }
                        break;
                    }
                    break;
                }
                break;
            }
            case 14: {
                this.expression.generateCode(currentScope, codeStream, valueRequired);
                break;
            }
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if (this.constant != Constant.NotAConstant && this.constant.typeID() == 5) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        if ((this.bits & 0xFC0) >> 6 == 11) {
            this.expression.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        }
        else {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
        }
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        output.append(this.operatorToString()).append(' ');
        return this.expression.printExpression(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final boolean expressionIsCast;
        if (expressionIsCast = (this.expression instanceof CastExpression)) {
            final Expression expression = this.expression;
            expression.bits |= 0x20;
        }
        final TypeBinding expressionType = this.expression.resolveType(scope);
        if (expressionType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        int expressionTypeID = expressionType.id;
        final boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
        if (use15specifics && !expressionType.isBaseType()) {
            expressionTypeID = scope.environment().computeBoxingType(expressionType).id;
        }
        if (expressionTypeID > 15) {
            this.constant = Constant.NotAConstant;
            scope.problemReporter().invalidOperator(this, expressionType);
            return null;
        }
        int tableId = 0;
        switch ((this.bits & 0xFC0) >> 6) {
            case 11: {
                tableId = 0;
                break;
            }
            case 12: {
                tableId = 10;
                break;
            }
            default: {
                tableId = 13;
                break;
            }
        }
        final int operatorSignature = UnaryExpression.OperatorSignatures[tableId][(expressionTypeID << 4) + expressionTypeID];
        this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), expressionType);
        this.bits |= (operatorSignature & 0xF);
        switch (operatorSignature & 0xF) {
            case 5: {
                this.resolvedType = TypeBinding.BOOLEAN;
                break;
            }
            case 3: {
                this.resolvedType = TypeBinding.BYTE;
                break;
            }
            case 2: {
                this.resolvedType = TypeBinding.CHAR;
                break;
            }
            case 8: {
                this.resolvedType = TypeBinding.DOUBLE;
                break;
            }
            case 9: {
                this.resolvedType = TypeBinding.FLOAT;
                break;
            }
            case 10: {
                this.resolvedType = TypeBinding.INT;
                break;
            }
            case 7: {
                this.resolvedType = TypeBinding.LONG;
                break;
            }
            default: {
                this.constant = Constant.NotAConstant;
                if (expressionTypeID != 0) {
                    scope.problemReporter().invalidOperator(this, expressionType);
                }
                return null;
            }
        }
        if (this.expression.constant != Constant.NotAConstant) {
            this.constant = Constant.computeConstantOperation(this.expression.constant, expressionTypeID, (this.bits & 0xFC0) >> 6);
        }
        else {
            this.constant = Constant.NotAConstant;
            if ((this.bits & 0xFC0) >> 6 == 11) {
                final Constant cst = this.expression.optimizedBooleanConstant();
                if (cst != Constant.NotAConstant) {
                    this.optimizedBooleanConstant = BooleanConstant.fromValue(!cst.booleanValue());
                }
            }
        }
        if (expressionIsCast) {
            CastExpression.checkNeedForArgumentCast(scope, tableId, operatorSignature, this.expression, expressionTypeID);
        }
        return this.resolvedType;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
}
