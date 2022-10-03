package sun.nio.ch;

import java.nio.channels.SelectableChannel;
import java.io.IOException;
import java.io.FileDescriptor;

abstract class FileDispatcher extends NativeDispatcher
{
    public static final int NO_LOCK = -1;
    public static final int LOCKED = 0;
    public static final int RET_EX_LOCK = 1;
    public static final int INTERRUPTED = 2;
    
    abstract long seek(final FileDescriptor p0, final long p1) throws IOException;
    
    abstract int force(final FileDescriptor p0, final boolean p1) throws IOException;
    
    abstract int truncate(final FileDescriptor p0, final long p1) throws IOException;
    
    abstract long size(final FileDescriptor p0) throws IOException;
    
    abstract int lock(final FileDescriptor p0, final boolean p1, final long p2, final long p3, final boolean p4) throws IOException;
    
    abstract void release(final FileDescriptor p0, final long p1, final long p2) throws IOException;
    
    abstract FileDescriptor duplicateForMapping(final FileDescriptor p0) throws IOException;
    
    abstract boolean canTransferToDirectly(final SelectableChannel p0);
    
    abstract boolean transferToDirectlyNeedsPositionLock();
}
