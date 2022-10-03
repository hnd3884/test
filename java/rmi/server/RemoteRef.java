package java.rmi.server;

import java.io.ObjectOutput;
import java.rmi.RemoteException;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.io.Externalizable;

public interface RemoteRef extends Externalizable
{
    public static final long serialVersionUID = 3632638527362204081L;
    public static final String packagePrefix = "sun.rmi.server";
    
    Object invoke(final Remote p0, final Method p1, final Object[] p2, final long p3) throws Exception;
    
    @Deprecated
    RemoteCall newCall(final RemoteObject p0, final Operation[] p1, final int p2, final long p3) throws RemoteException;
    
    @Deprecated
    void invoke(final RemoteCall p0) throws Exception;
    
    @Deprecated
    void done(final RemoteCall p0) throws RemoteException;
    
    String getRefClass(final ObjectOutput p0);
    
    int remoteHashCode();
    
    boolean remoteEquals(final RemoteRef p0);
    
    String remoteToString();
}
