package sun.rmi.server;

import java.io.IOException;
import java.rmi.server.RMISocketFactory;
import java.net.ServerSocket;
import java.rmi.NoSuchObjectException;
import java.rmi.activation.Activatable;
import java.rmi.activation.UnknownObjectException;
import java.security.PrivilegedActionException;
import java.security.AccessController;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Constructor;
import java.security.PrivilegedExceptionAction;
import java.rmi.server.RMIClassLoader;
import java.rmi.activation.ActivationException;
import sun.rmi.registry.RegistryImpl;
import java.rmi.activation.ActivationDesc;
import java.rmi.RemoteException;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.rmi.MarshalledObject;
import java.util.List;
import java.rmi.activation.ActivationGroupID;
import java.rmi.activation.ActivationID;
import java.util.Hashtable;
import java.rmi.activation.ActivationGroup;

public class ActivationGroupImpl extends ActivationGroup
{
    private static final long serialVersionUID = 5758693559430427303L;
    private final Hashtable<ActivationID, ActiveEntry> active;
    private boolean groupInactive;
    private final ActivationGroupID groupID;
    private final List<ActivationID> lockedIDs;
    
    public ActivationGroupImpl(final ActivationGroupID groupID, final MarshalledObject<?> marshalledObject) throws RemoteException {
        super(groupID);
        this.active = new Hashtable<ActivationID, ActiveEntry>();
        this.groupInactive = false;
        this.lockedIDs = new ArrayList<ActivationID>();
        this.groupID = groupID;
        UnicastRemoteObject.unexportObject(this, true);
        UnicastRemoteObject.exportObject(this, 0, null, new ServerSocketFactoryImpl());
        if (System.getSecurityManager() == null) {
            try {
                System.setSecurityManager(new SecurityManager());
            }
            catch (final Exception ex) {
                throw new RemoteException("unable to set security manager", ex);
            }
        }
    }
    
    private void acquireLock(final ActivationID activationID) {
        while (true) {
            final ActivationID activationID2;
            synchronized (this.lockedIDs) {
                final int index = this.lockedIDs.indexOf(activationID);
                if (index < 0) {
                    this.lockedIDs.add(activationID);
                    return;
                }
                activationID2 = this.lockedIDs.get(index);
            }
            synchronized (activationID2) {
                synchronized (this.lockedIDs) {
                    final int index2 = this.lockedIDs.indexOf(activationID2);
                    if (index2 < 0) {
                        continue;
                    }
                    if (this.lockedIDs.get(index2) != activationID2) {
                        continue;
                    }
                }
                try {
                    activationID2.wait();
                }
                catch (final InterruptedException ex) {}
            }
        }
    }
    
    private void releaseLock(ActivationID activationID) {
        synchronized (this.lockedIDs) {
            activationID = this.lockedIDs.remove(this.lockedIDs.indexOf(activationID));
        }
        synchronized (activationID) {
            activationID.notifyAll();
        }
    }
    
