package com.adventnet.cp;

import java.util.concurrent.Executor;
import java.sql.Struct;
import java.sql.Array;
import java.util.Properties;
import java.sql.SQLClientInfoException;
import java.sql.SQLXML;
import java.sql.NClob;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Savepoint;
import java.util.Map;
import java.sql.SQLWarning;
import java.sql.DatabaseMetaData;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import com.adventnet.persistence.PersistenceInitializer;
import java.sql.Statement;
import java.util.logging.Logger;
import java.sql.Connection;

public class WrappedConnection implements Connection
{
    static Logger OUT;
    private int i;
    private Connection[] dbConnections;
    
    public WrappedConnection() {
        this.dbConnections = new Connection[3];
        this.i = 0;
        for (int i = 0; i < 3; ++i) {
            this.dbConnections[i] = null;
        }
    }
    
    public void addConnection(final Connection conn) {
        this.dbConnections[this.i++] = conn;
    }
    
    public Connection getConnection(final int i) {
        return this.dbConnections[i];
    }
    
    private Connection getReadConnection() {
        final Integer readDBIndex = ClientFilter.getThreadLocalDB();
        if (readDBIndex != null && MultiDSUtil.isMultiDataSourceEnabled()) {
            return this.dbConnections[readDBIndex];
        }
        return this.dbConnections[0];
    }
    
    private Connection getDefaultConnection() {
        return this.dbConnections[0];
    }
    
