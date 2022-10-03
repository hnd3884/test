package sun.misc;

import java.io.FileDescriptor;

public interface JavaIOFileDescriptorAccess
{
    void set(final FileDescriptor p0, final int p1);
    
    int get(final FileDescriptor p0);
    
    void setHandle(final FileDescriptor p0, final long p1);
    
    long getHandle(final FileDescriptor p0);
}
