package sun.applet;

import java.lang.reflect.Array;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

class AppletObjectInputStream extends ObjectInputStream
{
    private AppletClassLoader loader;
    
    public AppletObjectInputStream(final InputStream inputStream, final AppletClassLoader loader) throws IOException, StreamCorruptedException {
        super(inputStream);
        if (loader == null) {
            throw new AppletIllegalArgumentException("appletillegalargumentexception.objectinputstream");
        }
        this.loader = loader;
    }
    
    private Class primitiveType(final char c) {
        switch (c) {
            case 'B': {
                return Byte.TYPE;
            }
            case 'C': {
                return Character.TYPE;
            }
            case 'D': {
                return Double.TYPE;
            }
            case 'F': {
                return Float.TYPE;
            }
            case 'I': {
                return Integer.TYPE;
            }
            case 'J': {
                return Long.TYPE;
            }
            case 'S': {
                return Short.TYPE;
            }
            case 'Z': {
                return Boolean.TYPE;
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    protected Class resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        final String name = objectStreamClass.getName();
        if (name.startsWith("[")) {
            int n;
            for (n = 1; name.charAt(n) == '['; ++n) {}
            Class<?> clazz;
            if (name.charAt(n) == 'L') {
                clazz = this.loader.loadClass(name.substring(n + 1, name.length() - 1));
            }
            else {
                if (name.length() != n + 1) {
                    throw new ClassNotFoundException(name);
                }
                clazz = this.primitiveType(name.charAt(n));
            }
            final int[] array = new int[n];
            for (int i = 0; i < n; ++i) {
                array[i] = 0;
            }
            return Array.newInstance(clazz, array).getClass();
        }
        return this.loader.loadClass(name);
    }
}
