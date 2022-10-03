package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;

public class JsonOperator extends BinaryExpression
{
    private String op;
    
    public JsonOperator(final String op) {
        this.op = op;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String getStringExpression() {
        return this.op;
    }
}
