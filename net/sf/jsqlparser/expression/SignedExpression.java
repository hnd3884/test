package net.sf.jsqlparser.expression;

public class SignedExpression implements Expression
{
    private char sign;
    private Expression expression;
    
    public SignedExpression(final char sign, final Expression expression) {
        this.setSign(sign);
        this.setExpression(expression);
    }
    
    public char getSign() {
        return this.sign;
    }
    
    public final void setSign(final char sign) {
        this.sign = sign;
        if (sign != '+' && sign != '-') {
            throw new IllegalArgumentException("illegal sign character, only + - allowed");
        }
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public final void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.getSign() + this.expression.toString();
    }
}
