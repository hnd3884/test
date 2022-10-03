package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class EmptyStatement extends Statement
{
    public EmptyStatement(final int startPosition, final int endPosition) {
        this.sourceStart = startPosition;
        this.sourceEnd = endPosition;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, final FlowInfo flowInfo) {
        return flowInfo;
    }
    
    @Override
    public int complainIfUnreachable(final FlowInfo flowInfo, final BlockScope scope, final int complaintLevel, final boolean endOfBlock) {
        if (scope.compilerOptions().complianceLevel < 3145728L) {
            return complaintLevel;
        }
        return super.complainIfUnreachable(flowInfo, scope, complaintLevel, endOfBlock);
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
    }
    
    @Override
    public StringBuffer printStatement(final int tab, final StringBuffer output) {
        return ASTNode.printIndent(tab, output).append(';');
    }
    
    @Override
    public void resolve(final BlockScope scope) {
        if ((this.bits & 0x1) == 0x0) {
            scope.problemReporter().superfluousSemicolon(this.sourceStart, this.sourceEnd);
        }
        else {
            scope.problemReporter().emptyControlFlowStatement(this.sourceStart, this.sourceEnd);
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        visitor.visit(this, scope);
        visitor.endVisit(this, scope);
    }
}
