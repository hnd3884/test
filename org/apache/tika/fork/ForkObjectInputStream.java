package org.apache.tika.fork;

import java.io.ObjectStreamClass;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

class ForkObjectInputStream extends ObjectInputStream
{
    private final ClassLoader loader;
    
    public ForkObjectInputStream(final InputStream input, final ClassLoader loader) throws IOException {
        super(input);
        this.loader = loader;
    }
    
    public static void sendObject(final Object object, final DataOutputStream output) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        final ObjectOutputStream serializer = new ObjectOutputStream(buffer);
        serializer.writeObject(object);
        serializer.close();
        final byte[] data = buffer.toByteArray();
        output.writeInt(data.length);
        output.write(data);
    }
    
    public static Object readObject(final DataInputStream input, final ClassLoader loader) throws IOException, ClassNotFoundException {
        final int n = input.readInt();
        final byte[] data = new byte[n];
        input.readFully(data);
        final ObjectInputStream deserializer = new ForkObjectInputStream(new ByteArrayInputStream(data), loader);
        return deserializer.readObject();
    }
    
    @Override
    protected Class<?> resolveClass(final ObjectStreamClass desc) throws ClassNotFoundException {
        return Class.forName(desc.getName(), false, this.loader);
    }
}
