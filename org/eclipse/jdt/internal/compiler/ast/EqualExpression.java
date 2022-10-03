package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class EqualExpression extends BinaryExpression
{
    public EqualExpression(final Expression left, final Expression right, final int operator) {
        super(left, right, operator);
    }
    
    private void checkNullComparison(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final FlowInfo initsWhenTrue, final FlowInfo initsWhenFalse) {
        final int rightStatus = this.right.nullStatus(flowInfo, flowContext);
        final int leftStatus = this.left.nullStatus(flowInfo, flowContext);
        boolean leftNonNullChecked = false;
        boolean rightNonNullChecked = false;
        final boolean checkEquality = (this.bits & 0xFC0) >> 6 == 18;
        if ((flowContext.tagBits & 0xF000) == 0x0) {
            if (leftStatus == 4 && rightStatus == 2) {
                leftNonNullChecked = scope.problemReporter().expressionNonNullComparison(this.left, checkEquality);
            }
            else if (leftStatus == 2 && rightStatus == 4) {
                rightNonNullChecked = scope.problemReporter().expressionNonNullComparison(this.right, checkEquality);
            }
        }
        final boolean contextualCheckEquality = checkEquality ^ (flowContext.tagBits & 0x4) != 0x0;
        if (!leftNonNullChecked) {
            final LocalVariableBinding local = this.left.localVariableBinding();
            if (local != null) {
                if ((local.type.tagBits & 0x2L) == 0x0L) {
                    this.checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, rightStatus, this.left);
                }
            }
            else if (this.left instanceof Reference && ((!contextualCheckEquality && rightStatus == 2) || (contextualCheckEquality && rightStatus == 4)) && scope.compilerOptions().enableSyntacticNullAnalysisForFields) {
                final FieldBinding field = ((Reference)this.left).lastFieldBinding();
                if (field != null && (field.type.tagBits & 0x2L) == 0x0L) {
                    flowContext.recordNullCheckedFieldReference((Reference)this.left, 1);
                }
            }
        }
        if (!rightNonNullChecked) {
            final LocalVariableBinding local = this.right.localVariableBinding();
            if (local != null) {
                if ((local.type.tagBits & 0x2L) == 0x0L) {
                    this.checkVariableComparison(scope, flowContext, flowInfo, initsWhenTrue, initsWhenFalse, local, leftStatus, this.right);
                }
            }
            else if (this.right instanceof Reference && ((!contextualCheckEquality && leftStatus == 2) || (contextualCheckEquality && leftStatus == 4)) && scope.compilerOptions().enableSyntacticNullAnalysisForFields) {
                final FieldBinding field = ((Reference)this.right).lastFieldBinding();
                if (field != null && (field.type.tagBits & 0x2L) == 0x0L) {
                    flowContext.recordNullCheckedFieldReference((Reference)this.right, 1);
                }
            }
        }
        if (leftNonNullChecked || rightNonNullChecked) {
            if (checkEquality) {
                initsWhenTrue.setReachMode(2);
            }
            else {
                initsWhenFalse.setReachMode(2);
            }
        }
    }
    
    private void checkVariableComparison(final BlockScope scope, final FlowContext flowContext, final FlowInfo flowInfo, final FlowInfo initsWhenTrue, final FlowInfo initsWhenFalse, final LocalVariableBinding local, final int nullStatus, final Expression reference) {
        switch (nullStatus) {
            case 2: {
                if ((this.bits & 0xFC0) >> 6 == 18) {
                    flowContext.recordUsingNullReference(scope, local, reference, 256, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNull(local);
                    initsWhenFalse.markAsComparedEqualToNonNull(local);
                    break;
                }
                flowContext.recordUsingNullReference(scope, local, reference, 512, flowInfo);
                initsWhenTrue.markAsComparedEqualToNonNull(local);
                initsWhenFalse.markAsComparedEqualToNull(local);
                break;
            }
            case 4: {
                if ((this.bits & 0xFC0) >> 6 == 18) {
                    flowContext.recordUsingNullReference(scope, local, reference, 513, flowInfo);
                    initsWhenTrue.markAsComparedEqualToNonNull(local);
                    break;
                }
                flowContext.recordUsingNullReference(scope, local, reference, 257, flowInfo);
                break;
            }
        }
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        FlowInfo result;
        if ((this.bits & 0xFC0) >> 6 == 18) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 5) {
                if (this.left.constant.booleanValue()) {
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo);
                }
                else {
                    result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            }
            else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 5) {
                if (this.right.constant.booleanValue()) {
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo);
                }
                else {
                    result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
                }
            }
            else {
                result = this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
            }
        }
        else if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 5) {
            if (!this.left.constant.booleanValue()) {
                result = this.right.analyseCode(currentScope, flowContext, flowInfo);
            }
            else {
                result = this.right.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
        }
        else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 5) {
            if (!this.right.constant.booleanValue()) {
                result = this.left.analyseCode(currentScope, flowContext, flowInfo);
            }
            else {
                result = this.left.analyseCode(currentScope, flowContext, flowInfo).asNegatedCondition();
            }
        }
        else {
            result = this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
        }
        if (result instanceof UnconditionalFlowInfo && (result.tagBits & 0x3) == 0x0) {
            result = FlowInfo.conditional(result.copy(), result.copy());
        }
        this.checkNullComparison(currentScope, flowContext, result, result.initsWhenTrue(), result.initsWhenFalse());
        return result;
    }
    
    public final void computeConstant(final TypeBinding leftType, final TypeBinding rightType) {
        if (this.left.constant != Constant.NotAConstant && this.right.constant != Constant.NotAConstant) {
            this.constant = Constant.computeConstantOperationEQUAL_EQUAL(this.left.constant, leftType.id, this.right.constant, rightType.id);
            if ((this.bits & 0xFC0) >> 6 == 29) {
                this.constant = BooleanConstant.fromValue(!this.constant.booleanValue());
            }
        }
        else {
            this.constant = Constant.NotAConstant;
        }
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
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            this.generateBooleanEqual(currentScope, codeStream, valueRequired);
        }
        else {
            this.generateNonBooleanEqual(currentScope, codeStream, valueRequired);
        }
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        if ((this.bits & 0xFC0) >> 6 == 18) {
            if ((this.left.implicitConversion & 0xF) == 0x5) {
                this.generateOptimizedBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            }
            else {
                this.generateOptimizedNonBooleanEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            }
        }
        else if ((this.left.implicitConversion & 0xF) == 0x5) {
            this.generateOptimizedBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        }
        else {
            this.generateOptimizedNonBooleanEqual(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
        }
    }
    
    public void generateBooleanEqual(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final boolean isEqualOperator = (this.bits & 0xFC0) >> 6 == 18;
        Constant cst = this.left.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            final Constant rightCst = this.right.optimizedBooleanConstant();
            if (rightCst != Constant.NotAConstant) {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
                if (valueRequired) {
                    final boolean leftBool = cst.booleanValue();
                    final boolean rightBool = rightCst.booleanValue();
                    if (isEqualOperator) {
                        if (leftBool == rightBool) {
                            codeStream.iconst_1();
                        }
                        else {
                            codeStream.iconst_0();
                        }
                    }
                    else if (leftBool != rightBool) {
                        codeStream.iconst_1();
                    }
                    else {
                        codeStream.iconst_0();
                    }
                }
            }
            else if (cst.booleanValue() == isEqualOperator) {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, valueRequired);
            }
            else if (valueRequired) {
                final BranchLabel falseLabel = new BranchLabel(codeStream);
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
                codeStream.iconst_0();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_1();
                }
                else {
                    final BranchLabel endLabel = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel);
                    codeStream.decrStackSize(1);
                    falseLabel.place();
                    codeStream.iconst_1();
                    endLabel.place();
                }
            }
            else {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
            }
            return;
        }
        cst = this.right.optimizedBooleanConstant();
        if (cst != Constant.NotAConstant) {
            if (cst.booleanValue() == isEqualOperator) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                this.right.generateCode(currentScope, codeStream, false);
            }
            else if (valueRequired) {
                final BranchLabel falseLabel2 = new BranchLabel(codeStream);
                this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel2, valueRequired);
                this.right.generateCode(currentScope, codeStream, false);
                codeStream.iconst_0();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel2.place();
                    codeStream.iconst_1();
                }
                else {
                    final BranchLabel endLabel2 = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel2);
                    codeStream.decrStackSize(1);
                    falseLabel2.place();
                    codeStream.iconst_1();
                    endLabel2.place();
                }
            }
            else {
                this.left.generateCode(currentScope, codeStream, false);
                this.right.generateCode(currentScope, codeStream, false);
            }
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (isEqualOperator) {
                final BranchLabel falseLabel2;
                codeStream.if_icmpne(falseLabel2 = new BranchLabel(codeStream));
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel2.place();
                    codeStream.iconst_0();
                }
                else {
                    final BranchLabel endLabel2 = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel2);
                    codeStream.decrStackSize(1);
                    falseLabel2.place();
                    codeStream.iconst_0();
                    endLabel2.place();
                }
            }
            else {
                codeStream.ixor();
            }
        }
    }
    
    public void generateOptimizedBooleanEqual(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if (this.left.constant != Constant.NotAConstant) {
            final boolean inline = this.left.constant.booleanValue();
            this.right.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
            return;
        }
        if (this.right.constant != Constant.NotAConstant) {
            final boolean inline = this.right.constant.booleanValue();
            this.left.generateOptimizedBoolean(currentScope, codeStream, inline ? trueLabel : falseLabel, inline ? falseLabel : trueLabel, valueRequired);
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        final int pc = codeStream.position;
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.if_icmpeq(trueLabel);
                }
            }
            else if (trueLabel == null) {
                codeStream.if_icmpne(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceEnd);
    }
    
    public void generateNonBooleanEqual(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final boolean isEqualOperator = (this.bits & 0xFC0) >> 6 == 18;
        if ((this.left.implicitConversion & 0xFF) >> 4 == 10) {
            Constant cst;
            if ((cst = this.left.constant) != Constant.NotAConstant && cst.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    final BranchLabel falseLabel = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifne(falseLabel);
                    }
                    else {
                        codeStream.ifeq(falseLabel);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0x0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel.place();
                        codeStream.iconst_0();
                    }
                    else {
                        final BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        falseLabel.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
                return;
            }
            if ((cst = this.right.constant) != Constant.NotAConstant && cst.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    final BranchLabel falseLabel = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifne(falseLabel);
                    }
                    else {
                        codeStream.ifeq(falseLabel);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0x0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel.place();
                        codeStream.iconst_0();
                    }
                    else {
                        final BranchLabel endLabel = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel);
                        codeStream.decrStackSize(1);
                        falseLabel.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
                return;
            }
        }
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                if (valueRequired) {
                    if (isEqualOperator) {
                        codeStream.iconst_1();
                    }
                    else {
                        codeStream.iconst_0();
                    }
                }
            }
            else {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    final BranchLabel falseLabel2 = new BranchLabel(codeStream);
                    if (isEqualOperator) {
                        codeStream.ifnonnull(falseLabel2);
                    }
                    else {
                        codeStream.ifnull(falseLabel2);
                    }
                    codeStream.iconst_1();
                    if ((this.bits & 0x10) != 0x0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel2.place();
                        codeStream.iconst_0();
                    }
                    else {
                        final BranchLabel endLabel2 = new BranchLabel(codeStream);
                        codeStream.goto_(endLabel2);
                        codeStream.decrStackSize(1);
                        falseLabel2.place();
                        codeStream.iconst_0();
                        endLabel2.place();
                    }
                }
            }
            return;
        }
        if (this.left instanceof NullLiteral) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                final BranchLabel falseLabel2 = new BranchLabel(codeStream);
                if (isEqualOperator) {
                    codeStream.ifnonnull(falseLabel2);
                }
                else {
                    codeStream.ifnull(falseLabel2);
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel2.place();
                    codeStream.iconst_0();
                }
                else {
                    final BranchLabel endLabel2 = new BranchLabel(codeStream);
                    codeStream.goto_(endLabel2);
                    codeStream.decrStackSize(1);
                    falseLabel2.place();
                    codeStream.iconst_0();
                    endLabel2.place();
                }
            }
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            final BranchLabel falseLabel2 = new BranchLabel(codeStream);
            if (isEqualOperator) {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpne(falseLabel2);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifne(falseLabel2);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifne(falseLabel2);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifne(falseLabel2);
                        break;
                    }
                    default: {
                        codeStream.if_acmpne(falseLabel2);
                        break;
                    }
                }
            }
            else {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpeq(falseLabel2);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifeq(falseLabel2);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifeq(falseLabel2);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifeq(falseLabel2);
                        break;
                    }
                    default: {
                        codeStream.if_acmpeq(falseLabel2);
                        break;
                    }
                }
            }
            codeStream.iconst_1();
            if ((this.bits & 0x10) != 0x0) {
                codeStream.generateImplicitConversion(this.implicitConversion);
                codeStream.generateReturnBytecode(this);
                falseLabel2.place();
                codeStream.iconst_0();
            }
            else {
                final BranchLabel endLabel2 = new BranchLabel(codeStream);
                codeStream.goto_(endLabel2);
                codeStream.decrStackSize(1);
                falseLabel2.place();
                codeStream.iconst_0();
                endLabel2.place();
            }
        }
    }
    
    public void generateOptimizedNonBooleanEqual(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int pc = codeStream.position;
        Constant inline;
        if ((inline = this.right.constant) != Constant.NotAConstant && (this.left.implicitConversion & 0xFF) >> 4 == 10 && inline.intValue() == 0) {
            this.left.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifeq(trueLabel);
                    }
                }
                else if (trueLabel == null) {
                    codeStream.ifne(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if ((inline = this.left.constant) != Constant.NotAConstant && (this.left.implicitConversion & 0xFF) >> 4 == 10 && inline.intValue() == 0) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifeq(trueLabel);
                    }
                }
                else if (trueLabel == null) {
                    codeStream.ifne(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if (this.right instanceof NullLiteral) {
            if (this.left instanceof NullLiteral) {
                if (valueRequired && falseLabel == null && trueLabel != null) {
                    codeStream.goto_(trueLabel);
                }
            }
            else {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifnull(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifnonnull(falseLabel);
                    }
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        if (this.left instanceof NullLiteral) {
            this.right.generateCode(currentScope, codeStream, valueRequired);
            if (valueRequired) {
                if (falseLabel == null) {
                    if (trueLabel != null) {
                        codeStream.ifnull(trueLabel);
                    }
                }
                else if (trueLabel == null) {
                    codeStream.ifnonnull(falseLabel);
                }
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch ((this.left.implicitConversion & 0xFF) >> 4) {
                        case 10: {
                            codeStream.if_icmpeq(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifeq(trueLabel);
                            break;
                        }
                        default: {
                            codeStream.if_acmpeq(trueLabel);
                            break;
                        }
                    }
                }
            }
            else if (trueLabel == null) {
                switch ((this.left.implicitConversion & 0xFF) >> 4) {
                    case 10: {
                        codeStream.if_icmpne(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifne(falseLabel);
                        break;
                    }
                    default: {
                        codeStream.if_acmpne(falseLabel);
                        break;
                    }
                }
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public boolean isCompactableOperation() {
        return false;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final boolean leftIsCast;
        if (leftIsCast = (this.left instanceof CastExpression)) {
            final Expression left = this.left;
            left.bits |= 0x20;
        }
        final TypeBinding originalLeftType = this.left.resolveType(scope);
        final boolean rightIsCast;
        if (rightIsCast = (this.right instanceof CastExpression)) {
            final Expression right = this.right;
            right.bits |= 0x20;
        }
        final TypeBinding originalRightType = this.right.resolveType(scope);
        if (originalLeftType == null || originalRightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        final CompilerOptions compilerOptions = scope.compilerOptions();
        if (compilerOptions.complainOnUninternedIdentityComparison && originalRightType.hasTypeBit(16) && originalLeftType.hasTypeBit(16)) {
            scope.problemReporter().uninternedIdentityComparison(this, originalLeftType, originalRightType, scope.referenceCompilationUnit());
        }
        final boolean use15specifics = compilerOptions.sourceLevel >= 3211264L;
        TypeBinding leftType = originalLeftType;
        TypeBinding rightType = originalRightType;
        if (use15specifics) {
            if (leftType != TypeBinding.NULL && leftType.isBaseType()) {
                if (!rightType.isBaseType()) {
                    rightType = scope.environment().computeBoxingType(rightType);
                }
            }
            else if (rightType != TypeBinding.NULL && rightType.isBaseType()) {
                leftType = scope.environment().computeBoxingType(leftType);
            }
        }
        if (leftType.isBaseType() && rightType.isBaseType()) {
            final int leftTypeID = leftType.id;
            final int rightTypeID = rightType.id;
            final int operatorSignature = EqualExpression.OperatorSignatures[18][(leftTypeID << 4) + rightTypeID];
            this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), originalLeftType);
            this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), originalRightType);
            this.bits |= (operatorSignature & 0xF);
            if ((operatorSignature & 0xF) == 0x0) {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
            if (leftIsCast || rightIsCast) {
                CastExpression.checkNeedForArgumentCasts(scope, 18, operatorSignature, this.left, leftType.id, leftIsCast, this.right, rightType.id, rightIsCast);
            }
            this.computeConstant(leftType, rightType);
            final Binding leftDirect = Expression.getDirectBinding(this.left);
            if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right)) {
                if (leftTypeID != 8 && leftTypeID != 9 && !(this.right instanceof Assignment)) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            }
            else if (this.constant != Constant.NotAConstant) {
                final int operator = (this.bits & 0xFC0) >> 6;
                if ((operator == 18 && this.constant == BooleanConstant.fromValue(true)) || (operator == 29 && this.constant == BooleanConstant.fromValue(false))) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
            }
            return this.resolvedType = TypeBinding.BOOLEAN;
        }
        else {
            if ((!leftType.isBaseType() || leftType == TypeBinding.NULL) && (!rightType.isBaseType() || rightType == TypeBinding.NULL) && (this.checkCastTypesCompatibility(scope, leftType, rightType, null) || this.checkCastTypesCompatibility(scope, rightType, leftType, null))) {
                if (rightType.id == 11 && leftType.id == 11) {
                    this.computeConstant(leftType, rightType);
                }
                else {
                    this.constant = Constant.NotAConstant;
                }
                final TypeBinding objectType = scope.getJavaLangObject();
                this.left.computeConversion(scope, objectType, leftType);
                this.right.computeConversion(scope, objectType, rightType);
                final boolean unnecessaryLeftCast = (this.left.bits & 0x4000) != 0x0;
                final boolean unnecessaryRightCast = (this.right.bits & 0x4000) != 0x0;
                if (unnecessaryLeftCast || unnecessaryRightCast) {
                    final TypeBinding alternateLeftType = unnecessaryLeftCast ? ((CastExpression)this.left).expression.resolvedType : leftType;
                    final TypeBinding alternateRightType = unnecessaryRightCast ? ((CastExpression)this.right).expression.resolvedType : rightType;
                    if (this.checkCastTypesCompatibility(scope, alternateLeftType, alternateRightType, null) || this.checkCastTypesCompatibility(scope, alternateRightType, alternateLeftType, null)) {
                        if (unnecessaryLeftCast) {
                            scope.problemReporter().unnecessaryCast((CastExpression)this.left);
                        }
                        if (unnecessaryRightCast) {
                            scope.problemReporter().unnecessaryCast((CastExpression)this.right);
                        }
                    }
                }
                final Binding leftDirect = Expression.getDirectBinding(this.left);
                if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right) && !(this.right instanceof Assignment)) {
                    scope.problemReporter().comparingIdenticalExpressions(this);
                }
                return this.resolvedType = TypeBinding.BOOLEAN;
            }
            this.constant = Constant.NotAConstant;
            scope.problemReporter().notCompatibleTypesError(this, leftType, rightType);
            return null;
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.left.traverse(visitor, scope);
            this.right.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
