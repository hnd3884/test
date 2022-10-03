package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.codegen.CodeStream;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;

public class PostfixExpression extends CompoundAssignment
{
    public PostfixExpression(final Expression lhs, final Expression expression, final int operator, final int pos) {
        super(lhs, expression, operator, pos);
        this.sourceStart = lhs.sourceStart;
        this.sourceEnd = pos;
    }
    
    @Override
    public boolean checkCastCompatibility() {
        return false;
    }
    
    @Override
    public void generateCode(final BlockScope currentScope, final CodeStream codeStream, final boolean valueRequired) {
        final int pc = codeStream.position;
        ((Reference)this.lhs).generatePostIncrement(currentScope, codeStream, this, valueRequired);
        if (valueRequired) {
            codeStream.generateImplicitConversion(this.implicitConversion);
        }
        codeStream.recordPositionsFrom(pc, this.sourceStart);
    }
    
    @Override
    public String operatorToString() {
        switch (this.operator) {
            case 14: {
                return "++";
            }
            case 13: {
                return "--";
            }
            default: {
                return "unknown operator";
            }
        }
    }
    
    @Override
    public StringBuffer printExpressionNoParenthesis(final int indent, final StringBuffer output) {
        return this.lhs.printExpression(indent, output).append(' ').append(this.operatorToString());
    }
    
    @Override
    public boolean restrainUsageToNumericTypes() {
        return true;
    }
    
    @Override
    public void traverse(final ASTVisitor visitor, final BlockScope scope) {
        if (visitor.visit(this, scope)) {
            this.lhs.traverse(visitor, scope);
        }
        visitor.endVisit(this, scope);
    }
}
