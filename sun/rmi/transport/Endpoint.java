package sun.rmi.transport;

import java.rmi.RemoteException;

public interface Endpoint
{
    Channel getChannel();
    
    void exportObject(final Target p0) throws RemoteException;
    
    Transport getInboundTransport();
    
    Transport getOutboundTransport();
}
