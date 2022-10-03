package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.UnconditionalFlowInfo;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.flow.LoopingFlowContext;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ForStatement extends Statement
{
    public Statement[] initializations;
    public Expression condition;
    public Statement[] increments;
    public Statement action;
    public BlockScope scope;
    private BranchLabel breakLabel;
    private BranchLabel continueLabel;
    int preCondInitStateIndex;
    int preIncrementsInitStateIndex;
    int condIfTrueInitStateIndex;
    int mergedInitStateIndex;
    
    public ForStatement(final Statement[] initializations, final Expression condition, final Statement[] increments, final Statement action, final boolean neededScope, final int s, final int e) {
        this.preCondInitStateIndex = -1;
        this.preIncrementsInitStateIndex = -1;
        this.condIfTrueInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.sourceStart = s;
        this.sourceEnd = e;
        this.initializations = initializations;
        this.condition = condition;
        this.increments = increments;
        this.action = action;
        if (action instanceof EmptyStatement) {
            action.bits |= 0x1;
        }
        if (neededScope) {
            this.bits |= 0x20000000;
        }
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.breakLabel = new BranchLabel();
        this.continueLabel = new BranchLabel();
        final int initialComplaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        if (this.initializations != null) {
            for (int i = 0, count = this.initializations.length; i < count; ++i) {
                flowInfo = this.initializations[i].analyseCode(this.scope, flowContext, flowInfo);
            }
        }
        this.preCondInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        Constant cst = (this.condition == null) ? null : this.condition.constant;
        final boolean isConditionTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        final boolean isConditionFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        cst = ((this.condition == null) ? null : this.condition.optimizedBooleanConstant());
        final boolean isConditionOptimizedTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        final boolean isConditionOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        LoopingFlowContext condLoopContext = null;
        FlowInfo condInfo = flowInfo.nullInfoLessUnconditionalCopy();
        if (this.condition != null && !isConditionTrue) {
            condInfo = this.condition.analyseCode(this.scope, condLoopContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, this.scope, true), condInfo);
            this.condition.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        }
        UnconditionalFlowInfo actionInfo;
        LoopingFlowContext loopingContext;
        if (this.action == null || (this.action.isEmptyBlock() && currentScope.compilerOptions().complianceLevel <= 3080192L)) {
            if (condLoopContext != null) {
                condLoopContext.complainOnDeferredFinalChecks(this.scope, condInfo);
            }
            if (isConditionTrue) {
                if (condLoopContext != null) {
                    condLoopContext.complainOnDeferredNullChecks(currentScope, condInfo);
                }
                return FlowInfo.DEAD_END;
            }
            if (isConditionFalse) {
                this.continueLabel = null;
            }
            actionInfo = condInfo.initsWhenTrue().unconditionalCopy();
            loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope, false);
        }
        else {
            loopingContext = new LoopingFlowContext(flowContext, flowInfo, this, this.breakLabel, this.continueLabel, this.scope, true);
            final FlowInfo initsWhenTrue = condInfo.initsWhenTrue();
            this.condIfTrueInitStateIndex = currentScope.methodScope().recordInitializationStates(initsWhenTrue);
            if (isConditionFalse) {
                actionInfo = FlowInfo.DEAD_END;
            }
            else {
                actionInfo = initsWhenTrue.unconditionalCopy();
                if (isConditionOptimizedFalse) {
                    actionInfo.setReachMode(1);
                }
            }
            if (this.action.complainIfUnreachable(actionInfo, this.scope, initialComplaintLevel, true) < 2) {
                actionInfo = this.action.analyseCode(this.scope, loopingContext, actionInfo).unconditionalInits();
            }
            if ((actionInfo.tagBits & loopingContext.initsOnContinue.tagBits & 0x1) != 0x0) {
                this.continueLabel = null;
            }
            else {
                if (condLoopContext != null) {
                    condLoopContext.complainOnDeferredFinalChecks(this.scope, condInfo);
                }
                actionInfo = actionInfo.mergedWith(loopingContext.initsOnContinue);
                loopingContext.complainOnDeferredFinalChecks(this.scope, actionInfo);
            }
        }
        final FlowInfo exitBranch = flowInfo.copy();
        LoopingFlowContext incrementContext = null;
        if (this.continueLabel != null) {
            if (this.increments != null) {
                incrementContext = new LoopingFlowContext(flowContext, flowInfo, this, null, null, this.scope, true);
                FlowInfo incrementInfo = actionInfo;
                this.preIncrementsInitStateIndex = currentScope.methodScope().recordInitializationStates(incrementInfo);
                for (int j = 0, count2 = this.increments.length; j < count2; ++j) {
                    incrementInfo = this.increments[j].analyseCode(this.scope, incrementContext, incrementInfo);
                }
                incrementContext.complainOnDeferredFinalChecks(this.scope, actionInfo = incrementInfo.unconditionalInits());
            }
            exitBranch.addPotentialInitializationsFrom(actionInfo).addInitializationsFrom(condInfo.initsWhenFalse());
        }
        else {
            exitBranch.addInitializationsFrom(condInfo.initsWhenFalse());
            if (this.increments != null && initialComplaintLevel == 0) {
                currentScope.problemReporter().fakeReachable(this.increments[0]);
            }
        }
        if (condLoopContext != null) {
            condLoopContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        }
        loopingContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        if (incrementContext != null) {
            incrementContext.complainOnDeferredNullChecks(currentScope, actionInfo);
        }
        if (loopingContext.hasEscapingExceptions()) {
            FlowInfo loopbackFlowInfo = flowInfo.copy();
            if (this.continueLabel != null) {
                loopbackFlowInfo = loopbackFlowInfo.mergedWith(loopbackFlowInfo.unconditionalCopy().addNullInfoFrom(actionInfo).unconditionalInits());
            }
            loopingContext.simulateThrowAfterLoopBack(loopbackFlowInfo);
        }
        final FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranches(((loopingContext.initsOnBreak.tagBits & 0x3) != 0x0) ? loopingContext.initsOnBreak : flowInfo.addInitializationsFrom(loopingContext.initsOnBreak), isConditionOptimizedTrue, exitBranch, isConditionOptimizedFalse, !isConditionTrue);
        if (this.initializations != null) {
            for (int j = 0; j < this.initializations.length; ++j) {
                final Statement init = this.initializations[j];
                if (init instanceof LocalDeclaration) {
                    final LocalVariableBinding binding = ((LocalDeclaration)init).binding;
                    mergedInfo.resetAssignmentInfo(binding);
                }
            }
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
        if (this.initializations != null) {
            for (int i = 0, max = this.initializations.length; i < max; ++i) {
                this.initializations[i].generateCode(this.scope, codeStream);
            }
        }
        final Constant cst = (this.condition == null) ? null : this.condition.optimizedBooleanConstant();
        final boolean isConditionOptimizedFalse = cst != null && cst != Constant.NotAConstant && !cst.booleanValue();
        if (isConditionOptimizedFalse) {
            this.condition.generateCode(this.scope, codeStream, false);
            if ((this.bits & 0x20000000) != 0x0) {
                codeStream.exitUserScope(this.scope);
            }
            if (this.mergedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            }
            codeStream.recordPositionsFrom(pc, this.sourceStart);
            return;
        }
        final BranchLabel branchLabel;
        final BranchLabel actionLabel = branchLabel = new BranchLabel(codeStream);
        branchLabel.tagBits |= 0x2;
        final BranchLabel conditionLabel = new BranchLabel(codeStream);
        this.breakLabel.initialize(codeStream);
        if (this.continueLabel == null) {
            conditionLabel.place();
            if (this.condition != null && this.condition.constant == Constant.NotAConstant) {
                this.condition.generateOptimizedBoolean(this.scope, codeStream, null, this.breakLabel, true);
            }
        }
        else {
            this.continueLabel.initialize(codeStream);
            if (this.condition != null && this.condition.constant == Constant.NotAConstant && ((this.action != null && !this.action.isEmptyBlock()) || this.increments != null)) {
                final BranchLabel branchLabel2 = conditionLabel;
                branchLabel2.tagBits |= 0x2;
                final int jumpPC = codeStream.position;
                codeStream.goto_(conditionLabel);
                codeStream.recordPositionsFrom(jumpPC, this.condition.sourceStart);
            }
        }
        if (this.action != null) {
            if (this.condIfTrueInitStateIndex != -1) {
                codeStream.addDefinitelyAssignedVariables(currentScope, this.condIfTrueInitStateIndex);
            }
            actionLabel.place();
            this.action.generateCode(this.scope, codeStream);
        }
        else {
            actionLabel.place();
        }
        if (this.preIncrementsInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.preIncrementsInitStateIndex);
        }
        if (this.continueLabel != null) {
            this.continueLabel.place();
            if (this.increments != null) {
                for (int j = 0, max2 = this.increments.length; j < max2; ++j) {
                    this.increments[j].generateCode(this.scope, codeStream);
                }
            }
            if (this.preCondInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
            }
            conditionLabel.place();
            if (this.condition != null && this.condition.constant == Constant.NotAConstant) {
                this.condition.generateOptimizedBoolean(this.scope, codeStream, actionLabel, null, true);
            }
            else {
                codeStream.goto_(actionLabel);
            }
        }
        else if (this.preCondInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preCondInitStateIndex);
        }
        if ((this.bits & 0x20000000) != 0x0) {
            codeStream.exitUserScope(this.scope);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        this.breakLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("for (");
        if (this.initializations != null) {
            for (int i = 0; i < this.initializations.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.initializations[i].print(0, output);
            }
        }
        output.append("; ");
        if (this.condition != null) {
            this.condition.printExpression(0, output);
        }
        output.append("; ");
        if (this.increments != null) {
            for (int i = 0; i < this.increments.length; ++i) {
                if (i > 0) {
                    output.append(", ");
                }
                this.increments[i].print(0, output);
            }
        }
        output.append(") ");
        if (this.action == null) {
            output.append(';');
        }
        else {
            output.append('\n');
            this.action.printStatement(tab + 1, output);
        }
        return output;
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        this.scope = (((this.bits & 0x20000000) != 0x0) ? new BlockScope(upperScope) : upperScope);
        if (this.initializations != null) {
            for (int i = 0, length = this.initializations.length; i < length; ++i) {
                this.initializations[i].resolve(this.scope);
            }
        }
        if (this.condition != null) {
            final TypeBinding type = this.condition.resolveTypeExpecting(this.scope, TypeBinding.BOOLEAN);
            this.condition.computeConversion(this.scope, type, type);
        }
        if (this.increments != null) {
            for (int i = 0, length = this.increments.length; i < length; ++i) {
                this.increments[i].resolve(this.scope);
            }
        }
        if (this.action != null) {
            this.action.resolve(this.scope);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            if (this.initializations != null) {
                for (int initializationsLength = this.initializations.length, i = 0; i < initializationsLength; ++i) {
                    this.initializations[i].traverse(visitor, this.scope);
                }
            }
            if (this.condition != null) {
                this.condition.traverse(visitor, this.scope);
            }
            if (this.increments != null) {
                for (int incrementsLength = this.increments.length, i = 0; i < incrementsLength; ++i) {
                    this.increments[i].traverse(visitor, this.scope);
                }
            }
            if (this.action != null) {
                this.action.traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        Constant cst = (this.condition == null) ? null : this.condition.constant;
        final boolean isConditionTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        cst = ((this.condition == null) ? null : this.condition.optimizedBooleanConstant());
        final boolean isConditionOptimizedTrue = cst == null || (cst != Constant.NotAConstant && cst.booleanValue());
        return (isConditionTrue || isConditionOptimizedTrue) && (this.action == null || !this.action.breaksOut(null));
    }
    
    @Override
    public boolean completesByContinue() {
        return this.action.continuesAtOuterLabel();
    }
}
