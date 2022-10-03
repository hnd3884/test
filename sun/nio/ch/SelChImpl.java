package sun.nio.ch;

import java.io.IOException;
import java.io.FileDescriptor;
import java.nio.channels.Channel;

public interface SelChImpl extends Channel
{
    FileDescriptor getFD();
    
    int getFDVal();
    
    boolean translateAndUpdateReadyOps(final int p0, final SelectionKeyImpl p1);
    
    boolean translateAndSetReadyOps(final int p0, final SelectionKeyImpl p1);
    
    void translateAndSetInterestOps(final int p0, final SelectionKeyImpl p1);
    
    int validOps();
    
    void kill() throws IOException;
}
