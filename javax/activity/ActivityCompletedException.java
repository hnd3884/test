package javax.activity;

import java.rmi.RemoteException;

public class ActivityCompletedException extends RemoteException
{
    public ActivityCompletedException() {
    }
    
    public ActivityCompletedException(final String s) {
        super(s);
    }
    
    public ActivityCompletedException(final Throwable t) {
        this("", t);
    }
    
    public ActivityCompletedException(final String s, final Throwable t) {
        super(s, t);
    }
}
