package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.core.OutSupport;

public class OutTag extends OutSupport
{
    private String valueExpression;
    private String defaultExpression;
    private String escapeXmlExpression;
    
    public void release() {
        this.valueExpression = null;
        this.defaultExpression = null;
        this.escapeXmlExpression = null;
        super.release();
    }
    
    public void setValue(final String value) {
        this.valueExpression = value;
    }
    
    public void setDefault(final String def) {
        this.defaultExpression = def;
    }
    
    public void setEscapeXml(final String escapeXml) {
        this.escapeXmlExpression = escapeXml;
    }
    
    protected Object evalValue() throws JspException {
        if (this.valueExpression == null) {
            return null;
        }
        return ExpressionEvaluatorManager.evaluate("value", this.valueExpression, Object.class, (Tag)this, this.pageContext);
    }
    
    protected String evalDefault() throws JspException {
        if (this.defaultExpression == null) {
            return null;
        }
        return (String)ExpressionEvaluatorManager.evaluate("default", this.defaultExpression, String.class, (Tag)this, this.pageContext);
    }
    
    protected boolean evalEscapeXml() throws JspException {
        if (this.escapeXmlExpression == null) {
            return true;
        }
        final Boolean result = (Boolean)ExpressionEvaluatorManager.evaluate("escapeXml", this.escapeXmlExpression, Boolean.class, (Tag)this, this.pageContext);
        return result == null || result;
    }
}
