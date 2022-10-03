package org.apache.taglibs.standard.tag.common.sql;

import java.util.Hashtable;
import java.io.PrintWriter;
import java.util.logging.Logger;
import org.apache.taglibs.standard.resources.Resources;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Properties;
import java.sql.Connection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Driver;
import javax.sql.DataSource;

public class DataSourceWrapper implements DataSource
{
    private Driver driver;
    private String jdbcURL;
    private String userName;
    private String password;
    
    public void setDriverClassName(final String driverClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final SecurityManager sm = System.getSecurityManager();
        ClassLoader cl;
        if (sm == null) {
            cl = Thread.currentThread().getContextClassLoader();
        }
        else {
            cl = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction<ClassLoader>() {
                public ClassLoader run() {
                    return Thread.currentThread().getContextClassLoader();
                }
            });
        }
        final Object instance = Class.forName(driverClassName, true, cl).newInstance();
        if (instance instanceof Driver) {
            this.driver = (Driver)instance;
        }
    }
    
    public void setJdbcURL(final String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public void setPassword(final String password) {
        this.password = password;
    }
    
    public Connection getConnection() throws SQLException {
        Connection conn = null;
        if (this.driver != null) {
            final Properties props = new Properties();
            if (this.userName != null) {
                ((Hashtable<String, String>)props).put("user", this.userName);
            }
            if (this.password != null) {
                ((Hashtable<String, String>)props).put("password", this.password);
            }
            conn = this.driver.connect(this.jdbcURL, props);
        }
        if (conn == null) {
            if (this.userName != null) {
                conn = DriverManager.getConnection(this.jdbcURL, this.userName, this.password);
            }
            else {
                conn = DriverManager.getConnection(this.jdbcURL);
            }
        }
        return conn;
    }
    
    public Connection getConnection(final String username, final String password) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public int getLoginTimeout() throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public Logger getParentLogger() {
        throw new UnsupportedOperationException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public void setLoginTimeout(final int seconds) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public synchronized void setLogWriter(final PrintWriter out) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public synchronized boolean isWrapperFor(final Class c) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
    
    public synchronized Object unwrap(final Class c) throws SQLException {
        throw new SQLException(Resources.getMessage("NOT_SUPPORTED"));
    }
}
