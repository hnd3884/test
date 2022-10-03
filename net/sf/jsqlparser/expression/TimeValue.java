package net.sf.jsqlparser.expression;

import java.sql.Time;

public class TimeValue implements Expression
{
    private Time value;
    
    public TimeValue(final String value) {
        this.value = Time.valueOf(value.substring(1, value.length() - 1));
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Time getValue() {
        return this.value;
    }
    
    public void setValue(final Time d) {
        this.value = d;
    }
    
    @Override
    public String toString() {
        return "{t '" + this.value + "'}";
    }
}
