package java.rmi.server;

import java.rmi.RemoteException;

public class ExportException extends RemoteException
{
    private static final long serialVersionUID = -9155485338494060170L;
    
    public ExportException(final String s) {
        super(s);
    }
    
    public ExportException(final String s, final Exception ex) {
        super(s, ex);
    }
}
