package javax.ejb;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface EJBObject extends Remote
{
    EJBHome getEJBHome() throws RemoteException;
    
    Handle getHandle() throws RemoteException;
    
    Object getPrimaryKey() throws RemoteException;
    
    boolean isIdentical(final EJBObject p0) throws RemoteException;
    
    void remove() throws RemoteException, RemoveException;
}
