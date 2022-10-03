package org.apache.taglibs.standard.tag.common.sql;

import javax.sql.DataSource;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.tag.common.core.Util;
import javax.servlet.jsp.tagext.TagSupport;

public class SetDataSourceTagSupport extends TagSupport
{
    protected Object dataSource;
    protected boolean dataSourceSpecified;
    protected String jdbcURL;
    protected String driverClassName;
    protected String userName;
    protected String password;
    private int scope;
    private String var;
    
    public SetDataSourceTagSupport() {
        this.init();
    }
    
    private void init() {
        this.dataSource = null;
        this.dataSourceSpecified = false;
        final String s = null;
        this.password = s;
        this.userName = s;
        this.driverClassName = s;
        this.jdbcURL = s;
        this.var = null;
        this.scope = 1;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public int doStartTag() throws JspException {
        DataSource ds;
        if (this.dataSource != null) {
            ds = DataSourceUtil.getDataSource(this.dataSource, this.pageContext);
        }
        else {
            if (this.dataSourceSpecified) {
                throw new JspException(Resources.getMessage("SQL_DATASOURCE_NULL"));
            }
            final DataSourceWrapper dsw = new DataSourceWrapper();
            try {
                if (this.driverClassName != null) {
                    dsw.setDriverClassName(this.driverClassName);
                }
            }
            catch (final Exception e) {
                throw new JspTagException(Resources.getMessage("DRIVER_INVALID_CLASS", e.toString()), (Throwable)e);
            }
            dsw.setJdbcURL(this.jdbcURL);
            dsw.setUserName(this.userName);
            dsw.setPassword(this.password);
            ds = dsw;
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)ds, this.scope);
        }
        else {
            Config.set(this.pageContext, "javax.servlet.jsp.jstl.sql.dataSource", (Object)ds, this.scope);
        }
        return 0;
    }
    
    public void release() {
        this.init();
    }
}
