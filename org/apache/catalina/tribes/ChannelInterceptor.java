package org.apache.catalina.tribes;

import org.apache.catalina.tribes.group.InterceptorPayload;

public interface ChannelInterceptor extends MembershipListener, Heartbeat
{
    int getOptionFlag();
    
    void setOptionFlag(final int p0);
    
    void setNext(final ChannelInterceptor p0);
    
    ChannelInterceptor getNext();
    
    void setPrevious(final ChannelInterceptor p0);
    
    ChannelInterceptor getPrevious();
    
    void sendMessage(final Member[] p0, final ChannelMessage p1, final InterceptorPayload p2) throws ChannelException;
    
    void messageReceived(final ChannelMessage p0);
    
    void heartbeat();
    
    boolean hasMembers();
    
    Member[] getMembers();
    
    Member getLocalMember(final boolean p0);
    
    Member getMember(final Member p0);
    
    void start(final int p0) throws ChannelException;
    
    void stop(final int p0) throws ChannelException;
    
    void fireInterceptorEvent(final InterceptorEvent p0);
    
    Channel getChannel();
    
    void setChannel(final Channel p0);
    
    public interface InterceptorEvent
    {
        int getEventType();
        
        String getEventTypeDesc();
        
        ChannelInterceptor getInterceptor();
    }
}
