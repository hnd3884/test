package javax.rmi.CORBA;

import java.security.Permission;
import java.io.SerializablePermission;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import org.omg.CORBA.ORB;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.SystemException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class Util
{
    private static final UtilDelegate utilDelegate;
    private static final String UtilClassKey = "javax.rmi.CORBA.UtilClass";
    private static final String ALLOW_CREATEVALUEHANDLER_PROP = "jdk.rmi.CORBA.allowCustomValueHandler";
    private static boolean allowCustomValueHandler;
    
    private static boolean readAllowCustomValueHandlerProperty() {
        return AccessController.doPrivileged((PrivilegedAction<Boolean>)new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return Boolean.getBoolean("jdk.rmi.CORBA.allowCustomValueHandler");
            }
        });
    }
    
    private Util() {
    }
    
    public static RemoteException mapSystemException(final SystemException ex) {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.mapSystemException(ex);
        }
        return null;
    }
    
    public static void writeAny(final OutputStream outputStream, final Object o) {
        if (Util.utilDelegate != null) {
            Util.utilDelegate.writeAny(outputStream, o);
        }
    }
    
    public static Object readAny(final InputStream inputStream) {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.readAny(inputStream);
        }
        return null;
    }
    
    public static void writeRemoteObject(final OutputStream outputStream, final Object o) {
        if (Util.utilDelegate != null) {
            Util.utilDelegate.writeRemoteObject(outputStream, o);
        }
    }
    
    public static void writeAbstractObject(final OutputStream outputStream, final Object o) {
        if (Util.utilDelegate != null) {
            Util.utilDelegate.writeAbstractObject(outputStream, o);
        }
    }
    
    public static void registerTarget(final Tie tie, final Remote remote) {
        if (Util.utilDelegate != null) {
            Util.utilDelegate.registerTarget(tie, remote);
        }
    }
    
    public static void unexportObject(final Remote remote) throws NoSuchObjectException {
        if (Util.utilDelegate != null) {
            Util.utilDelegate.unexportObject(remote);
        }
    }
    
    public static Tie getTie(final Remote remote) {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.getTie(remote);
        }
        return null;
    }
    
    public static ValueHandler createValueHandler() {
        isCustomSerializationPermitted();
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.createValueHandler();
        }
        return null;
    }
    
    public static String getCodebase(final Class clazz) {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.getCodebase(clazz);
        }
        return null;
    }
    
    public static Class loadClass(final String s, final String s2, final ClassLoader classLoader) throws ClassNotFoundException {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.loadClass(s, s2, classLoader);
        }
        return null;
    }
    
    public static boolean isLocal(final Stub stub) throws RemoteException {
        return Util.utilDelegate != null && Util.utilDelegate.isLocal(stub);
    }
    
    public static RemoteException wrapException(final Throwable t) {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.wrapException(t);
        }
        return null;
    }
    
    public static Object[] copyObjects(final Object[] array, final ORB orb) throws RemoteException {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.copyObjects(array, orb);
        }
        return null;
    }
    
    public static Object copyObject(final Object o, final ORB orb) throws RemoteException {
        if (Util.utilDelegate != null) {
            return Util.utilDelegate.copyObject(o, orb);
        }
        return null;
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
            return new com.sun.corba.se.impl.javax.rmi.CORBA.Util();
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
    
    private static void isCustomSerializationPermitted() {
        final SecurityManager securityManager = System.getSecurityManager();
        if (!Util.allowCustomValueHandler && securityManager != null) {
            securityManager.checkPermission(new SerializablePermission("enableCustomValueHandler"));
        }
    }
    
    static {
        utilDelegate = (UtilDelegate)createDelegate("javax.rmi.CORBA.UtilClass");
        Util.allowCustomValueHandler = readAllowCustomValueHandlerProperty();
    }
}
