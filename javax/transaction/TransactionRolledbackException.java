package javax.transaction;

import java.rmi.RemoteException;

public class TransactionRolledbackException extends RemoteException
{
    public TransactionRolledbackException() {
    }
    
    public TransactionRolledbackException(final String msg) {
        super(msg);
    }
}
