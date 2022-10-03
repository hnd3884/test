package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.SubSelect;

public class AnyComparisonExpression implements Expression
{
    private final SubSelect subSelect;
    private final AnyType anyType;
    
    public AnyComparisonExpression(final AnyType anyType, final SubSelect subSelect) {
        this.anyType = anyType;
        this.subSelect = subSelect;
    }
    
    public SubSelect getSubSelect() {
        return this.subSelect;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public AnyType getAnyType() {
        return this.anyType;
    }
    
    @Override
    public String toString() {
        return this.anyType.name() + " " + this.subSelect.toString();
    }
}
