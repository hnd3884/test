package net.sf.jsqlparser.expression.operators.arithmetic;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;

public class BitwiseXor extends BinaryExpression
{
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String getStringExpression() {
        return "^";
    }
}
