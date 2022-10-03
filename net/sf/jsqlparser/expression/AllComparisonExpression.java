package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.SubSelect;

public class AllComparisonExpression implements Expression
{
    private final SubSelect subSelect;
    
    public AllComparisonExpression(final SubSelect subSelect) {
        this.subSelect = subSelect;
    }
    
    public SubSelect getSubSelect() {
        return this.subSelect;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return "ALL " + this.subSelect.toString();
    }
}
