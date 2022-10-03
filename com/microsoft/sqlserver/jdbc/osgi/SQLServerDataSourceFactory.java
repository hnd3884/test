package com.microsoft.sqlserver.jdbc.osgi;

import java.util.ResourceBundle;
import java.util.Locale;
import java.util.logging.Level;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import java.sql.Driver;
import com.microsoft.sqlserver.jdbc.SQLServerXADataSource;
import javax.sql.XADataSource;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolDataSource;
import javax.sql.ConnectionPoolDataSource;
import java.sql.SQLException;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import javax.sql.DataSource;
import java.util.Properties;
import java.util.logging.Logger;
import org.osgi.service.jdbc.DataSourceFactory;

public class SQLServerDataSourceFactory implements DataSourceFactory
{
    private static Logger osgiLogger;
    private static final String NOT_SUPPORTED_MSG;
    
    public DataSource createDataSource(final Properties props) throws SQLException {
        final SQLServerDataSource source = new SQLServerDataSource();
        this.setup(source, props);
        return source;
    }
    
    public ConnectionPoolDataSource createConnectionPoolDataSource(final Properties props) throws SQLException {
        final SQLServerConnectionPoolDataSource poolDataSource = new SQLServerConnectionPoolDataSource();
        this.setupXSource(poolDataSource, props);
        return poolDataSource;
    }
    
    public XADataSource createXADataSource(final Properties props) throws SQLException {
        final SQLServerXADataSource xaDataSource = new SQLServerXADataSource();
        this.setupXSource(xaDataSource, props);
        return xaDataSource;
    }
    
    public Driver createDriver(final Properties props) throws SQLException {
        return new SQLServerDriver();
    }
    
    private void setup(final SQLServerDataSource source, final Properties props) {
        if (props == null) {
            return;
        }
        if (props.containsKey("databaseName")) {
            source.setDatabaseName(props.getProperty("databaseName"));
        }
        if (props.containsKey("dataSourceName")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "dataSourceName");
        }
        if (props.containsKey("description")) {
            source.setDescription(props.getProperty("description"));
        }
        if (props.containsKey("networkProtocol")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "networkProtocol");
        }
        if (props.containsKey("password")) {
            source.setPassword(props.getProperty("password"));
        }
        if (props.containsKey("portNumber")) {
            source.setPortNumber(Integer.parseInt(props.getProperty("portNumber")));
        }
        if (props.containsKey("roleName")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "roleName");
        }
        if (props.containsKey("serverName")) {
            source.setServerName(props.getProperty("serverName"));
        }
        if (props.containsKey("url")) {
            source.setURL(props.getProperty("url"));
        }
        if (props.containsKey("user")) {
            source.setUser(props.getProperty("user"));
        }
    }
    
    private void setupXSource(final SQLServerConnectionPoolDataSource source, final Properties props) {
        if (props == null) {
            return;
        }
        this.setup(source, props);
        if (props.containsKey("initialPoolSize")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "initialPoolSize");
        }
        if (props.containsKey("maxIdleTime")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "maxIdleTime");
        }
        if (props.containsKey("maxStatements")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "maxStatements");
        }
        if (props.containsKey("maxPoolSize")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "maxPoolSize");
        }
        if (props.containsKey("minPoolSize")) {
            SQLServerDataSourceFactory.osgiLogger.log(Level.WARNING, SQLServerDataSourceFactory.NOT_SUPPORTED_MSG, "minPoolSize");
        }
    }
    
    static {
        SQLServerDataSourceFactory.osgiLogger = Logger.getLogger("com.microsoft.sqlserver.jdbc.osgi.SQLServerDataSourceFactory");
        NOT_SUPPORTED_MSG = ResourceBundle.getBundle("com.microsoft.sqlserver.jdbc.SQLServerResource", Locale.getDefault()).getString("R_propertyNotSupported");
    }
}
