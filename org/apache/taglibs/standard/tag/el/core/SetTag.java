package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.SetSupport;

public class SetTag extends SetSupport
{
    private boolean valueSpecified;
    private String valueExpression;
    private String targetExpression;
    private String propertyExpression;
    
    public void setValue(final String value) {
        this.valueExpression = value;
        this.valueSpecified = true;
    }
    
    public void setTarget(final String target) {
        this.targetExpression = target;
    }
    
    public void setProperty(final String property) {
        this.propertyExpression = property;
    }
    
    public void release() {
        this.valueExpression = null;
        this.targetExpression = null;
        this.propertyExpression = null;
        this.valueSpecified = false;
        super.release();
    }
    
    protected boolean isValueSpecified() {
        return this.valueSpecified;
    }
    
    protected Object evalValue() throws JspException {
        return ExpressionEvaluatorManager.evaluate("value", this.valueExpression, Object.class, (Tag)this, this.pageContext);
    }
    
    protected Object evalTarget() throws JspException {
        return ExpressionEvaluatorManager.evaluate("target", this.targetExpression, Object.class, (Tag)this, this.pageContext);
    }
    
    protected String evalProperty() throws JspException {
        return (String)ExpressionEvaluatorManager.evaluate("property", this.propertyExpression, String.class, (Tag)this, this.pageContext);
    }
}
