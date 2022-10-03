package com.zoho.mickey.api;

import com.adventnet.persistence.PersistenceInitializer;
import com.adventnet.persistence.DataAccess;
import javax.transaction.Transaction;
import java.util.Objects;
import java.util.logging.Level;
import javax.transaction.TransactionManager;
import java.util.logging.Logger;

public class TransactionUtil
{
    private static final Logger LOGGER;
    public static final int REQUIRES = 1;
    public static final int REQUIRESNEW = 2;
    public static final int SUPPORTS = 3;
    public static final int NOSUPPORT = 4;
    private static final TransactionManager TRANSACTION_MANAGER;
    private static final int DEFAULT_TIMEOUT;
    
    public static void withTransaction(final TransactionalRunnable transactional) throws Throwable {
        transactional(transactional, 1, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static void withTransaction(final TransactionalRunnable transactional, final int timeout) throws Throwable {
        final boolean isTransactionPresent = TransactionUtil.TRANSACTION_MANAGER.getTransaction() != null;
        try {
            transactional(transactional, 1, timeout);
        }
        catch (final Throwable t) {
            if (isTransactionPresent) {
                TransactionUtil.LOGGER.log(Level.FINE, "Ignoring supplied timeout of [{0}] sec since transaction was already begun", timeout);
            }
            throw t;
        }
    }
    
    public static void withNewTransaction(final TransactionalRunnable transactional) throws Throwable {
        transactional(transactional, 2, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static void withNewTransaction(final TransactionalRunnable transactional, final int timeout) throws Throwable {
        transactional(transactional, 2, timeout);
    }
    
    public static void withIgnoreTransaction(final TransactionalRunnable transactional) throws Throwable {
        transactional(transactional, 3, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static void withoutTransaction(final TransactionalRunnable transactional) throws Throwable {
        transactional(transactional, 4, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static void transactional(final TransactionalRunnable transactional, final int transactionType, final int timeout) throws Throwable {
        final TransactionalCallable<Void> transactionalCallable = (TransactionalCallable<Void>)(() -> {
            transactional.execute();
            return null;
        });
        transactional(transactionalCallable, transactionType, timeout);
    }
    
    public static <R> R withTransaction(final TransactionalCallable<R> transactional) throws Throwable {
        return transactional(transactional, 1, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static <R> R withTransaction(final TransactionalCallable<R> transactional, final int timeout) throws Throwable {
        final boolean isTransactionPresent = TransactionUtil.TRANSACTION_MANAGER.getTransaction() != null;
        try {
            return transactional(transactional, 1, timeout);
        }
        catch (final Throwable t) {
            if (isTransactionPresent) {
                TransactionUtil.LOGGER.log(Level.FINE, "Ignoring supplied timeout of [{0}] sec since transaction was already begun", timeout);
            }
            throw t;
        }
    }
    
    public static <R> R withNewTransaction(final TransactionalCallable<R> transactional) throws Throwable {
        return transactional(transactional, 2, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static <R> R withNewTransaction(final TransactionalCallable<R> transactional, final int timeout) throws Throwable {
        return transactional(transactional, 2, timeout);
    }
    
    public static <R> R withIgnoreTransaction(final TransactionalCallable<R> transactional) throws Throwable {
        return transactional(transactional, 3, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static <R> R withoutTransaction(final TransactionalCallable<R> transactional) throws Throwable {
        return transactional(transactional, 4, TransactionUtil.DEFAULT_TIMEOUT);
    }
    
    public static <R> R transactional(final TransactionalCallable<R> transactional, final int transactionType, final int timeout) throws Throwable {
        Objects.requireNonNull(transactional, "Transactional object cannot be null");
        R result = null;
        switch (transactionType) {
            case 1: {
                final Transaction ttx = TransactionUtil.TRANSACTION_MANAGER.getTransaction();
                if (ttx == null) {
                    boolean commit = false;
                    TransactionUtil.TRANSACTION_MANAGER.setTransactionTimeout(timeout);
                    boolean transactionStarted = false;
                    TransactionUtil.TRANSACTION_MANAGER.begin();
                    transactionStarted = true;
                    try {
                        result = transactional.execute();
                        commit = true;
                    }
                    finally {
                        if (transactionStarted) {
                            doCommit(commit);
                        }
                    }
                    break;
                }
                result = transactional.execute();
                break;
            }
            case 2: {
                Transaction existingTx = null;
                if (TransactionUtil.TRANSACTION_MANAGER.getTransaction() != null) {
                    existingTx = TransactionUtil.TRANSACTION_MANAGER.suspend();
                }
                boolean commit = false;
                boolean transactionStarted = false;
                try {
                    TransactionUtil.TRANSACTION_MANAGER.setTransactionTimeout(timeout);
                    TransactionUtil.TRANSACTION_MANAGER.begin();
                    transactionStarted = true;
                    result = transactional.execute();
                    commit = true;
                }
                finally {
                    try {
                        if (transactionStarted) {
                            doCommit(commit);
                        }
                    }
                    finally {
                        if (existingTx != null) {
                            TransactionUtil.TRANSACTION_MANAGER.resume(existingTx);
                        }
                    }
                }
                break;
            }
            case 4: {
                Transaction existingTx = null;
                try {
                    if (TransactionUtil.TRANSACTION_MANAGER.getTransaction() != null) {
                        existingTx = TransactionUtil.TRANSACTION_MANAGER.suspend();
                    }
                    result = transactional.execute();
                }
                finally {
                    if (existingTx != null) {
                        TransactionUtil.TRANSACTION_MANAGER.resume(existingTx);
                    }
                }
                break;
            }
            default: {
                result = transactional.execute();
                break;
            }
        }
        return result;
    }
    
    private static void doCommit(final boolean commit) throws Throwable {
        final int txnStatus = TransactionUtil.TRANSACTION_MANAGER.getStatus();
        if (txnStatus == 1 || !commit) {
            TransactionUtil.TRANSACTION_MANAGER.rollback();
        }
        else {
            TransactionUtil.TRANSACTION_MANAGER.commit();
        }
    }
    
    static {
        LOGGER = Logger.getLogger(TransactionUtil.class.getName());
        TRANSACTION_MANAGER = DataAccess.getTransactionManager();
        DEFAULT_TIMEOUT = Integer.parseInt(PersistenceInitializer.getConfigurationValue("TransactionTimeOut"));
    }
    
    @FunctionalInterface
    public interface TransactionalCallable<R>
    {
        R execute() throws Throwable;
    }
    
    @FunctionalInterface
    public interface TransactionalRunnable
    {
        void execute() throws Throwable;
    }
}
