package sun.nio.ch;

import java.util.List;
import java.nio.channels.OverlappingFileLockException;
import java.nio.channels.FileLock;
import java.io.IOException;
import java.io.FileDescriptor;
import java.nio.channels.Channel;

abstract class FileLockTable
{
    protected FileLockTable() {
    }
    
    public static FileLockTable newSharedFileLockTable(final Channel channel, final FileDescriptor fileDescriptor) throws IOException {
        return new SharedFileLockTable(channel, fileDescriptor);
    }
    
    public abstract void add(final FileLock p0) throws OverlappingFileLockException;
    
    public abstract void remove(final FileLock p0);
    
    public abstract List<FileLock> removeAll();
    
    public abstract void replace(final FileLock p0, final FileLock p1);
}