    @Override
    public MarshalledObject<? extends Remote> newInstance(final ActivationID activationID, final ActivationDesc activationDesc) throws ActivationException, RemoteException {
        RegistryImpl.checkAccess("ActivationInstantiator.newInstance");
        if (!this.groupID.equals(activationDesc.getGroupID())) {
            throw new ActivationException("newInstance in wrong group");
        }
        try {
            this.acquireLock(activationID);
            synchronized (this) {
                if (this.groupInactive) {
                    throw new InactiveGroupException("group is inactive");
                }
            }
            final ActiveEntry activeEntry = this.active.get(activationID);
            if (activeEntry != null) {
                return activeEntry.mobj;
            }
            final Class<? extends Remote> subclass = RMIClassLoader.loadClass(activationDesc.getLocation(), activationDesc.getClassName()).asSubclass(Remote.class);
            Remote remote = null;
            final Thread currentThread = Thread.currentThread();
            final ClassLoader contextClassLoader = currentThread.getContextClassLoader();
            final ClassLoader classLoader = subclass.getClassLoader();
            final ClassLoader classLoader2 = covers(classLoader, contextClassLoader) ? classLoader : contextClassLoader;
            try {
                remote = AccessController.doPrivileged((PrivilegedExceptionAction<Remote>)new PrivilegedExceptionAction<Remote>() {
                    @Override
                    public Remote run() throws InstantiationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
                        final Constructor declaredConstructor = subclass.getDeclaredConstructor(ActivationID.class, MarshalledObject.class);
                        declaredConstructor.setAccessible(true);
                        try {
                            currentThread.setContextClassLoader(classLoader2);
                            return (Remote)declaredConstructor.newInstance(activationID, activationDesc.getData());
                        }
                        finally {
                            currentThread.setContextClassLoader(contextClassLoader);
                        }
                    }
                });
            }
            catch (final PrivilegedActionException ex) {
                final Exception exception = ex.getException();
                if (exception instanceof InstantiationException) {
                    throw (InstantiationException)exception;
                }
                if (exception instanceof NoSuchMethodException) {
                    throw (NoSuchMethodException)exception;
                }
                if (exception instanceof IllegalAccessException) {
                    throw (IllegalAccessException)exception;
                }
                if (exception instanceof InvocationTargetException) {
                    throw (InvocationTargetException)exception;
                }
                if (exception instanceof RuntimeException) {
                    throw (RuntimeException)exception;
                }
                if (exception instanceof Error) {
                    throw (Error)exception;
                }
            }
            final ActiveEntry activeEntry2 = new ActiveEntry(remote);
            this.active.put(activationID, activeEntry2);
            return activeEntry2.mobj;
        }
        catch (final NoSuchMethodException | NoSuchMethodError noSuchMethodException | NoSuchMethodError) {
            throw new ActivationException("Activatable object must provide an activation constructor", (Throwable)noSuchMethodException | NoSuchMethodError);
        }
        catch (final InvocationTargetException ex2) {
            throw new ActivationException("exception in object constructor", ex2.getTargetException());
        }
        catch (final Exception ex3) {
            throw new ActivationException("unable to activate object", ex3);
        }
        finally {
            this.releaseLock(activationID);
            this.checkInactiveGroup();
        }
    }
    
    @Override
    public boolean inactiveObject(final ActivationID activationID) throws ActivationException, UnknownObjectException, RemoteException {
        try {
            this.acquireLock(activationID);
            synchronized (this) {
                if (this.groupInactive) {
                    throw new ActivationException("group is inactive");
                }
            }
            final ActiveEntry activeEntry = this.active.get(activationID);
            if (activeEntry == null) {
                throw new UnknownObjectException("object not active");
            }
            try {
                if (!Activatable.unexportObject(activeEntry.impl, false)) {
                    return false;
                }
            }
            catch (final NoSuchObjectException ex) {}
            try {
                super.inactiveObject(activationID);
            }
            catch (final UnknownObjectException ex2) {}
            this.active.remove(activationID);
        }
        finally {
            this.releaseLock(activationID);
            this.checkInactiveGroup();
        }
        return true;
    }
    
    private void checkInactiveGroup() {
        boolean b = false;
        synchronized (this) {
            if (this.active.size() == 0 && this.lockedIDs.size() == 0 && !this.groupInactive) {
                this.groupInactive = true;
                b = true;
            }
        }
        if (b) {
            try {
                super.inactiveGroup();
            }
            catch (final Exception ex) {}
            try {
                UnicastRemoteObject.unexportObject(this, true);
            }
            catch (final NoSuchObjectException ex2) {}
        }
    }
    
    @Override
    public void activeObject(final ActivationID activationID, final Remote remote) throws ActivationException, UnknownObjectException, RemoteException {
        try {
            this.acquireLock(activationID);
            synchronized (this) {
                if (this.groupInactive) {
                    throw new ActivationException("group is inactive");
                }
            }
            if (!this.active.contains(activationID)) {
                final ActiveEntry activeEntry = new ActiveEntry(remote);
                this.active.put(activationID, activeEntry);
                try {
                    super.activeObject(activationID, activeEntry.mobj);
                }
                catch (final RemoteException ex) {}
            }
        }
        finally {
            this.releaseLock(activationID);
            this.checkInactiveGroup();
        }
    }
    
    private static boolean covers(ClassLoader parent, final ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }
        if (parent == null) {
            return false;
        }
        while (parent != classLoader) {
            parent = parent.getParent();
            if (parent == null) {
                return false;
            }
        }
        return true;
    }
    
    private static class ServerSocketFactoryImpl implements RMIServerSocketFactory
    {
        @Override
        public ServerSocket createServerSocket(final int n) throws IOException {
            RMISocketFactory rmiSocketFactory = RMISocketFactory.getSocketFactory();
            if (rmiSocketFactory == null) {
                rmiSocketFactory = RMISocketFactory.getDefaultSocketFactory();
            }
            return rmiSocketFactory.createServerSocket(n);
        }
    }
    
    private static class ActiveEntry
    {
        Remote impl;
        MarshalledObject<Remote> mobj;
        
        ActiveEntry(final Remote impl) throws ActivationException {
            this.impl = impl;
            try {
                this.mobj = new MarshalledObject<Remote>(impl);
            }
            catch (final IOException ex) {
                throw new ActivationException("failed to marshal remote object", ex);
            }
        }
    }
}
