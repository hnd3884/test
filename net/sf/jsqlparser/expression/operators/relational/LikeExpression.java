package net.sf.jsqlparser.expression.operators.relational;

import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.BinaryExpression;

public class LikeExpression extends BinaryExpression
{
    private boolean not;
    private String escape;
    private boolean caseInsensitive;
    
    public LikeExpression() {
        this.not = false;
        this.escape = null;
        this.caseInsensitive = false;
    }
    
    @Override
    public boolean isNot() {
        return this.not;
    }
    
    public void setNot(final boolean b) {
        this.not = b;
    }
    
    @Override
    public void accept(final ExpressionVisitor expressionVisitor) {
        expressionVisitor.visit(this);
    }
    
    @Override
    public String getStringExpression() {
        return (this.not ? "NOT " : "") + (this.caseInsensitive ? "ILIKE" : "LIKE");
    }
    
    @Override
    public String toString() {
        String retval = super.toString();
        if (this.escape != null) {
            retval = retval + " ESCAPE '" + this.escape + "'";
        }
        return retval;
    }
    
    public String getEscape() {
        return this.escape;
    }
    
    public void setEscape(final String escape) {
        this.escape = escape;
    }
    
    public boolean isCaseInsensitive() {
        return this.caseInsensitive;
    }
    
    public void setCaseInsensitive(final boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
    }
}
