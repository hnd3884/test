package org.glassfish.hk2.api.messaging;

import org.jvnet.hk2.annotations.Contract;

@Contract
public interface TopicDistributionService
{
    public static final String HK2_DEFAULT_TOPIC_DISTRIBUTOR = "HK2TopicDistributionService";
    
    void distributeMessage(final Topic<?> p0, final Object p1);
}
