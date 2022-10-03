package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.RedirectSupport;

public class RedirectTag extends RedirectSupport
{
    private String url_;
    private String context_;
    
    public RedirectTag() {
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
    
    public void setUrl(final String url_) {
        this.url_ = url_;
    }
    
    public void setContext(final String context_) {
        this.context_ = context_;
    }
    
    private void init() {
        final String s = null;
        this.context_ = s;
        this.url_ = s;
    }
    
    private void evaluateExpressions() throws JspException {
        this.url = (String)ExpressionUtil.evalNotNull("redirect", "url", this.url_, String.class, (Tag)this, this.pageContext);
        this.context = (String)ExpressionUtil.evalNotNull("redirect", "context", this.context_, String.class, (Tag)this, this.pageContext);
    }
}
