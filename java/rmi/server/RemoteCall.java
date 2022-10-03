package java.rmi.server;

import java.io.StreamCorruptedException;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

@Deprecated
public interface RemoteCall
{
    @Deprecated
    ObjectOutput getOutputStream() throws IOException;
    
    @Deprecated
    void releaseOutputStream() throws IOException;
    
    @Deprecated
    ObjectInput getInputStream() throws IOException;
    
    @Deprecated
    void releaseInputStream() throws IOException;
    
    @Deprecated
    ObjectOutput getResultStream(final boolean p0) throws IOException, StreamCorruptedException;
    
    @Deprecated
    void executeCall() throws Exception;
    
    @Deprecated
    void done() throws IOException;
}
