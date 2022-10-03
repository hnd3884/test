package net.sf.jsqlparser.expression;

public class IntervalExpression implements Expression
{
    private String parameter;
    private String intervalType;
    
    public IntervalExpression() {
        this.parameter = null;
        this.intervalType = null;
    }
    
    public String getParameter() {
        return this.parameter;
    }
    
    public void setParameter(final String parameter) {
        this.parameter = parameter;
    }
    
    public String getIntervalType() {
        return this.intervalType;
    }
    
    public void setIntervalType(final String intervalType) {
        this.intervalType = intervalType;
    }
    
    @Override
    public String toString() {
        return "INTERVAL " + this.parameter + ((this.intervalType != null) ? (" " + this.intervalType) : "");
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
}
