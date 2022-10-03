package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.PlainSelect;
import java.util.List;

public class CaseExpression implements Expression
{
    private Expression switchExpression;
    private List<Expression> whenClauses;
    private Expression elseExpression;
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public Expression getSwitchExpression() {
        return this.switchExpression;
    }
    
    public void setSwitchExpression(final Expression switchExpression) {
        this.switchExpression = switchExpression;
    }
    
    public Expression getElseExpression() {
        return this.elseExpression;
    }
    
    public void setElseExpression(final Expression elseExpression) {
        this.elseExpression = elseExpression;
    }
    
    public List<Expression> getWhenClauses() {
        return this.whenClauses;
    }
    
    public void setWhenClauses(final List<Expression> whenClauses) {
        this.whenClauses = whenClauses;
    }
    
    @Override
    public String toString() {
        return "CASE " + ((this.switchExpression != null) ? (this.switchExpression + " ") : "") + PlainSelect.getStringList(this.whenClauses, false, false) + " " + ((this.elseExpression != null) ? ("ELSE " + this.elseExpression + " ") : "") + "END";
    }
}
