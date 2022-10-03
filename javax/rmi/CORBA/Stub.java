package javax.rmi.CORBA;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.util.Properties;
import org.omg.CORBA.INITIALIZE;
import com.sun.corba.se.impl.javax.rmi.CORBA.StubDelegateImpl;
import java.security.PrivilegedAction;
import java.security.AccessController;
import com.sun.corba.se.impl.orbutil.GetPropertyAction;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import org.omg.CORBA.ORB;
import java.io.Serializable;
import org.omg.CORBA_2_3.portable.ObjectImpl;

public abstract class Stub extends ObjectImpl implements Serializable
{
    private static final long serialVersionUID = 1087775603798577179L;
    private transient StubDelegate stubDelegate;
    private static Class stubDelegateClass;
    private static final String StubClassKey = "javax.rmi.CORBA.StubClass";
    
    public Stub() {
        this.stubDelegate = null;
    }
    
    @Override
    public int hashCode() {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        if (this.stubDelegate != null) {
            return this.stubDelegate.hashCode(this);
        }
        return 0;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        return this.stubDelegate != null && this.stubDelegate.equals(this, o);
    }
    
    @Override
    public String toString() {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        if (this.stubDelegate == null) {
            return super.toString();
        }
        final String string = this.stubDelegate.toString(this);
        if (string == null) {
            return super.toString();
        }
        return string;
    }
    
    public void connect(final ORB orb) throws RemoteException {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        if (this.stubDelegate != null) {
            this.stubDelegate.connect(this, orb);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        if (this.stubDelegate != null) {
            this.stubDelegate.readObject(this, objectInputStream);
        }
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException {
        if (this.stubDelegate == null) {
            this.setDefaultDelegate();
        }
        if (this.stubDelegate != null) {
            this.stubDelegate.writeObject(this, objectOutputStream);
        }
    }
    
    private void setDefaultDelegate() {
        if (Stub.stubDelegateClass != null) {
            try {
                this.stubDelegate = Stub.stubDelegateClass.newInstance();
            }
            catch (final Exception ex) {}
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
            return new StubDelegateImpl();
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
        Stub.stubDelegateClass = null;
        final Object delegate = createDelegate("javax.rmi.CORBA.StubClass");
        if (delegate != null) {
            Stub.stubDelegateClass = delegate.getClass();
        }
    }
}
