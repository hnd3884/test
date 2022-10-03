package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class ContinueStatement extends BranchStatement
{
    public ContinueStatement(final char[] label, final int sourceStart, final int sourceEnd) {
        super(label, sourceStart, sourceEnd);
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        final FlowContext targetContext = (this.label == null) ? flowContext.getTargetContextForDefaultContinue() : flowContext.getTargetContextForContinueLabel(this.label);
        if (targetContext == null) {
            if (this.label == null) {
                currentScope.problemReporter().invalidContinue(this);
            }
            else {
                currentScope.problemReporter().undefinedLabel(this);
            }
            return flowInfo;
        }
        targetContext.recordAbruptExit();
        targetContext.expireNullCheckedFieldInfo();
        if (targetContext == FlowContext.NotContinuableContext) {
            currentScope.problemReporter().invalidContinue(this);
            return flowInfo;
        }
        this.initStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        this.targetLabel = targetContext.continueLabel();
        FlowContext traversedContext = flowContext;
        int subCount = 0;
        this.subroutines = new SubRoutineStatement[5];
        do {
            final SubRoutineStatement sub;
            if ((sub = traversedContext.subroutine()) != null) {
                if (subCount == this.subroutines.length) {
                    System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount * 2], 0, subCount);
                }
                this.subroutines[subCount++] = sub;
                if (sub.isSubRoutineEscaping()) {
                    break;
                }
            }
            traversedContext.recordReturnFrom(flowInfo.unconditionalInits());
            if (traversedContext instanceof InsideSubRoutineFlowContext) {
                final ASTNode node = traversedContext.associatedNode;
                if (!(node instanceof TryStatement)) {
                    continue;
                }
                final TryStatement tryStatement = (TryStatement)node;
                flowInfo.addInitializationsFrom(tryStatement.subRoutineInits);
            }
            else {
                if (traversedContext == targetContext) {
                    targetContext.recordContinueFrom(flowContext, flowInfo);
                    break;
                }
                continue;
            }
        } while ((traversedContext = traversedContext.getLocalParent()) != null);
        if (subCount != this.subroutines.length) {
            System.arraycopy(this.subroutines, 0, this.subroutines = new SubRoutineStatement[subCount], 0, subCount);
        }
        return FlowInfo.DEAD_END;
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        ASTNode.printIndent(tab, output).append("continue ");
        if (this.label != null) {
            output.append(this.label);
        }
        return output.append(';');
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        visitor.visit(this, blockScope);
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return true;
    }
    
    @Override
    public boolean completesByContinue() {
        return true;
    }
}
