package com.zoho.cp;

import java.sql.SQLException;
import java.util.logging.Level;
import javax.transaction.TransactionManager;
import java.util.logging.Logger;

public class MultiTxMgrLogicalConnection extends LogicalConnection
{
    private static final Logger LOGGER;
    private MultiTxMgr txMgr;
    
    public MultiTxMgrLogicalConnection(final ConnectionDetail detail) {
        super(detail);
        this.txMgr = null;
    }
    
    protected void setTransactionManager(final TransactionManager t) {
        this.txMgr = (MultiTxMgr)t;
    }
    
    public void close() throws SQLException {
        if (this.isClosed()) {
            return;
        }
        try {
            final Txn txn = (Txn)this.txMgr.getTransaction();
            if (this.txMgr.getStatus() == 6 || (txn != null && !txn.isEnlisted(this.getConnectionDetail()))) {
                this.getConnectionDetail().closeAndRemoveChildren();
                this.getConnectionDetail().returnToPool();
                MultiTxMgrLogicalConnection.LOGGER.log(Level.FINE, "Logical connection closed and return to the pool");
                return;
            }
            this.setClosed();
        }
        catch (final Exception exc) {
            throw new SQLException("Exception occurred during closing logical connection", exc);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(LogicalConnection.class.getName());
    }
}
