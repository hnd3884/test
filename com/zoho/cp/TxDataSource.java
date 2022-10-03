package com.zoho.cp;

import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Connection;
import javax.transaction.TransactionManager;
import javax.sql.DataSource;

public class TxDataSource implements DataSource
{
    ConnectionPool connPool;
    private TransactionManager txManager;
    
    public TxDataSource(final ConnectionPool connPool) {
        this.txManager = TxMgr.getInstance();
        this.connPool = connPool;
    }
    
    public TransactionManager getTxManager() throws Exception {
        return this.txManager;
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        try {
            if (this.txManager.getStatus() == 6) {
                final ConnectionDetail detail = this.getConnDetailFromPool();
                return this.getLogicalConnection(detail, true);
            }
            if (this.txManager.getStatus() == 0 || this.txManager.getStatus() == 8) {
                final Txn txn = (Txn)TxMgr.getInstance().getTransaction();
                ConnectionDetail detail2 = txn.getEnlistedConnectionDetail(this);
                if (detail2 == null) {
                    detail2 = this.getConnDetailFromPool();
                    txn.enlistConnectionDetail(this, detail2);
                }
                return this.getLogicalConnection(detail2, false);
            }
            throw new Exception("cannot getConnection when the Txn Status is " + Txn.getStatus(this.txManager.getStatus()));
        }
        catch (final Exception exc) {
            final SQLException sqlException = new SQLException(exc.toString() + " Exception occurred during get connection from datasource", exc);
            this.connPool.creator.getExceptionHandler().onConnectException(sqlException);
            throw sqlException;
        }
    }
    
    protected ConnectionDetail getConnDetailFromPool() throws Exception {
        final ConnectionDetail detail = this.connPool.getConnectionDetail();
        return detail;
    }
    
    private LogicalConnection getLogicalConnection(final ConnectionDetail detail, final boolean autoCommit) throws Exception {
        final LogicalConnection logicalConn = new LogicalConnection(detail);
        try {
            logicalConn.setAutoCommit(autoCommit);
            return logicalConn;
        }
        catch (final Exception exc) {
            logicalConn.close();
            throw exc;
        }
    }
    
    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return null;
    }
    
    @Override
    public Connection getConnection(final String username, final String password) throws SQLException {
        return this.getConnection();
    }
    
    @Override
    public void setLogWriter(final PrintWriter out) throws SQLException {
    }
    
    @Override
    public void setLoginTimeout(final int seconds) throws SQLException {
    }
    
    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
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
}
