package net.sf.jsqlparser.util.deparser;

import net.sf.jsqlparser.statement.SetStatement;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class SetStatementDeParser
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    
    public SetStatementDeParser(final ExpressionVisitor expressionVisitor, final StringBuilder buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final SetStatement set) {
        this.buffer.append("SET ").append(set.getName());
        if (set.isUseEqual()) {
            this.buffer.append(" =");
        }
        this.buffer.append(" ");
        set.getExpression().accept(this.expressionVisitor);
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
}
