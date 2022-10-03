package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Channel
{
    Connection newConnection() throws RemoteException;
    
    Endpoint getEndpoint();
    
    void free(final Connection p0, final boolean p1) throws RemoteException;
}
