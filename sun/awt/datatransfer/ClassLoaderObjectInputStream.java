package sun.awt.datatransfer;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.io.ObjectStreamClass;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.Map;
import java.io.ObjectInputStream;

final class ClassLoaderObjectInputStream extends ObjectInputStream
{
    private final Map<Set<String>, ClassLoader> map;
    
    ClassLoaderObjectInputStream(final InputStream inputStream, final Map<Set<String>, ClassLoader> map) throws IOException {
        super(inputStream);
        if (map == null) {
            throw new NullPointerException("Null map");
        }
        this.map = map;
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        final String name = objectStreamClass.getName();
        final HashSet set = new HashSet(1);
        set.add(name);
        final ClassLoader classLoader = this.map.get(set);
        if (classLoader != null) {
            return Class.forName(name, false, classLoader);
        }
        return super.resolveClass(objectStreamClass);
    }
    
    @Override
    protected Class<?> resolveProxyClass(final String[] array) throws IOException, ClassNotFoundException {
        final HashSet set = new HashSet(array.length);
        for (int i = 0; i < array.length; ++i) {
            set.add(array[i]);
        }
        final ClassLoader classLoader = this.map.get(set);
        if (classLoader == null) {
            return super.resolveProxyClass(array);
        }
        ClassLoader classLoader2 = null;
        int n = 0;
        final Class[] array2 = new Class[array.length];
        for (int j = 0; j < array.length; ++j) {
            final Class<?> forName = Class.forName(array[j], false, classLoader);
            if ((forName.getModifiers() & 0x1) == 0x0) {
                if (n != 0) {
                    if (classLoader2 != forName.getClassLoader()) {
                        throw new IllegalAccessError("conflicting non-public interface class loaders");
                    }
                }
                else {
                    classLoader2 = forName.getClassLoader();
                    n = 1;
                }
            }
            array2[j] = forName;
        }
        try {
            return Proxy.getProxyClass((n != 0) ? classLoader2 : classLoader, (Class<?>[])array2);
        }
        catch (final IllegalArgumentException ex) {
            throw new ClassNotFoundException(null, ex);
        }
    }
}
