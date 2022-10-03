package net.sf.jsqlparser.statement.select;

import java.util.List;

public class SetOperationList implements SelectBody
{
    private List<SelectBody> selects;
    private List<Boolean> brackets;
    private List<SetOperation> operations;
    private List<OrderByElement> orderByElements;
    private Limit limit;
    private Offset offset;
    private Fetch fetch;
    
    @Override
    public void accept(final SelectVisitor selectVisitor) {
        selectVisitor.visit(this);
    }
    
    public List<OrderByElement> getOrderByElements() {
        return this.orderByElements;
    }
    
    public List<SelectBody> getSelects() {
        return this.selects;
    }
    
    public List<SetOperation> getOperations() {
        return this.operations;
    }
    
    public List<Boolean> getBrackets() {
        return this.brackets;
    }
    
    public void setBrackets(final List<Boolean> brackets) {
        this.brackets = brackets;
    }
    
    public void setOrderByElements(final List<OrderByElement> orderByElements) {
        this.orderByElements = orderByElements;
    }
    
    public void setBracketsOpsAndSelects(final List<Boolean> brackets, final List<SelectBody> select, final List<SetOperation> ops) {
        this.selects = select;
        this.operations = ops;
        this.brackets = brackets;
        if (select.size() - 1 != ops.size() || select.size() != brackets.size()) {
            throw new IllegalArgumentException("list sizes are not valid");
        }
    }
    
    public Limit getLimit() {
        return this.limit;
    }
    
    public void setLimit(final Limit limit) {
        this.limit = limit;
    }
    
    public Offset getOffset() {
        return this.offset;
    }
    
    public void setOffset(final Offset offset) {
        this.offset = offset;
    }
    
    public Fetch getFetch() {
        return this.fetch;
    }
    
    public void setFetch(final Fetch fetch) {
        this.fetch = fetch;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < this.selects.size(); ++i) {
            if (i != 0) {
                buffer.append(" ").append(this.operations.get(i - 1).toString()).append(" ");
            }
            if (this.brackets == null || this.brackets.get(i)) {
                buffer.append("(").append(this.selects.get(i).toString()).append(")");
            }
            else {
                buffer.append(this.selects.get(i).toString());
            }
        }
        if (this.orderByElements != null) {
            buffer.append(PlainSelect.orderByToString(this.orderByElements));
        }
        if (this.limit != null) {
            buffer.append(this.limit.toString());
        }
        if (this.offset != null) {
            buffer.append(this.offset.toString());
        }
        if (this.fetch != null) {
            buffer.append(this.fetch.toString());
        }
        return buffer.toString();
    }
    
    public enum SetOperationType
    {
        INTERSECT, 
        EXCEPT, 
        MINUS, 
        UNION;
    }
}
