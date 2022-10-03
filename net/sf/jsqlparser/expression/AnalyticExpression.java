package net.sf.jsqlparser.expression;

import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.OrderByElement;
import java.util.List;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;

public class AnalyticExpression implements Expression
{
    private ExpressionList partitionExpressionList;
    private List<OrderByElement> orderByElements;
    private String name;
    private Expression expression;
    private Expression offset;
    private Expression defaultValue;
    private boolean allColumns;
    private WindowElement windowElement;
    private KeepExpression keep;
    
    public AnalyticExpression() {
        this.allColumns = false;
        this.keep = null;
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
    
    public KeepExpression getKeep() {
        return this.keep;
    }
    
    public void setKeep(final KeepExpression keep) {
        this.keep = keep;
    }
    
    public ExpressionList getPartitionExpressionList() {
        return this.partitionExpressionList;
    }
    
    public void setPartitionExpressionList(final ExpressionList partitionExpressionList) {
        this.partitionExpressionList = partitionExpressionList;
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
    
    public Expression getOffset() {
        return this.offset;
    }
    
    public void setOffset(final Expression offset) {
        this.offset = offset;
    }
    
    public Expression getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(final Expression defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public WindowElement getWindowElement() {
        return this.windowElement;
    }
    
    public void setWindowElement(final WindowElement windowElement) {
        this.windowElement = windowElement;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.name).append("(");
        if (this.expression != null) {
            b.append(this.expression.toString());
            if (this.offset != null) {
                b.append(", ").append(this.offset.toString());
                if (this.defaultValue != null) {
                    b.append(", ").append(this.defaultValue.toString());
                }
            }
        }
        else if (this.isAllColumns()) {
            b.append("*");
        }
        b.append(") ");
        if (this.keep != null) {
            b.append(this.keep.toString()).append(" ");
        }
        b.append("OVER (");
        this.toStringPartitionBy(b);
        this.toStringOrderByElements(b);
        b.append(")");
        return b.toString();
    }
    
    public boolean isAllColumns() {
        return this.allColumns;
    }
    
    public void setAllColumns(final boolean allColumns) {
        this.allColumns = allColumns;
    }
    
    private void toStringPartitionBy(final StringBuilder b) {
        if (this.partitionExpressionList != null && !this.partitionExpressionList.getExpressions().isEmpty()) {
            b.append("PARTITION BY ");
            b.append(PlainSelect.getStringList(this.partitionExpressionList.getExpressions(), true, false));
            b.append(" ");
        }
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
            if (this.windowElement != null) {
                b.append(' ');
                b.append(this.windowElement);
            }
        }
    }
}
