package javax.rmi.CORBA;

import org.omg.CORBA.ORB;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.SystemException;

public interface UtilDelegate
{
    RemoteException mapSystemException(final SystemException p0);
    
    void writeAny(final OutputStream p0, final Object p1);
    
    Object readAny(final InputStream p0);
    
    void writeRemoteObject(final OutputStream p0, final Object p1);
    
    void writeAbstractObject(final OutputStream p0, final Object p1);
    
    void registerTarget(final Tie p0, final Remote p1);
    
    void unexportObject(final Remote p0) throws NoSuchObjectException;
    
    Tie getTie(final Remote p0);
    
    ValueHandler createValueHandler();
    
    String getCodebase(final Class p0);
    
    Class loadClass(final String p0, final String p1, final ClassLoader p2) throws ClassNotFoundException;
    
    boolean isLocal(final Stub p0) throws RemoteException;
    
    RemoteException wrapException(final Throwable p0);
    
    Object copyObject(final Object p0, final ORB p1) throws RemoteException;
    
    Object[] copyObjects(final Object[] p0, final ORB p1) throws RemoteException;
}
