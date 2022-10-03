package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.SubRoutineStatement;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class InsideSubRoutineFlowContext extends TryFlowContext
{
    public UnconditionalFlowInfo initsOnReturn;
    
    public InsideSubRoutineFlowContext(final FlowContext parent, final ASTNode associatedNode) {
        super(parent, associatedNode);
        this.initsOnReturn = FlowInfo.DEAD_END;
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Inside SubRoutine flow context");
        buffer.append("[initsOnReturn -").append(this.initsOnReturn.toString()).append(']');
        return buffer.toString();
    }
    
    @Override
    public UnconditionalFlowInfo initsOnReturn() {
        return this.initsOnReturn;
    }
    
    @Override
    public boolean isNonReturningContext() {
        return ((SubRoutineStatement)this.associatedNode).isSubRoutineEscaping();
    }
    
    @Override
    public void recordReturnFrom(final UnconditionalFlowInfo flowInfo) {
        if ((flowInfo.tagBits & 0x1) == 0x0) {
            if (this.initsOnReturn == FlowInfo.DEAD_END) {
                this.initsOnReturn = (UnconditionalFlowInfo)flowInfo.copy();
            }
            else {
                this.initsOnReturn = this.initsOnReturn.mergedWith(flowInfo);
            }
        }
    }
    
    @Override
    public SubRoutineStatement subroutine() {
        return (SubRoutineStatement)this.associatedNode;
    }
}
