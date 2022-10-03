package org.apache.catalina.tribes;

import java.io.Serializable;

public interface Member extends Serializable
{
    public static final byte[] SHUTDOWN_PAYLOAD = { 66, 65, 66, 89, 45, 65, 76, 69, 88 };
    
    String getName();
    
    byte[] getHost();
    
    int getPort();
    
    int getSecurePort();
    
    int getUdpPort();
    
    long getMemberAliveTime();
    
    void setMemberAliveTime(final long p0);
    
    boolean isReady();
    
    boolean isSuspect();
    
    boolean isFailing();
    
    byte[] getUniqueId();
    
    byte[] getPayload();
    
    void setPayload(final byte[] p0);
    
    byte[] getCommand();
    
    void setCommand(final byte[] p0);
    
    byte[] getDomain();
    
    byte[] getData(final boolean p0);
    
    byte[] getData(final boolean p0, final boolean p1);
    
    int getDataLength();
    
    boolean isLocal();
    
    void setLocal(final boolean p0);
}
