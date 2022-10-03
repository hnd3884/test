package net.sf.jsqlparser.expression;

public class DoubleValue implements Expression
{
    private double value;
    private String stringValue;
    
    public DoubleValue(final String value) {
        String val = value;
        if (val.charAt(0) == '+') {
            val = val.substring(1);
        }
        this.value = Double.parseDouble(val);
        this.stringValue = val;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public double getValue() {
        return this.value;
    }
    
    public void setValue(final double d) {
        this.value = d;
    }
    
    @Override
    public String toString() {
        return this.stringValue;
    }
}
