package net.sf.jsqlparser.statement.select;

import net.sf.jsqlparser.expression.Expression;

public class OrderByElement
{
    private Expression expression;
    private boolean asc;
    private NullOrdering nullOrdering;
    private boolean ascDesc;
    
    public OrderByElement() {
        this.asc = true;
        this.ascDesc = false;
    }
    
    public boolean isAsc() {
        return this.asc;
    }
    
    public NullOrdering getNullOrdering() {
        return this.nullOrdering;
    }
    
    public void setNullOrdering(final NullOrdering nullOrdering) {
        this.nullOrdering = nullOrdering;
    }
    
    public void setAsc(final boolean b) {
        this.asc = b;
    }
    
    public void setAscDescPresent(final boolean b) {
        this.ascDesc = b;
    }
    
    public boolean isAscDescPresent() {
        return this.ascDesc;
    }
    
    public void accept(final OrderByVisitor orderByVisitor) {
        orderByVisitor.visit(this);
    }
    
    public Expression getExpression() {
        return this.expression;
    }
    
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }
    
    @Override
    public String toString() {
        final StringBuilder b = new StringBuilder();
        b.append(this.expression.toString());
        if (!this.asc) {
            b.append(" DESC");
        }
        else if (this.ascDesc) {
            b.append(" ASC");
        }
        if (this.nullOrdering != null) {
            b.append(' ');
            b.append((this.nullOrdering == NullOrdering.NULLS_FIRST) ? "NULLS FIRST" : "NULLS LAST");
        }
        return b.toString();
    }
    
    public enum NullOrdering
    {
        NULLS_FIRST, 
        NULLS_LAST;
    }
}
