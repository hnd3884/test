package sun.rmi.registry;

import java.security.ProtectionDomain;
import java.io.FilePermission;
import java.security.Permissions;
import java.security.Policy;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.PrivilegedAction;
import java.security.PermissionCollection;
import java.text.MessageFormat;
import sun.rmi.server.LoaderHandler;
import java.net.URLClassLoader;
import sun.misc.URLClassPath;
import java.rmi.RMISecurityManager;
import java.rmi.server.UID;
import java.rmi.activation.ActivationID;
import sun.rmi.server.UnicastRef;
import java.lang.reflect.Proxy;
import java.util.MissingResourceException;
import java.rmi.server.ServerNotActiveException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.security.PrivilegedActionException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.net.SocketPermission;
import java.security.Permission;
import sun.rmi.server.UnicastServerRef;
import sun.rmi.server.UnicastServerRef2;
import sun.rmi.transport.LiveRef;
import java.security.PrivilegedExceptionAction;
import java.rmi.RemoteException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import sun.rmi.runtime.Log;
import java.security.Security;
import sun.misc.ObjectInputFilter;
import java.util.ResourceBundle;
import java.rmi.server.ObjID;
import java.net.InetAddress;
import java.rmi.Remote;
import java.util.Hashtable;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;

public class RegistryImpl extends RemoteServer implements Registry
{
    private static final long serialVersionUID = 4666870661827494597L;
    private Hashtable<String, Remote> bindings;
    private static Hashtable<InetAddress, InetAddress> allowedAccessCache;
    private static RegistryImpl registry;
    private static ObjID id;
    private static ResourceBundle resources;
    private static final String REGISTRY_FILTER_PROPNAME = "sun.rmi.registry.registryFilter";
    private static final int REGISTRY_MAX_DEPTH = 20;
    private static final int REGISTRY_MAX_ARRAY_SIZE = 1000000;
    private static final ObjectInputFilter registryFilter;
    
    private static ObjectInputFilter initRegistryFilter() {
        Object filter2 = null;
        String s = System.getProperty("sun.rmi.registry.registryFilter");
        if (s == null) {
            s = Security.getProperty("sun.rmi.registry.registryFilter");
        }
        if (s != null) {
            filter2 = ObjectInputFilter.Config.createFilter2(s);
            final Log log = Log.getLog("sun.rmi.registry", "registry", -1);
            if (log.isLoggable(Log.BRIEF)) {
                log.log(Log.BRIEF, "registryFilter = " + filter2);
            }
        }
        return (ObjectInputFilter)filter2;
    }
    
    public RegistryImpl(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
        this(n, rmiClientSocketFactory, rmiServerSocketFactory, RegistryImpl::registryFilter);
    }
    
