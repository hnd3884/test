package com.me.idps.core.factory;

import com.me.idps.core.util.IdpsUtil;
import org.json.simple.JSONObject;
import java.util.Properties;
import javax.transaction.RollbackException;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import java.util.logging.Level;
import com.me.idps.core.IDPSlogger;
import com.me.devicemanagement.framework.server.util.SyMUtil;

public class TransactionExecutionImpl
{
    private static TransactionExecutionImpl transactionExecutionImpl;
    
    public static TransactionExecutionImpl getInstance() {
        if (TransactionExecutionImpl.transactionExecutionImpl == null) {
            TransactionExecutionImpl.transactionExecutionImpl = new TransactionExecutionImpl();
        }
        return TransactionExecutionImpl.transactionExecutionImpl;
    }
    
    public boolean clearActiveTransactionsIfAny() throws SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        boolean closedTransaction = false;
        final TransactionManager tm = SyMUtil.getUserTransaction();
        if (tm != null) {
            IDPSlogger.TXN.log(Level.FINE, "transaction status:{0}", new Object[] { tm.getStatus() });
            if (tm.getStatus() == 0) {
                IDPSlogger.TXN.log(Level.WARNING, "!!!committing!!!");
                tm.commit();
                IDPSlogger.TXN.log(Level.WARNING, "active transaction closed..current status:{0}", new Object[] { tm.getStatus() });
                closedTransaction = true;
            }
            if (tm.getStatus() == 1) {
                IDPSlogger.TXN.log(Level.WARNING, "rolling back");
                tm.rollback();
                IDPSlogger.TXN.log(Level.WARNING, "rolled back..current status:{0}", new Object[] { tm.getStatus() });
                closedTransaction = true;
            }
        }
        return closedTransaction;
    }
    
    public Object performTaskInTransactionMode(final String invokeClass, final Properties taskDetails) throws Exception {
        this.clearActiveTransactionsIfAny();
        final TransactionManager tm = SyMUtil.getUserTransaction();
        Object result = null;
        try {
            tm.begin();
            result = ((TransactionExecutionInterface)Class.forName(invokeClass).newInstance()).executeTxTask(taskDetails);
            tm.commit();
        }
        catch (final Exception ex) {
            final JSONObject logErrDetails = new JSONObject();
            logErrDetails.put((Object)"invokeClass", (Object)String.valueOf(invokeClass));
            logErrDetails.put((Object)"taskDetails", (Object)String.valueOf(taskDetails));
            IDPSlogger.TXN.log(Level.SEVERE, "error occurred for {0}.. marking status to rollback {1}", new Object[] { IdpsUtil.getPrettyJSON(logErrDetails), ex });
            try {
                tm.setRollbackOnly();
            }
            catch (final SystemException ex2) {
                IDPSlogger.TXN.log(Level.SEVERE, "exception occurred in try to mark transaction to rollback", ex);
            }
            throw ex;
        }
        finally {
            if (tm != null) {
                try {
                    IDPSlogger.TXN.log(Level.FINE, "trying to get tm status");
                    final int tmStatus = tm.getStatus();
                    IDPSlogger.TXN.log(Level.FINE, "tm statue {0}", new Object[] { tmStatus });
                    if (tmStatus == 1) {
                        IDPSlogger.TXN.log(Level.WARNING, "trying to rollback.. tmStatus :{0}", new Object[] { tmStatus });
                        tm.rollback();
                        IDPSlogger.TXN.log(Level.WARNING, "rolledback");
                    }
                    return result;
                }
                catch (final SystemException ex3) {
                    IDPSlogger.TXN.log(Level.SEVERE, "could not rollback!!", (Throwable)ex3);
                    throw ex3;
                }
            }
            IDPSlogger.TXN.log(Level.WARNING, "txn is null");
        }
        return result;
    }
    
    static {
        TransactionExecutionImpl.transactionExecutionImpl = null;
    }
}
