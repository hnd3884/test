package org.apache.catalina.tribes;

import java.util.Properties;

public interface MembershipService
{
    public static final int MBR_RX = 4;
    public static final int MBR_TX = 8;
    
    void setProperties(final Properties p0);
    
    Properties getProperties();
    
    void start() throws Exception;
    
    void start(final int p0) throws Exception;
    
    void stop(final int p0);
    
    boolean hasMembers();
    
    Member getMember(final Member p0);
    
    Member[] getMembers();
    
    Member getLocalMember(final boolean p0);
    
    String[] getMembersByName();
    
    Member findMemberByName(final String p0);
    
    void setLocalMemberProperties(final String p0, final int p1, final int p2, final int p3);
    
    void setMembershipListener(final MembershipListener p0);
    
    void removeMembershipListener();
    
    void setPayload(final byte[] p0);
    
    void setDomain(final byte[] p0);
    
    void broadcast(final ChannelMessage p0) throws ChannelException;
    
    Channel getChannel();
    
    void setChannel(final Channel p0);
}
