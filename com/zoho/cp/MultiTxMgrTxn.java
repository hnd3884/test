package com.zoho.cp;

import javax.transaction.Synchronization;
import javax.transaction.xa.XAResource;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

public class MultiTxMgrTxn extends Txn
{
    private MultiTxMgr txMgr;
    
    MultiTxMgrTxn(final int transactionTimeout, final MultiTxMgr t) {
        super(transactionTimeout);
        this.txMgr = t;
    }
    
    public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException {
        if (this.getStatus() == 6) {
            throw new SystemException("No Transaction to commit");
        }
        try {
            this.commitTransaction();
        }
        finally {
            this.txMgr.clearTxDetailInThread();
        }
    }
    
    public void rollback() throws IllegalStateException, SystemException {
        if (this.getStatus() == 6) {
            throw new SystemException("No Transaction to rollback");
        }
        try {
            this.rollbackTransaction();
        }
        finally {
            this.txMgr.clearTxDetailInThread();
        }
    }
}
