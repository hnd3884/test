package net.sf.jsqlparser.expression;

public class OracleHierarchicalExpression implements Expression
{
    private Expression startExpression;
    private Expression connectExpression;
    private boolean noCycle;
    boolean connectFirst;
    
    public OracleHierarchicalExpression() {
        this.noCycle = false;
        this.connectFirst = false;
    }
    
    public Expression getStartExpression() {
        return this.startExpression;
    }
    
    public void setStartExpression(final Expression startExpression) {
        this.startExpression = startExpression;
    }
    
    public Expression getConnectExpression() {
        return this.connectExpression;
    }
    
    public void setConnectExpression(final Expression connectExpression) {
        this.connectExpression = connectExpression;
    }
    
    public boolean isNoCycle() {
        return this.noCycle;
    }
    
    public void setNoCycle(final boolean noCycle) {
        this.noCycle = noCycle;
    }
    
    public boolean isConnectFirst() {
        return this.connectFirst;
    }
    
    public void setConnectFirst(final boolean connectFirst) {
        this.connectFirst = connectFirst;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        if (this.isConnectFirst()) {
            b.append(" CONNECT BY ");
            if (this.isNoCycle()) {
                b.append("NOCYCLE ");
            }
            b.append(this.connectExpression.toString());
            if (this.startExpression != null) {
                b.append(" START WITH ").append(this.startExpression.toString());
            }
        }
        else {
            if (this.startExpression != null) {
                b.append(" START WITH ").append(this.startExpression.toString());
            }
            b.append(" CONNECT BY ");
            if (this.isNoCycle()) {
                b.append("NOCYCLE ");
            }
            b.append(this.connectExpression.toString());
        }
        return b.toString();
    }
}
