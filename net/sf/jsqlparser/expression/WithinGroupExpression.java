package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;

public class WithinGroupExpression implements Expression
{
    private String name;
    private List<OrderByElement> orderByElements;
    private ExpressionList exprList;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public ExpressionList getExprList() {
        return this.exprList;
    }
    
    public void setExprList(final ExpressionList exprList) {
        this.exprList = exprList;
    }
    
    @Override
    public void accept(final ExpressionVisitor visitor) {
        visitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.name);
        b.append(this.exprList.toString());
        b.append(" WITHIN GROUP (");
        b.append("ORDER BY ");
        for (int i = 0; i < this.orderByElements.size(); ++i) {
            if (i > 0) {
                b.append(", ");
            }
            b.append(this.orderByElements.get(i).toString());
        }
        b.append(")");
        return b.toString();
    }
}
