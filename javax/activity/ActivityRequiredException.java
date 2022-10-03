package javax.activity;

import java.rmi.RemoteException;

public class ActivityRequiredException extends RemoteException
{
    public ActivityRequiredException() {
    }
    
    public ActivityRequiredException(final String s) {
        super(s);
    }
    
    public ActivityRequiredException(final Throwable t) {
        this("", t);
    }
    
    public ActivityRequiredException(final String s, final Throwable t) {
        super(s, t);
    }
}
