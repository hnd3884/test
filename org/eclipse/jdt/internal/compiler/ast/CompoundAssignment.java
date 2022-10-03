package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class CompoundAssignment extends Assignment implements OperatorIds
{
    public int operator;
    public int preAssignImplicitConversion;
    
    public CompoundAssignment(final Expression lhs, final Expression expression, final int operator, final int sourceEnd) {
        super(lhs, expression, sourceEnd);
        lhs.bits &= 0xFFFFDFFF;
        lhs.bits |= 0x10000;
        this.operator = operator;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.resolvedType.id != 11) {
            this.lhs.checkNPE(currentScope, flowContext, flowInfo);
            flowContext.recordAbruptExit();
        }
        flowInfo = ((Reference)this.lhs).analyseAssignment(currentScope, flowContext, flowInfo, this, true).unconditionalInits();
        if (this.resolvedType.id == 11) {
            final LocalVariableBinding local = this.lhs.localVariableBinding();
            if (local != null) {
                flowInfo.markAsDefinitelyNonNull(local);
                flowContext.markFinallyNullStatus(local, 4);
            }
        }
        return flowInfo;
    }
    
    public boolean checkCastCompatibility() {
        return true;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        ((Reference)this.lhs).generateCompoundAssignment(currentScope, codeStream, this.expression, this.operator, this.preAssignImplicitConversion, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public int nullStatus(final FlowInfo flowInfo, final FlowContext flowContext) {
        return 4;
    }
    
    public String operatorToString() {
        switch (this.operator) {
            case 14: {
                return "+=";
            }
            case 13: {
                return "-=";
            }
            case 15: {
                return "*=";
            }
            case 9: {
                return "/=";
            }
            case 2: {
                return "&=";
            }
            case 3: {
                return "|=";
            }
            case 8: {
                return "^=";
            }
            case 16: {
                return "%=";
            }
            case 10: {
                return "<<=";
            }
            case 17: {
                return ">>=";
            }
            case 19: {
                return ">>>=";
            }
            default: {
                return "unknown operator";
            }
        }
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        this.lhs.printExpression(indent, output).append(' ').append(this.operatorToString()).append(' ');
        return this.expression.printExpression(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        this.constant = Constant.NotAConstant;
        if (!(this.lhs instanceof Reference) || this.lhs.isThis()) {
            scope.problemReporter().expressionShouldBeAVariable(this.lhs);
            return null;
        }
        final boolean expressionIsCast = this.expression instanceof CastExpression;
        if (expressionIsCast) {
            final Expression expression = this.expression;
            expression.bits |= 0x20;
        }
        final TypeBinding originalLhsType = this.lhs.resolveType(scope);
        final TypeBinding originalExpressionType = this.expression.resolveType(scope);
        if (originalLhsType == null || originalExpressionType == null) {
            return null;
        }
        final LookupEnvironment env = scope.environment();
        TypeBinding lhsType = originalLhsType;
        TypeBinding expressionType = originalExpressionType;
        final boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
        boolean unboxedLhs = false;
        if (use15specifics) {
            if (!lhsType.isBaseType() && expressionType.id != 11 && expressionType.id != 12) {
                final TypeBinding unboxedType = env.computeBoxingType(lhsType);
                if (TypeBinding.notEquals(unboxedType, lhsType)) {
                    lhsType = unboxedType;
                    unboxedLhs = true;
                }
            }
            if (!expressionType.isBaseType() && lhsType.id != 11 && lhsType.id != 12) {
                expressionType = env.computeBoxingType(expressionType);
            }
        }
        if (this.restrainUsageToNumericTypes() && !lhsType.isNumericType()) {
            scope.problemReporter().operatorOnlyValidOnNumericType(this, lhsType, expressionType);
            return null;
        }
        final int lhsID = lhsType.id;
        int expressionID = expressionType.id;
        if (lhsID > 15 || expressionID > 15) {
            if (lhsID != 11) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
            expressionID = 1;
        }
        final int result = OperatorExpression.OperatorSignatures[this.operator][(lhsID << 4) + expressionID];
        if (result == 0) {
            scope.problemReporter().invalidOperator(this, lhsType, expressionType);
            return null;
        }
        if (this.operator == 14) {
            if (lhsID == 1 && scope.compilerOptions().complianceLevel < 3342336L) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
            if ((lhsType.isNumericType() || lhsID == 5) && !expressionType.isNumericType()) {
                scope.problemReporter().invalidOperator(this, lhsType, expressionType);
                return null;
            }
        }
        final TypeBinding resultType = TypeBinding.wellKnownType(scope, result & 0xF);
        if (this.checkCastCompatibility() && originalLhsType.id != 11 && resultType.id != 11 && !this.checkCastTypesCompatibility(scope, originalLhsType, resultType, null)) {
            scope.problemReporter().invalidOperator(this, originalLhsType, expressionType);
            return null;
        }
        this.lhs.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 16 & 0xF), originalLhsType);
        this.expression.computeConversion(scope, TypeBinding.wellKnownType(scope, result >>> 8 & 0xF), originalExpressionType);
        this.preAssignImplicitConversion = ((unboxedLhs ? 512 : 0) | lhsID << 4 | (result & 0xF));
        if (unboxedLhs) {
            scope.problemReporter().autoboxing(this, lhsType, originalLhsType);
        }
        if (expressionIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, this.operator, result, this.lhs, originalLhsType.id, false, this.expression, originalExpressionType.id, true);
        }
        return this.resolvedType = originalLhsType;
    }
    
    public boolean restrainUsageToNumericTypes() {
        return false;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.lhs.traverse(visitor, scope);
            this.expression.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
