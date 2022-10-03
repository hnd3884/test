package java.rmi.activation;

import java.rmi.Naming;
import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetIntegerAction;
import java.lang.reflect.InvocationTargetException;
import java.rmi.MarshalledObject;
import java.rmi.server.RMIClassLoader;
import sun.rmi.server.ActivationGroupImpl;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public abstract class ActivationGroup extends UnicastRemoteObject implements ActivationInstantiator
{
    private ActivationGroupID groupID;
    private ActivationMonitor monitor;
    private long incarnation;
    private static ActivationGroup currGroup;
    private static ActivationGroupID currGroupID;
    private static ActivationSystem currSystem;
    private static boolean canCreate;
    private static final long serialVersionUID = -7696947875314805420L;
    
    protected ActivationGroup(final ActivationGroupID groupID) throws RemoteException {
        this.groupID = groupID;
    }
    
    public boolean inactiveObject(final ActivationID activationID) throws ActivationException, UnknownObjectException, RemoteException {
        this.getMonitor().inactiveObject(activationID);
        return true;
    }
    
    public abstract void activeObject(final ActivationID p0, final Remote p1) throws ActivationException, UnknownObjectException, RemoteException;
    
    public static synchronized ActivationGroup createGroup(final ActivationGroupID currGroupID, final ActivationGroupDesc activationGroupDesc, final long incarnation) throws ActivationException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        if (ActivationGroup.currGroup != null) {
            throw new ActivationException("group already exists");
        }
        if (!ActivationGroup.canCreate) {
            throw new ActivationException("group deactivated and cannot be recreated");
        }
        try {
            final String className = activationGroupDesc.getClassName();
            final Class<ActivationGroupImpl> clazz = ActivationGroupImpl.class;
            Class<? extends ActivationGroup> subclass;
            if (className == null || className.equals(clazz.getName())) {
                subclass = clazz;
            }
            else {
                Class<?> loadClass;
                try {
                    loadClass = RMIClassLoader.loadClass(activationGroupDesc.getLocation(), className);
                }
                catch (final Exception ex) {
                    throw new ActivationException("Could not load group implementation class", ex);
                }
                if (!ActivationGroup.class.isAssignableFrom(loadClass)) {
                    throw new ActivationException("group not correct class: " + loadClass.getName());
                }
                subclass = loadClass.asSubclass(ActivationGroup.class);
            }
            final ActivationGroup currGroup = (ActivationGroup)subclass.getConstructor(ActivationGroupID.class, MarshalledObject.class).newInstance(currGroupID, activationGroupDesc.getData());
            ActivationGroup.currSystem = currGroupID.getSystem();
            currGroup.incarnation = incarnation;
            currGroup.monitor = ActivationGroup.currSystem.activeGroup(currGroupID, currGroup, incarnation);
            ActivationGroup.currGroup = currGroup;
            ActivationGroup.currGroupID = currGroupID;
            ActivationGroup.canCreate = false;
        }
        catch (final InvocationTargetException ex2) {
            ex2.getTargetException().printStackTrace();
            throw new ActivationException("exception in group constructor", ex2.getTargetException());
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new ActivationException("exception creating group", ex4);
        }
        return ActivationGroup.currGroup;
    }
    
    public static synchronized ActivationGroupID currentGroupID() {
        return ActivationGroup.currGroupID;
    }
    
    static synchronized ActivationGroupID internalCurrentGroupID() throws ActivationException {
        if (ActivationGroup.currGroupID == null) {
            throw new ActivationException("nonexistent group");
        }
        return ActivationGroup.currGroupID;
    }
    
    public static synchronized void setSystem(final ActivationSystem currSystem) throws ActivationException {
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            securityManager.checkSetFactory();
        }
        if (ActivationGroup.currSystem != null) {
            throw new ActivationException("activation system already set");
        }
        ActivationGroup.currSystem = currSystem;
    }
    
    public static synchronized ActivationSystem getSystem() throws ActivationException {
        if (ActivationGroup.currSystem == null) {
            try {
                ActivationGroup.currSystem = (ActivationSystem)Naming.lookup("//:" + (int)AccessController.doPrivileged((PrivilegedAction<Integer>)new GetIntegerAction("java.rmi.activation.port", 1098)) + "/java.rmi.activation.ActivationSystem");
            }
            catch (final Exception ex) {
                throw new ActivationException("unable to obtain ActivationSystem", ex);
            }
        }
        return ActivationGroup.currSystem;
    }
    
    protected void activeObject(final ActivationID activationID, final MarshalledObject<? extends Remote> marshalledObject) throws ActivationException, UnknownObjectException, RemoteException {
        this.getMonitor().activeObject(activationID, marshalledObject);
    }
    
    protected void inactiveGroup() throws UnknownGroupException, RemoteException {
        try {
            this.getMonitor().inactiveGroup(this.groupID, this.incarnation);
        }
        finally {
            destroyGroup();
        }
    }
    
    private ActivationMonitor getMonitor() throws RemoteException {
        synchronized (ActivationGroup.class) {
            if (this.monitor != null) {
                return this.monitor;
            }
        }
        throw new RemoteException("monitor not received");
    }
    
    private static synchronized void destroyGroup() {
        ActivationGroup.currGroup = null;
        ActivationGroup.currGroupID = null;
    }
    
    static synchronized ActivationGroup currentGroup() throws ActivationException {
        if (ActivationGroup.currGroup == null) {
            throw new ActivationException("group is not active");
        }
        return ActivationGroup.currGroup;
    }
    
    static {
        ActivationGroup.canCreate = true;
    }
}
