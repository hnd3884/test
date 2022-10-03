package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;

public class ExceptionInferenceFlowContext extends FieldInitsFakingFlowContext
{
    public ExceptionInferenceFlowContext(final FlowContext parent, final ASTNode associatedNode, final ReferenceBinding[] handledExceptions, final FlowContext initializationParent, final BlockScope scope, final UnconditionalFlowInfo flowInfo) {
        super(parent, associatedNode, handledExceptions, initializationParent, scope, flowInfo);
    }
}
