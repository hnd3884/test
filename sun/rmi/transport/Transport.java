package sun.rmi.transport;

import java.security.PermissionCollection;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.Permission;
import java.security.Permissions;
import java.rmi.server.LogStream;
import java.io.ObjectOutput;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.RemoteServer;
import sun.rmi.server.UnicastServerRef;
import java.security.PrivilegedActionException;
import java.rmi.Remote;
import sun.rmi.server.Dispatcher;
import java.security.PrivilegedExceptionAction;
import java.rmi.NoSuchObjectException;
import java.io.IOException;
import java.rmi.MarshalException;
import java.rmi.server.RemoteCall;
import java.rmi.RemoteException;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import java.security.AccessControlContext;
import java.rmi.server.ObjID;
import sun.rmi.runtime.Log;

public abstract class Transport
{
    static final int logLevel;
    static final Log transportLog;
    private static final ThreadLocal<Transport> currentTransport;
    private static final ObjID dgcID;
    private static final AccessControlContext SETCCL_ACC;
    
    private static String getLogLevel() {
        return AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.transport.logLevel"));
    }
    
    public abstract Channel getChannel(final Endpoint p0);
    
    public abstract void free(final Endpoint p0);
    
    public void exportObject(final Target target) throws RemoteException {
        target.setExportedTransport(this);
        ObjectTable.putTarget(target);
    }
    
    protected void targetUnexported() {
    }
    
    static Transport currentTransport() {
        return Transport.currentTransport.get();
    }
    
    protected abstract void checkAcceptPermission(final AccessControlContext p0);
    
    private static void setContextClassLoader(final ClassLoader classLoader) {
        AccessController.doPrivileged(() -> {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            return null;
        }, Transport.SETCCL_ACC);
    }
    
    public boolean serviceCall(final RemoteCall remoteCall) {
        try {
            ObjID read;
            try {
                read = ObjID.read(remoteCall.getInputStream());
            }
            catch (final IOException ex) {
                throw new MarshalException("unable to read objID", ex);
            }
            final Target target = ObjectTable.getTarget(new ObjectEndpoint(read, read.equals(Transport.dgcID) ? null : this));
            final Remote impl;
            if (target == null || (impl = target.getImpl()) == null) {
                throw new NoSuchObjectException("no such object in table");
            }
            final Dispatcher dispatcher = target.getDispatcher();
            target.incrementCallCount();
            try {
                Transport.transportLog.log(Log.VERBOSE, "call dispatcher");
                final AccessControlContext accessControlContext = target.getAccessControlContext();
                final ClassLoader contextClassLoader = target.getContextClassLoader();
                final ClassLoader contextClassLoader2 = Thread.currentThread().getContextClassLoader();
                try {
                    setContextClassLoader(contextClassLoader);
                    Transport.currentTransport.set(this);
                    try {
                        AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                            @Override
                            public Void run() throws IOException {
                                Transport.this.checkAcceptPermission(accessControlContext);
                                dispatcher.dispatch(impl, remoteCall);
                                return null;
                            }
                        }, accessControlContext);
                    }
                    catch (final PrivilegedActionException ex2) {
                        throw (IOException)ex2.getException();
                    }
                }
                finally {
                    setContextClassLoader(contextClassLoader2);
                    Transport.currentTransport.set(null);
                }
            }
            catch (final IOException ex3) {
                Transport.transportLog.log(Log.BRIEF, "exception thrown by dispatcher: ", ex3);
                return false;
            }
            finally {
                target.decrementCallCount();
            }
        }
        catch (final RemoteException ex4) {
            if (UnicastServerRef.callLog.isLoggable(Log.BRIEF)) {
                String string = "";
                try {
                    string = "[" + RemoteServer.getClientHost() + "] ";
                }
                catch (final ServerNotActiveException ex5) {}
                UnicastServerRef.callLog.log(Log.BRIEF, string + "exception: ", ex4);
            }
            try {
                final ObjectOutput resultStream = remoteCall.getResultStream(false);
                UnicastServerRef.clearStackTraces(ex4);
                resultStream.writeObject(ex4);
                remoteCall.releaseOutputStream();
            }
            catch (final IOException ex6) {
                Transport.transportLog.log(Log.BRIEF, "exception thrown marshalling exception: ", ex6);
                return false;
            }
        }
        return true;
    }
    
    static {
        logLevel = LogStream.parseLevel(getLogLevel());
        transportLog = Log.getLog("sun.rmi.transport.misc", "transport", Transport.logLevel);
        currentTransport = new ThreadLocal<Transport>();
        dgcID = new ObjID(2);
        final Permissions permissions = new Permissions();
        permissions.add(new RuntimePermission("setContextClassLoader"));
        SETCCL_ACC = new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(null, permissions) });
    }
}
