package net.sf.jsqlparser.expression;

public class HexValue implements Expression
{
    private String stringValue;
    
    public HexValue(final String value) {
        final String val = value;
        this.stringValue = val;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public String getValue() {
        return this.stringValue;
    }
    
    public void setValue(final String d) {
        this.stringValue = d;
    }
    
    @Override
    public String toString() {
        return this.stringValue;
    }
}
