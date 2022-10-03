package net.sf.jsqlparser.statement.create.table;

import net.sf.jsqlparser.expression.Expression;

public class ExcludeConstraint extends Index
{
    private Expression expression;
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        final StringBuilder exclusionStatement = new StringBuilder("EXCLUDE WHERE ");
        exclusionStatement.append("(");
        exclusionStatement.append(this.expression);
        exclusionStatement.append(")");
        return exclusionStatement.toString();
    }
}
