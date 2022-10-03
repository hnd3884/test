package javax.transaction;

import javax.transaction.xa.XAResource;

public interface Transaction
{
    void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, SystemException;
    
    void rollback() throws IllegalStateException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
    
    int getStatus() throws SystemException;
    
    boolean enlistResource(final XAResource p0) throws RollbackException, IllegalStateException, SystemException;
    
    boolean delistResource(final XAResource p0, final int p1) throws IllegalStateException, SystemException;
    
    void registerSynchronization(final Synchronization p0) throws RollbackException, IllegalStateException, SystemException;
}
