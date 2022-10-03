package javax.management.remote.rmi;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface RMIServer extends Remote
{
    String getVersion() throws RemoteException;
    
    RMIConnection newClient(final Object p0) throws IOException;
}
