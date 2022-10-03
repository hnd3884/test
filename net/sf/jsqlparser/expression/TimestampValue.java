package net.sf.jsqlparser.expression;

import java.sql.Timestamp;

public class TimestampValue implements Expression
{
    private Timestamp value;
    
    public TimestampValue(final String value) {
        this.value = Timestamp.valueOf(value.substring(1, value.length() - 1));
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Timestamp getValue() {
        return this.value;
    }
    
    public void setValue(final Timestamp d) {
        this.value = d;
    }
    
    @Override
    public String toString() {
        return "{ts '" + this.value + "'}";
    }
}
