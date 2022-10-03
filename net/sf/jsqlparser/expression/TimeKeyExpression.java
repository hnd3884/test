package net.sf.jsqlparser.expression;

public class TimeKeyExpression implements Expression
{
    private String stringValue;
    
    public TimeKeyExpression(final String value) {
        this.stringValue = value;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public String getStringValue() {
        return this.stringValue;
    }
    
    public void setStringValue(final String string) {
        this.stringValue = string;
    }
    
    @Override
    public String toString() {
        return this.getStringValue();
    }
}
