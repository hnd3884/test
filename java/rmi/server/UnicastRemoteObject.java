package java.rmi.server;

import java.rmi.NoSuchObjectException;
import sun.rmi.transport.ObjectTable;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.server.UnicastServerRef;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class UnicastRemoteObject extends RemoteServer
{
    private int port;
    private RMIClientSocketFactory csf;
    private RMIServerSocketFactory ssf;
    private static final long serialVersionUID = 4974527148936298033L;
    
    protected UnicastRemoteObject() throws RemoteException {
        this(0);
    }
    
    protected UnicastRemoteObject(final int port) throws RemoteException {
        this.port = 0;
        this.csf = null;
        this.ssf = null;
        exportObject(this, this.port = port);
    }
    
    protected UnicastRemoteObject(final int port, final RMIClientSocketFactory csf, final RMIServerSocketFactory ssf) throws RemoteException {
        this.port = 0;
        this.csf = null;
        this.ssf = null;
        exportObject(this, this.port = port, this.csf = csf, this.ssf = ssf);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.reexport();
    }
    
    public Object clone() throws CloneNotSupportedException {
        try {
            final UnicastRemoteObject unicastRemoteObject = (UnicastRemoteObject)super.clone();
            unicastRemoteObject.reexport();
            return unicastRemoteObject;
        }
        catch (final RemoteException ex) {
            throw new ServerCloneException("Clone failed", ex);
        }
    }
    
    private void reexport() throws RemoteException {
        if (this.csf == null && this.ssf == null) {
            exportObject(this, this.port);
        }
        else {
            exportObject(this, this.port, this.csf, this.ssf);
        }
    }
    
    @Deprecated
    public static RemoteStub exportObject(final Remote remote) throws RemoteException {
        return (RemoteStub)exportObject(remote, new UnicastServerRef(true));
    }
    
    public static Remote exportObject(final Remote remote, final int n) throws RemoteException {
        return exportObject(remote, new UnicastServerRef(n));
    }
    
    public static Remote exportObject(final Remote remote, final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        return exportObject(remote, new UnicastServerRef2(n, rmiClientSocketFactory, rmiServerSocketFactory));
    }
    
    public static boolean unexportObject(final Remote remote, final boolean b) throws NoSuchObjectException {
        return ObjectTable.unexportObject(remote, b);
    }
    
    private static Remote exportObject(final Remote remote, final UnicastServerRef ref) throws RemoteException {
        if (remote instanceof UnicastRemoteObject) {
            ((UnicastRemoteObject)remote).ref = ref;
        }
        return ref.exportObject(remote, null, false);
    }
}
