package javax.transaction;

public interface TransactionManager
{
    void begin() throws NotSupportedException, SystemException;
    
    void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException;
    
    void rollback() throws IllegalStateException, SecurityException, SystemException;
    
    void setRollbackOnly() throws IllegalStateException, SystemException;
    
    int getStatus() throws SystemException;
    
    Transaction getTransaction() throws SystemException;
    
    void setTransactionTimeout(final int p0) throws SystemException;
    
    Transaction suspend() throws SystemException;
    
    void resume(final Transaction p0) throws InvalidTransactionException, IllegalStateException, SystemException;
}
