package com.adventnet.ds.adapter.mds;

import java.sql.SQLFeatureNotSupportedException;
import java.io.PrintWriter;
import java.util.HashMap;
import com.adventnet.db.adapter.DBAdapter;
import com.adventnet.db.api.RelationalAPI;
import java.sql.Connection;
import java.sql.SQLException;
import com.adventnet.ds.DataSourceManager;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class MDSDataSource implements DataSource
{
    private static Logger logger;
    
    private DataSource lookUpDataSource(final String dataSourceName) throws SQLException {
        if (dataSourceName != null) {
            return DataSourceManager.getDataSource(dataSourceName);
        }
        throw new IllegalArgumentException("dataSourceName cannot be null");
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        DataSource dataSource = null;
        Connection connection = null;
        DBAdapter dbAdapter = null;
        String dsName = null;
        String dbName = null;
        final HashMap hashMap = DBThreadLocal.get();
        if (hashMap != null) {
            dsName = hashMap.get("dsName");
            dbName = hashMap.get("dbName");
            dbAdapter = RelationalAPI.getInstance().getDBAdapter();
            dataSource = this.lookUpDataSource(dsName);
            connection = dataSource.getConnection();
            dbAdapter.connectTo(connection, (dbName != null) ? dbName : dbAdapter.getDefaultDB(connection));
            return connection;
        }
        dataSource = this.lookUpDataSource("default");
        DataSourceManager.getInstance();
        dbAdapter = (DBAdapter)DataSourceManager.getDSAdapter("default");
        connection = dataSource.getConnection();
        dbAdapter.connectTo(connection, dbAdapter.getDefaultDB(connection));
        return connection;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return null;
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return -1;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
    
    static {
        MDSDataSource.logger = Logger.getLogger(MDSDataSource.class.getName());
    }
}
