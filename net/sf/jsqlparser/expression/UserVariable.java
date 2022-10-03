package net.sf.jsqlparser.expression;

public class UserVariable implements Expression
{
    private String name;
    private boolean doubleAdd;
    
    public UserVariable() {
        this.doubleAdd = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public boolean isDoubleAdd() {
        return this.doubleAdd;
    }
    
    public void setDoubleAdd(final boolean doubleAdd) {
        this.doubleAdd = doubleAdd;
    }
    
    @Override
    public String toString() {
        return "@" + (this.doubleAdd ? "@" : "") + this.name;
    }
}
