package org.apache.catalina.tribes.group;

import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.UniqueId;
import java.io.Serializable;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.ChannelException;

public interface GroupChannelMBean
{
    boolean getOptionCheck();
    
    boolean getHeartbeat();
    
    long getHeartbeatSleeptime();
    
    void start(final int p0) throws ChannelException;
    
    void stop(final int p0) throws ChannelException;
    
    UniqueId send(final Member[] p0, final Serializable p1, final int p2) throws ChannelException;
    
    UniqueId send(final Member[] p0, final Serializable p1, final int p2, final ErrorHandler p3) throws ChannelException;
    
    void addMembershipListener(final MembershipListener p0);
    
    void addChannelListener(final ChannelListener p0);
    
    void removeMembershipListener(final MembershipListener p0);
    
    void removeChannelListener(final ChannelListener p0);
    
    boolean hasMembers();
    
    Member[] getMembers();
    
    Member getLocalMember(final boolean p0);
}
