package org.apache.catalina.tribes;

import java.io.Serializable;

public interface Channel
{
    public static final int DEFAULT = 15;
    public static final int SND_RX_SEQ = 1;
    public static final int SND_TX_SEQ = 2;
    public static final int MBR_RX_SEQ = 4;
    public static final int MBR_TX_SEQ = 8;
    public static final int SEND_OPTIONS_BYTE_MESSAGE = 1;
    public static final int SEND_OPTIONS_USE_ACK = 2;
    public static final int SEND_OPTIONS_SYNCHRONIZED_ACK = 4;
    public static final int SEND_OPTIONS_ASYNCHRONOUS = 8;
    public static final int SEND_OPTIONS_SECURE = 16;
    public static final int SEND_OPTIONS_UDP = 32;
    public static final int SEND_OPTIONS_MULTICAST = 64;
    public static final int SEND_OPTIONS_DEFAULT = 2;
    
    void addInterceptor(final ChannelInterceptor p0);
    
    void start(final int p0) throws ChannelException;
    
    void stop(final int p0) throws ChannelException;
    
    UniqueId send(final Member[] p0, final Serializable p1, final int p2) throws ChannelException;
    
    UniqueId send(final Member[] p0, final Serializable p1, final int p2, final ErrorHandler p3) throws ChannelException;
    
    void heartbeat();
    
    void setHeartbeat(final boolean p0);
    
    void addMembershipListener(final MembershipListener p0);
    
    void addChannelListener(final ChannelListener p0);
    
    void removeMembershipListener(final MembershipListener p0);
    
    void removeChannelListener(final ChannelListener p0);
    
    boolean hasMembers();
    
    Member[] getMembers();
    
    Member getLocalMember(final boolean p0);
    
    Member getMember(final Member p0);
    
    String getName();
    
    void setName(final String p0);
}
