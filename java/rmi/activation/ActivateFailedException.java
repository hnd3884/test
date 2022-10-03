package java.rmi.activation;

import java.rmi.RemoteException;

public class ActivateFailedException extends RemoteException
{
    private static final long serialVersionUID = 4863550261346652506L;
    
    public ActivateFailedException(final String s) {
        super(s);
    }
    
    public ActivateFailedException(final String s, final Exception ex) {
        super(s, ex);
    }
}
