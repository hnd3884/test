package org.apache.catalina.ha;

import org.apache.catalina.tribes.Member;
import java.io.Serializable;

public interface ClusterMessage extends Serializable
{
    Member getAddress();
    
    void setAddress(final Member p0);
    
    String getUniqueId();
    
    long getTimestamp();
    
    void setTimestamp(final long p0);
}
