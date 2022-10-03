package sun.rmi.server;

import java.net.SocketException;
import java.net.Socket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.security.Permissions;
import java.net.URL;
import java.security.CodeSource;
import java.security.cert.Certificate;
import java.security.AllPermission;
import java.security.Policy;
import sun.security.provider.PolicyFile;
import java.security.PermissionCollection;
import com.sun.rmi.rmid.ExecPermission;
import java.security.AccessControlException;
import java.security.Permission;
import com.sun.rmi.rmid.ExecOptionPermission;
import java.rmi.ConnectIOException;
import java.rmi.ConnectException;
import java.rmi.NoSuchObjectException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationDesc;
import java.io.ObjectInput;
import java.rmi.MarshalledObject;
import sun.rmi.transport.LiveRef;
import java.rmi.server.ObjID;
import java.rmi.server.RemoteServer;
import java.rmi.AlreadyBoundException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import sun.rmi.registry.RegistryImpl;
import java.rmi.server.RMIClassLoader;
import java.util.MissingResourceException;
import java.net.ServerSocket;
import java.rmi.activation.ActivationGroup;
import sun.security.action.GetPropertyAction;
import sun.security.action.GetBooleanAction;
import java.util.Date;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.channels.ServerSocketChannel;
import java.security.PrivilegedExceptionAction;
import java.nio.channels.Channel;
import java.text.MessageFormat;
import java.io.File;
import java.rmi.RemoteException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.UnknownObjectException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.activation.ActivationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.activation.UnknownGroupException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.Remote;
import java.rmi.server.RemoteObject;
import sun.rmi.log.LogHandler;
import java.rmi.server.RMIServerSocketFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.util.ResourceBundle;
import java.rmi.registry.Registry;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationSystem;
import java.rmi.activation.Activator;
import sun.rmi.log.ReliableLog;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.util.Map;
import java.lang.reflect.Method;
import java.io.Serializable;

public class Activation implements Serializable
{
    private static final long serialVersionUID = 2921265612698155191L;
    private static final byte MAJOR_VERSION = 1;
    private static final byte MINOR_VERSION = 0;
    private static Object execPolicy;
    private static Method execPolicyMethod;
    private static boolean debugExec;
    private Map<ActivationID, ActivationGroupID> idTable;
    private Map<ActivationGroupID, GroupEntry> groupTable;
    private byte majorVersion;
    private byte minorVersion;
    private transient int groupSemaphore;
    private transient int groupCounter;
    private transient ReliableLog log;
    private transient int numUpdates;
    private transient String[] command;
    private static final long groupTimeout;
    private static final int snapshotInterval;
    private static final long execTimeout;
    private static final Object initLock;
    private static boolean initDone;
    private transient Activator activator;
    private transient Activator activatorStub;
    private transient ActivationSystem system;
    private transient ActivationSystem systemStub;
    private transient ActivationMonitor monitor;
    private transient Registry registry;
    private transient volatile boolean shuttingDown;
    private transient volatile Object startupLock;
    private transient Thread shutdownHook;
    private static ResourceBundle resources;
    
