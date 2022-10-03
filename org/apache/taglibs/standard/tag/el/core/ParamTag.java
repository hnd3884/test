package org.apache.taglibs.standard.tag.el.core;

import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.ParamSupport;

public class ParamTag extends ParamSupport
{
    private String name_;
    private String value_;
    
    public ParamTag() {
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
    
    public void setName(final String name_) {
        this.name_ = name_;
    }
    
    public void setValue(final String value_) {
        this.value_ = value_;
    }
    
    private void init() {
        final String s = null;
        this.value_ = s;
        this.name_ = s;
    }
    
    private void evaluateExpressions() throws JspException {
        this.name = (String)ExpressionUtil.evalNotNull("import", "name", this.name_, String.class, (Tag)this, this.pageContext);
        this.value = (String)ExpressionUtil.evalNotNull("import", "value", this.value_, String.class, (Tag)this, this.pageContext);
    }
}
