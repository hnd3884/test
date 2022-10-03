package com.adventnet.mfw.bean;

import com.adventnet.persistence.DataAccess;
import javax.transaction.Transaction;
import java.lang.reflect.Method;
import javax.transaction.TransactionManager;
import java.lang.reflect.InvocationHandler;

public class BeanProxy implements InvocationHandler
{
    private static final int REQUIRES = 1;
    private static final int REQUIRESNEW = 2;
    private static final int SUPPORTS = 3;
    private static final int NOSUPPORT = 4;
    private static TransactionManager txMgr;
    private Object obj;
    private int txType;
    
    public BeanProxy() {
    }
    
    public BeanProxy(final Object obj, final int txType) {
        this.obj = obj;
        this.txType = txType;
    }
    
    public void setParams(final Object obj, final int txType, final Object dist, final int operation) throws Exception {
        this.obj = obj;
        this.txType = txType;
    }
    
    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) throws Throwable {
        Object result = null;
        boolean commit = false;
        switch (this.txType) {
            case 1: {
                final Transaction ttx = BeanProxy.txMgr.getTransaction();
                if (ttx == null) {
                    BeanProxy.txMgr.begin();
                    try {
                        result = m.invoke(this.obj, args);
                        commit = true;
                    }
                    catch (final Exception e) {
                        throw e.getCause();
                    }
                    finally {
                        this.doCommit(commit);
                    }
                }
                else {
                    try {
                        result = m.invoke(this.obj, args);
                    }
                    catch (final Exception e) {
                        throw e.getCause();
                    }
                }
                return result;
            }
            case 2: {
                Transaction existingTx = null;
                if (BeanProxy.txMgr.getTransaction() != null) {
                    existingTx = BeanProxy.txMgr.suspend();
                }
                BeanProxy.txMgr.begin();
                try {
                    result = m.invoke(this.obj, args);
                    commit = true;
                }
                catch (final Exception e) {
                    throw e.getCause();
                }
                finally {
                    this.doCommit(commit);
                    if (existingTx != null) {
                        BeanProxy.txMgr.resume(existingTx);
                    }
                }
                return result;
            }
            case 4: {
                Transaction existingTx = null;
                try {
                    if (BeanProxy.txMgr.getTransaction() != null) {
                        existingTx = BeanProxy.txMgr.suspend();
                    }
                    result = m.invoke(this.obj, args);
                }
                catch (final Exception e) {
                    throw e.getCause();
                }
                finally {
                    if (existingTx != null) {
                        BeanProxy.txMgr.resume(existingTx);
                    }
                }
                return result;
            }
            default: {
                try {
                    result = m.invoke(this.obj, args);
                }
                catch (final Exception e2) {
                    throw e2.getCause();
                }
                return result;
            }
        }
    }
    
    private void doCommit(final boolean commit) throws Exception {
        final int txnStatus = BeanProxy.txMgr.getStatus();
        if (txnStatus == 1 || !commit) {
            BeanProxy.txMgr.rollback();
        }
        else {
            BeanProxy.txMgr.commit();
        }
    }
    
    static {
        BeanProxy.txMgr = DataAccess.getTransactionManager();
    }
}
