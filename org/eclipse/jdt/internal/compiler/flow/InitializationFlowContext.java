package org.eclipse.jdt.internal.compiler.flow;

import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class InitializationFlowContext extends ExceptionHandlingFlowContext
{
    public int exceptionCount;
    public TypeBinding[] thrownExceptions;
    public ASTNode[] exceptionThrowers;
    public FlowInfo[] exceptionThrowerFlowInfos;
    public FlowInfo initsBeforeContext;
    
    public InitializationFlowContext(final FlowContext parent, final ASTNode associatedNode, final FlowInfo initsBeforeContext, final FlowContext initializationParent, final BlockScope scope) {
        super(parent, associatedNode, Binding.NO_EXCEPTIONS, initializationParent, scope, FlowInfo.DEAD_END);
        this.thrownExceptions = new TypeBinding[5];
        this.exceptionThrowers = new ASTNode[5];
        this.exceptionThrowerFlowInfos = new FlowInfo[5];
        this.initsBeforeContext = initsBeforeContext;
    }
    
    public void checkInitializerExceptions(final BlockScope currentScope, final FlowContext initializerContext, final FlowInfo flowInfo) {
        for (int i = 0; i < this.exceptionCount; ++i) {
            initializerContext.checkExceptionHandlers(this.thrownExceptions[i], this.exceptionThrowers[i], this.exceptionThrowerFlowInfos[i], currentScope);
        }
    }
    
    @Override
    public FlowContext getInitializationContext() {
        return this;
    }
    
    @Override
    public String individualToString() {
        final StringBuffer buffer = new StringBuffer("Initialization flow context");
        for (int i = 0; i < this.exceptionCount; ++i) {
            buffer.append('[').append(this.thrownExceptions[i].readableName());
            buffer.append('-').append(this.exceptionThrowerFlowInfos[i].toString()).append(']');
        }
        return buffer.toString();
    }
    
    @Override
    public void recordHandlingException(final ReferenceBinding exceptionType, final UnconditionalFlowInfo flowInfo, final TypeBinding raisedException, final TypeBinding caughtException, final ASTNode invocationSite, final boolean wasMasked) {
        final int size = this.thrownExceptions.length;
        if (this.exceptionCount == size) {
            System.arraycopy(this.thrownExceptions, 0, this.thrownExceptions = new TypeBinding[size * 2], 0, size);
            System.arraycopy(this.exceptionThrowers, 0, this.exceptionThrowers = new ASTNode[size * 2], 0, size);
            System.arraycopy(this.exceptionThrowerFlowInfos, 0, this.exceptionThrowerFlowInfos = new FlowInfo[size * 2], 0, size);
        }
        this.thrownExceptions[this.exceptionCount] = raisedException;
        this.exceptionThrowers[this.exceptionCount] = invocationSite;
        this.exceptionThrowerFlowInfos[this.exceptionCount++] = flowInfo.copy();
    }
}
