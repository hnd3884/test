package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.server.RemoteStub;

public final class RMIServerImpl_Stub extends RemoteStub implements RMIServer
{
    private static final long serialVersionUID = 2L;
    private static Method $method_getVersion_0;
    private static Method $method_newClient_1;
    static /* synthetic */ Class class$javax$management$remote$rmi$RMIServer;
    static /* synthetic */ Class class$java$lang$Object;
    
    static {
        try {
            RMIServerImpl_Stub.$method_getVersion_0 = ((RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer != null) ? RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer : (RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer"))).getMethod("getVersion", (Class[])new Class[0]);
            RMIServerImpl_Stub.$method_newClient_1 = ((RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer != null) ? RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer : (RMIServerImpl_Stub.class$javax$management$remote$rmi$RMIServer = class$("javax.management.remote.rmi.RMIServer"))).getMethod("newClient", (RMIServerImpl_Stub.class$java$lang$Object != null) ? RMIServerImpl_Stub.class$java$lang$Object : (RMIServerImpl_Stub.class$java$lang$Object = class$("java.lang.Object")));
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public RMIServerImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public String getVersion() throws RemoteException {
        try {
            return (String)super.ref.invoke(this, RMIServerImpl_Stub.$method_getVersion_0, null, -8081107751519807347L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
    
    public RMIConnection newClient(final Object o) throws IOException {
        try {
            return (RMIConnection)super.ref.invoke(this, RMIServerImpl_Stub.$method_newClient_1, new Object[] { o }, -1089742558549201240L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final IOException ex2) {
            throw ex2;
        }
        catch (final Exception ex3) {
            throw new UnexpectedException("undeclared checked exception", ex3);
        }
    }
}
