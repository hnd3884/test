package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Expression;

public class Top
{
    private boolean hasParenthesis;
    private boolean isPercentage;
    private Expression expression;
    
    public Top() {
        this.hasParenthesis = false;
        this.isPercentage = false;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    public boolean hasParenthesis() {
        return this.hasParenthesis;
    }
    
    public void setParenthesis(final boolean hasParenthesis) {
        this.hasParenthesis = hasParenthesis;
    }
    
    public boolean isPercentage() {
        return this.isPercentage;
    }
    
    public void setPercentage(final boolean percentage) {
        this.isPercentage = percentage;
    }
    
    @Override
    public String toString() {
        String result = "TOP ";
        if (this.hasParenthesis) {
            result += "(";
        }
        result += this.expression.toString();
        if (this.hasParenthesis) {
            result += ")";
        }
        if (this.isPercentage) {
            result += " PERCENT";
        }
        return result;
    }
}
