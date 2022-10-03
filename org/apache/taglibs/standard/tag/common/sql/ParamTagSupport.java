package org.apache.taglibs.standard.tag.common.sql;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.jstl.sql.SQLExecutionTag;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParamTagSupport extends BodyTagSupport
{
    protected Object value;
    
    public int doEndTag() throws JspException {
        final SQLExecutionTag parent = (SQLExecutionTag)findAncestorWithClass((Tag)this, (Class)SQLExecutionTag.class);
        if (parent == null) {
            throw new JspTagException(Resources.getMessage("SQL_PARAM_OUTSIDE_PARENT"));
        }
        Object paramValue = null;
        if (this.value != null) {
            paramValue = this.value;
        }
        else if (this.bodyContent != null) {
            paramValue = this.bodyContent.getString().trim();
            if (((String)paramValue).trim().length() == 0) {
                paramValue = null;
            }
        }
        parent.addSQLParameter(paramValue);
        return 6;
    }
}
