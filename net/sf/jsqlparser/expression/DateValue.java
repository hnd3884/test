package net.sf.jsqlparser.expression;

import java.sql.Date;

public class DateValue implements Expression
{
    private Date value;
    
    public DateValue(final String value) {
        this.value = Date.valueOf(value.substring(1, value.length() - 1));
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Date getValue() {
        return this.value;
    }
    
    public void setValue(final Date d) {
        this.value = d;
    }
    
    @Override
    public String toString() {
        return "{d '" + this.value.toString() + "'}";
    }
}
