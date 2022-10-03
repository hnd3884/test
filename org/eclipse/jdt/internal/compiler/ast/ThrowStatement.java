package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

public class ThrowStatement extends Statement
{
    public Expression exception;
    public TypeBinding exceptionType;
    
    public ThrowStatement(final Expression exception, final int sourceStart, final int sourceEnd) {
        this.exception = exception;
        this.sourceStart = sourceStart;
        this.sourceEnd = sourceEnd;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        this.exception.analyseCode(currentScope, flowContext, flowInfo);
        this.exception.checkNPE(currentScope, flowContext, flowInfo);
        flowContext.checkExceptionHandlers(this.exceptionType, this, flowInfo, currentScope);
        currentScope.checkUnclosedCloseables(flowInfo, flowContext, this, currentScope);
        flowContext.recordAbruptExit();
        return FlowInfo.DEAD_END;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        this.exception.generateCode(currentScope, codeStream, true);
        codeStream.athrow();
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output).append("throw ");
        this.exception.printExpression(0, output);
        return output.append(';');
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        this.exceptionType = this.exception.resolveType(scope);
        if (this.exceptionType != null && this.exceptionType.isValidBinding()) {
            if (this.exceptionType == TypeBinding.NULL) {
                if (scope.compilerOptions().complianceLevel <= 3080192L) {
                    scope.problemReporter().cannotThrowNull(this.exception);
                }
            }
            else if (this.exceptionType.findSuperTypeOriginatingFrom(21, true) == null) {
                scope.problemReporter().cannotThrowType(this.exception, this.exceptionType);
            }
            this.exception.computeConversion(scope, this.exceptionType, this.exceptionType);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope)) {
            this.exception.traverse(visitor, blockScope);
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        return true;
    }
}
