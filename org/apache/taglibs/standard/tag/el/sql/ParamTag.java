package org.apache.taglibs.standard.tag.el.sql;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.sql.ParamTagSupport;

public class ParamTag extends ParamTagSupport
{
    private String valueEL;
    
    public void setValue(final String valueEL) {
        this.valueEL = valueEL;
    }
    
    public int doStartTag() throws JspException {
        if (this.valueEL != null) {
            this.value = ExpressionEvaluatorManager.evaluate("value", this.valueEL, Object.class, (Tag)this, this.pageContext);
        }
        return super.doStartTag();
    }
}
