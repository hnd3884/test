package java.rmi.activation;

import java.rmi.UnexpectedException;
import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.server.RemoteRef;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.server.RemoteStub;

public final class ActivationGroup_Stub extends RemoteStub implements ActivationInstantiator, Remote
{
    private static final long serialVersionUID = 2L;
    private static Method $method_newInstance_0;
    static /* synthetic */ Class class$java$rmi$activation$ActivationInstantiator;
    static /* synthetic */ Class class$java$rmi$activation$ActivationID;
    static /* synthetic */ Class class$java$rmi$activation$ActivationDesc;
    
    static {
        try {
            ActivationGroup_Stub.$method_newInstance_0 = ((ActivationGroup_Stub.class$java$rmi$activation$ActivationInstantiator != null) ? ActivationGroup_Stub.class$java$rmi$activation$ActivationInstantiator : (ActivationGroup_Stub.class$java$rmi$activation$ActivationInstantiator = class$("java.rmi.activation.ActivationInstantiator"))).getMethod("newInstance", (ActivationGroup_Stub.class$java$rmi$activation$ActivationID != null) ? ActivationGroup_Stub.class$java$rmi$activation$ActivationID : (ActivationGroup_Stub.class$java$rmi$activation$ActivationID = class$("java.rmi.activation.ActivationID")), (ActivationGroup_Stub.class$java$rmi$activation$ActivationDesc != null) ? ActivationGroup_Stub.class$java$rmi$activation$ActivationDesc : (ActivationGroup_Stub.class$java$rmi$activation$ActivationDesc = class$("java.rmi.activation.ActivationDesc")));
        }
        catch (final NoSuchMethodException ex) {
            throw new NoSuchMethodError("stub class initialization failed");
        }
    }
    
    public ActivationGroup_Stub(final RemoteRef remoteRef) {
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
    
    public MarshalledObject newInstance(final ActivationID activationID, final ActivationDesc activationDesc) throws RemoteException, ActivationException {
        try {
            return (MarshalledObject)super.ref.invoke(this, ActivationGroup_Stub.$method_newInstance_0, new Object[] { activationID, activationDesc }, -5274445189091581345L);
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
