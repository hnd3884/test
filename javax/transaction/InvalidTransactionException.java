package javax.transaction;

import java.rmi.RemoteException;

public class InvalidTransactionException extends RemoteException
{
    public InvalidTransactionException() {
    }
    
    public InvalidTransactionException(final String msg) {
        super(msg);
    }
}
