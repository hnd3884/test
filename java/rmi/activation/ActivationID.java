package java.rmi.activation;

import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationHandler;
import java.rmi.server.RemoteRef;
import java.io.ObjectOutput;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.io.InvalidObjectException;
import java.lang.reflect.Proxy;
import java.rmi.server.RemoteObject;
import java.io.ObjectOutputStream;
import java.security.PrivilegedActionException;
import java.rmi.UnmarshalException;
import java.rmi.RemoteException;
import java.security.AccessController;
import java.io.IOException;
import java.rmi.MarshalledObject;
import java.security.PrivilegedExceptionAction;
import java.rmi.Remote;
import java.security.AccessControlContext;
import java.rmi.server.UID;
import java.io.Serializable;

public class ActivationID implements Serializable
{
    private transient Activator activator;
    private transient UID uid;
    private static final long serialVersionUID = -4608673054848209235L;
    private static final AccessControlContext NOPERMS_ACC;
    
    public ActivationID(final Activator activator) {
        this.uid = new UID();
        this.activator = activator;
    }
    
    public Remote activate(final boolean b) throws ActivationException, UnknownObjectException, RemoteException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Remote>)new PrivilegedExceptionAction<Remote>() {
                final /* synthetic */ MarshalledObject val$mobj = ActivationID.this.activator.activate(this, b);
                
                @Override
                public Remote run() throws IOException, ClassNotFoundException {
                    return this.val$mobj.get();
                }
            }, ActivationID.NOPERMS_ACC);
        }
        catch (final PrivilegedActionException ex) {
            final Exception exception = ex.getException();
            if (exception instanceof RemoteException) {
                throw (RemoteException)exception;
            }
            throw new UnmarshalException("activation failed", exception);
        }
    }
    
    @Override
    public int hashCode() {
        return this.uid.hashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o instanceof ActivationID) {
            final ActivationID activationID = (ActivationID)o;
            return this.uid.equals(activationID.uid) && this.activator.equals(activationID.activator);
        }
        return false;
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        objectOutputStream.writeObject(this.uid);
        RemoteRef remoteRef;
        if (this.activator instanceof RemoteObject) {
            remoteRef = ((RemoteObject)this.activator).getRef();
        }
        else {
            if (!Proxy.isProxyClass(this.activator.getClass())) {
                throw new InvalidObjectException("unexpected activator type");
            }
            final InvocationHandler invocationHandler = Proxy.getInvocationHandler(this.activator);
            if (!(invocationHandler instanceof RemoteObjectInvocationHandler)) {
                throw new InvalidObjectException("unexpected invocation handler");
            }
            remoteRef = ((RemoteObjectInvocationHandler)invocationHandler).getRef();
        }
        objectOutputStream.writeUTF(remoteRef.getRefClass(objectOutputStream));
        remoteRef.writeExternal(objectOutputStream);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        this.uid = (UID)objectInputStream.readObject();
        try {
            final RemoteRef remoteRef = (RemoteRef)Class.forName("sun.rmi.server." + objectInputStream.readUTF()).asSubclass(RemoteRef.class).newInstance();
            remoteRef.readExternal(objectInputStream);
            this.activator = (Activator)Proxy.newProxyInstance(null, new Class[] { Activator.class }, new RemoteObjectInvocationHandler(remoteRef));
        }
        catch (final InstantiationException ex) {
            throw (IOException)new InvalidObjectException("Unable to create remote reference").initCause(ex);
        }
        catch (final IllegalAccessException ex2) {
            throw (IOException)new InvalidObjectException("Unable to create remote reference").initCause(ex2);
        }
    }
    
    static {
        NOPERMS_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, new Permissions()) });
    }
}