    @Override
    public Statement createStatement() {
        final Statement stmt = new WrappedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedStatement)stmt).addStatement(this.dbConnections[i].createStatement());
                    }
                }
            }
            else {
                ((WrappedStatement)stmt).addStatement(this.getDefaultConnection().createStatement());
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a statement");
            e.printStackTrace();
        }
        return stmt;
    }
    
    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return this.getReadConnection().unwrap(iface);
    }
    
    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return this.getReadConnection().isWrapperFor(iface);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return this.getDefaultConnection().prepareCall(sql);
    }
    
    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return this.getReadConnection().nativeSQL(sql);
    }
    
    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setAutoCommit(autoCommit);
            }
        }
    }
    
    @Override
    public boolean getAutoCommit() throws SQLException {
        boolean result = true;
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                result = (result && this.dbConnections[i].getAutoCommit());
            }
        }
        return result;
    }
    
    @Override
    public void commit() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].commit();
            }
        }
    }
    
    @Override
    public void rollback() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].rollback();
            }
        }
    }
    
    @Override
    public void close() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].close();
            }
        }
    }
    
    @Override
    public boolean isClosed() throws SQLException {
        boolean result = true;
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                result = (result && this.dbConnections[i].isClosed());
            }
        }
        return result;
    }
    
    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return this.getReadConnection().getMetaData();
    }
    
    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setReadOnly(readOnly);
            }
        }
    }
    
    @Override
    public boolean isReadOnly() throws SQLException {
        boolean result = true;
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                result = (result && this.dbConnections[i].isReadOnly());
            }
        }
        return result;
    }
    
    @Override
    public void setCatalog(final String catalog) throws SQLException {
        this.getDefaultConnection().setCatalog(catalog);
    }
    
    @Override
    public String getCatalog() throws SQLException {
        return this.getDefaultConnection().getCatalog();
    }
    
    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        this.getReadConnection().setTransactionIsolation(level);
    }
    
    @Override
    public int getTransactionIsolation() throws SQLException {
        return this.getReadConnection().getTransactionIsolation();
    }
    
    @Override
    public SQLWarning getWarnings() throws SQLException {
        return this.getReadConnection().getWarnings();
    }
    
    @Override
    public void clearWarnings() throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].clearWarnings();
            }
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        final Statement stmt = new WrappedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedStatement)stmt).addStatement(this.dbConnections[i].createStatement(resultSetType, resultSetConcurrency));
                    }
                }
            }
            else {
                ((WrappedStatement)stmt).addStatement(this.getDefaultConnection().createStatement(resultSetType, resultSetConcurrency));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a statement");
            e.printStackTrace();
        }
        return stmt;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql, resultSetType, resultSetConcurrency));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql, resultSetType, resultSetConcurrency));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return this.getDefaultConnection().prepareCall(sql, resultSetType, resultSetConcurrency);
    }
    
    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return this.getReadConnection().getTypeMap();
    }
    
    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setTypeMap(map);
            }
        }
    }
    
    @Override
    public void setHoldability(final int holdability) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setHoldability(holdability);
            }
        }
    }
    
    @Override
    public int getHoldability() throws SQLException {
        return this.getReadConnection().getHoldability();
    }
    
    @Override
    public Savepoint setSavepoint() throws SQLException {
        Savepoint savepoint = null;
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                savepoint = this.dbConnections[i].setSavepoint();
            }
        }
        return savepoint;
    }
    
    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        Savepoint savepoint = null;
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                savepoint = this.dbConnections[i].setSavepoint(name);
            }
        }
        return savepoint;
    }
    
    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].rollback(savepoint);
            }
        }
    }
    
    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].releaseSavepoint(savepoint);
            }
        }
    }
    
    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final Statement stmt = new WrappedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedStatement)stmt).addStatement(this.dbConnections[i].createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
                    }
                }
            }
            else {
                ((WrappedStatement)stmt).addStatement(this.getDefaultConnection().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a statement");
            e.printStackTrace();
        }
        return stmt;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return this.getDefaultConnection().prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql, autoGeneratedKeys));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql, autoGeneratedKeys));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql, columnIndexes));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql, columnIndexes));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        final PreparedStatement pstmt = new WrappedPreparedStatement();
        try {
            if (MultiDSUtil.isMultiDataSourceEnabled()) {
                for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
                    if (this.dbConnections[i] != null) {
                        ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.dbConnections[i].prepareStatement(sql, columnNames));
                    }
                }
            }
            else {
                ((WrappedPreparedStatement)pstmt).addPreparedStatement(this.getDefaultConnection().prepareStatement(sql, columnNames));
            }
        }
        catch (final SQLException e) {
            WrappedConnection.OUT.log(Level.SEVERE, "Problem while creating a prepared statement");
            e.printStackTrace();
        }
        return pstmt;
    }
    
    @Override
    public Clob createClob() throws SQLException {
        return this.getDefaultConnection().createClob();
    }
    
    @Override
    public Blob createBlob() throws SQLException {
        return this.getDefaultConnection().createBlob();
    }
    
    @Override
    public NClob createNClob() throws SQLException {
        return this.getDefaultConnection().createNClob();
    }
    
    @Override
    public SQLXML createSQLXML() throws SQLException {
        return this.getDefaultConnection().createSQLXML();
    }
    
    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return this.getReadConnection().isValid(timeout);
    }
    
    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setClientInfo(name, value);
            }
        }
    }
    
    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setClientInfo(properties);
            }
        }
    }
    
    @Override
    public String getClientInfo(final String name) throws SQLException {
        return this.getReadConnection().getClientInfo(name);
    }
    
    @Override
    public Properties getClientInfo() throws SQLException {
        return this.getReadConnection().getClientInfo();
    }
    
    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return this.getDefaultConnection().createArrayOf(typeName, elements);
    }
    
    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return this.getDefaultConnection().createStruct(typeName, attributes);
    }
    
    @Override
    public void setSchema(final String schema) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setSchema(schema);
            }
        }
    }
    
    @Override
    public String getSchema() throws SQLException {
        return this.getReadConnection().getSchema();
    }
    
    @Override
    public void abort(final Executor executor) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].abort(executor);
            }
        }
    }
    
    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        for (int i = 0; i < PersistenceInitializer.getMWSRConfigCount(); ++i) {
            if (this.dbConnections[i] != null) {
                this.dbConnections[i].setNetworkTimeout(executor, milliseconds);
            }
        }
    }
    
    @Override
    public int getNetworkTimeout() throws SQLException {
        return this.getReadConnection().getNetworkTimeout();
    }
    
    static {
        WrappedConnection.OUT = Logger.getLogger(WrappedConnection.class.getName());
    }
}
