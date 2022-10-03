package net.sf.jsqlparser.expression.operators.conditional;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.BinaryExpression;

public class OrExpression extends BinaryExpression
{
    public OrExpression(final Expression leftExpression, final Expression rightExpression) {
        this.setLeftExpression(leftExpression);
        this.setRightExpression(rightExpression);
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String getStringExpression() {
        return "OR";
    }
}
