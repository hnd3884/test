package javax.rmi.CORBA;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.Remote;

public interface PortableRemoteObjectDelegate
{
    void exportObject(final Remote p0) throws RemoteException;
    
    Remote toStub(final Remote p0) throws NoSuchObjectException;
    
    void unexportObject(final Remote p0) throws NoSuchObjectException;
    
    Object narrow(final Object p0, final Class p1) throws ClassCastException;
    
    void connect(final Remote p0, final Remote p1) throws RemoteException;
}
