package java.rmi.server;

import java.rmi.RemoteException;

@Deprecated
public class SkeletonNotFoundException extends RemoteException
{
    private static final long serialVersionUID = -7860299673822761231L;
    
    public SkeletonNotFoundException(final String s) {
        super(s);
    }
    
    public SkeletonNotFoundException(final String s, final Exception ex) {
        super(s, ex);
    }
}
