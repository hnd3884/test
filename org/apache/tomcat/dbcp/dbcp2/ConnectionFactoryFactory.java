package org.apache.tomcat.dbcp.dbcp2;

import java.util.Hashtable;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.Driver;

class ConnectionFactoryFactory
{
    static ConnectionFactory createConnectionFactory(final BasicDataSource basicDataSource, final Driver driver) throws SQLException {
        final Properties connectionProperties = basicDataSource.getConnectionProperties();
        final String url = basicDataSource.getUrl();
        final String user = basicDataSource.getUsername();
        if (user != null) {
            ((Hashtable<String, String>)connectionProperties).put("user", user);
        }
        else {
            basicDataSource.log("DBCP DataSource configured without a 'username'");
        }
        final String pwd = basicDataSource.getPassword();
        if (pwd != null) {
            ((Hashtable<String, String>)connectionProperties).put("password", pwd);
        }
        else {
            basicDataSource.log("DBCP DataSource configured without a 'password'");
        }
        final String connectionFactoryClassName = basicDataSource.getConnectionFactoryClassName();
        if (connectionFactoryClassName != null) {
            try {
                final Class<?> connectionFactoryFromCCL = Class.forName(connectionFactoryClassName);
                return (ConnectionFactory)connectionFactoryFromCCL.getConstructor(Driver.class, String.class, Properties.class).newInstance(driver, url, connectionProperties);
            }
            catch (final Exception t) {
                final String message = "Cannot load ConnectionFactory implementation '" + connectionFactoryClassName + "'";
                basicDataSource.log(message, t);
                throw new SQLException(message, t);
            }
        }
        return new DriverConnectionFactory(driver, url, connectionProperties);
    }
}
