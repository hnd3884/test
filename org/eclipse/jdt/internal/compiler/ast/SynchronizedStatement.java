package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.InsideSubRoutineFlowContext;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class SynchronizedStatement extends SubRoutineStatement
{
    public Expression expression;
    public Block block;
    public BlockScope scope;
    public LocalVariableBinding synchroVariable;
    static final char[] SecretLocalDeclarationName;
    int preSynchronizedInitStateIndex;
    int mergedSynchronizedInitStateIndex;
    
    static {
        SecretLocalDeclarationName = " syncValue".toCharArray();
    }
    
    public SynchronizedStatement(final Expression expression, final Block statement, final int s, final int e) {
        this.preSynchronizedInitStateIndex = -1;
        this.mergedSynchronizedInitStateIndex = -1;
        this.expression = expression;
        this.block = statement;
        this.sourceEnd = e;
        this.sourceStart = s;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        this.preSynchronizedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        this.synchroVariable.useFlag = 1;
        flowInfo = this.block.analyseCode(this.scope, new InsideSubRoutineFlowContext(flowContext, this), this.expression.analyseCode(this.scope, flowContext, flowInfo));
        this.mergedSynchronizedInitStateIndex = currentScope.methodScope().recordInitializationStates(flowInfo);
        if ((flowInfo.tagBits & 0x1) != 0x0) {
            this.bits |= 0x20000000;
        }
        return flowInfo;
    }
    
    @Override
    public boolean isSubRoutineEscaping() {
        return false;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        this.anyExceptionLabel = null;
        final int pc = codeStream.position;
        this.expression.generateCode(this.scope, codeStream, true);
        if (this.block.isEmptyBlock()) {
            switch (this.synchroVariable.type.id) {
                case 7:
                case 8: {
                    codeStream.dup2();
                    break;
                }
                default: {
                    codeStream.dup();
                    break;
                }
            }
            codeStream.monitorenter();
            codeStream.monitorexit();
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope);
            }
        }
        else {
            codeStream.store(this.synchroVariable, true);
            codeStream.addVariable(this.synchroVariable);
            codeStream.monitorenter();
            this.enterAnyExceptionHandler(codeStream);
            this.block.generateCode(this.scope, codeStream);
            if (this.scope != currentScope) {
                codeStream.exitUserScope(this.scope, this.synchroVariable);
            }
            final BranchLabel endLabel = new BranchLabel(codeStream);
            if ((this.bits & 0x20000000) == 0x0) {
                codeStream.load(this.synchroVariable);
                codeStream.monitorexit();
                this.exitAnyExceptionHandler();
                codeStream.goto_(endLabel);
                this.enterAnyExceptionHandler(codeStream);
            }
            codeStream.pushExceptionOnStack(this.scope.getJavaLangThrowable());
            if (this.preSynchronizedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.preSynchronizedInitStateIndex);
            }
            this.placeAllAnyExceptionHandler();
            codeStream.load(this.synchroVariable);
            codeStream.monitorexit();
            this.exitAnyExceptionHandler();
            codeStream.athrow();
            if (this.mergedSynchronizedInitStateIndex != -1) {
                codeStream.removeNotDefinitelyAssignedVariables(currentScope, this.mergedSynchronizedInitStateIndex);
                codeStream.addDefinitelyAssignedVariables(currentScope, this.mergedSynchronizedInitStateIndex);
            }
            if (this.scope != currentScope) {
                codeStream.removeVariable(this.synchroVariable);
            }
            if ((this.bits & 0x20000000) == 0x0) {
                endLabel.place();
            }
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public boolean generateSubRoutineInvocation(final BlockScope currentScope, final CodeStream codeStream, final Object targetLocation, final int stateIndex, final LocalVariableBinding secretLocal) {
        codeStream.load(this.synchroVariable);
        codeStream.monitorexit();
        this.exitAnyExceptionHandler();
        return false;
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        this.scope = new BlockScope(upperScope);
        final TypeBinding type = this.expression.resolveType(this.scope);
        if (type != null) {
            switch (type.id) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 7:
                case 8:
                case 9:
                case 10: {
                    this.scope.problemReporter().invalidTypeToSynchronize(this.expression, type);
                    break;
                }
                case 6: {
                    this.scope.problemReporter().illegalVoidExpression(this.expression);
                    break;
                }
                case 12: {
                    this.scope.problemReporter().invalidNullToSynchronize(this.expression);
                    break;
                }
            }
            this.synchroVariable = new LocalVariableBinding(SynchronizedStatement.SecretLocalDeclarationName, type, 0, false);
            this.scope.addLocalVariable(this.synchroVariable);
            this.synchroVariable.setConstant(Constant.NotAConstant);
            this.expression.computeConversion(this.scope, type, type);
        }
        this.block.resolveUsing(this.scope);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        output.append("synchronized (");
        this.expression.printExpression(0, output).append(')');
        output.append('\n');
        return this.block.printStatement(indent + 1, output);
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.expression.traverse(visitor, this.scope);
            this.block.traverse(visitor, this.scope);
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return this.block.doesNotCompleteNormally();
    }
    
    @Override
    public boolean completesByContinue() {
        return this.block.completesByContinue();
    }
}
