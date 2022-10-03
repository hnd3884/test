package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public abstract class TryFlowContext extends FlowContext
{
    public FlowContext outerTryContext;
    
    public TryFlowContext(final FlowContext parent, final ASTNode associatedNode) {
        super(parent, associatedNode);
    }
    
    @Override
    public void markFinallyNullStatus(final LocalVariableBinding local, final int nullStatus) {
        if (this.outerTryContext != null) {
            this.outerTryContext.markFinallyNullStatus(local, nullStatus);
        }
        super.markFinallyNullStatus(local, nullStatus);
    }
    
    @Override
    public void mergeFinallyNullInfo(final FlowInfo flowInfo) {
        if (this.outerTryContext != null) {
            this.outerTryContext.mergeFinallyNullInfo(flowInfo);
        }
        super.mergeFinallyNullInfo(flowInfo);
    }
}
