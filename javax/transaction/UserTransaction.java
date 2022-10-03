package javax.transaction;

public interface UserTransaction
{
    void begin() throws NotSupportedException, SystemException;
    
    void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException;
    
    void rollback() throws IllegalStateException, SecurityException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
    
    int getStatus() throws SystemException;
    
    void setTransactionTimeout(final int p0) throws SystemException;
}
