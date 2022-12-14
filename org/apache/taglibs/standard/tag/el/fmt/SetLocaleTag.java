package org.apache.taglibs.standard.tag.el.fmt;

import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.fmt.SetLocaleSupport;

public class SetLocaleTag extends SetLocaleSupport
{
    private String value_;
    private String variant_;
    
    public SetLocaleTag() {
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
    
    public void setValue(final String value_) {
        this.value_ = value_;
    }
    
    public void setVariant(final String variant_) {
        this.variant_ = variant_;
    }
    
    private void init() {
        final String s = null;
        this.variant_ = s;
        this.value_ = s;
    }
    
    private void evaluateExpressions() throws JspException {
        this.value = ExpressionEvaluatorManager.evaluate("value", this.value_, Object.class, (Tag)this, this.pageContext);
        if (this.variant_ != null) {
            this.variant = (String)ExpressionEvaluatorManager.evaluate("variant", this.variant_, String.class, (Tag)this, this.pageContext);
        }
    }
}