    private static int getInt(final String s, final int n) {
        return AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction(s, n));
    }
    
    private Activation() {
        this.idTable = new ConcurrentHashMap<ActivationID, ActivationGroupID>();
        this.groupTable = new ConcurrentHashMap<ActivationGroupID, GroupEntry>();
        this.majorVersion = 1;
        this.minorVersion = 0;
        this.shuttingDown = false;
    }
    
    private static void startActivation(final int n, final RMIServerSocketFactory rmiServerSocketFactory, final String s, final String[] array) throws Exception {
        final ReliableLog reliableLog = new ReliableLog(s, new ActLogHandler());
        ((Activation)reliableLog.recover()).init(n, rmiServerSocketFactory, reliableLog, array);
    }
    
    private void init(final int n, final RMIServerSocketFactory rmiServerSocketFactory, final ReliableLog log, final String[] array) throws Exception {
        this.log = log;
        this.numUpdates = 0;
        this.shutdownHook = new ShutdownHook();
        this.groupSemaphore = getInt("sun.rmi.activation.groupThrottle", 3);
        this.groupCounter = 0;
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        final ActivationGroupID[] array2 = this.groupTable.keySet().toArray(new ActivationGroupID[0]);
        final Object startupLock = new Object();
        synchronized (this.startupLock = startupLock) {
            this.activator = new ActivatorImpl(n, rmiServerSocketFactory);
            this.activatorStub = (Activator)RemoteObject.toStub(this.activator);
            this.system = new ActivationSystemImpl(n, rmiServerSocketFactory);
            this.systemStub = (ActivationSystem)RemoteObject.toStub(this.system);
            this.monitor = new ActivationMonitorImpl(n, rmiServerSocketFactory);
            this.initCommand(array);
            this.registry = new SystemRegistryImpl(n, null, rmiServerSocketFactory, this.systemStub);
            if (rmiServerSocketFactory != null) {
                synchronized (Activation.initLock) {
                    Activation.initDone = true;
                    Activation.initLock.notifyAll();
                }
            }
        }
        this.startupLock = null;
        int length = array2.length;
        while (--length >= 0) {
            try {
                this.getGroupEntry(array2[length]).restartServices();
            }
            catch (final UnknownGroupException ex) {
                System.err.println(getTextResource("rmid.restart.group.warning"));
                ex.printStackTrace();
            }
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (!(this.groupTable instanceof ConcurrentHashMap)) {
            this.groupTable = new ConcurrentHashMap<ActivationGroupID, GroupEntry>(this.groupTable);
        }
        if (!(this.idTable instanceof ConcurrentHashMap)) {
            this.idTable = new ConcurrentHashMap<ActivationID, ActivationGroupID>(this.idTable);
        }
    }
    
    private void checkShutdown() throws ActivationException {
        final Object startupLock = this.startupLock;
        if (startupLock != null) {
            synchronized (startupLock) {
                monitorexit(startupLock);
            }
        }
        if (this.shuttingDown) {
            throw new ActivationException("activation system shutting down");
        }
    }
    
    private static void unexport(final Remote remote) {
        while (true) {
            try {
                while (!UnicastRemoteObject.unexportObject(remote, false)) {
                    Thread.sleep(100L);
                }
            }
            catch (final Exception ex) {
                continue;
            }
            break;
        }
    }
    
    private ActivationGroupID getGroupID(final ActivationID activationID) throws UnknownObjectException {
        final ActivationGroupID activationGroupID = this.idTable.get(activationID);
        if (activationGroupID != null) {
            return activationGroupID;
        }
        throw new UnknownObjectException("unknown object: " + activationID);
    }
    
    private GroupEntry getGroupEntry(final ActivationGroupID activationGroupID, final boolean b) throws UnknownGroupException {
        if (activationGroupID.getClass() == ActivationGroupID.class) {
            GroupEntry groupEntry;
            if (b) {
                groupEntry = this.groupTable.remove(activationGroupID);
            }
            else {
                groupEntry = this.groupTable.get(activationGroupID);
            }
            if (groupEntry != null && !groupEntry.removed) {
                return groupEntry;
            }
        }
        throw new UnknownGroupException("group unknown");
    }
    
    private GroupEntry getGroupEntry(final ActivationGroupID activationGroupID) throws UnknownGroupException {
        return this.getGroupEntry(activationGroupID, false);
    }
    
    private GroupEntry removeGroupEntry(final ActivationGroupID activationGroupID) throws UnknownGroupException {
        return this.getGroupEntry(activationGroupID, true);
    }
    
    private GroupEntry getGroupEntry(final ActivationID activationID) throws UnknownObjectException {
        final GroupEntry groupEntry = this.groupTable.get(this.getGroupID(activationID));
        if (groupEntry != null && !groupEntry.removed) {
            return groupEntry;
        }
        throw new UnknownObjectException("object's group removed");
    }
    
    private String[] activationArgs(final ActivationGroupDesc activationGroupDesc) {
        final ActivationGroupDesc.CommandEnvironment commandEnvironment = activationGroupDesc.getCommandEnvironment();
        final ArrayList list = new ArrayList();
        list.add((commandEnvironment != null && commandEnvironment.getCommandPath() != null) ? commandEnvironment.getCommandPath() : this.command[0]);
        if (commandEnvironment != null && commandEnvironment.getCommandOptions() != null) {
            list.addAll(Arrays.asList(commandEnvironment.getCommandOptions()));
        }
        final Properties propertyOverrides = activationGroupDesc.getPropertyOverrides();
        if (propertyOverrides != null) {
            final Enumeration<?> propertyNames = propertyOverrides.propertyNames();
            while (propertyNames.hasMoreElements()) {
                final String s = (String)propertyNames.nextElement();
                list.add("-D" + s + "=" + propertyOverrides.getProperty(s));
            }
        }
        for (int i = 1; i < this.command.length; ++i) {
            list.add(this.command[i]);
        }
        final String[] array = new String[list.size()];
        System.arraycopy(list.toArray(), 0, array, 0, array.length);
        return array;
    }
    
    private void checkArgs(final ActivationGroupDesc activationGroupDesc, String[] activationArgs) throws SecurityException, ActivationException {
        if (Activation.execPolicyMethod != null) {
            if (activationArgs == null) {
                activationArgs = this.activationArgs(activationGroupDesc);
            }
            try {
                Activation.execPolicyMethod.invoke(Activation.execPolicy, activationGroupDesc, activationArgs);
            }
            catch (final InvocationTargetException ex) {
                final Throwable targetException = ex.getTargetException();
                if (targetException instanceof SecurityException) {
                    throw (SecurityException)targetException;
                }
                throw new ActivationException(Activation.execPolicyMethod.getName() + ": unexpected exception", ex);
            }
            catch (final Exception ex2) {
                throw new ActivationException(Activation.execPolicyMethod.getName() + ": unexpected exception", ex2);
            }
        }
    }
    
    private void addLogRecord(final LogRecord logRecord) throws ActivationException {
        synchronized (this.log) {
            this.checkShutdown();
            try {
                this.log.update(logRecord, true);
            }
            catch (final Exception ex) {
                this.numUpdates = Activation.snapshotInterval;
                System.err.println(getTextResource("rmid.log.update.warning"));
                ex.printStackTrace();
            }
            if (++this.numUpdates < Activation.snapshotInterval) {
                return;
            }
            try {
                this.log.snapshot(this);
                this.numUpdates = 0;
            }
            catch (final Exception ex2) {
                System.err.println(getTextResource("rmid.log.snapshot.warning"));
                ex2.printStackTrace();
                try {
                    this.system.shutdown();
                }
                catch (final RemoteException ex3) {}
                throw new ActivationException("log snapshot failed", ex2);
            }
        }
    }
    
    private void initCommand(final String[] array) {
        this.command = new String[array.length + 2];
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                try {
                    Activation.this.command[0] = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
                }
                catch (final Exception ex) {
                    System.err.println(getTextResource("rmid.unfound.java.home.property"));
                    Activation.this.command[0] = "java";
                }
                return null;
            }
        });
        System.arraycopy(array, 0, this.command, 1, array.length);
        this.command[this.command.length - 1] = "sun.rmi.server.ActivationGroupInit";
    }
    
    private static void bomb(final String s) {
        System.err.println("rmid: " + s);
        System.err.println(MessageFormat.format(getTextResource("rmid.usage"), "rmid"));
        System.exit(1);
    }
    
    public static void main(final String[] array) {
        boolean b = false;
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            int n = 1098;
            RMIServerSocketFactory rmiServerSocketFactory = null;
            final Channel channel = AccessController.doPrivileged((PrivilegedExceptionAction<Channel>)new PrivilegedExceptionAction<Channel>() {
                @Override
                public Channel run() throws IOException {
                    return System.inheritedChannel();
                }
            });
            if (channel != null && channel instanceof ServerSocketChannel) {
                AccessController.doPrivileged((PrivilegedExceptionAction<Object>)new PrivilegedExceptionAction<Void>() {
                    @Override
                    public Void run() throws IOException {
                        System.setErr(new PrintStream(new FileOutputStream(Files.createTempFile("rmid-err", null, (FileAttribute<?>[])new FileAttribute[0]).toFile())));
                        return null;
                    }
                });
                final ServerSocket socket = ((ServerSocketChannel)channel).socket();
                n = socket.getLocalPort();
                rmiServerSocketFactory = new ActivationServerSocketFactory(socket);
                System.err.println(new Date());
                System.err.println(getTextResource("rmid.inherited.channel.info") + ": " + channel);
            }
            String s = null;
            final ArrayList list = new ArrayList();
            for (int i = 0; i < array.length; ++i) {
                if (array[i].equals("-port")) {
                    if (rmiServerSocketFactory != null) {
                        bomb(getTextResource("rmid.syntax.port.badarg"));
                    }
                    if (i + 1 < array.length) {
                        try {
                            n = Integer.parseInt(array[++i]);
                        }
                        catch (final NumberFormatException ex) {
                            bomb(getTextResource("rmid.syntax.port.badnumber"));
                        }
                    }
                    else {
                        bomb(getTextResource("rmid.syntax.port.missing"));
                    }
                }
                else if (array[i].equals("-log")) {
                    if (i + 1 < array.length) {
                        s = array[++i];
                    }
                    else {
                        bomb(getTextResource("rmid.syntax.log.missing"));
                    }
                }
                else if (array[i].equals("-stop")) {
                    b = true;
                }
                else if (array[i].startsWith("-C")) {
                    list.add(array[i].substring(2));
                }
                else {
                    bomb(MessageFormat.format(getTextResource("rmid.syntax.illegal.option"), array[i]));
                }
            }
            if (s == null) {
                if (rmiServerSocketFactory != null) {
                    bomb(getTextResource("rmid.syntax.log.required"));
                }
                else {
                    s = "log";
                }
            }
            Activation.debugExec = AccessController.doPrivileged((PrivilegedAction<Boolean>)new GetBooleanAction("sun.rmi.server.activation.debugExec"));
            String name = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("sun.rmi.activation.execPolicy", null));
            if (name == null) {
                if (!b) {
                    DefaultExecPolicy.checkConfiguration();
                }
                name = "default";
            }
            if (!name.equals("none")) {
                if (name.equals("") || name.equals("default")) {
                    name = DefaultExecPolicy.class.getName();
                }
                try {
                    final Class<?> rmiClass = getRMIClass(name);
                    Activation.execPolicy = rmiClass.newInstance();
                    Activation.execPolicyMethod = rmiClass.getMethod("checkExecCommand", ActivationGroupDesc.class, String[].class);
                }
                catch (final Exception ex2) {
                    if (Activation.debugExec) {
                        System.err.println(getTextResource("rmid.exec.policy.exception"));
                        ex2.printStackTrace();
                    }
                    bomb(getTextResource("rmid.exec.policy.invalid"));
                }
            }
            if (b) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        System.setProperty("java.rmi.activation.port", Integer.toString(n));
                        return null;
                    }
                });
                ActivationGroup.getSystem().shutdown();
                System.exit(0);
            }
            startActivation(n, rmiServerSocketFactory, s, (String[])list.toArray(new String[list.size()]));
        Label_0603_Outer:
            while (true) {
                while (true) {
                    try {
                        while (true) {
                            Thread.sleep(Long.MAX_VALUE);
                        }
                    }
                    catch (final InterruptedException ex3) {
                        continue Label_0603_Outer;
                    }
                    continue;
                }
            }
        }
        catch (final Exception ex4) {
            System.err.println(MessageFormat.format(getTextResource("rmid.unexpected.exception"), ex4));
            ex4.printStackTrace();
            System.exit(1);
        }
    }
    
    private static String getTextResource(final String s) {
        if (Activation.resources == null) {
            try {
                Activation.resources = ResourceBundle.getBundle("sun.rmi.server.resources.rmid");
            }
            catch (final MissingResourceException ex) {}
            if (Activation.resources == null) {
                return "[missing resource file: " + s + "]";
            }
        }
        String string = null;
        try {
            string = Activation.resources.getString(s);
        }
        catch (final MissingResourceException ex2) {}
        if (string == null) {
            return "[missing resource: " + s + "]";
        }
        return string;
    }
    
    private static Class<?> getRMIClass(final String s) throws Exception {
        return RMIClassLoader.loadClass(s);
    }
    
    private synchronized String Pstartgroup() throws ActivationException {
        while (true) {
            this.checkShutdown();
            if (this.groupSemaphore > 0) {
                break;
            }
            try {
                this.wait();
            }
            catch (final InterruptedException ex) {}
        }
        --this.groupSemaphore;
        return "Group-" + this.groupCounter++;
    }
    
    private synchronized void Vstartgroup() {
        ++this.groupSemaphore;
        this.notifyAll();
    }
    
    static {
        groupTimeout = getInt("sun.rmi.activation.groupTimeout", 60000);
        snapshotInterval = getInt("sun.rmi.activation.snapshotInterval", 200);
        execTimeout = getInt("sun.rmi.activation.execTimeout", 30000);
        initLock = new Object();
        Activation.initDone = false;
        Activation.resources = null;
    }
    
    private static class SystemRegistryImpl extends RegistryImpl
    {
        private static final String NAME;
        private static final long serialVersionUID = 4877330021609408794L;
        private final ActivationSystem systemStub;
        
        SystemRegistryImpl(final int n, final RMIClientSocketFactory rmiClientSocketFactory, final RMIServerSocketFactory rmiServerSocketFactory, final ActivationSystem systemStub) throws RemoteException {
            super(n, rmiClientSocketFactory, rmiServerSocketFactory);
            this.systemStub = systemStub;
        }
        
        @Override
        public Remote lookup(final String s) throws RemoteException, NotBoundException {
            if (s.equals(SystemRegistryImpl.NAME)) {
                return this.systemStub;
            }
            return super.lookup(s);
        }
        
        @Override
        public String[] list() throws RemoteException {
            final String[] list = super.list();
            final int length = list.length;
            final String[] array = new String[length + 1];
            if (length > 0) {
                System.arraycopy(list, 0, array, 0, length);
            }
            array[length] = SystemRegistryImpl.NAME;
            return array;
        }
        
        @Override
        public void bind(final String s, final Remote remote) throws RemoteException, AlreadyBoundException, AccessException {
            if (s.equals(SystemRegistryImpl.NAME)) {
                throw new AccessException("binding ActivationSystem is disallowed");
            }
            RegistryImpl.checkAccess("ActivationSystem.bind");
            super.bind(s, remote);
        }
        
        @Override
        public void unbind(final String s) throws RemoteException, NotBoundException, AccessException {
            if (s.equals(SystemRegistryImpl.NAME)) {
                throw new AccessException("unbinding ActivationSystem is disallowed");
            }
            RegistryImpl.checkAccess("ActivationSystem.unbind");
            super.unbind(s);
        }
        
        @Override
        public void rebind(final String s, final Remote remote) throws RemoteException, AccessException {
            if (s.equals(SystemRegistryImpl.NAME)) {
                throw new AccessException("binding ActivationSystem is disallowed");
            }
            RegistryImpl.checkAccess("ActivationSystem.rebind");
            super.rebind(s, remote);
        }
        
        static {
            NAME = ActivationSystem.class.getName();
        }
    }
    
    class ActivatorImpl extends RemoteServer implements Activator
    {
        private static final long serialVersionUID = -3654244726254566136L;
        
        ActivatorImpl(final int n, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
            ((UnicastServerRef)(this.ref = new UnicastServerRef(new LiveRef(new ObjID(1), n, null, rmiServerSocketFactory)))).exportObject(this, null, false);
        }
        
        @Override
        public MarshalledObject<? extends Remote> activate(final ActivationID activationID, final boolean b) throws ActivationException, UnknownObjectException, RemoteException {
            Activation.this.checkShutdown();
            return Activation.this.getGroupEntry(activationID).activate(activationID, b);
        }
    }
    
    class ActivationMonitorImpl extends UnicastRemoteObject implements ActivationMonitor
    {
        private static final long serialVersionUID = -6214940464757948867L;
        
        ActivationMonitorImpl(final int n, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
            super(n, null, rmiServerSocketFactory);
        }
        
        @Override
        public void inactiveObject(final ActivationID activationID) throws UnknownObjectException, RemoteException {
            try {
                Activation.this.checkShutdown();
            }
            catch (final ActivationException ex) {
                return;
            }
            RegistryImpl.checkAccess("Activator.inactiveObject");
            Activation.this.getGroupEntry(activationID).inactiveObject(activationID);
        }
        
        @Override
        public void activeObject(final ActivationID activationID, final MarshalledObject<? extends Remote> marshalledObject) throws UnknownObjectException, RemoteException {
            try {
                Activation.this.checkShutdown();
            }
            catch (final ActivationException ex) {
                return;
            }
            RegistryImpl.checkAccess("ActivationSystem.activeObject");
            Activation.this.getGroupEntry(activationID).activeObject(activationID, marshalledObject);
        }
        
        @Override
        public void inactiveGroup(final ActivationGroupID activationGroupID, final long n) throws UnknownGroupException, RemoteException {
            try {
                Activation.this.checkShutdown();
            }
            catch (final ActivationException ex) {
                return;
            }
            RegistryImpl.checkAccess("ActivationMonitor.inactiveGroup");
            Activation.this.getGroupEntry(activationGroupID).inactiveGroup(n, false);
        }
    }
    
    static class SameHostOnlyServerRef extends UnicastServerRef
    {
        private static final long serialVersionUID = 1234L;
        private String accessKind;
        
        SameHostOnlyServerRef(final LiveRef liveRef, final String accessKind) {
            super(liveRef);
            this.accessKind = accessKind;
        }
        
        @Override
        protected void unmarshalCustomCallData(final ObjectInput objectInput) throws IOException, ClassNotFoundException {
            RegistryImpl.checkAccess(this.accessKind);
            super.unmarshalCustomCallData(objectInput);
        }
    }
    
    class ActivationSystemImpl extends RemoteServer implements ActivationSystem
    {
        private static final long serialVersionUID = 9100152600327688967L;
        
        ActivationSystemImpl(final int n, final RMIServerSocketFactory rmiServerSocketFactory) throws RemoteException {
            ((UnicastServerRef)(this.ref = new SameHostOnlyServerRef(new LiveRef(new ObjID(4), n, null, rmiServerSocketFactory), "ActivationSystem.nonLocalAccess"))).exportObject(this, null);
        }
        
        @Override
        public ActivationID registerObject(final ActivationDesc activationDesc) throws ActivationException, UnknownGroupException, RemoteException {
            Activation.this.checkShutdown();
            final ActivationGroupID groupID = activationDesc.getGroupID();
            final ActivationID activationID = new ActivationID(Activation.this.activatorStub);
            Activation.this.getGroupEntry(groupID).registerObject(activationID, activationDesc, true);
            return activationID;
        }
        
        @Override
        public void unregisterObject(final ActivationID activationID) throws ActivationException, UnknownObjectException, RemoteException {
            Activation.this.checkShutdown();
            Activation.this.getGroupEntry(activationID).unregisterObject(activationID, true);
        }
        
        @Override
        public ActivationGroupID registerGroup(final ActivationGroupDesc activationGroupDesc) throws ActivationException, RemoteException {
            Thread.dumpStack();
            Activation.this.checkShutdown();
            Activation.this.checkArgs(activationGroupDesc, null);
            final ActivationGroupID activationGroupID = new ActivationGroupID(Activation.this.systemStub);
            Activation.this.groupTable.put(activationGroupID, new GroupEntry(activationGroupID, activationGroupDesc));
            Activation.this.addLogRecord(new LogRegisterGroup(activationGroupID, activationGroupDesc));
            return activationGroupID;
        }
        
        @Override
        public ActivationMonitor activeGroup(final ActivationGroupID activationGroupID, final ActivationInstantiator activationInstantiator, final long n) throws ActivationException, UnknownGroupException, RemoteException {
            Activation.this.checkShutdown();
            Activation.this.getGroupEntry(activationGroupID).activeGroup(activationInstantiator, n);
            return Activation.this.monitor;
        }
        
        @Override
        public void unregisterGroup(final ActivationGroupID activationGroupID) throws ActivationException, UnknownGroupException, RemoteException {
            Activation.this.checkShutdown();
            Activation.this.removeGroupEntry(activationGroupID).unregisterGroup(true);
        }
        
        @Override
        public ActivationDesc setActivationDesc(final ActivationID activationID, final ActivationDesc activationDesc) throws ActivationException, UnknownObjectException, RemoteException {
            Activation.this.checkShutdown();
            if (!Activation.this.getGroupID(activationID).equals(activationDesc.getGroupID())) {
                throw new ActivationException("ActivationDesc contains wrong group");
            }
            return Activation.this.getGroupEntry(activationID).setActivationDesc(activationID, activationDesc, true);
        }
        
        @Override
        public ActivationGroupDesc setActivationGroupDesc(final ActivationGroupID activationGroupID, final ActivationGroupDesc activationGroupDesc) throws ActivationException, UnknownGroupException, RemoteException {
            Activation.this.checkShutdown();
            Activation.this.checkArgs(activationGroupDesc, null);
            return Activation.this.getGroupEntry(activationGroupID).setActivationGroupDesc(activationGroupID, activationGroupDesc, true);
        }
        
        @Override
        public ActivationDesc getActivationDesc(final ActivationID activationID) throws ActivationException, UnknownObjectException, RemoteException {
            Activation.this.checkShutdown();
            return Activation.this.getGroupEntry(activationID).getActivationDesc(activationID);
        }
        
        @Override
        public ActivationGroupDesc getActivationGroupDesc(final ActivationGroupID activationGroupID) throws ActivationException, UnknownGroupException, RemoteException {
            Activation.this.checkShutdown();
            return Activation.this.getGroupEntry(activationGroupID).desc;
        }
        
        @Override
        public void shutdown() throws AccessException {
            final Object access$1100 = Activation.this.startupLock;
            if (access$1100 != null) {
                synchronized (access$1100) {
                    monitorexit(access$1100);
                }
            }
            synchronized (Activation.this) {
                if (!Activation.this.shuttingDown) {
                    Activation.this.shuttingDown = true;
                    new Shutdown().start();
                }
            }
        }
    }
    
    private class Shutdown extends Thread
    {
        Shutdown() {
            super("rmid Shutdown");
        }
        
        @Override
        public void run() {
            try {
                unexport(Activation.this.activator);
                unexport(Activation.this.system);
                final Iterator iterator = Activation.this.groupTable.values().iterator();
                while (iterator.hasNext()) {
                    ((GroupEntry)iterator.next()).shutdown();
                }
                Runtime.getRuntime().removeShutdownHook(Activation.this.shutdownHook);
                unexport(Activation.this.monitor);
                try {
                    synchronized (Activation.this.log) {
                        Activation.this.log.close();
                    }
                }
                catch (final IOException ex) {}
            }
            finally {
                System.err.println(getTextResource("rmid.daemon.shutdown"));
                System.exit(0);
            }
        }
    }
    
    private class ShutdownHook extends Thread
    {
        ShutdownHook() {
            super("rmid ShutdownHook");
        }
        
        @Override
        public void run() {
            synchronized (Activation.this) {
                Activation.this.shuttingDown = true;
            }
            final Iterator iterator = Activation.this.groupTable.values().iterator();
            while (iterator.hasNext()) {
                ((GroupEntry)iterator.next()).shutdownFast();
            }
        }
    }
    
    private class GroupEntry implements Serializable
    {
        private static final long serialVersionUID = 7222464070032993304L;
        private static final int MAX_TRIES = 2;
        private static final int NORMAL = 0;
        private static final int CREATING = 1;
        private static final int TERMINATE = 2;
        private static final int TERMINATING = 3;
        ActivationGroupDesc desc;
        ActivationGroupID groupID;
        long incarnation;
        Map<ActivationID, ObjectEntry> objects;
        Set<ActivationID> restartSet;
        transient ActivationInstantiator group;
        transient int status;
        transient long waitTime;
        transient String groupName;
        transient Process child;
        transient boolean removed;
        transient Watchdog watchdog;
        
        GroupEntry(final ActivationGroupID groupID, final ActivationGroupDesc desc) {
            this.desc = null;
            this.groupID = null;
            this.incarnation = 0L;
            this.objects = new HashMap<ActivationID, ObjectEntry>();
            this.restartSet = new HashSet<ActivationID>();
            this.group = null;
            this.status = 0;
            this.waitTime = 0L;
            this.groupName = null;
            this.child = null;
            this.removed = false;
            this.watchdog = null;
            this.groupID = groupID;
            this.desc = desc;
        }
        
        void restartServices() {
            Iterator iterator = null;
            synchronized (this) {
                if (this.restartSet.isEmpty()) {
                    return;
                }
                iterator = new HashSet(this.restartSet).iterator();
            }
            while (iterator.hasNext()) {
                final ActivationID activationID = (ActivationID)iterator.next();
                try {
                    this.activate(activationID, true);
                }
                catch (final Exception ex) {
                    if (Activation.this.shuttingDown) {
                        return;
                    }
                    System.err.println(getTextResource("rmid.restart.service.warning"));
                    ex.printStackTrace();
                }
            }
        }
        
        synchronized void activeGroup(final ActivationInstantiator group, final long n) throws ActivationException, UnknownGroupException {
            if (this.incarnation != n) {
                throw new ActivationException("invalid incarnation");
            }
            if (this.group != null) {
                if (this.group.equals(group)) {
                    return;
                }
                throw new ActivationException("group already active");
            }
            else {
                if (this.child != null && this.status != 1) {
                    throw new ActivationException("group not being created");
                }
                this.group = group;
                this.status = 0;
                this.notifyAll();
            }
        }
        
        private void checkRemoved() throws UnknownGroupException {
            if (this.removed) {
                throw new UnknownGroupException("group removed");
            }
        }
        
        private ObjectEntry getObjectEntry(final ActivationID activationID) throws UnknownObjectException {
            if (this.removed) {
                throw new UnknownObjectException("object's group removed");
            }
            final ObjectEntry objectEntry = this.objects.get(activationID);
            if (objectEntry == null) {
                throw new UnknownObjectException("object unknown");
            }
            return objectEntry;
        }
        
        synchronized void registerObject(final ActivationID activationID, final ActivationDesc activationDesc, final boolean b) throws UnknownGroupException, ActivationException {
            this.checkRemoved();
            this.objects.put(activationID, new ObjectEntry(activationDesc));
            if (activationDesc.getRestartMode()) {
                this.restartSet.add(activationID);
            }
            Activation.this.idTable.put(activationID, this.groupID);
            if (b) {
                Activation.this.addLogRecord(new LogRegisterObject(activationID, activationDesc));
            }
        }
        
        synchronized void unregisterObject(final ActivationID activationID, final boolean b) throws UnknownGroupException, ActivationException {
            final ObjectEntry objectEntry = this.getObjectEntry(activationID);
            objectEntry.removed = true;
            this.objects.remove(activationID);
            if (objectEntry.desc.getRestartMode()) {
                this.restartSet.remove(activationID);
            }
            Activation.this.idTable.remove(activationID);
            if (b) {
                Activation.this.addLogRecord(new LogUnregisterObject(activationID));
            }
        }
        
        synchronized void unregisterGroup(final boolean b) throws UnknownGroupException, ActivationException {
            this.checkRemoved();
            this.removed = true;
            for (final Map.Entry entry : this.objects.entrySet()) {
                Activation.this.idTable.remove(entry.getKey());
                ((ObjectEntry)entry.getValue()).removed = true;
            }
            this.objects.clear();
            this.restartSet.clear();
            this.reset();
            this.childGone();
            if (b) {
                Activation.this.addLogRecord(new LogUnregisterGroup(this.groupID));
            }
        }
        
        synchronized ActivationDesc setActivationDesc(final ActivationID activationID, final ActivationDesc desc, final boolean b) throws UnknownObjectException, UnknownGroupException, ActivationException {
            final ObjectEntry objectEntry = this.getObjectEntry(activationID);
            final ActivationDesc desc2 = objectEntry.desc;
            objectEntry.desc = desc;
            if (desc.getRestartMode()) {
                this.restartSet.add(activationID);
            }
            else {
                this.restartSet.remove(activationID);
            }
            if (b) {
                Activation.this.addLogRecord(new LogUpdateDesc(activationID, desc));
            }
            return desc2;
        }
        
        synchronized ActivationDesc getActivationDesc(final ActivationID activationID) throws UnknownObjectException, UnknownGroupException {
            return this.getObjectEntry(activationID).desc;
        }
        
        synchronized ActivationGroupDesc setActivationGroupDesc(final ActivationGroupID activationGroupID, final ActivationGroupDesc desc, final boolean b) throws UnknownGroupException, ActivationException {
            this.checkRemoved();
            final ActivationGroupDesc desc2 = this.desc;
            this.desc = desc;
            if (b) {
                Activation.this.addLogRecord(new LogUpdateGroupDesc(activationGroupID, desc));
            }
            return desc2;
        }
        
        synchronized void inactiveGroup(final long n, final boolean b) throws UnknownGroupException {
            this.checkRemoved();
            if (this.incarnation != n) {
                throw new UnknownGroupException("invalid incarnation");
            }
            this.reset();
            if (b) {
                this.terminate();
            }
            else if (this.child != null && this.status == 0) {
                this.status = 2;
                this.watchdog.noRestart();
            }
        }
        
        synchronized void activeObject(final ActivationID activationID, final MarshalledObject<? extends Remote> stub) throws UnknownObjectException {
            this.getObjectEntry(activationID).stub = stub;
        }
        
        synchronized void inactiveObject(final ActivationID activationID) throws UnknownObjectException {
            this.getObjectEntry(activationID).reset();
        }
        
        private synchronized void reset() {
            this.group = null;
            final Iterator<ObjectEntry> iterator = this.objects.values().iterator();
            while (iterator.hasNext()) {
                iterator.next().reset();
            }
        }
        
        private void childGone() {
            if (this.child != null) {
                this.child = null;
                this.watchdog.dispose();
                this.watchdog = null;
                this.status = 0;
                this.notifyAll();
            }
        }
        
        private void terminate() {
            if (this.child != null && this.status != 3) {
                this.child.destroy();
                this.status = 3;
                this.waitTime = System.currentTimeMillis() + Activation.groupTimeout;
                this.notifyAll();
            }
        }
        
        private void await() {
            while (true) {
                switch (this.status) {
                    case 0: {
                        return;
                    }
                    case 2: {
                        this.terminate();
                    }
                    case 3: {
                        try {
                            this.child.exitValue();
                        }
                        catch (final IllegalThreadStateException ex) {
                            final long currentTimeMillis = System.currentTimeMillis();
                            if (this.waitTime > currentTimeMillis) {
                                try {
                                    this.wait(this.waitTime - currentTimeMillis);
                                }
                                catch (final InterruptedException ex2) {}
                                continue;
                            }
                        }
                        this.childGone();
                        return;
                    }
                    case 1: {
                        try {
                            this.wait();
                        }
                        catch (final InterruptedException ex3) {}
                        continue;
                    }
                }
            }
        }
        
        void shutdownFast() {
            final Process child = this.child;
            if (child != null) {
                child.destroy();
            }
        }
        
        synchronized void shutdown() {
            this.reset();
            this.terminate();
            this.await();
        }
        
        MarshalledObject<? extends Remote> activate(final ActivationID activationID, final boolean b) throws ActivationException {
            Throwable t = null;
            for (int i = 2; i > 0; --i) {
                final ObjectEntry objectEntry;
                final ActivationInstantiator instantiator;
                final long incarnation;
                synchronized (this) {
                    objectEntry = this.getObjectEntry(activationID);
                    if (!b && objectEntry.stub != null) {
                        return objectEntry.stub;
                    }
                    instantiator = this.getInstantiator(this.groupID);
                    incarnation = this.incarnation;
                }
                boolean b2 = false;
                boolean b3 = false;
                try {
                    return objectEntry.activate(activationID, b, instantiator);
                }
                catch (final NoSuchObjectException ex) {
                    b2 = true;
                    t = ex;
                }
                catch (final ConnectException ex2) {
                    b2 = true;
                    b3 = true;
                    t = ex2;
                }
                catch (final ConnectIOException ex3) {
                    b2 = true;
                    b3 = true;
                    t = ex3;
                }
                catch (final InactiveGroupException ex4) {
                    b2 = true;
                    t = ex4;
                }
                catch (final RemoteException ex5) {
                    if (t == null) {
                        t = ex5;
                    }
                }
                if (b2) {
                    try {
                        System.err.println(MessageFormat.format(getTextResource("rmid.group.inactive"), t.toString()));
                        t.printStackTrace();
                        Activation.this.getGroupEntry(this.groupID).inactiveGroup(incarnation, b3);
                    }
                    catch (final UnknownGroupException ex6) {}
                }
            }
            throw new ActivationException("object activation failed after 2 tries", t);
        }
        
        private ActivationInstantiator getInstantiator(final ActivationGroupID activationGroupID) throws ActivationException {
            assert Thread.holdsLock(this);
            this.await();
            if (this.group != null) {
                return this.group;
            }
            this.checkRemoved();
            boolean b = false;
            try {
                this.groupName = Activation.this.Pstartgroup();
                b = true;
                final String[] access$2200 = Activation.this.activationArgs(this.desc);
                Activation.this.checkArgs(this.desc, access$2200);
                if (Activation.debugExec) {
                    final StringBuffer sb = new StringBuffer(access$2200[0]);
                    for (int i = 1; i < access$2200.length; ++i) {
                        sb.append(' ');
                        sb.append(access$2200[i]);
                    }
                    System.err.println(MessageFormat.format(getTextResource("rmid.exec.command"), sb.toString()));
                }
                try {
                    this.child = Runtime.getRuntime().exec(access$2200);
                    this.status = 1;
                    ++this.incarnation;
                    (this.watchdog = new Watchdog()).start();
                    Activation.this.addLogRecord(new LogGroupIncarnation(activationGroupID, this.incarnation));
                    PipeWriter.plugTogetherPair(this.child.getInputStream(), System.out, this.child.getErrorStream(), System.err);
                    try (final MarshalOutputStream marshalOutputStream = new MarshalOutputStream(this.child.getOutputStream())) {
                        marshalOutputStream.writeObject(activationGroupID);
                        marshalOutputStream.writeObject(this.desc);
                        marshalOutputStream.writeLong(this.incarnation);
                        marshalOutputStream.flush();
                    }
                }
                catch (final IOException ex) {
                    this.terminate();
                    throw new ActivationException("unable to create activation group", ex);
                }
                try {
                    long n = System.currentTimeMillis();
                    final long n2 = n + Activation.execTimeout;
                    do {
                        this.wait(n2 - n);
                        if (this.group != null) {
                            return this.group;
                        }
                        n = System.currentTimeMillis();
                    } while (this.status == 1 && n < n2);
                }
                catch (final InterruptedException ex2) {}
                this.terminate();
                throw new ActivationException(this.removed ? "activation group unregistered" : "timeout creating child process");
            }
            finally {
                if (b) {
                    Activation.this.Vstartgroup();
                }
            }
        }
        
        private class Watchdog extends Thread
        {
            private final Process groupProcess;
            private final long groupIncarnation;
            private boolean canInterrupt;
            private boolean shouldQuit;
            private boolean shouldRestart;
            
            Watchdog() {
                super("WatchDog-" + GroupEntry.this.groupName + "-" + GroupEntry.this.incarnation);
                this.groupProcess = GroupEntry.this.child;
                this.groupIncarnation = GroupEntry.this.incarnation;
                this.canInterrupt = true;
                this.shouldQuit = false;
                this.setDaemon(this.shouldRestart = true);
            }
            
            @Override
            public void run() {
                if (this.shouldQuit) {
                    return;
                }
                try {
                    this.groupProcess.waitFor();
                }
                catch (final InterruptedException ex) {
                    return;
                }
                int n = 0;
                synchronized (GroupEntry.this) {
                    if (this.shouldQuit) {
                        return;
                    }
                    this.canInterrupt = false;
                    interrupted();
                    if (this.groupIncarnation == GroupEntry.this.incarnation) {
                        n = ((this.shouldRestart && !Activation.this.shuttingDown) ? 1 : 0);
                        GroupEntry.this.reset();
                        GroupEntry.this.childGone();
                    }
                }
                if (n != 0) {
                    GroupEntry.this.restartServices();
                }
            }
            
            void dispose() {
                this.shouldQuit = true;
                if (this.canInterrupt) {
                    this.interrupt();
                }
            }
            
            void noRestart() {
                this.shouldRestart = false;
            }
        }
    }
    
    private static class ObjectEntry implements Serializable
    {
        private static final long serialVersionUID = -5500114225321357856L;
        ActivationDesc desc;
        transient volatile MarshalledObject<? extends Remote> stub;
        transient volatile boolean removed;
        
        ObjectEntry(final ActivationDesc desc) {
            this.stub = null;
            this.removed = false;
            this.desc = desc;
        }
        
        synchronized MarshalledObject<? extends Remote> activate(final ActivationID activationID, final boolean b, final ActivationInstantiator activationInstantiator) throws RemoteException, ActivationException {
            final MarshalledObject<? extends Remote> stub = this.stub;
            if (this.removed) {
                throw new UnknownObjectException("object removed");
            }
            if (!b && stub != null) {
                return stub;
            }
            return this.stub = activationInstantiator.newInstance(activationID, this.desc);
        }
        
        void reset() {
            this.stub = null;
        }
    }
    
    private static class ActLogHandler extends LogHandler
    {
        ActLogHandler() {
        }
        
        @Override
        public Object initialSnapshot() {
            return new Activation(null);
        }
        
        @Override
        public Object applyUpdate(final Object o, final Object o2) throws Exception {
            return ((LogRecord)o).apply(o2);
        }
    }
    
    private abstract static class LogRecord implements Serializable
    {
        private static final long serialVersionUID = 8395140512322687529L;
        
        abstract Object apply(final Object p0) throws Exception;
    }
    
    private static class LogRegisterObject extends LogRecord
    {
        private static final long serialVersionUID = -6280336276146085143L;
        private ActivationID id;
        private ActivationDesc desc;
        
        LogRegisterObject(final ActivationID id, final ActivationDesc desc) {
            this.id = id;
            this.desc = desc;
        }
        
        @Override
        Object apply(final Object o) {
            try {
                ((Activation)o).getGroupEntry(this.desc.getGroupID()).registerObject(this.id, this.desc, false);
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogRegisterObject"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    private static class LogUnregisterObject extends LogRecord
    {
        private static final long serialVersionUID = 6269824097396935501L;
        private ActivationID id;
        
        LogUnregisterObject(final ActivationID id) {
            this.id = id;
        }
        
        @Override
        Object apply(final Object o) {
            try {
                ((Activation)o).getGroupEntry(this.id).unregisterObject(this.id, false);
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogUnregisterObject"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    private static class LogRegisterGroup extends LogRecord
    {
        private static final long serialVersionUID = -1966827458515403625L;
        private ActivationGroupID id;
        private ActivationGroupDesc desc;
        
        LogRegisterGroup(final ActivationGroupID id, final ActivationGroupDesc desc) {
            this.id = id;
            this.desc = desc;
        }
        
        @Override
        Object apply(final Object o) {
            ((Activation)o).groupTable.put(this.id, (Activation)o.new GroupEntry(this.id, this.desc));
            return o;
        }
    }
    
    private static class LogUpdateDesc extends LogRecord
    {
        private static final long serialVersionUID = 545511539051179885L;
        private ActivationID id;
        private ActivationDesc desc;
        
        LogUpdateDesc(final ActivationID id, final ActivationDesc desc) {
            this.id = id;
            this.desc = desc;
        }
        
        @Override
        Object apply(final Object o) {
            try {
                ((Activation)o).getGroupEntry(this.id).setActivationDesc(this.id, this.desc, false);
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogUpdateDesc"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    private static class LogUpdateGroupDesc extends LogRecord
    {
        private static final long serialVersionUID = -1271300989218424337L;
        private ActivationGroupID id;
        private ActivationGroupDesc desc;
        
        LogUpdateGroupDesc(final ActivationGroupID id, final ActivationGroupDesc desc) {
            this.id = id;
            this.desc = desc;
        }
        
        @Override
        Object apply(final Object o) {
            try {
                ((Activation)o).getGroupEntry(this.id).setActivationGroupDesc(this.id, this.desc, false);
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogUpdateGroupDesc"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    private static class LogUnregisterGroup extends LogRecord
    {
        private static final long serialVersionUID = -3356306586522147344L;
        private ActivationGroupID id;
        
        LogUnregisterGroup(final ActivationGroupID id) {
            this.id = id;
        }
        
        @Override
        Object apply(final Object o) {
            final GroupEntry groupEntry = ((Activation)o).groupTable.remove(this.id);
            try {
                groupEntry.unregisterGroup(false);
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogUnregisterGroup"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    private static class LogGroupIncarnation extends LogRecord
    {
        private static final long serialVersionUID = 4146872747377631897L;
        private ActivationGroupID id;
        private long inc;
        
        LogGroupIncarnation(final ActivationGroupID id, final long inc) {
            this.id = id;
            this.inc = inc;
        }
        
        @Override
        Object apply(final Object o) {
            try {
                ((Activation)o).getGroupEntry(this.id).incarnation = this.inc;
            }
            catch (final Exception ex) {
                System.err.println(MessageFormat.format(getTextResource("rmid.log.recover.warning"), "LogGroupIncarnation"));
                ex.printStackTrace();
            }
            return o;
        }
    }
    
    public static class DefaultExecPolicy
    {
        public void checkExecCommand(final ActivationGroupDesc activationGroupDesc, final String[] array) throws SecurityException {
            final PermissionCollection execPermissions = getExecPermissions();
            final Properties propertyOverrides = activationGroupDesc.getPropertyOverrides();
            if (propertyOverrides != null) {
                final Enumeration<?> propertyNames = propertyOverrides.propertyNames();
                while (propertyNames.hasMoreElements()) {
                    final String s = (String)propertyNames.nextElement();
                    final String property = propertyOverrides.getProperty(s);
                    final String string = "-D" + s + "=" + property;
                    try {
                        checkPermission(execPermissions, new ExecOptionPermission(string));
                    }
                    catch (final AccessControlException ex) {
                        if (!property.equals("")) {
                            throw ex;
                        }
                        checkPermission(execPermissions, new ExecOptionPermission("-D" + s));
                    }
                }
            }
            final String className = activationGroupDesc.getClassName();
            if ((className != null && !className.equals(ActivationGroupImpl.class.getName())) || activationGroupDesc.getLocation() != null || activationGroupDesc.getData() != null) {
                throw new AccessControlException("access denied (custom group implementation not allowed)");
            }
            final ActivationGroupDesc.CommandEnvironment commandEnvironment = activationGroupDesc.getCommandEnvironment();
            if (commandEnvironment != null) {
                final String commandPath = commandEnvironment.getCommandPath();
                if (commandPath != null) {
                    checkPermission(execPermissions, new ExecPermission(commandPath));
                }
                final String[] commandOptions = commandEnvironment.getCommandOptions();
                if (commandOptions != null) {
                    final String[] array2 = commandOptions;
                    for (int length = array2.length, i = 0; i < length; ++i) {
                        checkPermission(execPermissions, new ExecOptionPermission(array2[i]));
                    }
                }
            }
        }
        
        static void checkConfiguration() {
            if (!(AccessController.doPrivileged((PrivilegedAction<Policy>)new PrivilegedAction<Policy>() {
                @Override
                public Policy run() {
                    return Policy.getPolicy();
                }
            }) instanceof PolicyFile)) {
                return;
            }
            final Enumeration<Permission> elements = getExecPermissions().elements();
            while (elements.hasMoreElements()) {
                final Permission permission = elements.nextElement();
                if (permission instanceof AllPermission || permission instanceof ExecPermission || permission instanceof ExecOptionPermission) {
                    return;
                }
            }
            System.err.println(getTextResource("rmid.exec.perms.inadequate"));
        }
        
        private static PermissionCollection getExecPermissions() {
            return AccessController.doPrivileged((PrivilegedAction<PermissionCollection>)new PrivilegedAction<PermissionCollection>() {
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
        }
        
        private static void checkPermission(final PermissionCollection collection, final Permission permission) throws AccessControlException {
            if (!collection.implies(permission)) {
                throw new AccessControlException("access denied " + permission.toString());
            }
        }
    }
    
    private static class ActivationServerSocketFactory implements RMIServerSocketFactory
    {
        private final ServerSocket serverSocket;
        
        ActivationServerSocketFactory(final ServerSocket serverSocket) {
            this.serverSocket = serverSocket;
        }
        
        @Override
        public ServerSocket createServerSocket(final int n) throws IOException {
            return new DelayedAcceptServerSocket(this.serverSocket);
        }
    }
    
    private static class DelayedAcceptServerSocket extends ServerSocket
    {
        private final ServerSocket serverSocket;
        
        DelayedAcceptServerSocket(final ServerSocket serverSocket) throws IOException {
            this.serverSocket = serverSocket;
        }
        
        @Override
        public void bind(final SocketAddress socketAddress) throws IOException {
            this.serverSocket.bind(socketAddress);
        }
        
        @Override
        public void bind(final SocketAddress socketAddress, final int n) throws IOException {
            this.serverSocket.bind(socketAddress, n);
        }
        
        @Override
        public InetAddress getInetAddress() {
            return AccessController.doPrivileged((PrivilegedAction<InetAddress>)new PrivilegedAction<InetAddress>() {
                @Override
                public InetAddress run() {
                    return DelayedAcceptServerSocket.this.serverSocket.getInetAddress();
                }
            });
        }
        
        @Override
        public int getLocalPort() {
            return this.serverSocket.getLocalPort();
        }
        
        @Override
        public SocketAddress getLocalSocketAddress() {
            return AccessController.doPrivileged((PrivilegedAction<SocketAddress>)new PrivilegedAction<SocketAddress>() {
                @Override
                public SocketAddress run() {
                    return DelayedAcceptServerSocket.this.serverSocket.getLocalSocketAddress();
                }
            });
        }
        
        @Override
        public Socket accept() throws IOException {
            synchronized (Activation.initLock) {
                try {
                    while (!Activation.initDone) {
                        Activation.initLock.wait();
                    }
                }
                catch (final InterruptedException ex) {
                    throw new AssertionError((Object)ex);
                }
            }
            return this.serverSocket.accept();
        }
        
        @Override
        public void close() throws IOException {
            this.serverSocket.close();
        }
        
        @Override
        public ServerSocketChannel getChannel() {
            return this.serverSocket.getChannel();
        }
        
        @Override
        public boolean isBound() {
            return this.serverSocket.isBound();
        }
        
        @Override
        public boolean isClosed() {
            return this.serverSocket.isClosed();
        }
        
        @Override
        public void setSoTimeout(final int soTimeout) throws SocketException {
            this.serverSocket.setSoTimeout(soTimeout);
        }
        
        @Override
        public int getSoTimeout() throws IOException {
            return this.serverSocket.getSoTimeout();
        }
        
        @Override
        public void setReuseAddress(final boolean reuseAddress) throws SocketException {
            this.serverSocket.setReuseAddress(reuseAddress);
        }
        
        @Override
        public boolean getReuseAddress() throws SocketException {
            return this.serverSocket.getReuseAddress();
        }
        
        @Override
        public String toString() {
            return this.serverSocket.toString();
        }
        
        @Override
        public void setReceiveBufferSize(final int receiveBufferSize) throws SocketException {
            this.serverSocket.setReceiveBufferSize(receiveBufferSize);
        }
        
        @Override
        public int getReceiveBufferSize() throws SocketException {
            return this.serverSocket.getReceiveBufferSize();
        }
    }
}
