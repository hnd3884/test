package javax.crypto;

import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

final class extObjectInputStream extends ObjectInputStream
{
    private static ClassLoader systemClassLoader;
    
    extObjectInputStream(final InputStream inputStream) throws IOException, StreamCorruptedException {
        super(inputStream);
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        try {
            return super.resolveClass(objectStreamClass);
        }
        catch (final ClassNotFoundException ex) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                if (extObjectInputStream.systemClassLoader == null) {
                    extObjectInputStream.systemClassLoader = ClassLoader.getSystemClassLoader();
                }
                classLoader = extObjectInputStream.systemClassLoader;
                if (classLoader == null) {
                    throw new ClassNotFoundException(objectStreamClass.getName());
                }
            }
            return Class.forName(objectStreamClass.getName(), false, classLoader);
        }
    }
    
    static {
        extObjectInputStream.systemClassLoader = null;
    }
}
