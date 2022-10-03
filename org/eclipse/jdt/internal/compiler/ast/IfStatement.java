package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class IfStatement extends Statement
{
    public Expression condition;
    public Statement thenStatement;
    public Statement elseStatement;
    int thenInitStateIndex;
    int elseInitStateIndex;
    int mergedInitStateIndex;
    
    public IfStatement(final Expression condition, final Statement thenStatement, final int sourceStart, final int sourceEnd) {
        this.thenInitStateIndex = -1;
        this.elseInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.condition = condition;
        this.thenStatement = thenStatement;
        if (thenStatement instanceof EmptyStatement) {
            thenStatement.bits |= 0x1;
        }
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    public IfStatement(final Expression condition, final Statement thenStatement, final Statement elseStatement, final int sourceStart, final int sourceEnd) {
        this.thenInitStateIndex = -1;
        this.elseInitStateIndex = -1;
        this.mergedInitStateIndex = -1;
        this.condition = condition;
        this.thenStatement = thenStatement;
        if (thenStatement instanceof EmptyStatement) {
            thenStatement.bits |= 0x1;
        }
        this.elseStatement = elseStatement;
        if (elseStatement instanceof IfStatement) {
            elseStatement.bits |= 0x20000000;
        }
        if (elseStatement instanceof EmptyStatement) {
            elseStatement.bits |= 0x1;
        }
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final FlowInfo conditionFlowInfo = this.condition.analyseCode(currentScope, flowContext, flowInfo);
        final int initialComplaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        final Constant cst = this.condition.optimizedBooleanConstant();
        this.condition.checkNPEbyUnboxing(currentScope, flowContext, flowInfo);
        final boolean isConditionOptimizedTrue = cst != Constant.NotAConstant && cst.booleanValue();
        final boolean isConditionOptimizedFalse = cst != Constant.NotAConstant && !cst.booleanValue();
        ++flowContext.conditionalLevel;
        FlowInfo thenFlowInfo = conditionFlowInfo.safeInitsWhenTrue();
        if (isConditionOptimizedFalse) {
            thenFlowInfo.setReachMode(1);
        }
        FlowInfo elseFlowInfo = conditionFlowInfo.initsWhenFalse().copy();
        if (isConditionOptimizedTrue) {
            elseFlowInfo.setReachMode(1);
        }
        if ((flowInfo.tagBits & 0x3) == 0x0 && (thenFlowInfo.tagBits & 0x3) != 0x0) {
            this.bits |= 0x100;
        }
        else if ((flowInfo.tagBits & 0x3) == 0x0 && (elseFlowInfo.tagBits & 0x3) != 0x0) {
            this.bits |= 0x80;
        }
        final boolean reportDeadCodeForKnownPattern = !Statement.isKnowDeadCodePattern(this.condition) || currentScope.compilerOptions().reportDeadCodeInTrivialIfStatement;
        if (this.thenStatement != null) {
            this.thenInitStateIndex = currentScope.methodScope().recordInitializationStates(thenFlowInfo);
            if (isConditionOptimizedFalse || (this.bits & 0x100) != 0x0) {
                if (reportDeadCodeForKnownPattern) {
                    this.thenStatement.complainIfUnreachable(thenFlowInfo, currentScope, initialComplaintLevel, false);
                }
                else {
                    this.bits &= 0xFFFFFEFF;
                }
            }
            thenFlowInfo = this.thenStatement.analyseCode(currentScope, flowContext, thenFlowInfo);
            if (!(this.thenStatement instanceof Block)) {
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        flowContext.expireNullCheckedFieldInfo();
        if ((thenFlowInfo.tagBits & 0x1) != 0x0) {
            this.bits |= 0x40000000;
        }
        if (this.elseStatement != null) {
            if (thenFlowInfo == FlowInfo.DEAD_END && (this.bits & 0x20000000) == 0x0 && !(this.elseStatement instanceof IfStatement)) {
                currentScope.problemReporter().unnecessaryElse(this.elseStatement);
            }
            this.elseInitStateIndex = currentScope.methodScope().recordInitializationStates(elseFlowInfo);
            if (isConditionOptimizedTrue || (this.bits & 0x80) != 0x0) {
                if (reportDeadCodeForKnownPattern) {
                    this.elseStatement.complainIfUnreachable(elseFlowInfo, currentScope, initialComplaintLevel, false);
                }
                else {
                    this.bits &= 0xFFFFFF7F;
                }
            }
            elseFlowInfo = this.elseStatement.analyseCode(currentScope, flowContext, elseFlowInfo);
            if (!(this.elseStatement instanceof Block)) {
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        currentScope.correlateTrackingVarsIfElse(thenFlowInfo, elseFlowInfo);
        final FlowInfo mergedInfo = FlowInfo.mergedOptimizedBranchesIfElse(thenFlowInfo, isConditionOptimizedTrue, elseFlowInfo, isConditionOptimizedFalse, true, flowInfo, this, reportDeadCodeForKnownPattern);
        this.mergedInitStateIndex = currentScope.methodScope().recordInitializationStates(mergedInfo);
        --flowContext.conditionalLevel;
        return mergedInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        final BranchLabel endifLabel = new BranchLabel(codeStream);
        final Constant cst;
        final boolean hasThenPart = ((cst = this.condition.optimizedBooleanConstant()) == Constant.NotAConstant || cst.booleanValue()) && this.thenStatement != null && !this.thenStatement.isEmptyBlock();
        final boolean hasElsePart = (cst == Constant.NotAConstant || !cst.booleanValue()) && this.elseStatement != null && !this.elseStatement.isEmptyBlock();
        if (hasThenPart) {
            BranchLabel falseLabel = null;
            if (cst != Constant.NotAConstant && cst.booleanValue()) {
                this.condition.generateCode(currentScope, codeStream, false);
            }
            else {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, null, hasElsePart ? (falseLabel = new BranchLabel(codeStream)) : endifLabel, true);
            }
            if (this.thenInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.thenInitStateIndex);
            }
            this.thenStatement.generateCode(currentScope, codeStream);
            if (hasElsePart) {
                if ((this.bits & 0x40000000) == 0x0) {
                    this.thenStatement.branchChainTo(endifLabel);
                    final int position = codeStream.position;
                    codeStream.goto_(endifLabel);
                    codeStream.recordPositionsFrom(position, this.thenStatement.sourceEnd);
                }
                if (this.elseInitStateIndex != -1) {
                    codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                    codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                }
                if (falseLabel != null) {
                    falseLabel.place();
                }
                this.elseStatement.generateCode(currentScope, codeStream);
            }
        }
        else if (hasElsePart) {
            if (cst != Constant.NotAConstant && !cst.booleanValue()) {
                this.condition.generateCode(currentScope, codeStream, false);
            }
            else {
                this.condition.generateOptimizedBoolean(currentScope, codeStream, endifLabel, null, true);
            }
            if (this.elseInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.elseInitStateIndex);
            }
            this.elseStatement.generateCode(currentScope, codeStream);
        }
        else {
            this.condition.generateCode(currentScope, codeStream, false);
            codeStream.recordPositionsFrom(pc, this.sourceStart);
        }
        if (this.mergedInitStateIndex != -1) {
            codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
            codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedInitStateIndex);
        }
        endifLabel.place();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("if (");
        this.condition.printExpression(0, output).append(")\n");
        this.thenStatement.printStatement(indent + 2, output);
        if (this.elseStatement != null) {
            output.append('\n');
            ASTNode.printIndent(indent, output);
            output.append("else\n");
            this.elseStatement.printStatement(indent + 2, output);
        }
        return output;
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        final TypeBinding type = this.condition.resolveTypeExpecting(scope, TypeBinding.BOOLEAN);
        this.condition.computeConversion(scope, type, type);
        if (this.thenStatement != null) {
            this.thenStatement.resolve(scope);
        }
        if (this.elseStatement != null) {
            this.elseStatement.resolve(scope);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.condition.traverse(visitor, blockScope);
            if (this.thenStatement != null) {
                this.thenStatement.traverse(visitor, blockScope);
            }
            if (this.elseStatement != null) {
                this.elseStatement.traverse(visitor, blockScope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return this.thenStatement != null && this.thenStatement.doesNotCompleteNormally() && this.elseStatement != null && this.elseStatement.doesNotCompleteNormally();
    }
    
    @Override
    public boolean completesByContinue() {
        return (this.thenStatement != null && this.thenStatement.completesByContinue()) || (this.elseStatement != null && this.elseStatement.completesByContinue());
    }
}
