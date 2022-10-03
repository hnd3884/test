package org.apache.catalina.tribes;

import org.apache.catalina.tribes.io.XByteBuffer;
import java.io.Serializable;

public interface ChannelMessage extends Serializable, Cloneable
{
    Member getAddress();
    
    void setAddress(final Member p0);
    
    long getTimestamp();
    
    void setTimestamp(final long p0);
    
    byte[] getUniqueId();
    
    void setMessage(final XByteBuffer p0);
    
    XByteBuffer getMessage();
    
    int getOptions();
    
    void setOptions(final int p0);
    
    Object clone();
    
    Object deepclone();
}
