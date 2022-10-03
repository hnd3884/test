package net.sf.jsqlparser.expression;

public class NumericBind implements Expression
{
    private int bindId;
    
    public int getBindId() {
        return this.bindId;
    }
    
    public void setBindId(final int bindId) {
        this.bindId = bindId;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return ":" + this.bindId;
    }
}
