package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;

public class KeepExpression implements Expression
{
    private String name;
    private List<OrderByElement> orderByElements;
    private boolean first;
    
    public KeepExpression() {
        this.first = false;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public boolean isFirst() {
        return this.first;
    }
    
    public void setFirst(final boolean first) {
        this.first = first;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("KEEP (").append(this.name);
        b.append(" ").append(this.first ? "FIRST" : "LAST").append(" ");
        this.toStringOrderByElements(b);
        b.append(")");
        return b.toString();
    }
    
    private void toStringOrderByElements(final StringBuilder b) {
        if (this.orderByElements != null && !this.orderByElements.isEmpty()) {
            b.append("ORDER BY ");
            for (int i = 0; i < this.orderByElements.size(); ++i) {
                if (i > 0) {
                    b.append(", ");
                }
                b.append(this.orderByElements.get(i).toString());
            }
        }
    }
}
