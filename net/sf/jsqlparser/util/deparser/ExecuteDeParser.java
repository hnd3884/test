package net.sf.jsqlparser.util.deparser;

import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class ExecuteDeParser
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    
    public ExecuteDeParser(final ExpressionVisitor expressionVisitor, final StringBuilder buffer) {
        this.buffer = buffer;
        this.expressionVisitor = expressionVisitor;
    }
    
    public StringBuilder getBuffer() {
        return this.buffer;
    }
    
    public void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
    
    public void deParse(final Execute execute) {
        this.buffer.append("EXECUTE ").append(execute.getName());
        final List<Expression> expressions = execute.getExprList().getExpressions();
        for (int i = 0; i < expressions.size(); ++i) {
            if (i > 0) {
                this.buffer.append(",");
            }
            this.buffer.append(" ");
            expressions.get(i).accept(this.expressionVisitor);
        }
    }
    
    public ExpressionVisitor getExpressionVisitor() {
        return this.expressionVisitor;
    }
    
    public void setExpressionVisitor(final ExpressionVisitor visitor) {
        this.expressionVisitor = visitor;
    }
}
