package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;
import java.sql.Connection;
import javax.sql.DataSource;

public class DataSourceConnectionFactory implements ConnectionFactory
{
    private final DataSource dataSource;
    private final String userName;
    private final char[] userPassword;
    
    public DataSourceConnectionFactory(final DataSource dataSource) {
        this(dataSource, null, (char[])null);
    }
    
    public DataSourceConnectionFactory(final DataSource dataSource, final String userName, final char[] userPassword) {
        this.dataSource = dataSource;
        this.userName = userName;
        this.userPassword = Utils.clone(userPassword);
    }
    
    public DataSourceConnectionFactory(final DataSource dataSource, final String userName, final String password) {
        this.dataSource = dataSource;
        this.userName = userName;
        this.userPassword = Utils.toCharArray(password);
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        if (null == this.userName && null == this.userPassword) {
            return this.dataSource.getConnection();
        }
        return this.dataSource.getConnection(this.userName, Utils.toString(this.userPassword));
    }
    
    public DataSource getDataSource() {
        return this.dataSource;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public char[] getUserPassword() {
        return (char[])((this.userPassword == null) ? null : ((char[])this.userPassword.clone()));
    }
}
