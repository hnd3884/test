package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class AND_AND_Expression extends BinaryExpression
{
    int rightInitStateIndex;
    int mergedInitStateIndex;
    
    public AND_AND_Expression(final Expression left, final Expression right, final int operator) {
        super(left, right, operator);
        this.rightInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final Constant cst = this.left.optimizedBooleanConstant();
        final boolean isLeftOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isLeftOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        if (isLeftOptimizedTrue) {
            FlowInfo mergedInfo = this.left.analyseCode(currentScope, flowContext, flowInfo).unconditionalInits();
            mergedInfo = this.right.analyseCode(currentScope, flowContext, mergedInfo);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }
        final FlowInfo leftInfo = this.left.analyseCode(currentScope, flowContext, flowInfo);
        if ((flowContext.tagBits & 0x4) != 0x0) {
            flowContext.expireNullCheckedFieldInfo();
        }
        FlowInfo rightInfo = leftInfo.initsWhenTrue().unconditionalCopy();
        this.rightInitStateIndex = currentScope.methodScope().recordInitializationStates(rightInfo);
        final int previousMode = rightInfo.reachMode();
        if (isLeftOptimizedFalse && (rightInfo.reachMode() & 0x3) == 0x0) {
            currentScope.problemReporter().fakeReachable(this.right);
            rightInfo.setReachMode(1);
        }
        rightInfo = this.right.analyseCode(currentScope, flowContext, rightInfo);
        if ((flowContext.tagBits & 0x4) != 0x0) {
            flowContext.expireNullCheckedFieldInfo();
        }
        this.left.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        this.right.checkNPEbyUnboxing(currentScope, flowContext, leftInfo.initsWhenTrue());
        final FlowInfo mergedInfo2 = FlowInfo.conditional(rightInfo.safeInitsWhenTrue(), leftInfo.initsWhenFalse().unconditionalInits().mergedWith(rightInfo.initsWhenFalse().setReachMode(previousMode).unconditionalInits()));
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo2);
        return mergedInfo2;
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
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant) {
            if (cst.booleanValue()) {
                this.left.generateCode(currentScope, codeStream, valueRequired);
            }
            else {
                this.left.generateCode(currentScope, codeStream, false);
                if (valueRequired) {
                    codeStream.iconst_0();
                }
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.generateImplicitConversion(this.implicitConversion);
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        final BranchLabel falseLabel = new BranchLabel(codeStream);
        cst = this.left.optimizedBooleanConstant();
        final boolean leftIsConst = cst != Constant.NotAConstant;
        final boolean leftIsTrue = leftIsConst && cst.booleanValue();
        cst = this.right.optimizedBooleanConstant();
        final boolean rightIsConst = cst != Constant.NotAConstant;
        final boolean rightIsTrue = rightIsConst && cst.booleanValue();
        Label_0317: {
            if (leftIsConst) {
                this.left.generateCode(currentScope, codeStream, false);
                if (!leftIsTrue) {
                    break Label_0317;
                }
            }
            else {
                this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, true);
            }
            if (this.rightInitStateIndex != -1) {
                codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
            }
            if (rightIsConst) {
                this.right.generateCode(currentScope, codeStream, false);
            }
            else {
                this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired);
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        if (valueRequired) {
            if (leftIsConst && !leftIsTrue) {
                codeStream.iconst_0();
            }
            else {
                if (rightIsConst && !rightIsTrue) {
                    codeStream.iconst_0();
                }
                else {
                    codeStream.iconst_1();
                }
                if (falseLabel.forwardReferenceCount() > 0) {
                    if ((this.bits & 0x10) != 0x0) {
                        codeStream.generateImplicitConversion(this.implicitConversion);
                        codeStream.generateReturnBytecode(this);
                        falseLabel.place();
                        codeStream.iconst_0();
                    }
                    else {
                        final BranchLabel endLabel;
                        codeStream.goto_(endLabel = new BranchLabel(codeStream));
                        codeStream.decrStackSize(1);
                        falseLabel.place();
                        codeStream.iconst_0();
                        endLabel.place();
                    }
                }
                else {
                    falseLabel.place();
                }
            }
            codeStream.generateImplicitConversion(this.implicitConversion);
            codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
        }
        else {
            falseLabel.place();
        }
    }
    
    @Override
    public void generateOptimizedBoolean(final BlockScope currentScope, final CodeStream codeStream, final BranchLabel trueLabel, final BranchLabel falseLabel, final boolean valueRequired) {
        if (this.constant != Constant.NotAConstant) {
            super.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            return;
        }
        Constant cst = this.right.constant;
        if (cst != Constant.NotAConstant && cst.booleanValue()) {
            final int pc = codeStream.position;
            this.left.generateOptimizedBoolean(currentScope, codeStream, trueLabel, falseLabel, valueRequired);
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        cst = this.left.optimizedBooleanConstant();
        final boolean leftIsConst = cst != Constant.NotAConstant;
        final boolean leftIsTrue = leftIsConst && cst.booleanValue();
        cst = this.right.optimizedBooleanConstant();
        final boolean rightIsConst = cst != Constant.NotAConstant;
        final boolean rightIsTrue = rightIsConst && cst.booleanValue();
        if (falseLabel == null) {
            if (trueLabel != null) {
                final BranchLabel internalFalseLabel = new BranchLabel(codeStream);
                this.left.generateOptimizedBoolean(currentScope, codeStream, null, internalFalseLabel, !leftIsConst);
                if (leftIsConst && !leftIsTrue) {
                    internalFalseLabel.place();
                }
                else {
                    if (this.rightInitStateIndex != -1) {
                        codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
                    }
                    this.right.generateOptimizedBoolean(currentScope, codeStream, trueLabel, null, valueRequired && !rightIsConst);
                    if (valueRequired && rightIsConst && rightIsTrue) {
                        codeStream.goto_(trueLabel);
                        codeStream.recordPositionsFrom(codeStream.position, this.sourceEnd);
                    }
                    internalFalseLabel.place();
                }
            }
        }
        else if (trueLabel == null) {
            this.left.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, !leftIsConst);
            final int pc2 = codeStream.position;
            if (leftIsConst && !leftIsTrue) {
                if (valueRequired) {
                    codeStream.goto_(falseLabel);
                }
                codeStream.recordPositionsFrom(pc2, this.sourceEnd);
            }
            else {
                if (this.rightInitStateIndex != -1) {
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.rightInitStateIndex);
                }
                this.right.generateOptimizedBoolean(currentScope, codeStream, null, falseLabel, valueRequired && !rightIsConst);
                if (valueRequired && rightIsConst && !rightIsTrue) {
                    codeStream.goto_(falseLabel);
                    codeStream.recordPositionsFrom(pc2, this.sourceEnd);
                }
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
    }
    
    @Override
    public boolean isCompactableOperation() {
        return false;
    }
    
    @Override
    public TypeBinding resolveType(final BlockScope scope) {
        final TypeBinding result = super.resolveType(scope);
        final Binding leftDirect = Expression.getDirectBinding(this.left);
        if (leftDirect != null && leftDirect == Expression.getDirectBinding(this.right) && !(this.right instanceof Assignment)) {
            scope.problemReporter().comparingIdenticalExpressions(this);
        }
        return result;
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
