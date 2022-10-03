package java.rmi;

import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.rmi.server.MarshalInputStream;
import java.io.ObjectOutputStream;
import sun.rmi.server.MarshalOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import sun.misc.ObjectInputFilter;
import java.io.Serializable;

public final class MarshalledObject<T> implements Serializable
{
    private byte[] objBytes;
    private byte[] locBytes;
    private int hash;
    private transient ObjectInputFilter objectInputFilter;
    private static final long serialVersionUID = 8988374069173025854L;
    
    public MarshalledObject(final T t) throws IOException {
        this.objBytes = null;
        this.locBytes = null;
        this.objectInputFilter = null;
        if (t == null) {
            this.hash = 13;
            return;
        }
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
        final MarshalledObjectOutputStream marshalledObjectOutputStream = new MarshalledObjectOutputStream(byteArrayOutputStream, byteArrayOutputStream2);
        marshalledObjectOutputStream.writeObject(t);
        marshalledObjectOutputStream.flush();
        this.objBytes = byteArrayOutputStream.toByteArray();
        this.locBytes = (byte[])(marshalledObjectOutputStream.hadAnnotations() ? byteArrayOutputStream2.toByteArray() : null);
        int hash = 0;
        for (int i = 0; i < this.objBytes.length; ++i) {
            hash = 31 * hash + this.objBytes[i];
        }
        this.hash = hash;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.objectInputFilter = ObjectInputFilter.Config.getObjectInputFilter(objectInputStream);
    }
    
    public T get() throws IOException, ClassNotFoundException {
        if (this.objBytes == null) {
            return null;
        }
        final MarshalledObjectInputStream marshalledObjectInputStream = new MarshalledObjectInputStream(new ByteArrayInputStream(this.objBytes), (this.locBytes == null) ? null : new ByteArrayInputStream(this.locBytes), this.objectInputFilter);
        final Object object = marshalledObjectInputStream.readObject();
        marshalledObjectInputStream.close();
        return (T)object;
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof MarshalledObject)) {
            return false;
        }
        final MarshalledObject marshalledObject = (MarshalledObject)o;
        if (this.objBytes == null || marshalledObject.objBytes == null) {
            return this.objBytes == marshalledObject.objBytes;
        }
        if (this.objBytes.length != marshalledObject.objBytes.length) {
            return false;
        }
        for (int i = 0; i < this.objBytes.length; ++i) {
            if (this.objBytes[i] != marshalledObject.objBytes[i]) {
                return false;
            }
        }
        return true;
    }
    
    private static class MarshalledObjectOutputStream extends MarshalOutputStream
    {
        private ObjectOutputStream locOut;
        private boolean hadAnnotations;
        
        MarshalledObjectOutputStream(final OutputStream outputStream, final OutputStream outputStream2) throws IOException {
            super(outputStream);
            this.useProtocolVersion(2);
            this.locOut = new ObjectOutputStream(outputStream2);
            this.hadAnnotations = false;
        }
        
        boolean hadAnnotations() {
            return this.hadAnnotations;
        }
        
        @Override
        protected void writeLocation(final String s) throws IOException {
            this.hadAnnotations |= (s != null);
            this.locOut.writeObject(s);
        }
        
        @Override
        public void flush() throws IOException {
            super.flush();
            this.locOut.flush();
        }
    }
    
    private static class MarshalledObjectInputStream extends MarshalInputStream
    {
        private ObjectInputStream locIn;
        
        MarshalledObjectInputStream(final InputStream inputStream, final InputStream inputStream2, final ObjectInputFilter objectInputFilter) throws IOException {
            super(inputStream);
            this.locIn = ((inputStream2 == null) ? null : new ObjectInputStream(inputStream2));
            if (objectInputFilter != null) {
                AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
                    @Override
                    public Void run() {
                        ObjectInputFilter.Config.setObjectInputFilter(MarshalledObjectInputStream.this, objectInputFilter);
                        if (MarshalledObjectInputStream.this.locIn != null) {
                            ObjectInputFilter.Config.setObjectInputFilter(MarshalledObjectInputStream.this.locIn, objectInputFilter);
                        }
                        return null;
                    }
                });
            }
        }
        
        @Override
        protected Object readLocation() throws IOException, ClassNotFoundException {
            return (this.locIn == null) ? null : this.locIn.readObject();
        }
    }
}
