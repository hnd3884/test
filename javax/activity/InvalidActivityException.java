package javax.activity;

import java.rmi.RemoteException;

public class InvalidActivityException extends RemoteException
{
    public InvalidActivityException() {
    }
    
    public InvalidActivityException(final String s) {
        super(s);
    }
    
    public InvalidActivityException(final Throwable t) {
        this("", t);
    }
    
    public InvalidActivityException(final String s, final Throwable t) {
        super(s, t);
    }
}
