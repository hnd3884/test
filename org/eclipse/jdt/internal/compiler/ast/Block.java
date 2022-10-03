package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.codegen.BranchLabel;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.flow.FlowInfo;
import org.eclipse.jdt.internal.compiler.flow.FlowContext;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class Block extends Statement
{
    public Statement[] statements;
    public int explicitDeclarations;
    public BlockScope scope;
    
    public Block(final int explicitDeclarations) {
        this.explicitDeclarations = explicitDeclarations;
    }
    
    @Override
    public FlowInfo analyseCode(final BlockScope currentScope, final FlowContext flowContext, FlowInfo flowInfo) {
        if (this.statements == null) {
            return flowInfo;
        }
        int complaintLevel = ((flowInfo.reachMode() & 0x3) != 0x0) ? 1 : 0;
        final boolean enableSyntacticNullAnalysisForFields = currentScope.compilerOptions().enableSyntacticNullAnalysisForFields;
        for (int i = 0, max = this.statements.length; i < max; ++i) {
            final Statement stat = this.statements[i];
            if ((complaintLevel = stat.complainIfUnreachable(flowInfo, this.scope, complaintLevel, true)) < 2) {
                flowInfo = stat.analyseCode(this.scope, flowContext, flowInfo);
            }
            flowContext.mergeFinallyNullInfo(flowInfo);
            if (enableSyntacticNullAnalysisForFields) {
                flowContext.expireNullCheckedFieldInfo();
            }
        }
        if (this.scope != currentScope) {
            this.scope.checkUnclosedCloseables(flowInfo, flowContext, null, null);
        }
        if (this.explicitDeclarations > 0) {
            final LocalVariableBinding[] locals = this.scope.locals;
            if (locals != null) {
                for (int numLocals = this.scope.localIndex, j = 0; j < numLocals; ++j) {
                    flowInfo.resetAssignmentInfo(locals[j]);
                }
            }
        }
        return flowInfo;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream) {
        if ((this.bits & Integer.MIN_VALUE) == 0x0) {
            return;
        }
        final int pc = codeStream.position;
        if (this.statements != null) {
            for (int i = 0, max = this.statements.length; i < max; ++i) {
                this.statements[i].generateCode(this.scope, codeStream);
            }
        }
        if (this.scope != currentScope) {
            codeStream.exitUserScope(this.scope);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public boolean isEmptyBlock() {
        return this.statements == null;
    }
    
    public StringBuffer printBody(final int indent, final StringBuffer output) {
        if (this.statements == null) {
            return output;
        }
        for (int i = 0; i < this.statements.length; ++i) {
            this.statements[i].printStatement(indent + 1, output);
            output.append('\n');
        }
        return output;
    }
    
    @Override
    public StringBuffer printStatement(final int indent, final StringBuffer output) {
        ASTNode.printIndent(indent, output);
        output.append("{\n");
        this.printBody(indent, output);
        return ASTNode.printIndent(indent, output).append('}');
    }
    
    @Override
    public void resolve(final BlockScope upperScope) {
        if ((this.bits & 0x8) != 0x0) {
            upperScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
        }
        if (this.statements != null) {
            this.scope = ((this.explicitDeclarations == 0) ? upperScope : new BlockScope(upperScope, this.explicitDeclarations));
            for (int i = 0, length = this.statements.length; i < length; ++i) {
                this.statements[i].resolve(this.scope);
            }
        }
    }
    
    public void resolveUsing(final BlockScope givenScope) {
        if ((this.bits & 0x8) != 0x0) {
            givenScope.problemReporter().undocumentedEmptyBlock(this.sourceStart, this.sourceEnd);
        }
        this.scope = givenScope;
        if (this.statements != null) {
            for (int i = 0, length = this.statements.length; i < length; ++i) {
                this.statements[i].resolve(this.scope);
            }
        }
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope blockScope) {
        if (visitor.visit(this, blockScope) && this.statements != null) {
            for (int i = 0, length = this.statements.length; i < length; ++i) {
                this.statements[i].traverse(visitor, this.scope);
            }
        }
        visitor.endVisit(this, blockScope);
    }
    
    @Override
    public void branchChainTo(final BranchLabel label) {
        if (this.statements != null) {
            this.statements[this.statements.length - 1].branchChainTo(label);
        }
    }
    
    @Override
    public boolean doesNotCompleteNormally() {
        final int length = (this.statements == null) ? 0 : this.statements.length;
        return length > 0 && this.statements[length - 1].doesNotCompleteNormally();
    }
    
    @Override
    public boolean completesByContinue() {
        final int length = (this.statements == null) ? 0 : this.statements.length;
        return length > 0 && this.statements[length - 1].completesByContinue();
    }
}
