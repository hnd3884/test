package net.sf.jsqlparser.util.deparser;

import java.util.Iterator;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;
import net.sf.jsqlparser.expression.ExpressionVisitor;

public class OrderByDeParser
{
    private StringBuilder buffer;
    private ExpressionVisitor expressionVisitor;
    
    OrderByDeParser() {
    }
    
    public OrderByDeParser(final ExpressionVisitor expressionVisitor, final StringBuilder buffer) {
        this.expressionVisitor = expressionVisitor;
        this.buffer = buffer;
    }
    
    public void deParse(final List<OrderByElement> orderByElementList) {
        this.deParse(false, orderByElementList);
    }
    
    public void deParse(final boolean oracleSiblings, final List<OrderByElement> orderByElementList) {
        if (oracleSiblings) {
            this.buffer.append(" ORDER SIBLINGS BY ");
        }
        else {
            this.buffer.append(" ORDER BY ");
        }
        final Iterator<OrderByElement> iter = orderByElementList.iterator();
        while (iter.hasNext()) {
            final OrderByElement orderByElement = iter.next();
            this.deParseElement(orderByElement);
            if (iter.hasNext()) {
                this.buffer.append(", ");
            }
        }
    }
    
    public void deParseElement(final OrderByElement orderBy) {
        orderBy.getExpression().accept(this.expressionVisitor);
        if (!orderBy.isAsc()) {
            this.buffer.append(" DESC");
        }
        else if (orderBy.isAscDescPresent()) {
            this.buffer.append(" ASC");
        }
        if (orderBy.getNullOrdering() != null) {
            this.buffer.append(' ');
            this.buffer.append((orderBy.getNullOrdering() == OrderByElement.NullOrdering.NULLS_FIRST) ? "NULLS FIRST" : "NULLS LAST");
        }
    }
    
    void setExpressionVisitor(final ExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }
    
    void setBuffer(final StringBuilder buffer) {
        this.buffer = buffer;
    }
}
