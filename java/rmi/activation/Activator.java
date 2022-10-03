package java.rmi.activation;

import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;

public interface Activator extends Remote
{
    MarshalledObject<? extends Remote> activate(final ActivationID p0, final boolean p1) throws ActivationException, UnknownObjectException, RemoteException;
}
