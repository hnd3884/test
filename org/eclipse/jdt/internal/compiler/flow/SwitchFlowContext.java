package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;

public class SwitchFlowContext extends FlowContext
{
    public BranchLabel breakLabel;
    public UnconditionalFlowInfo initsOnBreak;
    
    public SwitchFlowContext(final FlowContext parent, final ASTNode associatedNode, final BranchLabel breakLabel, final boolean isPreTest) {
        super(parent, associatedNode);
        this.initsOnBreak = FlowInfo.DEAD_END;
        this.breakLabel = breakLabel;
        if (isPreTest && parent.conditionalLevel > -1) {
            ++this.conditionalLevel;
        }
    }
    
    @Override
    public BranchLabel breakLabel() {
        return this.breakLabel;
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Switch flow context");
        buffer.append("[initsOnBreak -").append(this.initsOnBreak.toString()).append(']');
        return buffer.toString();
    }
    
    @Override
    public boolean isBreakable() {
        return true;
    }
    
    @Override
    public void recordBreakFrom(final FlowInfo flowInfo) {
        if ((this.initsOnBreak.tagBits & 0x1) == 0x0) {
            this.initsOnBreak = this.initsOnBreak.mergedWith(flowInfo.unconditionalInits());
        }
        else {
            this.initsOnBreak = flowInfo.unconditionalCopy();
        }
    }
}
