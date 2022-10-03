package org.apache.taglibs.standard.tag.el.fmt;

import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;

public class MessageTag extends MessageSupport
{
    private String key_;
    private String bundle_;
    
    public MessageTag() {
        this.init();
    }
    
    public int doStartTag() throws JspException {
        this.evaluateExpressions();
        return super.doStartTag();
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public void setKey(final String key_) {
        this.key_ = key_;
        this.keySpecified = true;
    }
    
    public void setBundle(final String bundle_) {
        this.bundle_ = bundle_;
        this.bundleSpecified = true;
    }
    
    private void init() {
        final String s = null;
        this.bundle_ = s;
        this.key_ = s;
    }
    
    private void evaluateExpressions() throws JspException {
        if (this.keySpecified) {
            this.keyAttrValue = (String)ExpressionEvaluatorManager.evaluate("key", this.key_, String.class, (Tag)this, this.pageContext);
        }
        if (this.bundleSpecified) {
            this.bundleAttrValue = (LocalizationContext)ExpressionEvaluatorManager.evaluate("bundle", this.bundle_, LocalizationContext.class, (Tag)this, this.pageContext);
        }
    }
}
