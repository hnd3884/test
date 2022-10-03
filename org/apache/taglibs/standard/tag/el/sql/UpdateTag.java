package org.apache.taglibs.standard.tag.el.sql;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.apache.taglibs.standard.tag.common.sql.UpdateTagSupport;

public class UpdateTag extends UpdateTagSupport
{
    private String dataSourceEL;
    private String sqlEL;
    
    public void setDataSource(final String dataSourceEL) {
        this.dataSourceEL = dataSourceEL;
        this.dataSourceSpecified = true;
    }
    
    public void setSql(final String sqlEL) {
        this.sqlEL = sqlEL;
    }
    
    public int doStartTag() throws JspException {
        if (this.dataSourceEL != null) {
            this.rawDataSource = ExpressionEvaluatorManager.evaluate("dataSource", this.dataSourceEL, Object.class, (Tag)this, this.pageContext);
        }
        if (this.sqlEL != null) {
            this.sql = (String)ExpressionEvaluatorManager.evaluate("sql", this.sqlEL, String.class, (Tag)this, this.pageContext);
        }
        return super.doStartTag();
    }
}
