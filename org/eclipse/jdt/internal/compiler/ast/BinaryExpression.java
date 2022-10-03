package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.impl.Constant;

public class BinaryExpression extends OperatorExpression
{
    public Expression left;
    public Expression right;
    public Constant optimizedBooleanConstant;
    
    public BinaryExpression(final Expression left, final Expression right, final int operator) {
        this.left = left;
        this.right = right;
        this.bits |= operator << 6;
        this.sourceStart = left.sourceStart;
        this.sourceEnd = right.sourceEnd;
    }
    
    public BinaryExpression(final BinaryExpression expression) {
        this.left = expression.left;
        this.right = expression.right;
        this.bits = expression.bits;
        this.sourceStart = expression.sourceStart;
        this.sourceEnd = expression.sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        try {
            if (this.resolvedType.id == 11) {
                return this.right.analyseCode(currentScope, flowContext, this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits()).unconditionalInits();
            }
            this.left.checkNPE(currentScope, flowContext, flowInfo);
            flowInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            if ((this.bits & 0xFC0) >> 6 != 2) {
                flowContext.expireNullCheckedFieldInfo();
            }
            this.right.checkNPE(currentScope, flowContext, flowInfo);
            flowInfo = this.right.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            if ((this.bits & 0xFC0) >> 6 != 2) {
                flowContext.expireNullCheckedFieldInfo();
            }
            return flowInfo;
        }
        finally {
            flowContext.recordAbruptExit();
        }
    }
    
