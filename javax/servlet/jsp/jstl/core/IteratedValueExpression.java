package javax.servlet.jsp.jstl.core;

import javax.el.ELContext;
import javax.el.ValueExpression;

public final class IteratedValueExpression extends ValueExpression
{
    private static final long serialVersionUID = 1L;
    protected final int i;
    protected final IteratedExpression iteratedExpression;
    
    public IteratedValueExpression(final IteratedExpression _iteratedExpr, final int i) {
        this.iteratedExpression = _iteratedExpr;
        this.i = i;
    }
    
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final IteratedValueExpression that = (IteratedValueExpression)obj;
        return this.i == that.i && this.iteratedExpression.equals(that.iteratedExpression);
    }
    
    public Class getExpectedType() {
        return this.iteratedExpression.getValueExpression().getExpectedType();
    }
    
    public String getExpressionString() {
        return this.iteratedExpression.getValueExpression().getExpressionString();
    }
    
    public Class getType(final ELContext elContext) {
        return this.iteratedExpression.getValueExpression().getType(elContext);
    }
    
    public Object getValue(final ELContext elContext) {
        return this.iteratedExpression.getItem(elContext, this.i);
    }
    
    public int hashCode() {
        return this.iteratedExpression.hashCode() + this.i;
    }
    
    public boolean isLiteralText() {
        return false;
    }
    
    public boolean isReadOnly(final ELContext elContext) {
        return true;
    }
    
    public void setValue(final ELContext elContext, final Object arg1) {
    }
}
