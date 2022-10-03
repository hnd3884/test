package java.rmi.activation;

import java.rmi.MarshalledObject;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface ActivationMonitor extends Remote
{
    void inactiveObject(final ActivationID p0) throws UnknownObjectException, RemoteException;
    
    void activeObject(final ActivationID p0, final MarshalledObject<? extends Remote> p1) throws UnknownObjectException, RemoteException;
    
    void inactiveGroup(final ActivationGroupID p0, final long p1) throws UnknownGroupException, RemoteException;
}
