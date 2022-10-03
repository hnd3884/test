package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.create.table.ColDataType;

public class CastExpression implements Expression
{
    private Expression leftExpression;
    private ColDataType type;
    private boolean useCastKeyword;
    
    public CastExpression() {
        this.useCastKeyword = true;
    }
    
    public ColDataType getType() {
        return this.type;
    }
    
    public void setType(final ColDataType type) {
        this.type = type;
    }
    
    public Expression getLeftExpression() {
        return this.leftExpression;
    }
    
    public void setLeftExpression(final Expression expression) {
        this.leftExpression = expression;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public boolean isUseCastKeyword() {
        return this.useCastKeyword;
    }
    
    public void setUseCastKeyword(final boolean useCastKeyword) {
        this.useCastKeyword = useCastKeyword;
    }
    
    @Override
    public String toString() {
        if (this.useCastKeyword) {
            return "CAST(" + this.leftExpression + " AS " + this.type.toString() + ")";
        }
        return this.leftExpression + "::" + this.type.toString();
    }
}
