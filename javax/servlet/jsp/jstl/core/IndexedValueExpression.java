package javax.servlet.jsp.jstl.core;

import javax.el.ELContext;
import java.io.Serializable;
import javax.el.ValueExpression;

public final class IndexedValueExpression extends ValueExpression implements Serializable
{
    private static final long serialVersionUID = 1L;
    protected final Integer i;
    protected final ValueExpression orig;
    
    public IndexedValueExpression(final ValueExpression valueExpression, final int i) {
        this.orig = valueExpression;
        this.i = i;
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final IndexedValueExpression that = (IndexedValueExpression)o;
        return this.i.equals(that.i) && this.orig.equals((Object)that.orig);
    }
    
    public Class getExpectedType() {
        return this.orig.getExpectedType();
    }
    
    public String getExpressionString() {
        return this.orig.getExpressionString();
    }
    
    public Class getType(final ELContext elContext) {
        return elContext.getELResolver().getType(elContext, this.orig.getValue(elContext), (Object)this.i);
    }
    
    public Object getValue(final ELContext elContext) {
        return elContext.getELResolver().getValue(elContext, this.orig.getValue(elContext), (Object)this.i);
    }
    
    public int hashCode() {
        return this.orig.hashCode() + this.i;
    }
    
    public boolean isLiteralText() {
        return false;
    }
    
    public boolean isReadOnly(final ELContext elContext) {
        return elContext.getELResolver().isReadOnly(elContext, this.orig.getValue(elContext), (Object)this.i);
    }
    
    public void setValue(final ELContext elContext, final Object arg1) {
        elContext.getELResolver().setValue(elContext, this.orig.getValue(elContext), (Object)this.i, arg1);
    }
}
