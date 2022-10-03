package sun.rmi.server;

import java.security.PrivilegedAction;
import java.security.AccessController;
import sun.security.action.GetPropertyAction;
import sun.misc.ObjectStreamClassValidator;
import sun.misc.SharedSecrets;
import java.security.Permission;
import sun.misc.VM;
import java.security.AccessControlException;
import java.rmi.server.RMIClassLoader;
import java.io.ObjectStreamClass;
import java.util.Iterator;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import java.io.ObjectInputStream;

public class MarshalInputStream extends ObjectInputStream
{
    private volatile StreamChecker streamChecker;
    private static final boolean useCodebaseOnlyProperty;
    protected static Map<String, Class<?>> permittedSunClasses;
    private boolean skipDefaultResolveClass;
    private final Map<Object, Runnable> doneCallbacks;
    private boolean useCodebaseOnly;
    
    public MarshalInputStream(final InputStream inputStream) throws IOException, StreamCorruptedException {
        super(inputStream);
        this.streamChecker = null;
        this.skipDefaultResolveClass = false;
        this.doneCallbacks = new HashMap<Object, Runnable>(3);
        this.useCodebaseOnly = MarshalInputStream.useCodebaseOnlyProperty;
    }
    
    public Runnable getDoneCallback(final Object o) {
        return this.doneCallbacks.get(o);
    }
    
    public void setDoneCallback(final Object o, final Runnable runnable) {
        this.doneCallbacks.put(o, runnable);
    }
    
    public void done() {
        final Iterator<Runnable> iterator = this.doneCallbacks.values().iterator();
        while (iterator.hasNext()) {
            iterator.next().run();
        }
        this.doneCallbacks.clear();
    }
    
    @Override
    public void close() throws IOException {
        this.done();
        super.close();
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        final Object location = this.readLocation();
        final String name = objectStreamClass.getName();
        final ClassLoader classLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
        String s = null;
        if (!this.useCodebaseOnly && location instanceof String) {
            s = (String)location;
        }
        try {
            return RMIClassLoader.loadClass(s, name, classLoader);
        }
        catch (final AccessControlException ex) {
            return this.checkSunClass(name, ex);
        }
        catch (final ClassNotFoundException ex2) {
            try {
                if (Character.isLowerCase(name.charAt(0)) && name.indexOf(46) == -1) {
                    return super.resolveClass(objectStreamClass);
                }
            }
            catch (final ClassNotFoundException ex3) {}
            throw ex2;
        }
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] array) throws IOException, ClassNotFoundException {
        final StreamChecker streamChecker = this.streamChecker;
        if (streamChecker != null) {
            streamChecker.checkProxyInterfaceNames(array);
        }
        final Object location = this.readLocation();
        final ClassLoader classLoader = this.skipDefaultResolveClass ? null : latestUserDefinedLoader();
        String s = null;
        if (!this.useCodebaseOnly && location instanceof String) {
            s = (String)location;
        }
        return RMIClassLoader.loadProxyClass(s, array, classLoader);
    }
    
    private static ClassLoader latestUserDefinedLoader() {
        return VM.latestUserDefinedLoader();
    }
    
    private Class<?> checkSunClass(final String s, final AccessControlException ex) throws AccessControlException {
        final Permission permission = ex.getPermission();
        String name = null;
        if (permission != null) {
            name = permission.getName();
        }
        final Class clazz = MarshalInputStream.permittedSunClasses.get(s);
        if (name == null || clazz == null || (!name.equals("accessClassInPackage.sun.rmi.server") && !name.equals("accessClassInPackage.sun.rmi.registry"))) {
            throw ex;
        }
        return clazz;
    }
    
    protected Object readLocation() throws IOException, ClassNotFoundException {
        return this.readObject();
    }
    
    void skipDefaultResolveClass() {
        this.skipDefaultResolveClass = true;
    }
    
    void useCodebaseOnly() {
        this.useCodebaseOnly = true;
    }
    
    synchronized void setStreamChecker(final StreamChecker streamChecker) {
        this.streamChecker = streamChecker;
        SharedSecrets.getJavaObjectInputStreamAccess().setValidator(this, streamChecker);
    }
    
    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
        final ObjectStreamClass classDescriptor = super.readClassDescriptor();
        this.validateDesc(classDescriptor);
        return classDescriptor;
    }
    
    private void validateDesc(final ObjectStreamClass objectStreamClass) {
        final StreamChecker streamChecker;
        synchronized (this) {
            streamChecker = this.streamChecker;
        }
        if (streamChecker != null) {
            streamChecker.validateDescriptor(objectStreamClass);
        }
    }
    
    static {
        useCodebaseOnlyProperty = !AccessController.doPrivileged((PrivilegedAction<String>)new GetPropertyAction("java.rmi.server.useCodebaseOnly", "true")).equalsIgnoreCase("false");
        MarshalInputStream.permittedSunClasses = new HashMap<String, Class<?>>(3);
        try {
            final String s = "sun.rmi.server.Activation$ActivationSystemImpl_Stub";
            final String s2 = "sun.rmi.registry.RegistryImpl_Stub";
            MarshalInputStream.permittedSunClasses.put(s, Class.forName(s));
            MarshalInputStream.permittedSunClasses.put(s2, Class.forName(s2));
        }
        catch (final ClassNotFoundException ex) {
            throw new NoClassDefFoundError("Missing system class: " + ex.getMessage());
        }
    }
    
    interface StreamChecker extends ObjectStreamClassValidator
    {
        void checkProxyInterfaceNames(final String[] p0);
    }
}
