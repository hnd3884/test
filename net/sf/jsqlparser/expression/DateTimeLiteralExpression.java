package net.sf.jsqlparser.expression;

public class DateTimeLiteralExpression implements Expression
{
    private String value;
    private DateTime type;
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public DateTime getType() {
        return this.type;
    }
    
    public void setType(final DateTime type) {
        this.type = type;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.type.name() + " " + this.value;
    }
    
    public enum DateTime
    {
        DATE, 
        TIME, 
        TIMESTAMP;
    }
}
