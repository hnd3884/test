package com.sun.corba.se.impl.presentation.rmi;

import com.sun.corba.se.impl.util.JDKBridge;
import com.sun.corba.se.impl.util.RepositoryId;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.omg.CORBA.portable.OutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.Object;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.Delegate;
import com.sun.corba.se.impl.ior.StubIORImpl;
import java.io.Serializable;
import com.sun.corba.se.spi.presentation.rmi.DynamicStub;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public class DynamicStubImpl extends ObjectImpl implements DynamicStub, Serializable
{
    private static final long serialVersionUID = 4852612040012087675L;
    private String[] typeIds;
    private StubIORImpl ior;
    private DynamicStub self;
    
    public void setSelf(final DynamicStub self) {
        this.self = self;
    }
    
    public DynamicStub getSelf() {
        return this.self;
    }
    
    public DynamicStubImpl(final String[] typeIds) {
        this.self = null;
        this.typeIds = typeIds;
        this.ior = null;
    }
    
    @Override
    public void setDelegate(final Delegate delegate) {
        this._set_delegate(delegate);
    }
    
    @Override
    public Delegate getDelegate() {
        return this._get_delegate();
    }
    
    @Override
    public ORB getORB() {
        return this._orb();
    }
    
    @Override
    public String[] _ids() {
        return this.typeIds;
    }
    
    @Override
    public String[] getTypeIds() {
        return this._ids();
    }
    
    @Override
    public void connect(final ORB orb) throws RemoteException {
        this.ior = StubConnectImpl.connect(this.ior, this.self, this, orb);
    }
    
    @Override
    public boolean isLocal() {
        return this._is_local();
    }
    
    @Override
    public OutputStream request(final String s, final boolean b) {
        return this._request(s, b);
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        (this.ior = new StubIORImpl()).doRead(objectInputStream);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.ior == null) {
            this.ior = new StubIORImpl(this);
        }
        this.ior.doWrite(objectOutputStream);
    }
    
    public java.lang.Object readResolve() {
        final String className = RepositoryId.cache.getId(this.ior.getRepositoryId()).getClassName();
        Class loadClass = null;
        try {
            loadClass = JDKBridge.loadClass(className, null, null);
        }
        catch (final ClassNotFoundException ex) {}
        return ((InvocationHandlerFactoryImpl)com.sun.corba.se.spi.orb.ORB.getPresentationManager().getClassData(loadClass).getInvocationHandlerFactory()).getInvocationHandler(this);
    }
}
