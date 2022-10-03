package com.zoho.cp;

import java.sql.SQLException;
import java.sql.Connection;
import javax.transaction.TransactionManager;

public class MultiTxMgrTxDataSource extends TxDataSource
{
    private TransactionManager txManager;
    
    public MultiTxMgrTxDataSource(final ConnectionPool connPool) {
        super(connPool);
        this.txManager = (TransactionManager)new MultiTxMgr();
    }
    
    public TransactionManager getTxManager() throws Exception {
        return this.txManager;
    }
    
    public Connection getConnection() throws SQLException {
        try {
            if (this.txManager.getStatus() == 6) {
                final ConnectionDetail detail = this.getConnDetailFromPool();
                return (Connection)this.getLogicalConnection(detail, true, this.txManager);
            }
            if (this.txManager.getStatus() == 0 || this.txManager.getStatus() == 8) {
                final Txn txn = (Txn)this.txManager.getTransaction();
                ConnectionDetail detail2 = txn.getEnlistedConnectionDetail((TxDataSource)this);
                if (detail2 == null) {
                    detail2 = this.getConnDetailFromPool();
                    txn.enlistConnectionDetail((TxDataSource)this, detail2);
                }
                return (Connection)this.getLogicalConnection(detail2, false, this.txManager);
            }
            throw new Exception("cannot getConnection when the Txn Status is " + Txn.getStatus(this.txManager.getStatus()));
        }
        catch (final Exception exc) {
            throw new SQLException(exc.toString() + " Exception occurred during get connection from datasource", exc);
        }
    }
    
    private MultiTxMgrLogicalConnection getLogicalConnection(final ConnectionDetail detail, final boolean autoCommit, final TransactionManager t) throws Exception {
        final MultiTxMgrLogicalConnection logicalConn = new MultiTxMgrLogicalConnection(detail);
        try {
            logicalConn.setAutoCommit(autoCommit);
            logicalConn.setTransactionManager(t);
            return logicalConn;
        }
        catch (final Exception exc) {
            logicalConn.close();
            throw exc;
        }
    }
}
