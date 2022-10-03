package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.Expression;
import java.util.List;

public class ExpressionList implements ItemsList
{
    private List<Expression> expressions;
    
    public ExpressionList() {
    }
    
    public ExpressionList(final List<Expression> expressions) {
        this.expressions = expressions;
    }
    
    public List<Expression> getExpressions() {
        return this.expressions;
    }
    
    public void setExpressions(final List<Expression> list) {
        this.expressions = list;
    }
    
    @Override
    public void accept(final ItemsListVisitor itemsListVisitor) {
        itemsListVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return PlainSelect.getStringList(this.expressions, true, true);
    }
}
