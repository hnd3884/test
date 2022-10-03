package net.sf.jsqlparser.expression;

public class JdbcParameter implements Expression
{
    private Integer index;
    private boolean useFixedIndex;
    
    public JdbcParameter() {
        this.useFixedIndex = false;
    }
    
    public JdbcParameter(final Integer index, final boolean useFixedIndex) {
        this.useFixedIndex = false;
        this.index = index;
        this.useFixedIndex = useFixedIndex;
    }
    
    public Integer getIndex() {
        return this.index;
    }
    
    public void setIndex(final Integer index) {
        this.index = index;
    }
    
    public boolean isUseFixedIndex() {
        return this.useFixedIndex;
    }
    
    public void setUseFixedIndex(final boolean useFixedIndex) {
        this.useFixedIndex = useFixedIndex;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String toString() {
        return this.useFixedIndex ? ("?" + this.index) : "?";
    }
}
