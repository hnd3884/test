package javax.management.remote.rmi;

import java.io.ObjectStreamClass;
import java.lang.reflect.Method;
import sun.rmi.server.DeserializationChecker;
import javax.security.auth.Subject;
import java.rmi.server.RemoteObject;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.server.UnicastServerRef;
import com.sun.jmx.remote.util.EnvHelp;
import com.sun.jmx.remote.internal.RMIExporter;
import java.rmi.Remote;
import java.io.IOException;
import java.util.List;
import sun.reflect.misc.ReflectUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;

public class RMIJRMPServerImpl extends RMIServerImpl
{
    private final ExportedWrapper exportedWrapper;
    private final int port;
    private final RMIClientSocketFactory csf;
    private final RMIServerSocketFactory ssf;
    private final Map<String, ?> env;
    
    public RMIJRMPServerImpl(final int port, final RMIClientSocketFactory csf, final RMIServerSocketFactory ssf, final Map<String, ?> map) throws IOException {
        super(map);
        if (port < 0) {
            throw new IllegalArgumentException("Negative port: " + port);
        }
        this.port = port;
        this.csf = csf;
        this.ssf = ssf;
        this.env = ((map == null) ? Collections.emptyMap() : map);
        final String[] array = (Object)this.env.get("jmx.remote.rmi.server.credential.types");
        List list = null;
        if (array != null) {
            list = new ArrayList();
            for (final String s : array) {
                if (s == null) {
                    throw new IllegalArgumentException("A credential type is null.");
                }
                ReflectUtil.checkPackageAccess(s);
                list.add(s);
            }
        }
        this.exportedWrapper = ((list != null) ? new ExportedWrapper((RMIServer)this, (List)list) : null);
    }
    
    @Override
    protected void export() throws IOException {
        if (this.exportedWrapper != null) {
            this.export(this.exportedWrapper);
        }
        else {
            this.export(this);
        }
    }
    
    private void export(final Remote remote) throws RemoteException {
        final RMIExporter rmiExporter = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
        final boolean serverDaemon = EnvHelp.isServerDaemon(this.env);
        if (serverDaemon && rmiExporter != null) {
            throw new IllegalArgumentException("If jmx.remote.x.daemon is specified as true, com.sun.jmx.remote.rmi.exporter cannot be used to specify an exporter!");
        }
        if (serverDaemon) {
            if (this.csf == null && this.ssf == null) {
                new UnicastServerRef(this.port).exportObject(remote, null, true);
            }
            else {
                new UnicastServerRef2(this.port, this.csf, this.ssf).exportObject(remote, null, true);
            }
        }
        else if (rmiExporter != null) {
            rmiExporter.exportObject(remote, this.port, this.csf, this.ssf);
        }
        else {
            UnicastRemoteObject.exportObject(remote, this.port, this.csf, this.ssf);
        }
    }
    
    private void unexport(final Remote remote, final boolean b) throws NoSuchObjectException {
        final RMIExporter rmiExporter = (RMIExporter)this.env.get("com.sun.jmx.remote.rmi.exporter");
        if (rmiExporter == null) {
            UnicastRemoteObject.unexportObject(remote, b);
        }
        else {
            rmiExporter.unexportObject(remote, b);
        }
    }
    
    @Override
    protected String getProtocol() {
        return "rmi";
    }
    
    @Override
    public Remote toStub() throws IOException {
        if (this.exportedWrapper != null) {
            return RemoteObject.toStub(this.exportedWrapper);
        }
        return RemoteObject.toStub(this);
    }
    
    @Override
    protected RMIConnection makeClient(final String s, final Subject subject) throws IOException {
        if (s == null) {
            throw new NullPointerException("Null connectionId");
        }
        final RMIConnectionImpl rmiConnectionImpl = new RMIConnectionImpl(this, s, this.getDefaultClassLoader(), subject, this.env);
        this.export(rmiConnectionImpl);
        return rmiConnectionImpl;
    }
    
    @Override
    protected void closeClient(final RMIConnection rmiConnection) throws IOException {
        this.unexport(rmiConnection, true);
    }
    
    @Override
    protected void closeServer() throws IOException {
        if (this.exportedWrapper != null) {
            this.unexport(this.exportedWrapper, true);
        }
        else {
            this.unexport(this, true);
        }
    }
    
    private static class ExportedWrapper implements RMIServer, DeserializationChecker
    {
        private final RMIServer impl;
        private final List<String> allowedTypes;
        
        private ExportedWrapper(final RMIServer impl, final List<String> allowedTypes) {
            this.impl = impl;
            this.allowedTypes = allowedTypes;
        }
        
        @Override
        public String getVersion() throws RemoteException {
            return this.impl.getVersion();
        }
        
        @Override
        public RMIConnection newClient(final Object o) throws IOException {
            return this.impl.newClient(o);
        }
        
        @Override
        public void check(final Method method, final ObjectStreamClass objectStreamClass, final int n, final int n2) {
            final String name = objectStreamClass.getName();
            if (!this.allowedTypes.contains(name)) {
                throw new ClassCastException("Unsupported type: " + name);
            }
        }
        
        @Override
        public void checkProxyClass(final Method method, final String[] array, final int n, final int n2) {
            if (array != null && array.length > 0) {
                for (final String s : array) {
                    if (!this.allowedTypes.contains(s)) {
                        throw new ClassCastException("Unsupported type: " + s);
                    }
                }
            }
        }
    }
}
