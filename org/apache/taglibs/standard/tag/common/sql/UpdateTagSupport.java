package org.apache.taglibs.standard.tag.common.sql;

import javax.sql.DataSource;
import javax.servlet.jsp.tagext.Tag;
import java.util.ArrayList;
import java.sql.PreparedStatement;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import java.sql.SQLException;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.List;
import java.sql.Connection;
import javax.servlet.jsp.jstl.sql.SQLExecutionTag;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class UpdateTagSupport extends BodyTagSupport implements TryCatchFinally, SQLExecutionTag
{
    private String var;
    private int scope;
    protected Object rawDataSource;
    protected boolean dataSourceSpecified;
    protected String sql;
    private Connection conn;
    private List parameters;
    private boolean isPartOfTransaction;
    
    public UpdateTagSupport() {
        this.init();
    }
    
    private void init() {
        this.rawDataSource = null;
        this.sql = null;
        this.conn = null;
        this.parameters = null;
        final boolean b = false;
        this.dataSourceSpecified = b;
        this.isPartOfTransaction = b;
        this.scope = 1;
        this.var = null;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scopeName) {
        this.scope = Util.getScope(scopeName);
    }
    
    public int doStartTag() throws JspException {
        try {
            this.conn = this.getConnection();
        }
        catch (final SQLException e) {
            throw new JspException(this.sql + ": " + e.getMessage(), (Throwable)e);
        }
        return 2;
    }
    
    public int doEndTag() throws JspException {
        String sqlStatement = null;
        if (this.sql != null) {
            sqlStatement = this.sql;
        }
        else if (this.bodyContent != null) {
            sqlStatement = this.bodyContent.getString();
        }
        if (sqlStatement == null || sqlStatement.trim().length() == 0) {
            throw new JspTagException(Resources.getMessage("SQL_NO_STATEMENT"));
        }
        int result = 0;
        PreparedStatement ps = null;
        try {
            ps = this.conn.prepareStatement(sqlStatement);
            this.setParameters(ps, this.parameters);
            result = ps.executeUpdate();
        }
        catch (final Throwable e) {
            throw new JspException(sqlStatement + ": " + e.getMessage(), e);
        }
        finally {
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (final SQLException sqe) {
                    throw new JspException(sqe.getMessage(), (Throwable)sqe);
                }
            }
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)new Integer(result), this.scope);
        }
        return 6;
    }
    
    public void doCatch(final Throwable t) throws Throwable {
        throw t;
    }
    
    public void doFinally() {
        if (this.conn != null && !this.isPartOfTransaction) {
            try {
                this.conn.close();
            }
            catch (final SQLException ex) {}
        }
        this.parameters = null;
        this.conn = null;
    }
    
    public void addSQLParameter(final Object o) {
        if (this.parameters == null) {
            this.parameters = new ArrayList();
        }
        this.parameters.add(o);
    }
    
    private Connection getConnection() throws JspException, SQLException {
        Connection conn = null;
        this.isPartOfTransaction = false;
        final TransactionTagSupport parent = (TransactionTagSupport)findAncestorWithClass((Tag)this, (Class)TransactionTagSupport.class);
        if (parent != null) {
            if (this.dataSourceSpecified) {
                throw new JspTagException(Resources.getMessage("ERROR_NESTED_DATASOURCE"));
            }
            conn = parent.getSharedConnection();
            this.isPartOfTransaction = true;
        }
        else {
            if (this.rawDataSource == null && this.dataSourceSpecified) {
                throw new JspException(Resources.getMessage("SQL_DATASOURCE_NULL"));
            }
            final DataSource dataSource = DataSourceUtil.getDataSource(this.rawDataSource, this.pageContext);
            try {
                conn = dataSource.getConnection();
            }
            catch (final Exception ex) {
                throw new JspException(Resources.getMessage("DATASOURCE_INVALID", ex.toString()));
            }
        }
        return conn;
    }
    
    private void setParameters(final PreparedStatement ps, final List parameters) throws SQLException {
        if (parameters != null) {
            for (int i = 0; i < parameters.size(); ++i) {
                ps.setObject(i + 1, parameters.get(i));
            }
        }
    }
}
