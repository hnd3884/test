package org.eclipse.jdt.internal.compiler.ast;

import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.ASTVisitor;

public class PrefixExpression extends CompoundAssignment
{
    public PrefixExpression(final Expression lhs, final Expression expression, final int operator, final int pos) {
        super(lhs, expression, operator, lhs.sourceEnd);
        this.sourceStart = pos;
        this.sourceEnd = lhs.sourceEnd;
    }
    
    @Override
    public boolean checkCastCompatibility() {
        return false;
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
        output.append(this.operatorToString()).append(' ');
        return this.lhs.printExpression(0, output);
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
