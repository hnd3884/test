package javax.ejb;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface EJBHome extends Remote
{
    EJBMetaData getEJBMetaData() throws RemoteException;
    
    HomeHandle getHomeHandle() throws RemoteException;
    
    void remove(final Object p0) throws RemoteException, RemoveException;
    
    void remove(final Handle p0) throws RemoteException, RemoveException;
}
