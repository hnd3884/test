package java.rmi.server;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.rmi.MarshalException;
import java.io.ObjectOutputStream;
import sun.rmi.server.Util;
import java.rmi.NoSuchObjectException;
import sun.rmi.transport.ObjectTable;
import java.lang.reflect.Proxy;
import java.io.Serializable;
import java.rmi.Remote;

public abstract class RemoteObject implements Remote, Serializable
{
    protected transient RemoteRef ref;
    private static final long serialVersionUID = -3215090123894869218L;
    
    protected RemoteObject() {
        this.ref = null;
    }
    
    protected RemoteObject(final RemoteRef ref) {
        this.ref = ref;
    }
    
    public RemoteRef getRef() {
        return this.ref;
    }
    
    public static Remote toStub(final Remote remote) throws NoSuchObjectException {
        if (remote instanceof RemoteStub || (remote != null && Proxy.isProxyClass(remote.getClass()) && Proxy.getInvocationHandler(remote) instanceof RemoteObjectInvocationHandler)) {
            return remote;
        }
        return ObjectTable.getStub(remote);
    }
    
    @Override
    public int hashCode() {
        return (this.ref == null) ? super.hashCode() : this.ref.remoteHashCode();
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RemoteObject)) {
            return o != null && o.equals(this);
        }
        if (this.ref == null) {
            return o == this;
        }
        return this.ref.remoteEquals(((RemoteObject)o).ref);
    }
    
    @Override
    public String toString() {
        final String unqualifiedName = Util.getUnqualifiedName(this.getClass());
        return (this.ref == null) ? unqualifiedName : (unqualifiedName + "[" + this.ref.remoteToString() + "]");
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        if (this.ref == null) {
            throw new MarshalException("Invalid remote object");
        }
        final String refClass = this.ref.getRefClass(objectOutputStream);
        if (refClass == null || refClass.length() == 0) {
            objectOutputStream.writeUTF("");
            objectOutputStream.writeObject(this.ref);
        }
        else {
            objectOutputStream.writeUTF(refClass);
            this.ref.writeExternal(objectOutputStream);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final String utf = objectInputStream.readUTF();
        if (utf == null || utf.length() == 0) {
            this.ref = (RemoteRef)objectInputStream.readObject();
        }
        else {
            final String string = "sun.rmi.server." + utf;
            final Class<?> forName = Class.forName(string);
            try {
                this.ref = (RemoteRef)forName.newInstance();
            }
            catch (final InstantiationException ex) {
                throw new ClassNotFoundException(string, ex);
            }
            catch (final IllegalAccessException ex2) {
                throw new ClassNotFoundException(string, ex2);
            }
            catch (final ClassCastException ex3) {
                throw new ClassNotFoundException(string, ex3);
            }
            this.ref.readExternal(objectInputStream);
        }
    }
}
