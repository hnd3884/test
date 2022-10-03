package com.adventnet.cp;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.SQLException;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.Connection;
import javax.sql.DataSource;

public class WrappedDataSource implements DataSource
{
    private int i;
    private DataSource[] datasources;
    
    public WrappedDataSource() {
        this.datasources = new DataSource[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.datasources[i] = null;
        }
    }
    
    public void addDataSource(final DataSource ds) {
        this.datasources[this.i++] = ds;
    }
    
    public DataSource getDataSource(final int i) {
        return this.datasources[i];
    }
    
    private DataSource getReadDataSource() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return this.datasources[readDBIndex];
        }
        return this.datasources[0];
    }
    
    private DataSource getDefaultDataSource() {
        return this.datasources[0];
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        final Connection conn = new WrappedConnection();
        try {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                ((WrappedConnection)conn).addConnection(this.datasources[i].getConnection());
            }
        }
        catch (final SQLException e) {
            throw new SQLException(e.toString() + " Connection couldn't be established", e);
        }
        return conn;
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
    }
    
    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return null;
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return false;
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        final Connection conn = new WrappedConnection();
        try {
            for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                ((WrappedConnection)conn).addConnection(this.datasources[i].getConnection(username, password));
            }
        }
        catch (final SQLException e) {
            throw new SQLException(e.toString() + " Connection couldn't be established", e);
        }
        return conn;
    }
}
