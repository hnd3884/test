package java.rmi.activation;

import java.rmi.RemoteException;
import java.rmi.Remote;

public interface ActivationSystem extends Remote
{
    public static final int SYSTEM_PORT = 1098;
    
    ActivationID registerObject(final ActivationDesc p0) throws ActivationException, UnknownGroupException, RemoteException;
    
    void unregisterObject(final ActivationID p0) throws ActivationException, UnknownObjectException, RemoteException;
    
    ActivationGroupID registerGroup(final ActivationGroupDesc p0) throws ActivationException, RemoteException;
    
    ActivationMonitor activeGroup(final ActivationGroupID p0, final ActivationInstantiator p1, final long p2) throws UnknownGroupException, ActivationException, RemoteException;
    
    void unregisterGroup(final ActivationGroupID p0) throws ActivationException, UnknownGroupException, RemoteException;
    
    void shutdown() throws RemoteException;
    
    ActivationDesc setActivationDesc(final ActivationID p0, final ActivationDesc p1) throws ActivationException, UnknownObjectException, UnknownGroupException, RemoteException;
    
    ActivationGroupDesc setActivationGroupDesc(final ActivationGroupID p0, final ActivationGroupDesc p1) throws ActivationException, UnknownGroupException, RemoteException;
    
    ActivationDesc getActivationDesc(final ActivationID p0) throws ActivationException, UnknownObjectException, RemoteException;
    
    ActivationGroupDesc getActivationGroupDesc(final ActivationGroupID p0) throws ActivationException, UnknownGroupException, RemoteException;
}
