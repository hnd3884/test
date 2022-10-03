package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class MySQLGroupConcat implements Expression
{
    private ExpressionList expressionList;
    private boolean distinct;
    private List<OrderByElement> orderByElements;
    private String separator;
    
    public MySQLGroupConcat() {
        this.distinct = false;
    }
    
    public ExpressionList getExpressionList() {
        return this.expressionList;
    }
    
    public void setExpressionList(final ExpressionList expressionList) {
        this.expressionList = expressionList;
    }
    
    public boolean isDistinct() {
        return this.distinct;
    }
    
    public void setDistinct(final boolean distinct) {
        this.distinct = distinct;
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public String getSeparator() {
        return this.separator;
    }
    
    public void setSeparator(final String separator) {
        this.separator = separator;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append("GROUP_CONCAT(");
        if (this.isDistinct()) {
            b.append("DISTINCT ");
        }
        b.append(PlainSelect.getStringList(this.expressionList.getExpressions(), true, false));
        if (this.orderByElements != null && !this.orderByElements.isEmpty()) {
            b.append(" ORDER BY ");
            for (int i = 0; i < this.orderByElements.size(); ++i) {
                if (i > 0) {
                    b.append(", ");
                }
                b.append(this.orderByElements.get(i).toString());
            }
        }
        if (this.separator != null) {
            b.append(" SEPARATOR ").append(this.separator);
        }
        b.append(")");
        return b.toString();
    }
}
