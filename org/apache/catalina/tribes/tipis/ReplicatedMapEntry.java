package org.apache.catalina.tribes.tipis;

import java.io.IOException;
import java.io.Serializable;

public interface ReplicatedMapEntry extends Serializable
{
    boolean isDirty();
    
    boolean isDiffable();
    
    byte[] getDiff() throws IOException;
    
    void applyDiff(final byte[] p0, final int p1, final int p2) throws IOException, ClassNotFoundException;
    
    void resetDiff();
    
    void lock();
    
    void unlock();
    
    void setOwner(final Object p0);
    
    long getVersion();
    
    void setVersion(final long p0);
    
    long getLastTimeReplicated();
    
    void setLastTimeReplicated(final long p0);
    
    boolean isAccessReplicate();
    
    void accessEntry();
}
