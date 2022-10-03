package java.beans;

import com.sun.beans.finder.ClassFinder;
import java.io.ObjectStreamClass;
import java.io.StreamCorruptedException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

class ObjectInputStreamWithLoader extends ObjectInputStream
{
    private ClassLoader loader;
    
    public ObjectInputStreamWithLoader(final InputStream inputStream, final ClassLoader loader) throws IOException, StreamCorruptedException {
        super(inputStream);
        if (loader == null) {
            throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
        }
        this.loader = loader;
    }
    
    @Override
    protected Class resolveClass(final ObjectStreamClass objectStreamClass) throws IOException, ClassNotFoundException {
        return ClassFinder.resolveClass(objectStreamClass.getName(), this.loader);
    }
}
