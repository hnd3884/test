package org.apache.taglibs.standard.tag.rt.sql;

import org.apache.taglibs.standard.tag.common.sql.SetDataSourceTagSupport;

public class SetDataSourceTag extends SetDataSourceTagSupport
{
    public void setDataSource(final Object dataSource) {
        this.dataSource = dataSource;
        this.dataSourceSpecified = true;
    }
    
    public void setDriver(final String driverClassName) {
        this.driverClassName = driverClassName;
    }
    
    public void setUrl(final String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }
    
    public void setUser(final String userName) {
        this.userName = userName;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
}
