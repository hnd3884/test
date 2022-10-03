package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.tag.common.core.NullAttributeException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.jstl.core.ConditionalTagSupport;

public class IfTag extends ConditionalTagSupport
{
    private String test;
    
    public IfTag() {
        this.init();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    protected boolean condition() throws JspTagException {
        try {
            final Object r = ExpressionEvaluatorManager.evaluate("test", this.test, Boolean.class, (Tag)this, this.pageContext);
            if (r == null) {
                throw new NullAttributeException("if", "test");
            }
            return (boolean)r;
        }
        catch (final JspException ex) {
            throw new JspTagException(ex.toString(), (Throwable)ex);
        }
    }
    
    public void setTest(final String test) {
        this.test = test;
    }
    
    private void init() {
        this.test = null;
    }
}
