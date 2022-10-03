package org.apache.taglibs.standard.tag.el.fmt;

import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

public class BundleTag extends BundleSupport
{
    private String basename_;
    private String prefix_;
    
    public BundleTag() {
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
    
    public void setBasename(final String basename_) {
        this.basename_ = basename_;
    }
    
    public void setPrefix(final String prefix_) {
        this.prefix_ = prefix_;
    }
    
    private void init() {
        final String s = null;
        this.prefix_ = s;
        this.basename_ = s;
    }
    
    private void evaluateExpressions() throws JspException {
        this.basename = (String)ExpressionEvaluatorManager.evaluate("basename", this.basename_, String.class, (Tag)this, this.pageContext);
        if (this.prefix_ != null) {
            this.prefix = (String)ExpressionEvaluatorManager.evaluate("prefix", this.prefix_, String.class, (Tag)this, this.pageContext);
        }
    }
}
