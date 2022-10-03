package com.sun.corba.se.impl.javax.rmi.CORBA;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.portable.ObjectImpl;
import com.sun.corba.se.impl.presentation.rmi.StubConnectImpl;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import javax.rmi.CORBA.Stub;
import com.sun.corba.se.impl.ior.StubIORImpl;
import com.sun.corba.se.impl.logging.UtilSystemException;
import javax.rmi.CORBA.StubDelegate;

public class StubDelegateImpl implements StubDelegate
{
    static UtilSystemException wrapper;
    private StubIORImpl ior;
    
    public StubIORImpl getIOR() {
        return this.ior;
    }
    
    public StubDelegateImpl() {
        this.ior = null;
    }
    
    private void init(final Stub stub) {
        if (this.ior == null) {
            this.ior = new StubIORImpl(stub);
        }
    }
    
    @Override
    public int hashCode(final Stub stub) {
        this.init(stub);
        return this.ior.hashCode();
    }
    
    @Override
    public boolean equals(final Stub stub, final Object o) {
        if (stub == o) {
            return true;
        }
        if (!(o instanceof Stub)) {
            return false;
        }
        final Stub stub2 = (Stub)o;
        return stub2.hashCode() == stub.hashCode() && stub.toString().equals(stub2.toString());
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StubDelegateImpl)) {
            return false;
        }
        final StubDelegateImpl stubDelegateImpl = (StubDelegateImpl)o;
        if (this.ior == null) {
            return this.ior == stubDelegateImpl.ior;
        }
        return this.ior.equals(stubDelegateImpl.ior);
    }
    
    @Override
    public int hashCode() {
        if (this.ior == null) {
            return 0;
        }
        return this.ior.hashCode();
    }
    
    @Override
    public String toString(final Stub stub) {
        if (this.ior == null) {
            return null;
        }
        return this.ior.toString();
    }
    
    @Override
    public void connect(final Stub stub, final ORB orb) throws RemoteException {
        this.ior = StubConnectImpl.connect(this.ior, stub, stub, orb);
    }
    
    @Override
    public void readObject(final Stub stub, final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (this.ior == null) {
            this.ior = new StubIORImpl();
        }
        this.ior.doRead(objectInputStream);
    }
    
    @Override
    public void writeObject(final Stub stub, final ObjectOutputStream objectOutputStream) throws IOException {
        this.init(stub);
        this.ior.doWrite(objectOutputStream);
    }
    
    static {
        StubDelegateImpl.wrapper = UtilSystemException.get("rmiiiop");
    }
}
