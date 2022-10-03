package org.apache.taglibs.standard.tag.common.core;

import javax.servlet.jsp.jstl.core.IteratedValueExpression;
import javax.servlet.jsp.JspTagException;
import javax.el.ValueExpression;
import javax.servlet.jsp.jstl.core.IteratedExpression;
import java.util.StringTokenizer;
import javax.servlet.jsp.jstl.core.LoopTagSupport;

public abstract class ForTokensSupport extends LoopTagSupport
{
    protected Object items;
    protected String delims;
    protected StringTokenizer st;
    protected int currentIndex;
    private IteratedExpression itemsValueIteratedExpression;
    
    protected void prepare() throws JspTagException {
        if (this.items instanceof ValueExpression) {
            this.deferredExpression = (ValueExpression)this.items;
            this.itemsValueIteratedExpression = new IteratedExpression(this.deferredExpression, this.getDelims());
            this.currentIndex = 0;
            final Object originalValue = this.deferredExpression.getValue(this.pageContext.getELContext());
            if (!(originalValue instanceof String)) {
                throw new JspTagException();
            }
            this.st = new StringTokenizer((String)originalValue, this.delims);
        }
        else {
            this.st = new StringTokenizer((String)this.items, this.delims);
        }
    }
    
    protected boolean hasNext() throws JspTagException {
        return this.st.hasMoreElements();
    }
    
    protected Object next() throws JspTagException {
        if (this.deferredExpression != null) {
            this.st.nextElement();
            return new IteratedValueExpression(this.itemsValueIteratedExpression, this.currentIndex++);
        }
        return this.st.nextElement();
    }
    
    public void release() {
        super.release();
        final String s = null;
        this.delims = s;
        this.items = s;
        this.st = null;
    }
    
    protected String getDelims() {
        return this.delims;
    }
}
