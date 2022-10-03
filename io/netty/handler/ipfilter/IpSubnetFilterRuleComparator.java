package io.netty.handler.ipfilter;

import java.net.InetSocketAddress;
import java.util.Comparator;

final class IpSubnetFilterRuleComparator implements Comparator<Object>
{
    static final IpSubnetFilterRuleComparator INSTANCE;
    
    private IpSubnetFilterRuleComparator() {
    }
    
    @Override
    public int compare(final Object o1, final Object o2) {
        return ((IpSubnetFilterRule)o1).compareTo((InetSocketAddress)o2);
    }
    
    static {
        INSTANCE = new IpSubnetFilterRuleComparator();
    }
}
