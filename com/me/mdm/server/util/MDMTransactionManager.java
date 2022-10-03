package com.me.mdm.server.util;

import javax.transaction.HeuristicMixedException;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import com.adventnet.sym.server.mdm.util.MDMUtil;

public class MDMTransactionManager
{
    private Boolean transactionStartedByThisInstance;
    
    public MDMTransactionManager() {
        this.transactionStartedByThisInstance = Boolean.FALSE;
    }
    
    public void begin() throws SystemException, NotSupportedException {
        if (MDMUtil.getUserTransaction().getStatus() == 6) {
            MDMUtil.getUserTransaction().begin();
            this.transactionStartedByThisInstance = Boolean.TRUE;
        }
    }
    
    public void commit() throws HeuristicRollbackException, RollbackException, HeuristicMixedException, SystemException {
        if (this.transactionStartedByThisInstance) {
            MDMUtil.getUserTransaction().commit();
        }
    }
    
    public void rollBack() throws SystemException {
        if (this.transactionStartedByThisInstance) {
            MDMUtil.getUserTransaction().rollback();
        }
    }
    
    public Boolean isMDMTransaction() {
        return this.transactionStartedByThisInstance;
    }
}
