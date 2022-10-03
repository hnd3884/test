package org.apache.tomcat.dbcp.dbcp2;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.Properties;
import java.sql.Driver;

public class DriverConnectionFactory implements ConnectionFactory
{
    private final String connectionString;
    private final Driver driver;
    private final Properties properties;
    
    public DriverConnectionFactory(final Driver driver, final String connectString, final Properties properties) {
        this.driver = driver;
        this.connectionString = connectString;
        this.properties = properties;
    }
    
    @Override
    public Connection createConnection() throws SQLException {
        return this.driver.connect(this.connectionString, this.properties);
    }
    
    public String getConnectionString() {
        return this.connectionString;
    }
    
    public Driver getDriver() {
        return this.driver;
    }
    
    public Properties getProperties() {
        return this.properties;
    }
    
    @Override
    public String toString() {
        return this.getClass().getName() + " [" + this.driver + ";" + this.connectionString + ";" + Utils.cloneWithoutCredentials(this.properties) + "]";
    }
}
