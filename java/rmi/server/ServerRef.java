package java.rmi.server;

import java.rmi.RemoteException;
import java.rmi.Remote;

@Deprecated
public interface ServerRef extends RemoteRef
{
    public static final long serialVersionUID = -4557750989390278438L;
    
    RemoteStub exportObject(final Remote p0, final Object p1) throws RemoteException;
    
    String getClientHost() throws ServerNotActiveException;
}
