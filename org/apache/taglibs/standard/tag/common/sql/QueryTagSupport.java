package org.apache.taglibs.standard.tag.common.sql;

import javax.sql.DataSource;
import javax.servlet.jsp.tagext.Tag;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.JspTagException;
import java.sql.SQLException;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.jstl.core.Config;
import java.util.ArrayList;
import org.apache.taglibs.standard.tag.common.core.Util;
import java.util.List;
import java.sql.Connection;
import javax.servlet.jsp.jstl.sql.SQLExecutionTag;
import javax.servlet.jsp.tagext.TryCatchFinally;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class QueryTagSupport extends BodyTagSupport implements TryCatchFinally, SQLExecutionTag
{
    private String var;
    private int scope;
    protected Object rawDataSource;
    protected boolean dataSourceSpecified;
    protected String sql;
    protected int maxRows;
    protected boolean maxRowsSpecified;
    protected int startRow;
    private Connection conn;
    private List parameters;
    private boolean isPartOfTransaction;
    
    public QueryTagSupport() {
        this.init();
    }
    
    private void init() {
        this.startRow = 0;
        this.maxRows = -1;
        final boolean b = false;
        this.dataSourceSpecified = b;
        this.maxRowsSpecified = b;
        this.isPartOfTransaction = false;
        this.conn = null;
        this.rawDataSource = null;
        this.parameters = null;
        this.sql = null;
        this.var = null;
        this.scope = 1;
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scopeName) {
        this.scope = Util.getScope(scopeName);
    }
    
    public void addSQLParameter(final Object o) {
        if (this.parameters == null) {
            this.parameters = new ArrayList();
        }
        this.parameters.add(o);
    }
    
    public int doStartTag() throws JspException {
        Label_0102: {
            if (!this.maxRowsSpecified) {
                final Object obj = Config.find(this.pageContext, "javax.servlet.jsp.jstl.sql.maxRows");
                if (obj != null) {
                    if (!(obj instanceof Integer)) {
                        if (obj instanceof String) {
                            try {
                                this.maxRows = Integer.parseInt((String)obj);
                                break Label_0102;
                            }
                            catch (final NumberFormatException nfe) {
                                throw new JspException(Resources.getMessage("SQL_MAXROWS_PARSE_ERROR", obj), (Throwable)nfe);
                            }
                        }
                        throw new JspException(Resources.getMessage("SQL_MAXROWS_INVALID"));
                    }
                    this.maxRows = (int)obj;
                }
            }
            try {
                this.conn = this.getConnection();
            }
            catch (final SQLException e) {
                throw new JspException(this.sql + ": " + e.getMessage(), (Throwable)e);
            }
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
        if (this.startRow < 0 || this.maxRows < -1) {
            throw new JspException(Resources.getMessage("PARAM_BAD_VALUE"));
        }
        Result result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Throwable queryError = null;
        try {
            ps = this.conn.prepareStatement(sqlStatement);
            this.setParameters(ps, this.parameters);
            rs = ps.executeQuery();
            result = (Result)new ResultImpl(rs, this.startRow, this.maxRows);
        }
        catch (final Throwable e) {
            queryError = e;
        }
        finally {
            SQLException rsCloseExc = null;
            SQLException psCloseExc = null;
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (final SQLException sqe) {
                    rsCloseExc = sqe;
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (final SQLException sqe) {
                    psCloseExc = sqe;
                }
            }
            if (queryError != null) {
                throw new JspException(sqlStatement + ": " + queryError.getMessage(), queryError);
            }
            if (rsCloseExc != null) {
                throw new JspException(rsCloseExc.getMessage(), (Throwable)rsCloseExc);
            }
            if (psCloseExc != null) {
                throw new JspException(psCloseExc.getMessage(), (Throwable)psCloseExc);
            }
        }
        this.pageContext.setAttribute(this.var, (Object)result, this.scope);
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
        this.conn = null;
        this.parameters = null;
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
