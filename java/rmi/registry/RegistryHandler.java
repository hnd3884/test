package java.rmi.registry;

import java.rmi.UnknownHostException;
import java.rmi.RemoteException;

@Deprecated
public interface RegistryHandler
{
    @Deprecated
    Registry registryStub(final String p0, final int p1) throws RemoteException, UnknownHostException;
    
    @Deprecated
    Registry registryImpl(final int p0) throws RemoteException;
}
