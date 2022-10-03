package org.apache.catalina.tribes;

import java.util.Iterator;

public interface ManagedChannel extends Channel
{
    void setChannelSender(final ChannelSender p0);
    
    void setChannelReceiver(final ChannelReceiver p0);
    
    void setMembershipService(final MembershipService p0);
    
    ChannelSender getChannelSender();
    
    ChannelReceiver getChannelReceiver();
    
    MembershipService getMembershipService();
    
    Iterator<ChannelInterceptor> getInterceptors();
}
