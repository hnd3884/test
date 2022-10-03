package sun.rmi.server;

import java.rmi.activation.ActivationGroupDesc;
import java.rmi.activation.UnknownObjectException;
import java.rmi.activation.ActivationDesc;
import java.rmi.activation.ActivationID;
import java.rmi.activation.UnknownGroupException;
import java.rmi.UnexpectedException;
import java.rmi.activation.ActivationException;
import java.rmi.RemoteException;
import java.rmi.activation.ActivationMonitor;
import java.rmi.activation.ActivationInstantiator;
import java.rmi.activation.ActivationGroupID;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.activation.ActivationSystem;
import java.rmi.server.RemoteStub;

public final class Activation$ActivationSystemImpl_Stub extends RemoteStub implements ActivationSystem, Remote
{
    private static final long serialVersionUID = 2L;
    private static Method $method_activeGroup_0;
    private static Method $method_getActivationDesc_1;
    private static Method $method_getActivationGroupDesc_2;
    private static Method $method_registerGroup_3;
    private static Method $method_registerObject_4;
    private static Method $method_setActivationDesc_5;
    private static Method $method_setActivationGroupDesc_6;
    private static Method $method_shutdown_7;
    private static Method $method_unregisterGroup_8;
    private static Method $method_unregisterObject_9;
    static /* synthetic */ Class class$java$rmi$activation$ActivationSystem;
    static /* synthetic */ Class class$java$rmi$activation$ActivationGroupID;
    static /* synthetic */ Class class$java$rmi$activation$ActivationInstantiator;
    static /* synthetic */ Class class$java$rmi$activation$ActivationID;
    static /* synthetic */ Class class$java$rmi$activation$ActivationGroupDesc;
    static /* synthetic */ Class class$java$rmi$activation$ActivationDesc;
    
    static {
        try {
            Activation$ActivationSystemImpl_Stub.$method_activeGroup_0 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("activeGroup", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")), (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationInstantiator != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationInstantiator : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationInstantiator = class$("java.rmi.activation.ActivationInstantiator")), Long.TYPE);
            Activation$ActivationSystemImpl_Stub.$method_getActivationDesc_1 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("getActivationDesc", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")));
            Activation$ActivationSystemImpl_Stub.$method_getActivationGroupDesc_2 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("getActivationGroupDesc", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")));
            Activation$ActivationSystemImpl_Stub.$method_registerGroup_3 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("registerGroup", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc = class$("java.rmi.activation.ActivationGroupDesc")));
            Activation$ActivationSystemImpl_Stub.$method_registerObject_4 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("registerObject", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
            Activation$ActivationSystemImpl_Stub.$method_setActivationDesc_5 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("setActivationDesc", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")), (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
            Activation$ActivationSystemImpl_Stub.$method_setActivationGroupDesc_6 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("setActivationGroupDesc", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")), (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupDesc = class$("java.rmi.activation.ActivationGroupDesc")));
            Activation$ActivationSystemImpl_Stub.$method_shutdown_7 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("shutdown", (Class[])new Class[0]);
            Activation$ActivationSystemImpl_Stub.$method_unregisterGroup_8 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("unregisterGroup", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationGroupID = class$("java.rmi.activation.ActivationGroupID")));
            Activation$ActivationSystemImpl_Stub.$method_unregisterObject_9 = ((Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationSystem = class$("java.rmi.activation.ActivationSystem"))).getMethod("unregisterObject", (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID != null) ? Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID : (Activation$ActivationSystemImpl_Stub.class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")));
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public Activation$ActivationSystemImpl_Stub(final RemoteRef remoteRef) {
        super(remoteRef);
    }
    
    public ActivationMonitor activeGroup(final ActivationGroupID activationGroupID, final ActivationInstantiator activationInstantiator, final long n) throws RemoteException, ActivationException, UnknownGroupException {
        try {
            return (ActivationMonitor)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_activeGroup_0, new Object[] { activationGroupID, activationInstantiator, new Long(n) }, -4575843150759415294L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    static /* synthetic */ Class class$(final String s) {
        try {
            return Class.forName(s);
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError(ex.getMessage());
        }
    }
    
    public ActivationDesc getActivationDesc(final ActivationID activationID) throws RemoteException, ActivationException, UnknownObjectException {
        try {
            return (ActivationDesc)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_getActivationDesc_1, new Object[] { activationID }, 4830055440982622087L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public ActivationGroupDesc getActivationGroupDesc(final ActivationGroupID activationGroupID) throws RemoteException, ActivationException, UnknownGroupException {
        try {
            return (ActivationGroupDesc)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_getActivationGroupDesc_2, new Object[] { activationGroupID }, -8701843806548736528L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public ActivationGroupID registerGroup(final ActivationGroupDesc activationGroupDesc) throws RemoteException, ActivationException {
        try {
            return (ActivationGroupID)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_registerGroup_3, new Object[] { activationGroupDesc }, 6921515268192657754L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public ActivationID registerObject(final ActivationDesc activationDesc) throws RemoteException, ActivationException, UnknownGroupException {
        try {
            return (ActivationID)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_registerObject_4, new Object[] { activationDesc }, -3006759798994351347L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public ActivationDesc setActivationDesc(final ActivationID activationID, final ActivationDesc activationDesc) throws RemoteException, ActivationException, UnknownGroupException, UnknownObjectException {
        try {
            return (ActivationDesc)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_setActivationDesc_5, new Object[] { activationID, activationDesc }, 7128043237057180796L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public ActivationGroupDesc setActivationGroupDesc(final ActivationGroupID activationGroupID, final ActivationGroupDesc activationGroupDesc) throws RemoteException, ActivationException, UnknownGroupException {
        try {
            return (ActivationGroupDesc)super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_setActivationGroupDesc_6, new Object[] { activationGroupID, activationGroupDesc }, 1213918527826541191L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void shutdown() throws RemoteException {
        try {
            super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_shutdown_7, null, -7207851917985848402L);
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
    
    public void unregisterGroup(final ActivationGroupID activationGroupID) throws RemoteException, ActivationException, UnknownGroupException {
        try {
            super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_unregisterGroup_8, new Object[] { activationGroupID }, 3768097077835970701L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
    
    public void unregisterObject(final ActivationID activationID) throws RemoteException, ActivationException, UnknownObjectException {
        try {
            super.ref.invoke(this, Activation$ActivationSystemImpl_Stub.$method_unregisterObject_9, new Object[] { activationID }, -6843850585331411084L);
        }
        catch (final RuntimeException ex) {
            throw ex;
        }
        catch (final RemoteException ex2) {
            throw ex2;
        }
        catch (final ActivationException ex3) {
            throw ex3;
        }
        catch (final Exception ex4) {
            throw new UnexpectedException("undeclared checked exception", ex4);
        }
    }
}
