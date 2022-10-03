package sun.awt.datatransfer;

import java.util.HashSet;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.util.HashMap;
import java.io.OutputStream;
import java.util.Set;
import java.util.Map;
import java.io.ObjectOutputStream;

final class ClassLoaderObjectOutputStream extends ObjectOutputStream
{
    private final Map<Set<String>, ClassLoader> map;
    
    ClassLoaderObjectOutputStream(final OutputStream outputStream) throws IOException {
        super(outputStream);
        this.map = new HashMap<Set<String>, ClassLoader>();
    }
    
    @Override
    protected void annotateClass(final Class<?> clazz) throws IOException {
        final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getClassLoader();
            }
        });
        final HashSet set = new HashSet(1);
        set.add(clazz.getName());
        this.map.put(set, classLoader);
    }
    
    @Override
    protected void annotateProxyClass(final Class<?> clazz) throws IOException {
        final ClassLoader classLoader = AccessController.doPrivileged((PrivilegedAction<ClassLoader>)new PrivilegedAction() {
            @Override
            public Object run() {
                return clazz.getClassLoader();
            }
        });
        final Class[] interfaces = clazz.getInterfaces();
        final HashSet set = new HashSet(interfaces.length);
        for (int i = 0; i < interfaces.length; ++i) {
            set.add((Object)interfaces[i].getName());
        }
        this.map.put((HashSet)set, classLoader);
    }
    
    Map<Set<String>, ClassLoader> getClassLoaderMap() {
        return new HashMap<Set<String>, ClassLoader>(this.map);
    }
}
