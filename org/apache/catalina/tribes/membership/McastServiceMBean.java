package org.apache.catalina.tribes.membership;

import org.apache.catalina.tribes.Member;
import java.util.Properties;

public interface McastServiceMBean
{
    String getAddress();
    
    int getPort();
    
    long getFrequency();
    
    long getDropTime();
    
    String getBind();
    
    int getTtl();
    
    byte[] getDomain();
    
    int getSoTimeout();
    
    boolean getRecoveryEnabled();
    
    int getRecoveryCounter();
    
    long getRecoverySleepTime();
    
    boolean getLocalLoopbackDisabled();
    
    String getLocalMemberName();
    
    Properties getProperties();
    
    boolean hasMembers();
    
    String[] getMembersByName();
    
    Member findMemberByName(final String p0);
}
