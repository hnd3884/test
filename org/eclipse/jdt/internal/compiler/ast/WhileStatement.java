package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public class WhileStatement extends Statement
{
    public Expression condition;
    public Statement action;
    private BranchLabel breakLabel;
    private BranchLabel continueLabel;
    int preCondInitStateIndex;
    int condIfTrueInitStateIndex;
    int mergedInitStateIndex;
    
    public WhileStatement(final Expression condition, final Statement action, final int s, final int e) {
        this.preCondInitStateIndex = -1;
        this.condIfTrueInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.condition = condition;
        this.action = action;
        if (action instanceof EmptyStatement) {
            action.bits |= 0x1;
        }
        this.sourceStart = s;
        this.sourceEnd = e;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        final int initialComplaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        Constant cst = this.condition.constant;
        final boolean isConditionTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isConditionFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        this.preCondInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        FlowInfo condInfo = flowInfo.nullInfoLessUnconditionalCopy();
        final LoopingFlowContext condLoopContext;
        condInfo = this.condition.analyseCode(currentScope, condLoopContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, currentScope, true), condInfo);
        this.condition.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        if (this.action != null && (!this.action.isEmptyBlock() || currentScope.compilerOptions().complianceLevel > 3080192L)) {
            final LoopingFlowContext loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, currentScope, true);
            FlowInfo actionInfo;
            if (isConditionFalse) {
                actionInfo = FlowInfo.DEAD_END;
            }
            else {
                actionInfo = condInfo.initsWhenTrue().copy();
                if (isConditionOptimizedFalse) {
                    actionInfo.setReachMode(1);
                }
            }
            this.condIfTrueInitStateIndex = currentScope.methodScope().recordInitializationStates(condInfo.initsWhenTrue());
            if (this.action.complainIfUnreachable(actionInfo, currentScope, initialComplaintLevel, true) < 2) {
                actionInfo = this.action.analyseCode(currentScope, loopingContext, actionInfo);
            }
            final FlowInfo exitBranch = flowInfo.copy();
            final int combinedTagBits = actionInfo.tagBits & loopingContext.initsOnContinue.tagBits;
            if ((combinedTagBits & 0x3) != 0x0) {
                if ((combinedTagBits & 0x1) != 0x0) {
                    this.continueLabel = null;
                }
                exitBranch.addInitializationsFrom(condInfo.initsWhenFalse());
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue.unconditionalInits());
                condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo, false);
                loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo, false);
            }
            else {
                condLoopContext.complainOnDeferredFinalChecks(currentScope, condInfo);
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue.unconditionalInits());
                condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo);
                loopingContext.complainOnDeferredFinalChecks(currentScope, actionInfo);
                loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);
                exitBranch.addPotentialInitializationsFrom(actionInfo.unconditionalInits()).addInitializationsFrom(condInfo.initsWhenFalse());
            }
            if (loopingContext.hasEscapingExceptions()) {
                FlowInfo loopbackFlowInfo = flowInfo.copy();
                if (this.continueLabel != null) {
                    loopbackFlowInfo = loopbackFlowInfo.mergedWith(loopbackFlowInfo.unconditionalCopy().addNullInfoFrom(actionInfo).unconditionalInits());
                }
                loopingContext.simulateThrowAfterLoopBack(loopbackFlowInfo);
            }
            final FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(((loopingContext.initsOnBreak.tagBits & 0x3) != 0x0) ? loopingContext.initsOnBreak : flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), isConditionOptimizedTrue, exitBranch, isConditionOptimizedFalse, !isConditionTrue);
            this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
            return mergedInfo;
        }
        condLoopContext.complainOnDeferredFinalChecks(currentScope, condInfo);
        condLoopContext.complainOnDeferredNullChecks(currentScope, condInfo.unconditionalInits());
        if (isConditionTrue) {
            return FlowInfo.DEAD_END;
        }
        final FlowInfo mergedInfo = flowInfo.copy().addInitializationsFrom(condInfo.initsWhenFalse());
        if (isConditionOptimizedTrue) {
            mergedInfo.setReachMode(1);
        }
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        final Constant cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        if (isConditionOptimizedFalse) {
            this.condition.generateCode(currentScope, codeStream, false);
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        this.breakLabel.initialize(codeStream);
        if (this.continueLabel == null) {
            if (this.condition.constant == Constant.NotAConstant) {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, null, this.breakLabel, true);
            }
        }
        else {
            this.continueLabel.initialize(codeStream);
            if ((this.condition.constant == Constant.NotAConstant || !this.condition.constant.booleanValue()) && this.action != null && !this.action.isEmptyBlock()) {
                final int jumpPC = codeStream.position;
                codeStream.goto_(this.continueLabel);
                codeStream.recordPositionsFrom(jumpPC, this.condition.sourceStart);
            }
        }
        final BranchLabel actionLabel = new BranchLabel(codeStream);
        if (this.action != null) {
            final BranchLabel branchLabel = actionLabel;
            branchLabel.tagBits |= 0x2;
            if (this.condIfTrueInitStateIndex != -1) {
                codeStream.addDefinitelyAssignedVariables(currentScope, this.condIfTrueInitStateIndex);
            }
            actionLabel.place();
            this.action.generateCode(currentScope, codeStream);
            if (this.preCondInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
            }
        }
        else {
            actionLabel.place();
        }
        if (this.continueLabel != null) {
            this.continueLabel.place();
            this.condition.generateOptimizedBoolean(currentScope, codeStream, actionLabel, null, true);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        this.breakLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
        this.condition.computeConversion(scope, type, type);
        if (this.action != null) {
            this.action.resolve(scope);
        }
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("while (");
        this.condition.printExpression(0, output).append(')');
        if (this.action == null) {
            output.append(';');
        }
        else {
            this.action.printStatement(tab + 1, output);
        }
        return output;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.condition.traverse(visitor, blockScope);
            if (this.action != null) {
                this.action.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        Constant cst = this.condition.constant;
        final boolean isConditionTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        return (isConditionTrue || isConditionOptimizedTrue) && (this.action == null || !this.action.breaksOut(null));
    }
    
    @Override
    public boolean completesByContinue() {
        return this.action.continuesAtOuterLabel();
    }
}
