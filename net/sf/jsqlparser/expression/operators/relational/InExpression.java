package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Expression;

public class InExpression implements Expression, SupportsOldOracleJoinSyntax
{
    private Expression leftExpression;
    private ItemsList leftItemsList;
    private ItemsList rightItemsList;
    private boolean not;
    private int oldOracleJoinSyntax;
    
    public InExpression() {
        this.not = false;
        this.oldOracleJoinSyntax = 0;
    }
    
    public InExpression(final Expression leftExpression, final ItemsList itemsList) {
        this.not = false;
        this.oldOracleJoinSyntax = 0;
        this.setLeftExpression(leftExpression);
        this.setRightItemsList(itemsList);
    }
    
    @Override
    public void setOldOracleJoinSyntax(final int oldOracleJoinSyntax) {
        this.oldOracleJoinSyntax = oldOracleJoinSyntax;
        if (oldOracleJoinSyntax < 0 || oldOracleJoinSyntax > 1) {
            throw new IllegalArgumentException("unexpected join type for oracle found with IN (type=" + oldOracleJoinSyntax + ")");
        }
    }
    
    @Override
    public int getOldOracleJoinSyntax() {
        return this.oldOracleJoinSyntax;
    }
    
    public ItemsList getRightItemsList() {
        return this.rightItemsList;
    }
    
    public Expression getLeftExpression() {
        return this.leftExpression;
    }
    
    public final void setRightItemsList(final ItemsList list) {
        this.rightItemsList = list;
    }
    
    public final void setLeftExpression(final Expression expression) {
        this.leftExpression = expression;
    }
    
    public boolean isNot() {
        return this.not;
    }
    
    public void setNot(final boolean b) {
        this.not = b;
    }
    
    public ItemsList getLeftItemsList() {
        return this.leftItemsList;
    }
    
    public void setLeftItemsList(final ItemsList leftItemsList) {
        this.leftItemsList = leftItemsList;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    private String getLeftExpressionString() {
        return this.leftExpression + ((this.oldOracleJoinSyntax == 1) ? "(+)" : "");
    }
    
    @Override
    public String toString() {
        return ((this.leftExpression == null) ? this.leftItemsList : this.getLeftExpressionString()) + " " + (this.not ? "NOT " : "") + "IN " + this.rightItemsList + "";
    }
    
    @Override
    public int getOraclePriorPosition() {
        return 0;
    }
    
    @Override
    public void setOraclePriorPosition(final int priorPosition) {
        if (priorPosition != 0) {
            throw new IllegalArgumentException("unexpected prior for oracle found");
        }
    }
}