    public void computeConstant(final BlockScope scope, final int leftId, final int rightId) {
        if (this.left.constant != Constant.NotAConstant && this.right.constant != Constant.NotAConstant) {
            try {
                this.constant = Constant.computeConstantOperation(this.left.constant, leftId, (this.bits & 0xFC0) >> 6, this.right.constant, rightId);
            }
            catch (final ArithmeticException ex) {
                this.constant = Constant.NotAConstant;
            }
        }
        else {
            this.constant = Constant.NotAConstant;
            this.optimizedBooleanConstant(leftId, (this.bits & 0xFC0) >> 6, rightId);
        }
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
            case 14: {
                switch (this.bits & 0xF) {
                    case 11: {
                        codeStream.generateStringConcatenationAppend(currentScope, this.left, this.right);
                        if (!valueRequired) {
                            codeStream.pop();
                            break;
                        }
                        break;
                    }
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.iadd();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ladd();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.dadd();
                            break;
                        }
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.fadd();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 13: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.isub();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lsub();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.dsub();
                            break;
                        }
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.fsub();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 15: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.imul();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lmul();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.dmul();
                            break;
                        }
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.fmul();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 9: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.idiv();
                        if (!valueRequired) {
                            codeStream.pop();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.ldiv();
                        if (!valueRequired) {
                            codeStream.pop2();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ddiv();
                            break;
                        }
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.fdiv();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 16: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.irem();
                        if (!valueRequired) {
                            codeStream.pop();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, true);
                        this.right.generateCode(currentScope, codeStream, true);
                        codeStream.lrem();
                        if (!valueRequired) {
                            codeStream.pop2();
                            break;
                        }
                        break;
                    }
                    case 8: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.drem();
                            break;
                        }
                        break;
                    }
                    case 9: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.frem();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 2: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, false);
                            if (valueRequired) {
                                codeStream.iconst_0();
                                break;
                            }
                            break;
                        }
                        else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, false);
                            if (valueRequired) {
                                codeStream.iconst_0();
                                break;
                            }
                            break;
                        }
                        else {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            if (valueRequired) {
                                codeStream.iand();
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, false);
                            if (valueRequired) {
                                codeStream.lconst_0();
                                break;
                            }
                            break;
                        }
                        else if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, false);
                            if (valueRequired) {
                                codeStream.lconst_0();
                                break;
                            }
                            break;
                        }
                        else {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            if (valueRequired) {
                                codeStream.land();
                                break;
                            }
                            break;
                        }
                        break;
                    }
                    case 5: {
                        this.generateLogicalAnd(currentScope, codeStream, valueRequired);
                        break;
                    }
                }
                break;
            }
            case 3: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ior();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lor();
                            break;
                        }
                        break;
                    }
                    case 5: {
                        this.generateLogicalOr(currentScope, codeStream, valueRequired);
                        break;
                    }
                }
                break;
            }
            case 8: {
                switch (this.bits & 0xF) {
                    case 10: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 10 && this.left.constant.intValue() == 0) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 10 && this.right.constant.intValue() == 0) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ixor();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        if (this.left.constant != Constant.NotAConstant && this.left.constant.typeID() == 7 && this.left.constant.longValue() == 0L) {
                            this.right.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        if (this.right.constant != Constant.NotAConstant && this.right.constant.typeID() == 7 && this.right.constant.longValue() == 0L) {
                            this.left.generateCode(currentScope, codeStream, valueRequired);
                            break;
                        }
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lxor();
                            break;
                        }
                        break;
                    }
                    case 5: {
                        this.generateLogicalXor(currentScope, codeStream, valueRequired);
                        break;
                    }
                }
                break;
            }
            case 10: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ishl();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lshl();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 17: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.ishr();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lshr();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 19: {
                switch (this.bits & 0xF) {
                    case 10: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.iushr();
                            break;
                        }
                        break;
                    }
                    case 7: {
                        this.left.generateCode(currentScope, codeStream, valueRequired);
                        this.right.generateCode(currentScope, codeStream, valueRequired);
                        if (valueRequired) {
                            codeStream.lushr();
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case 6: {
                final BranchLabel falseLabel;
                this.generateOptimizedGreaterThan(currentScope, codeStream, null, falseLabel = new BranchLabel(codeStream), valueRequired);
                if (!valueRequired) {
                    break;
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                final BranchLabel endLabel;
                codeStream.goto_(endLabel = new BranchLabel(codeStream));
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 7: {
                final BranchLabel falseLabel;
                this.generateOptimizedGreaterThanOrEqual(currentScope, codeStream, null, falseLabel = new BranchLabel(codeStream), valueRequired);
                if (!valueRequired) {
                    break;
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                final BranchLabel endLabel;
                codeStream.goto_(endLabel = new BranchLabel(codeStream));
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 4: {
                final BranchLabel falseLabel;
                this.generateOptimizedLessThan(currentScope, codeStream, null, falseLabel = new BranchLabel(codeStream), valueRequired);
                if (!valueRequired) {
                    break;
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                final BranchLabel endLabel;
                codeStream.goto_(endLabel = new BranchLabel(codeStream));
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
                break;
            }
            case 5: {
                final BranchLabel falseLabel;
                this.generateOptimizedLessThanOrEqual(currentScope, codeStream, null, falseLabel = new BranchLabel(codeStream), valueRequired);
                if (!valueRequired) {
                    break;
                }
                codeStream.iconst_1();
                if ((this.bits & 0x10) != 0x0) {
                    codeStream.generateImplicitConversion(this.implicitConversion);
                    codeStream.generateReturnBytecode(this);
                    falseLabel.place();
                    codeStream.iconst_0();
                    break;
                }
                final BranchLabel endLabel;
                codeStream.goto_(endLabel = new BranchLabel(codeStream));
                codeStream.decrStackSize(1);
                falseLabel.place();
                codeStream.iconst_0();
                endLabel.place();
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
        switch ((this.bits & 0xFC0) >> 6) {
            case 4: {
                this.generateOptimizedLessThan(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 5: {
                this.generateOptimizedLessThanOrEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 6: {
                this.generateOptimizedGreaterThan(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 7: {
                this.generateOptimizedGreaterThanOrEqual(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 2: {
                this.generateOptimizedLogicalAnd(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 3: {
                this.generateOptimizedLogicalOr(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            case 8: {
                this.generateOptimizedLogicalXor(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                return;
            }
            default: {
                super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            }
        }
    }
    
    public void generateOptimizedGreaterThan(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.iflt(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifge(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifgt(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifle(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmpgt(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifgt(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifgt(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifgt(trueLabel);
                            break;
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
            }
            else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmple(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.ifle(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifle(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.ifle(falseLabel);
                        break;
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
            }
        }
    }
    
    public void generateOptimizedGreaterThanOrEqual(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifle(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifgt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifge(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.iflt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmpge(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpl();
                            codeStream.ifge(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifge(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpl();
                            codeStream.ifge(trueLabel);
                            break;
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
            }
            else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmplt(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpl();
                        codeStream.iflt(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.iflt(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpl();
                        codeStream.iflt(falseLabel);
                        break;
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
            }
        }
    }
    
    public void generateOptimizedLessThan(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifgt(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifle(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.iflt(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifge(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmplt(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpg();
                            codeStream.iflt(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.iflt(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpg();
                            codeStream.iflt(trueLabel);
                            break;
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
            }
            else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmpge(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpg();
                        codeStream.ifge(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifge(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpg();
                        codeStream.ifge(falseLabel);
                        break;
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
            }
        }
    }
    
    public void generateOptimizedLessThanOrEqual(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        final int promotedTypeID = (this.left.implicitConversion & 0xFF) >> 4;
        if (promotedTypeID == 10) {
            if (this.left.constant != Constant.NotAConstant && this.left.constant.intValue() == 0) {
                this.right.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifge(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.iflt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
            if (this.right.constant != Constant.NotAConstant && this.right.constant.intValue() == 0) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
                if (valueRequired) {
                    if (falseLabel == null) {
                        if (trueLabel != null) {
                            codeStream.ifle(trueLabel);
                        }
                    }
                    else if (trueLabel == null) {
                        codeStream.ifgt(falseLabel);
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            if (falseLabel == null) {
                if (trueLabel != null) {
                    switch (promotedTypeID) {
                        case 10: {
                            codeStream.if_icmple(trueLabel);
                            break;
                        }
                        case 9: {
                            codeStream.fcmpg();
                            codeStream.ifle(trueLabel);
                            break;
                        }
                        case 7: {
                            codeStream.lcmp();
                            codeStream.ifle(trueLabel);
                            break;
                        }
                        case 8: {
                            codeStream.dcmpg();
                            codeStream.ifle(trueLabel);
                            break;
                        }
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
            }
            else if (trueLabel == null) {
                switch (promotedTypeID) {
                    case 10: {
                        codeStream.if_icmpgt(falseLabel);
                        break;
                    }
                    case 9: {
                        codeStream.fcmpg();
                        codeStream.ifgt(falseLabel);
                        break;
                    }
                    case 7: {
                        codeStream.lcmp();
                        codeStream.ifgt(falseLabel);
                        break;
                    }
                    case 8: {
                        codeStream.dcmpg();
                        codeStream.ifgt(falseLabel);
                        break;
                    }
                }
                codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
            }
        }
    }
    
    public void generateLogicalAnd(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                }
                else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_0();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                }
                else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_0();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.iand();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    public void generateLogicalOr(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                else {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ior();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    public void generateLogicalXor(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                    }
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                    if (valueRequired) {
                        codeStream.ixor();
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                }
                else {
                    this.left.generateCode(currentScope, codeStream, false);
                    this.right.generateCode(currentScope, codeStream, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                    if (valueRequired) {
                        codeStream.iconst_1();
                        codeStream.ixor();
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                }
                else {
                    this.left.generateCode(currentScope, codeStream, valueRequired);
                    this.right.generateCode(currentScope, codeStream, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ixor();
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    public void generateOptimizedLogicalAnd(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                }
                else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && falseLabel != null) {
                        codeStream.goto_(falseLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                else {
                    final BranchLabel internalTrueLabel = new BranchLabel(codeStream);
                    this.left.generateOptimizedBoolean(currentScope, codeStream, internalTrueLabel, falseLabel, false);
                    internalTrueLabel.place();
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && falseLabel != null) {
                        codeStream.goto_(falseLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.iand();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            }
            else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    public void generateOptimizedLogicalOr(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    final BranchLabel internalFalseLabel = new BranchLabel(codeStream);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, internalFalseLabel, false);
                    internalFalseLabel.place();
                    if (valueRequired && trueLabel != null) {
                        codeStream.goto_(trueLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    final BranchLabel internalFalseLabel = new BranchLabel(codeStream);
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, internalFalseLabel, false);
                    internalFalseLabel.place();
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    if (valueRequired && trueLabel != null) {
                        codeStream.goto_(trueLabel);
                    }
                    codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                }
                else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ior();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            }
            else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    public void generateOptimizedLogicalXor(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if ((this.left.implicitConversion & 0xF) == 0x5) {
            Constant condConst;
            if ((condConst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
                }
                else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                }
                return;
            }
            if ((condConst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                if (condConst.booleanValue()) {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, falseLabel, trueLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                else {
                    this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, false);
                }
                return;
            }
        }
        this.left.generateCode(currentScope, codeStream, valueRequired);
        this.right.generateCode(currentScope, codeStream, valueRequired);
        if (valueRequired) {
            codeStream.ixor();
            if (falseLabel == null) {
                if (trueLabel != null) {
                    codeStream.ifne(trueLabel);
                }
            }
            else if (trueLabel == null) {
                codeStream.ifeq(falseLabel);
            }
        }
        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
    }
    
    @Override
    public void generateOptimizedStringConcatenation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        if ((this.bits & 0xFC0) >> 6 == 14 && (this.bits & 0xF) == 0xB) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.generateConstant(this.constant, this.implicitConversion);
                codeStream.invokeStringConcatenationAppendForType(this.implicitConversion & 0xF);
            }
            else {
                int pc = codeStream.position;
                this.left.generateOptimizedStringConcatenation(blockScope, codeStream, this.left.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.left.sourceStart);
                pc = codeStream.position;
                this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.right.sourceStart);
            }
        }
        else {
            super.generateOptimizedStringConcatenation(blockScope, codeStream, typeID);
        }
    }
    
    @Override
    public void generateOptimizedStringConcatenationCreation(final BlockScope blockScope, final CodeStream codeStream, final int typeID) {
        if ((this.bits & 0xFC0) >> 6 == 14 && (this.bits & 0xF) == 0xB) {
            if (this.constant != Constant.NotAConstant) {
                codeStream.newStringContatenation();
                codeStream.dup();
                codeStream.ldc(this.constant.stringValue());
                codeStream.invokeStringConcatenationStringConstructor();
            }
            else {
                int pc = codeStream.position;
                this.left.generateOptimizedStringConcatenationCreation(blockScope, codeStream, this.left.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.left.sourceStart);
                pc = codeStream.position;
                this.right.generateOptimizedStringConcatenation(blockScope, codeStream, this.right.implicitConversion & 0xF);
                codeStream.recordPositionsFrom(pc, this.right.sourceStart);
            }
        }
        else {
            super.generateOptimizedStringConcatenationCreation(blockScope, codeStream, typeID);
        }
    }
    
    @Override
    public boolean isCompactableOperation() {
        return true;
    }
    
    void nonRecursiveResolveTypeUpwards(final BlockScope scope) {
        final TypeBinding leftType = this.left.resolvedType;
        final boolean rightIsCast;
        if (rightIsCast = (this.right instanceof CastExpression)) {
            final Expression right = this.right;
            right.bits |= 0x20;
        }
        final TypeBinding rightType = this.right.resolveType(scope);
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return;
        }
        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;
        final boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != 11 && rightTypeID != 12) {
                leftTypeID = scope.environment().computeBoxingType(leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != 11 && leftTypeID != 12) {
                rightTypeID = scope.environment().computeBoxingType(rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) {
            if (leftTypeID == 11) {
                rightTypeID = 1;
            }
            else {
                if (rightTypeID != 11) {
                    this.constant = Constant.NotAConstant;
                    scope.problemReporter().invalidOperator(this, leftType, rightType);
                    return;
                }
                leftTypeID = 1;
            }
        }
        if ((this.bits & 0xFC0) >> 6 == 14) {
            if (leftTypeID == 11) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)rightType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == 11) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)leftType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }
        final int operator = (this.bits & 0xFC0) >> 6;
        final int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];
        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
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
            case 11: {
                this.resolvedType = scope.getJavaLangString();
                break;
            }
            default: {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return;
            }
        }
        final boolean leftIsCast;
        if ((leftIsCast = (this.left instanceof CastExpression)) || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        this.computeConstant(scope, leftTypeID, rightTypeID);
    }
    
    public void optimizedBooleanConstant(final int leftId, final int operator, final int rightId) {
        switch (operator) {
            case 2: {
                if (leftId != 5 || rightId != 5) {
                    return;
                }
            }
            case 0: {
                Constant cst;
                if ((cst = this.left.optimizedBooleanConstant()) == Constant.NotAConstant) {
                    if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant && !cst.booleanValue()) {
                        this.optimizedBooleanConstant = cst;
                    }
                    return;
                }
                if (!cst.booleanValue()) {
                    this.optimizedBooleanConstant = cst;
                    return;
                }
                if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    this.optimizedBooleanConstant = cst;
                }
                return;
            }
            case 3: {
                if (leftId != 5 || rightId != 5) {
                    return;
                }
            }
            case 1: {
                Constant cst;
                if ((cst = this.left.optimizedBooleanConstant()) != Constant.NotAConstant) {
                    if (cst.booleanValue()) {
                        this.optimizedBooleanConstant = cst;
                        return;
                    }
                    if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant) {
                        this.optimizedBooleanConstant = cst;
                    }
                    return;
                }
                else {
                    if ((cst = this.right.optimizedBooleanConstant()) != Constant.NotAConstant && cst.booleanValue()) {
                        this.optimizedBooleanConstant = cst;
                        break;
                    }
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        this.left.printExpression(indent, output).append(' ').append(this.operatorToString()).append(' ');
        return this.right.printExpression(0, output);
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final boolean leftIsCast;
        if (leftIsCast = (this.left instanceof CastExpression)) {
            final Expression left = this.left;
            left.bits |= 0x20;
        }
        final TypeBinding leftType = this.left.resolveType(scope);
        final boolean rightIsCast;
        if (rightIsCast = (this.right instanceof CastExpression)) {
            final Expression right = this.right;
            right.bits |= 0x20;
        }
        final TypeBinding rightType = this.right.resolveType(scope);
        if (leftType == null || rightType == null) {
            this.constant = Constant.NotAConstant;
            return null;
        }
        int leftTypeID = leftType.id;
        int rightTypeID = rightType.id;
        final boolean use15specifics = scope.compilerOptions().sourceLevel >= 3211264L;
        if (use15specifics) {
            if (!leftType.isBaseType() && rightTypeID != 11 && rightTypeID != 12) {
                leftTypeID = scope.environment().computeBoxingType(leftType).id;
            }
            if (!rightType.isBaseType() && leftTypeID != 11 && leftTypeID != 12) {
                rightTypeID = scope.environment().computeBoxingType(rightType).id;
            }
        }
        if (leftTypeID > 15 || rightTypeID > 15) {
            if (leftTypeID == 11) {
                rightTypeID = 1;
            }
            else {
                if (rightTypeID != 11) {
                    this.constant = Constant.NotAConstant;
                    scope.problemReporter().invalidOperator(this, leftType, rightType);
                    return null;
                }
                leftTypeID = 1;
            }
        }
        if ((this.bits & 0xFC0) >> 6 == 14) {
            if (leftTypeID == 11) {
                this.left.computeConversion(scope, leftType, leftType);
                if (rightType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)rightType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.right);
                }
            }
            if (rightTypeID == 11) {
                this.right.computeConversion(scope, rightType, rightType);
                if (leftType.isArrayType() && TypeBinding.equalsEquals(((ArrayBinding)leftType).elementsType(), TypeBinding.CHAR)) {
                    scope.problemReporter().signalNoImplicitStringConversionForCharArrayExpression(this.left);
                }
            }
        }
        final int operator = (this.bits & 0xFC0) >> 6;
        final int operatorSignature = OperatorExpression.OperatorSignatures[operator][(leftTypeID << 4) + rightTypeID];
        this.left.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 16 & 0xF), leftType);
        this.right.computeConversion(scope, TypeBinding.wellKnownType(scope, operatorSignature >>> 8 & 0xF), rightType);
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
            case 11: {
                this.resolvedType = scope.getJavaLangString();
                break;
            }
            default: {
                this.constant = Constant.NotAConstant;
                scope.problemReporter().invalidOperator(this, leftType, rightType);
                return null;
            }
        }
        if (leftIsCast || rightIsCast) {
            CastExpression.checkNeedForArgumentCasts(scope, operator, operatorSignature, this.left, leftTypeID, leftIsCast, this.right, rightTypeID, rightIsCast);
        }
        this.computeConstant(scope, leftTypeID, rightTypeID);
        return this.resolvedType;
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
