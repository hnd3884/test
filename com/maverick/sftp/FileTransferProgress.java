package com.maverick.sftp;

public interface FileTransferProgress
{
    void started(final long p0, final String p1);
    
    boolean isCancelled();
    
    void progressed(final long p0);
    
    void completed();
}
