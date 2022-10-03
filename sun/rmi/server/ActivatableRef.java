package sun.rmi.server;

import java.rmi.UnmarshalException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.rmi.server.RemoteCall;
import java.rmi.server.Operation;
import java.rmi.server.RemoteObject;
import java.rmi.activation.ActivationException;
import java.rmi.activation.ActivateFailedException;
import java.rmi.activation.UnknownObjectException;
import java.lang.reflect.Proxy;
import java.rmi.server.RemoteObjectInvocationHandler;
import java.rmi.server.RemoteStub;
import java.rmi.RemoteException;
import java.rmi.ServerException;
import java.rmi.ServerError;
import java.rmi.MarshalException;
import java.rmi.ConnectIOException;
import java.rmi.UnknownHostException;
import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.StubNotFoundException;
import java.rmi.server.RMIClassLoader;
import java.rmi.Remote;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationID;
import java.rmi.server.RemoteRef;

public class ActivatableRef implements RemoteRef
{
    private static final long serialVersionUID = 7579060052569229166L;
    protected ActivationID id;
    protected RemoteRef ref;
    transient boolean force;
    private static final int MAX_RETRIES = 3;
    private static final String versionComplaint = "activation requires 1.2 stubs";
    
    public ActivatableRef() {
        this.force = false;
    }
    
    public ActivatableRef(final ActivationID id, final RemoteRef ref) {
        this.force = false;
        this.id = id;
        this.ref = ref;
    }
    
    public static Remote getStub(final ActivationDesc activationDesc, final ActivationID activationID) throws StubNotFoundException {
        final String className = activationDesc.getClassName();
        try {
            return Util.createProxy(RMIClassLoader.loadClass(activationDesc.getLocation(), className), new ActivatableRef(activationID, null), false);
        }
        catch (final IllegalArgumentException ex) {
            throw new StubNotFoundException("class implements an illegal remote interface", ex);
        }
        catch (final ClassNotFoundException ex2) {
            throw new StubNotFoundException("unable to load class: " + className, ex2);
        }
        catch (final MalformedURLException ex3) {
            throw new StubNotFoundException("malformed URL", ex3);
        }
    }
    
    @Override
    public Object invoke(final Remote remote, final Method method, final Object[] array, final long n) throws Exception {
        boolean b = false;
        RemoteException ex = null;
        RemoteRef remoteRef;
        synchronized (this) {
            if (this.ref == null) {
                remoteRef = this.activate(b);
                b = true;
            }
            else {
                remoteRef = this.ref;
            }
        }
        for (int i = 3; i > 0; --i) {
            try {
                return remoteRef.invoke(remote, method, array, n);
            }
            catch (final NoSuchObjectException ex2) {
                ex = ex2;
            }
            catch (final ConnectException ex3) {
                ex = ex3;
            }
            catch (final UnknownHostException ex4) {
                ex = ex4;
            }
            catch (final ConnectIOException ex5) {
                ex = ex5;
            }
            catch (final MarshalException ex6) {
                throw ex6;
            }
            catch (final ServerError serverError) {
                throw serverError;
            }
            catch (final ServerException ex7) {
                throw ex7;
            }
            catch (final RemoteException ex8) {
                synchronized (this) {
                    if (remoteRef == this.ref) {
                        this.ref = null;
                    }
                }
                throw ex8;
            }
            if (i > 1) {
                synchronized (this) {
                    if (remoteRef.remoteEquals(this.ref) || this.ref == null) {
                        RemoteRef remoteRef2 = this.activate(b);
                        if (remoteRef2.remoteEquals(remoteRef) && ex instanceof NoSuchObjectException && !b) {
                            remoteRef2 = this.activate(true);
                        }
                        remoteRef = remoteRef2;
                        b = true;
                    }
                    else {
                        remoteRef = this.ref;
                        b = false;
                    }
                }
            }
        }
        throw ex;
    }
    
    private synchronized RemoteRef getRef() throws RemoteException {
        if (this.ref == null) {
            this.ref = this.activate(false);
        }
        return this.ref;
    }
    
    private RemoteRef activate(final boolean b) throws RemoteException {
        assert Thread.holdsLock(this);
        this.ref = null;
        try {
            final Remote activate = this.id.activate(b);
            ActivatableRef activatableRef;
            if (activate instanceof RemoteStub) {
                activatableRef = (ActivatableRef)((RemoteStub)activate).getRef();
            }
            else {
                activatableRef = (ActivatableRef)((RemoteObjectInvocationHandler)Proxy.getInvocationHandler(activate)).getRef();
            }
            return this.ref = activatableRef.ref;
        }
        catch (final ConnectException ex) {
            throw new ConnectException("activation failed", ex);
        }
        catch (final RemoteException ex2) {
            throw new ConnectIOException("activation failed", ex2);
        }
        catch (final UnknownObjectException ex3) {
            throw new NoSuchObjectException("object not registered");
        }
        catch (final ActivationException ex4) {
            throw new ActivateFailedException("activation failed", ex4);
        }
    }
    
    @Override
    public synchronized RemoteCall newCall(final RemoteObject remoteObject, final Operation[] array, final int n, final long n2) throws RemoteException {
        throw new UnsupportedOperationException("activation requires 1.2 stubs");
    }
    
    @Override
    public void invoke(final RemoteCall remoteCall) throws Exception {
        throw new UnsupportedOperationException("activation requires 1.2 stubs");
    }
    
    @Override
    public void done(final RemoteCall remoteCall) throws RemoteException {
        throw new UnsupportedOperationException("activation requires 1.2 stubs");
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return "ActivatableRef";
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        final RemoteRef ref = this.ref;
        objectOutput.writeObject(this.id);
        if (ref == null) {
            objectOutput.writeUTF("");
        }
        else {
            objectOutput.writeUTF(ref.getRefClass(objectOutput));
            ref.writeExternal(objectOutput);
        }
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.id = (ActivationID)objectInput.readObject();
        this.ref = null;
        final String utf = objectInput.readUTF();
        if (utf.equals("")) {
            return;
        }
        try {
            (this.ref = (RemoteRef)Class.forName("sun.rmi.server." + utf).newInstance()).readExternal(objectInput);
        }
        catch (final InstantiationException ex) {
            throw new UnmarshalException("Unable to create remote reference", ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new UnmarshalException("Illegal access creating remote reference");
        }
    }
    
    @Override
    public String remoteToString() {
        return Util.getUnqualifiedName(this.getClass()) + " [remoteRef: " + this.ref + "]";
    }
    
    @Override
    public int remoteHashCode() {
        return this.id.hashCode();
    }
    
    @Override
    public boolean remoteEquals(final RemoteRef remoteRef) {
        return remoteRef instanceof ActivatableRef && this.id.equals(((ActivatableRef)remoteRef).id);
    }
}
