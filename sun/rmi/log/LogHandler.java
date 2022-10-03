package sun.rmi.log;

import sun.rmi.server.MarshalInputStream;
import java.io.InputStream;
import sun.rmi.server.MarshalOutputStream;
import java.io.OutputStream;

public abstract class LogHandler
{
    public abstract Object initialSnapshot() throws Exception;
    
    public void snapshot(final OutputStream outputStream, final Object o) throws Exception {
        final MarshalOutputStream marshalOutputStream = new MarshalOutputStream(outputStream);
        marshalOutputStream.writeObject(o);
        marshalOutputStream.flush();
    }
    
    public Object recover(final InputStream inputStream) throws Exception {
        return new MarshalInputStream(inputStream).readObject();
    }
    
    public void writeUpdate(final LogOutputStream logOutputStream, final Object o) throws Exception {
        final MarshalOutputStream marshalOutputStream = new MarshalOutputStream(logOutputStream);
        marshalOutputStream.writeObject(o);
        marshalOutputStream.flush();
    }
    
    public Object readUpdate(final LogInputStream logInputStream, final Object o) throws Exception {
        return this.applyUpdate(new MarshalInputStream(logInputStream).readObject(), o);
    }
    
    public abstract Object applyUpdate(final Object p0, final Object p1) throws Exception;
}
