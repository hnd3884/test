package org.apache.taglibs.standard.tag.el.xml;

import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.tag.el.core.ExpressionUtil;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.xml.ParamSupport;

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
        this.name = (String)ExpressionUtil.evalNotNull("param", "name", this.name_, String.class, (Tag)this, this.pageContext);
        this.value = ExpressionUtil.evalNotNull("param", "value", this.value_, Object.class, (Tag)this, this.pageContext);
    }
}
