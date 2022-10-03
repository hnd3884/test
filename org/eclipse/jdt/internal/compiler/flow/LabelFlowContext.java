package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class LabelFlowContext extends SwitchFlowContext
{
    public char[] labelName;
    
    public LabelFlowContext(final FlowContext parent, final ASTNode associatedNode, final char[] labelName, final BranchLabel breakLabel, final BlockScope scope) {
        super(parent, associatedNode, breakLabel, false);
        this.labelName = labelName;
        this.checkLabelValidity(scope);
    }
    
    void checkLabelValidity(final BlockScope scope) {
        for (FlowContext current = this.getLocalParent(); current != null; current = current.getLocalParent()) {
            final char[] currentLabelName;
            if ((currentLabelName = current.labelName()) != null && CharOperation.equals(currentLabelName, this.labelName)) {
                scope.problemReporter().alreadyDefinedLabel(this.labelName, this.associatedNode);
            }
        }
    }
    
    @Override
    public String individualToString() {
        return "Label flow context [label:" + String.valueOf(this.labelName) + "]";
    }
    
    @Override
    public char[] labelName() {
        return this.labelName;
    }
}
