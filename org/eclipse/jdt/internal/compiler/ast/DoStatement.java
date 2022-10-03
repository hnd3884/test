package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public class DoStatement extends Statement
{
    public Expression condition;
    public Statement action;
    private BranchLabel breakLabel;
    private BranchLabel continueLabel;
    int mergedInitStateIndex;
    int preConditionInitStateIndex;
    
    public DoStatement(final Expression condition, final Statement action, final int sourceStart, final int sourceEnd) {
        this.mergedInitStateIndex = -1;
        this.preConditionInitStateIndex = -1;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
        this.condition = condition;
        this.action = action;
        if (action instanceof EmptyStatement) {
            action.bits |= 0x1;
        }
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        final LoopingFlowContext loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, currentScope, false);
        Constant cst = this.condition.constant;
        final boolean isConditionTrue = cst != Constant.NotAConstant && cst.booleanValue();
        cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        final int previousMode = flowInfo.reachMode();
        FlowInfo initsOnCondition = flowInfo;
        UnconditionalFlowInfo actionInfo = flowInfo.nullInfoLessUnconditionalCopy();
        if (this.action != null && !this.action.isEmptyBlock()) {
            actionInfo = this.action.analyseCode(currentScope, loopingContext, actionInfo).unconditionalInits();
            if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & 0x1) != 0x0) {
                this.continueLabel = null;
            }
            if ((this.condition.implicitConversion & 0x400) != 0x0) {
                initsOnCondition = flowInfo.unconditionalInits().addInitializationsFrom(actionInfo.mergedWith(loopingContext.initsOnContinue));
            }
        }
        this.condition.checkNPEbyUnboxing(currentScope, flowContext, initsOnCondition);
        actionInfo.setReachMode(previousMode);
        final LoopingFlowContext condLoopContext;
        final FlowInfo condInfo = this.condition.analyseCode(currentScope, condLoopContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, currentScope, true), ((this.action == null) ? actionInfo : actionInfo.mergedWith(loopingContext.initsOnContinue)).copy());
        this.preConditionInitStateIndex = currentScope.methodScope().recordInitializationStates(actionInfo.mergedWith(loopingContext.initsOnContinue));
        if (!isConditionOptimizedFalse && this.continueLabel != null) {
            loopingContext.complainOnDeferredFinalChecks(currentScope, condInfo);
            condLoopContext.complainOnDeferredFinalChecks(currentScope, condInfo);
            loopingContext.complainOnDeferredNullChecks(currentScope, flowInfo.unconditionalCopy().addPotentialNullInfoFrom(condInfo.initsWhenTrue().unconditionalInits()));
            condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo.addPotentialNullInfoFrom(condInfo.initsWhenTrue().unconditionalInits()));
        }
        else {
            loopingContext.complainOnDeferredNullChecks(currentScope, flowInfo.unconditionalCopy().addPotentialNullInfoFrom(condInfo.initsWhenTrue().unconditionalInits()), false);
            condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo.addPotentialNullInfoFrom(condInfo.initsWhenTrue().unconditionalInits()), false);
        }
        if (loopingContext.hasEscapingExceptions()) {
            FlowInfo loopbackFlowInfo = flowInfo.copy();
            loopbackFlowInfo = loopbackFlowInfo.mergedWith(loopbackFlowInfo.unconditionalCopy().addNullInfoFrom(condInfo.initsWhenTrue()).unconditionalInits());
            loopingContext.simulateThrowAfterLoopBack(loopbackFlowInfo);
        }
        final FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(((loopingContext.initsOnBreak.tagBits & 0x3) != 0x0) ? loopingContext.initsOnBreak : flowInfo.unconditionalCopy().addInitializationsFrom(loopingContext.initsOnBreak), isConditionOptimizedTrue, ((condInfo.tagBits & 0x3) == 0x0) ? flowInfo.copy().addInitializationsFrom(condInfo.initsWhenFalse()) : condInfo, false, !isConditionTrue);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        return mergedInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        final BranchLabel actionLabel = new BranchLabel(codeStream);
        if (this.action != null) {
            final BranchLabel branchLabel = actionLabel;
            branchLabel.tagBits |= 0x2;
        }
        actionLabel.place();
        this.breakLabel.initialize(codeStream);
        final boolean hasContinueLabel = this.continueLabel != null;
        if (hasContinueLabel) {
            this.continueLabel.initialize(codeStream);
        }
        if (this.action != null) {
            this.action.generateCode(currentScope, codeStream);
        }
        if (hasContinueLabel) {
            this.continueLabel.place();
            if (this.preConditionInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preConditionInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.preConditionInitStateIndex);
            }
            final Constant cst = this.condition.optimizedBooleanConstant();
            final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
            if (isConditionOptimizedFalse) {
                this.condition.generateCode(currentScope, codeStream, false);
            }
            else {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, actionLabel, null, true);
            }
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        if (this.breakLabel.forwardReferenceCount() > 0) {
            this.breakLabel.place();
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("do");
        if (this.action == null) {
            output.append(" ;\n");
        }
        else {
            output.append('\n');
            this.action.printStatement(indent + 1, output).append('\n');
        }
        output.append("while (");
        return this.condition.printExpression(0, output).append(");");
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
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            if (this.action != null) {
                this.action.traverse(visitor, scope);
            }
            this.condition.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        Constant cst = this.condition.constant;
        final boolean isConditionTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        cst = this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        if (isConditionTrue || isConditionOptimizedTrue) {
            return this.action == null || !this.action.breaksOut(null);
        }
        return this.action != null && !this.action.breaksOut(null) && (this.action.doesNotCompleteNormally() && !this.action.completesByContinue());
    }
    
    @Override
    public boolean completesByContinue() {
        return this.action.continuesAtOuterLabel();
    }
}
