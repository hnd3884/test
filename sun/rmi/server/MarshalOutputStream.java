package sun.rmi.server;

import java.rmi.server.RMIClassLoader;
import sun.rmi.transport.Target;
import sun.rmi.transport.ObjectTable;
import java.rmi.server.RemoteStub;
import java.rmi.Remote;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;

public class MarshalOutputStream extends ObjectOutputStream
{
    public MarshalOutputStream(final OutputStream outputStream) throws IOException {
        this(outputStream, 1);
    }
    
    public MarshalOutputStream(final OutputStream outputStream, final int n) throws IOException {
        super(outputStream);
        this.useProtocolVersion(n);
        AccessController.doPrivileged((PrivilegedAction<Object>)new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                ObjectOutputStream.this.enableReplaceObject(true);
                return null;
            }
        });
    }
    
    @Override
    protected final Object replaceObject(final Object o) throws IOException {
        if (o instanceof Remote && !(o instanceof RemoteStub)) {
            final Target target = ObjectTable.getTarget((Remote)o);
            if (target != null) {
                return target.getStub();
            }
        }
        return o;
    }
    
    @Override
    protected void annotateClass(final Class<?> clazz) throws IOException {
        this.writeLocation(RMIClassLoader.getClassAnnotation(clazz));
    }
    
    @Override
    protected void annotateProxyClass(final Class<?> clazz) throws IOException {
        this.annotateClass(clazz);
    }
    
    protected void writeLocation(final String s) throws IOException {
        this.writeObject(s);
    }
}
