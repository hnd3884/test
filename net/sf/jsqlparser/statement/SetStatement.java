package net.sf.jsqlparser.statement;

import net.sf.jsqlparser.expression.Expression;

public class SetStatement implements Statement
{
    private String name;
    private Expression expression;
    private boolean useEqual;
    
    public SetStatement(final String name, final Expression expression) {
        this.name = name;
        this.expression = expression;
    }
    
    public boolean isUseEqual() {
        return this.useEqual;
    }
    
    public SetStatement setUseEqual(final boolean useEqual) {
        this.useEqual = useEqual;
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        return "SET " + this.name + (this.useEqual ? " = " : " ") + this.expression.toString();
    }
    
    @Override
    public void accept(final StatementVisitor statementVisitor) {
        statementVisitor.visit(this);
    }
}
