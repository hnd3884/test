package java.rmi.activation;

import java.rmi.NoSuchObjectException;
import sun.rmi.transport.ObjectTable;
import sun.rmi.server.ActivatableServerRef;
import sun.rmi.server.ActivatableRef;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.MarshalledObject;
import java.rmi.server.RemoteServer;

public abstract class Activatable extends RemoteServer
{
    private ActivationID id;
    private static final long serialVersionUID = -3120617863591563455L;
    
    protected Activatable(final String s, final MarshalledObject<?> marshalledObject, final boolean b, final int n) throws ActivationException, RemoteException {
        this.id = exportObject(this, s, marshalledObject, b, n);
    }
    
    protected Activatable(final String s, final MarshalledObject<?> marshalledObject, final boolean b, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws ActivationException, RemoteException {
        this.id = exportObject(this, s, marshalledObject, b, n, rmiClientSocketFactory, rmiServerSocketFactory);
    }
    
    protected Activatable(final ActivationID id, final int n) throws RemoteException {
        exportObject(this, this.id = id, n);
    }
    
    protected Activatable(final ActivationID id, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        exportObject(this, this.id = id, n, rmiClientSocketFactory, rmiServerSocketFactory);
    }
    
    protected ActivationID getID() {
        return this.id;
    }
    
    public static Remote register(final ActivationDesc activationDesc) throws UnknownGroupException, ActivationException, RemoteException {
        return ActivatableRef.getStub(activationDesc, ActivationGroup.getSystem().registerObject(activationDesc));
    }
    
    public static boolean inactive(final ActivationID activationID) throws UnknownObjectException, ActivationException, RemoteException {
        return ActivationGroup.currentGroup().inactiveObject(activationID);
    }
    
    public static void unregister(final ActivationID activationID) throws UnknownObjectException, ActivationException, RemoteException {
        ActivationGroup.getSystem().unregisterObject(activationID);
    }
    
    public static ActivationID exportObject(final Remote remote, final String s, final MarshalledObject<?> marshalledObject, final boolean b, final int n) throws ActivationException, RemoteException {
        return exportObject(remote, s, marshalledObject, b, n, null, null);
    }
    
    public static ActivationID exportObject(final Remote remote, final String s, final MarshalledObject<?> marshalledObject, final boolean b, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws ActivationException, RemoteException {
        final ActivationDesc activationDesc = new ActivationDesc(remote.getClass().getName(), s, marshalledObject, b);
        final ActivationSystem system = ActivationGroup.getSystem();
        final ActivationID registerObject = system.registerObject(activationDesc);
        try {
            exportObject(remote, registerObject, n, rmiClientSocketFactory, rmiServerSocketFactory);
        }
        catch (final RemoteException ex) {
            try {
                system.unregisterObject(registerObject);
            }
            catch (final Exception ex2) {}
            throw ex;
        }
        ActivationGroup.currentGroup().activeObject(registerObject, remote);
        return registerObject;
    }
    
    public static Remote exportObject(final Remote remote, final ActivationID activationID, final int n) throws RemoteException {
        return exportObject(remote, new ActivatableServerRef(activationID, n));
    }
    
    public static Remote exportObject(final Remote remote, final ActivationID activationID, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        return exportObject(remote, new ActivatableServerRef(activationID, n, rmiClientSocketFactory, rmiServerSocketFactory));
    }
    
    public static boolean unexportObject(final Remote remote, final boolean b) throws NoSuchObjectException {
        return ObjectTable.unexportObject(remote, b);
    }
    
    private static Remote exportObject(final Remote remote, final ActivatableServerRef ref) throws RemoteException {
        if (remote instanceof Activatable) {
            ((Activatable)remote).ref = ref;
        }
        return ref.exportObject(remote, null, false);
    }
}
