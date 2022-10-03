package javax.rmi;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.Remote;
import javax.rmi.CORBA.PortableRemoteObjectDelegate;

public class PortableRemoteObject
{
    private static final PortableRemoteObjectDelegate proDelegate;
    private static final String PortableRemoteObjectClassKey = "javax.rmi.CORBA.PortableRemoteObjectClass";
    
    protected PortableRemoteObject() throws RemoteException {
        if (PortableRemoteObject.proDelegate != null) {
            exportObject((Remote)this);
        }
    }
    
    public static void exportObject(final Remote remote) throws RemoteException {
        if (PortableRemoteObject.proDelegate != null) {
            PortableRemoteObject.proDelegate.exportObject(remote);
        }
    }
    
    public static Remote toStub(final Remote remote) throws NoSuchObjectException {
        if (PortableRemoteObject.proDelegate != null) {
            return PortableRemoteObject.proDelegate.toStub(remote);
        }
        return null;
    }
    
    public static void unexportObject(final Remote remote) throws NoSuchObjectException {
        if (PortableRemoteObject.proDelegate != null) {
            PortableRemoteObject.proDelegate.unexportObject(remote);
        }
    }
    
    public static Object narrow(final Object o, final Class clazz) throws ClassCastException {
        if (PortableRemoteObject.proDelegate != null) {
            return PortableRemoteObject.proDelegate.narrow(o, clazz);
        }
        return null;
    }
    
    public static void connect(final Remote remote, final Remote remote2) throws RemoteException {
        if (PortableRemoteObject.proDelegate != null) {
            PortableRemoteObject.proDelegate.connect(remote, remote2);
        }
    }
    
    private static Object createDelegate(final String s) {
        String property = AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction(s));
        if (property == null) {
            final Properties orbPropertiesFile = getORBPropertiesFile();
            if (orbPropertiesFile != null) {
                property = orbPropertiesFile.getProperty(s);
            }
        }
        if (property == null) {
            return new com.sun.corba.se.impl.javax.rmi.PortableRemoteObject();
        }
        try {
            return loadDelegateClass(property).newInstance();
        }
        catch (final ClassNotFoundException ex) {
            final INITIALIZE initialize = new INITIALIZE("Cannot instantiate " + property);
            initialize.initCause(ex);
            throw initialize;
        }
        catch (final Exception ex2) {
            final INITIALIZE initialize2 = new INITIALIZE("Error while instantiating" + property);
            initialize2.initCause(ex2);
            throw initialize2;
        }
    }
    
    private static Class loadDelegateClass(final String s) throws ClassNotFoundException {
        try {
            return Class.forName(s, false, Thread.currentThread().getContextClassLoader());
        }
        catch (final ClassNotFoundException ex) {
            try {
                return RMIClassLoader.loadClass(s);
            }
            catch (final MalformedURLException ex2) {
                throw new ClassNotFoundException("Could not load " + s + ": " + ex2.toString());
            }
        }
    }
    
    private static Properties getORBPropertiesFile() {
        return AccessController.doPrivileged((PrivilegedAction<Properties>)new GetORBPropertiesFileAction());
    }
    
    static {
        proDelegate = (PortableRemoteObjectDelegate)createDelegate("javax.rmi.CORBA.PortableRemoteObjectClass");
    }
}
