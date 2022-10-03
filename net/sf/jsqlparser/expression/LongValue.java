package net.sf.jsqlparser.expression;

import java.math.BigInteger;

public class LongValue implements Expression
{
    private String stringValue;
    
    public LongValue(final String value) {
        String val = value;
        if (val.charAt(0) == '+') {
            val = val.substring(1);
        }
        this.stringValue = val;
    }
    
    public LongValue(final long value) {
        this.stringValue = String.valueOf(value);
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public long getValue() {
        return Long.valueOf(this.stringValue);
    }
    
    public BigInteger getBigIntegerValue() {
        return new BigInteger(this.stringValue);
    }
    
    public void setValue(final long d) {
        this.stringValue = String.valueOf(d);
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
