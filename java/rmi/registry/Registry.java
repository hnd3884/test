package java.rmi.registry;

import java.rmi.AlreadyBoundException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Registry extends Remote
{
    public static final int REGISTRY_PORT = 1099;
    
    Remote lookup(final String p0) throws RemoteException, NotBoundException, AccessException;
    
    void bind(final String p0, final Remote p1) throws RemoteException, AlreadyBoundException, AccessException;
    
    void unbind(final String p0) throws RemoteException, NotBoundException, AccessException;
    
    void rebind(final String p0, final Remote p1) throws RemoteException, AccessException;
    
    String[] list() throws RemoteException, AccessException;
}
