package org.apache.taglibs.standard.tag.el.sql;

import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import java.util.Date;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.sql.DateParamTagSupport;

public class DateParamTag extends DateParamTagSupport
{
    private String valueEL;
    private String typeEL;
    
    public void setValue(final String valueEL) {
        this.valueEL = valueEL;
    }
    
    public void setType(final String typeEL) {
        this.typeEL = typeEL;
    }
    
    public int doStartTag() throws JspException {
        this.evaluateExpressions();
        return super.doStartTag();
    }
    
    private void evaluateExpressions() throws JspException {
        if (this.valueEL != null) {
            this.value = (Date)ExpressionEvaluatorManager.evaluate("value", this.valueEL, Date.class, (Tag)this, this.pageContext);
        }
        if (this.typeEL != null) {
            this.type = (String)ExpressionEvaluatorManager.evaluate("type", this.typeEL, String.class, (Tag)this, this.pageContext);
        }
    }
}
