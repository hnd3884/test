package org.apache.taglibs.standard.tag.common.sql;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspException;
import org.apache.taglibs.standard.resources.Resources;
import javax.naming.NamingException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.jsp.jstl.core.Config;
import javax.sql.DataSource;
import javax.servlet.jsp.PageContext;

public class DataSourceUtil
{
    private static final String ESCAPE = "\\";
    private static final String TOKEN = ",";
    
    static DataSource getDataSource(Object rawDataSource, final PageContext pc) throws JspException {
        DataSource dataSource = null;
        if (rawDataSource == null) {
            rawDataSource = Config.find(pc, "javax.servlet.jsp.jstl.sql.dataSource");
        }
        if (rawDataSource == null) {
            return null;
        }
        if (rawDataSource instanceof String) {
            try {
                final Context ctx = new InitialContext();
                final Context envCtx = (Context)ctx.lookup("java:comp/env");
                dataSource = (DataSource)envCtx.lookup((String)rawDataSource);
            }
            catch (final NamingException ex) {
                dataSource = getDataSource((String)rawDataSource);
            }
        }
        else {
            if (!(rawDataSource instanceof DataSource)) {
                throw new JspException(Resources.getMessage("SQL_DATASOURCE_INVALID_TYPE"));
            }
            dataSource = (DataSource)rawDataSource;
        }
        return dataSource;
    }
    
    private static DataSource getDataSource(final String params) throws JspException {
        final DataSourceWrapper dataSource = new DataSourceWrapper();
        final String[] paramString = new String[4];
        int escCount = 0;
        int aryCount = 0;
        int begin = 0;
        for (int index = 0; index < params.length(); ++index) {
            final char nextChar = params.charAt(index);
            if (",".indexOf(nextChar) != -1 && escCount == 0) {
                paramString[aryCount] = params.substring(begin, index).trim();
                begin = index + 1;
                if (++aryCount > 4) {
                    throw new JspTagException(Resources.getMessage("JDBC_PARAM_COUNT"));
                }
            }
            if ("\\".indexOf(nextChar) != -1) {
                ++escCount;
            }
            else {
                escCount = 0;
            }
        }
        paramString[aryCount] = params.substring(begin).trim();
        dataSource.setJdbcURL(paramString[0]);
        if (paramString[1] != null) {
            try {
                dataSource.setDriverClassName(paramString[1]);
            }
            catch (final Exception ex) {
                throw new JspTagException(Resources.getMessage("DRIVER_INVALID_CLASS", ex.toString()), (Throwable)ex);
            }
        }
        dataSource.setUserName(paramString[2]);
        dataSource.setPassword(paramString[3]);
        return dataSource;
    }
}