    public RegistryImpl(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory, final ObjectInputFilter objectInputFilter) throws RemoteException {
        this.bindings = new Hashtable<String, Remote>(101);
        if (n == 1099 && System.getSecurityManager() != null) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws RemoteException {
                        RegistryImpl.this.setup(new UnicastServerRef2(new LiveRef(RegistryImpl.id, n, rmiClientSocketFactory, rmiServerSocketFactory), objectInputFilter));
                        return null;
                    }
                }, null, new SocketPermission("localhost:" + n, "listen,accept"));
                return;
            }
            catch (final PrivilegedActionException ex) {
                throw (RemoteException)ex.getException();
            }
        }
        this.setup(new UnicastServerRef2(new LiveRef(RegistryImpl.id, n, rmiClientSocketFactory, rmiServerSocketFactory), objectInputFilter));
    }
    
    public RegistryImpl(final int n) throws RemoteException {
        this.bindings = new Hashtable<String, Remote>(101);
        if (n == 1099 && System.getSecurityManager() != null) {
            try {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws RemoteException {
                        RegistryImpl.this.setup(new UnicastServerRef(new LiveRef(RegistryImpl.id, n), filterInfo -> registryFilter(filterInfo)));
                        return null;
                    }
                }, null, new SocketPermission("localhost:" + n, "listen,accept"));
                return;
            }
            catch (final PrivilegedActionException ex) {
                throw (RemoteException)ex.getException();
            }
        }
        this.setup(new UnicastServerRef(new LiveRef(RegistryImpl.id, n), RegistryImpl::registryFilter));
    }
    
    private void setup(final UnicastServerRef ref) throws RemoteException {
        ((UnicastServerRef)(this.ref = ref)).exportObject(this, null, true);
    }
    
    @Override
    public Remote lookup(final String s) throws RemoteException, NotBoundException {
        synchronized (this.bindings) {
            final Remote remote = this.bindings.get(s);
            if (remote == null) {
                throw new NotBoundException(s);
            }
            return remote;
        }
    }
    
    @Override
    public void bind(final String s, final Remote remote) throws RemoteException, AlreadyBoundException, AccessException {
        synchronized (this.bindings) {
            if (this.bindings.get(s) != null) {
                throw new AlreadyBoundException(s);
            }
            this.bindings.put(s, remote);
        }
    }
    
    @Override
    public void unbind(final String s) throws RemoteException, NotBoundException, AccessException {
        synchronized (this.bindings) {
            if (this.bindings.get(s) == null) {
                throw new NotBoundException(s);
            }
            this.bindings.remove(s);
        }
    }
    
    @Override
    public void rebind(final String s, final Remote remote) throws RemoteException, AccessException {
        this.bindings.put(s, remote);
    }
    
    @Override
    public String[] list() throws RemoteException {
        final String[] array;
        synchronized (this.bindings) {
            int size = this.bindings.size();
            array = new String[size];
            final Enumeration<String> keys = this.bindings.keys();
            while (--size >= 0) {
                array[size] = keys.nextElement();
            }
        }
        return array;
    }
    
    public static void checkAccess(final String s) throws AccessException {
        try {
            final String clientHost = RemoteServer.getClientHost();
            InetAddress inetAddress;
            try {
                inetAddress = AccessController.doPrivileged((PrivilegedExceptionAction<InetAddress>)new PrivilegedExceptionAction<InetAddress>() {
                    @Override
                    public InetAddress run() throws UnknownHostException {
                        return InetAddress.getByName(clientHost);
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                throw (UnknownHostException)ex.getException();
            }
            if (RegistryImpl.allowedAccessCache.get(inetAddress) == null) {
                if (inetAddress.isAnyLocalAddress()) {
                    throw new AccessException(s + " disallowed; origin unknown");
                }
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                        @Override
                        public Void run() throws IOException {
                            new ServerSocket(0, 10, inetAddress).close();
                            RegistryImpl.allowedAccessCache.put(inetAddress, inetAddress);
                            return null;
                        }
                    });
                }
                catch (final PrivilegedActionException ex2) {
                    throw new AccessException(s + " disallowed; origin " + inetAddress + " is non-local host");
                }
            }
        }
        catch (final ServerNotActiveException ex3) {}
        catch (final UnknownHostException ex4) {
            throw new AccessException(s + " disallowed; origin is unknown host");
        }
    }
    
    public static ObjID getID() {
        return RegistryImpl.id;
    }
    
    private static String getTextResource(final String s) {
        if (RegistryImpl.resources == null) {
            try {
                RegistryImpl.resources = ResourceBundle.getBundle("sun.rmi.registry.resources.rmiregistry");
            }
            catch (final MissingResourceException ex) {}
            if (RegistryImpl.resources == null) {
                return "[missing resource file: " + s + "]";
            }
        }
        String string = null;
        try {
            string = RegistryImpl.resources.getString(s);
        }
        catch (final MissingResourceException ex2) {}
        if (string == null) {
            return "[missing resource: " + s + "]";
        }
        return string;
    }
    
    private static ObjectInputFilter.Status registryFilter(final ObjectInputFilter.FilterInfo filterInfo) {
        if (RegistryImpl.registryFilter != null) {
            final ObjectInputFilter.Status checkInput = RegistryImpl.registryFilter.checkInput(filterInfo);
            if (checkInput != ObjectInputFilter.Status.UNDECIDED) {
                return checkInput;
            }
        }
        if (filterInfo.depth() > 20L) {
            return ObjectInputFilter.Status.REJECTED;
        }
        final Class<?> serialClass = filterInfo.serialClass();
        if (serialClass == null) {
            return ObjectInputFilter.Status.UNDECIDED;
        }
        if (serialClass.isArray()) {
            return (filterInfo.arrayLength() >= 0L && filterInfo.arrayLength() > 1000000L) ? ObjectInputFilter.Status.REJECTED : ObjectInputFilter.Status.UNDECIDED;
        }
        if (String.class == serialClass || Number.class.isAssignableFrom(serialClass) || Remote.class.isAssignableFrom(serialClass) || Proxy.class.isAssignableFrom(serialClass) || UnicastRef.class.isAssignableFrom(serialClass) || RMIClientSocketFactory.class.isAssignableFrom(serialClass) || RMIServerSocketFactory.class.isAssignableFrom(serialClass) || ActivationID.class.isAssignableFrom(serialClass) || UID.class.isAssignableFrom(serialClass)) {
            return ObjectInputFilter.Status.ALLOWED;
        }
        return ObjectInputFilter.Status.REJECTED;
    }
    
    public static void main(final String[] array) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }
        try {
            String property = System.getProperty("env.class.path");
            if (property == null) {
                property = ".";
            }
            final URLClassLoader contextClassLoader = new URLClassLoader(URLClassPath.pathToURLs(property));
            LoaderHandler.registerCodebaseLoader(contextClassLoader);
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            final int n = (array.length >= 1) ? Integer.parseInt(array[0]) : 1099;
        Label_0111:
            while (true) {
                try {
                    RegistryImpl.registry = AccessController.doPrivileged((PrivilegedExceptionAction<RegistryImpl>)new PrivilegedExceptionAction<RegistryImpl>() {
                        @Override
                        public RegistryImpl run() throws RemoteException {
                            return new RegistryImpl(n);
                        }
                    }, getAccessControlContext(n));
                    break Label_0111;
                }
                catch (final PrivilegedActionException ex) {
                    throw (RemoteException)ex.getException();
                }
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(Long.MAX_VALUE);
                        }
                    }
                    catch (final InterruptedException ex2) {
                        continue;
                    }
                    continue Label_0111;
                }
                break;
            }
        }
        catch (final NumberFormatException ex3) {
            System.err.println(MessageFormat.format(getTextResource("rmiregistry.port.badnumber"), array[0]));
            System.err.println(MessageFormat.format(getTextResource("rmiregistry.usage"), "rmiregistry"));
        }
        catch (final Exception ex4) {
            ex4.printStackTrace();
        }
        System.exit(1);
    }
    
    private static AccessControlContext getAccessControlContext(final int n) {
        final PermissionCollection collection = AccessController.doPrivileged((PrivilegedAction<PermissionCollection>)new PrivilegedAction<PermissionCollection>() {
            @Override
            public PermissionCollection run() {
                final CodeSource codeSource = new CodeSource(null, (Certificate[])null);
                final Policy policy = Policy.getPolicy();
                if (policy != null) {
                    return policy.getPermissions(codeSource);
                }
                return new Permissions();
            }
        });
        collection.add(new SocketPermission("*", "connect,accept"));
        collection.add(new SocketPermission("localhost:" + n, "listen,accept"));
        collection.add(new RuntimePermission("accessClassInPackage.sun.jvmstat.*"));
        collection.add(new RuntimePermission("accessClassInPackage.sun.jvm.hotspot.*"));
        collection.add(new FilePermission("<<ALL FILES>>", "read"));
        return new AccessControlContext(new ProtectionDomain[] { new ProtectionDomain(new CodeSource(null, (Certificate[])null), collection) });
    }
    
    static {
        RegistryImpl.allowedAccessCache = new Hashtable<InetAddress, InetAddress>(3);
        RegistryImpl.id = new ObjID(0);
        RegistryImpl.resources = null;
        registryFilter = AccessController.doPrivileged(RegistryImpl::initRegistryFilter);
    }
}
