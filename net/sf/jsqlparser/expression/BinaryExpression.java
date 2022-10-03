package net.sf.jsqlparser.expression;

public abstract class BinaryExpression implements Expression
{
    private Expression leftExpression;
    private Expression rightExpression;
    private boolean not;
    
    public BinaryExpression() {
        this.not = false;
    }
    
    public Expression getLeftExpression() {
        return this.leftExpression;
    }
    
    public Expression getRightExpression() {
        return this.rightExpression;
    }
    
    public void setLeftExpression(final Expression expression) {
        this.leftExpression = expression;
    }
    
    public void setRightExpression(final Expression expression) {
        this.rightExpression = expression;
    }
    
    public void setNot() {
        this.not = true;
    }
    
    public void removeNot() {
        this.not = false;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    @Override
    public String toString() {
        return (this.not ? "NOT " : "") + this.getLeftExpression() + " " + this.getStringExpression() + " " + this.getRightExpression();
    }
    
    public abstract String getStringExpression();
}
