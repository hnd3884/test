package java.rmi.server;

import java.rmi.RemoteException;

@Deprecated
public class SkeletonMismatchException extends RemoteException
{
    private static final long serialVersionUID = -7780460454818859281L;
    
    @Deprecated
    public SkeletonMismatchException(final String s) {
        super(s);
    }
}
