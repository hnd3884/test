package com.sun.jmx.remote.internal;

import java.rmi.server.Operation;
import java.rmi.server.RemoteObject;
import java.rmi.RemoteException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteCall;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.rmi.server.RemoteRef;

public class ProxyRef implements RemoteRef
{
    private static final long serialVersionUID = -6503061366316814723L;
    protected RemoteRef ref;
    
    public ProxyRef(final RemoteRef ref) {
        this.ref = ref;
    }
    
    @Override
    public void readExternal(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
        this.ref.readExternal(objectInput);
    }
    
    @Override
    public void writeExternal(final ObjectOutput objectOutput) throws IOException {
        this.ref.writeExternal(objectOutput);
    }
    
    @Deprecated
    @Override
    public void invoke(final RemoteCall remoteCall) throws Exception {
        this.ref.invoke(remoteCall);
    }
    
    @Override
    public Object invoke(final Remote remote, final Method method, final Object[] array, final long n) throws Exception {
        return this.ref.invoke(remote, method, array, n);
    }
    
    @Deprecated
    @Override
    public void done(final RemoteCall remoteCall) throws RemoteException {
        this.ref.done(remoteCall);
    }
    
    @Override
    public String getRefClass(final ObjectOutput objectOutput) {
        return this.ref.getRefClass(objectOutput);
    }
    
    @Deprecated
    @Override
    public RemoteCall newCall(final RemoteObject remoteObject, final Operation[] array, final int n, final long n2) throws RemoteException {
        return this.ref.newCall(remoteObject, array, n, n2);
    }
    
    @Override
    public boolean remoteEquals(final RemoteRef remoteRef) {
        return this.ref.remoteEquals(remoteRef);
    }
    
    @Override
    public int remoteHashCode() {
        return this.ref.remoteHashCode();
    }
    
    @Override
    public String remoteToString() {
        return this.ref.remoteToString();
    }
}
