package java.rmi.activation;

import java.rmi.RemoteException;
import java.rmi.MarshalledObject;
import java.rmi.Remote;

public interface ActivationInstantiator extends Remote
{
    MarshalledObject<? extends Remote> newInstance(final ActivationID p0, final ActivationDesc p1) throws ActivationException, RemoteException;
}
