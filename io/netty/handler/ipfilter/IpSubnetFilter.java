package io.netty.handler.ipfilter;

import java.net.SocketAddress;
import java.util.Comparator;
import java.util.Collections;
import java.net.Inet4Address;
import io.netty.channel.ChannelHandlerContext;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class IpSubnetFilter extends AbstractRemoteAddressFilter<InetSocketAddress>
{
    private final boolean acceptIfNotFound;
    private final List<IpSubnetFilterRule> ipv4Rules;
    private final List<IpSubnetFilterRule> ipv6Rules;
    private final IpFilterRuleType ipFilterRuleTypeIPv4;
    private final IpFilterRuleType ipFilterRuleTypeIPv6;
    
    public IpSubnetFilter(final IpSubnetFilterRule... rules) {
        this(true, Arrays.asList((IpSubnetFilterRule[])ObjectUtil.checkNotNull((T[])rules, "rules")));
    }
    
    public IpSubnetFilter(final boolean acceptIfNotFound, final IpSubnetFilterRule... rules) {
        this(acceptIfNotFound, Arrays.asList((IpSubnetFilterRule[])ObjectUtil.checkNotNull((T[])rules, "rules")));
    }
    
    public IpSubnetFilter(final List<IpSubnetFilterRule> rules) {
        this(true, rules);
    }
    
    public IpSubnetFilter(final boolean acceptIfNotFound, final List<IpSubnetFilterRule> rules) {
        ObjectUtil.checkNotNull(rules, "rules");
        this.acceptIfNotFound = acceptIfNotFound;
        int numAcceptIPv4 = 0;
        int numRejectIPv4 = 0;
        int numAcceptIPv5 = 0;
        int numRejectIPv5 = 0;
        final List<IpSubnetFilterRule> unsortedIPv4Rules = new ArrayList<IpSubnetFilterRule>();
        final List<IpSubnetFilterRule> unsortedIPv6Rules = new ArrayList<IpSubnetFilterRule>();
        for (final IpSubnetFilterRule ipSubnetFilterRule : rules) {
            ObjectUtil.checkNotNull(ipSubnetFilterRule, "rule");
            if (ipSubnetFilterRule.getFilterRule() instanceof IpSubnetFilterRule.Ip4SubnetFilterRule) {
                unsortedIPv4Rules.add(ipSubnetFilterRule);
                if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
                    ++numAcceptIPv4;
                }
                else {
                    ++numRejectIPv4;
                }
            }
            else {
                unsortedIPv6Rules.add(ipSubnetFilterRule);
                if (ipSubnetFilterRule.ruleType() == IpFilterRuleType.ACCEPT) {
                    ++numAcceptIPv5;
                }
                else {
                    ++numRejectIPv5;
                }
            }
        }
        if (numAcceptIPv4 == 0 && numRejectIPv4 > 0) {
            this.ipFilterRuleTypeIPv4 = IpFilterRuleType.REJECT;
        }
        else if (numAcceptIPv4 > 0 && numRejectIPv4 == 0) {
            this.ipFilterRuleTypeIPv4 = IpFilterRuleType.ACCEPT;
        }
        else {
            this.ipFilterRuleTypeIPv4 = null;
        }
        if (numAcceptIPv5 == 0 && numRejectIPv5 > 0) {
            this.ipFilterRuleTypeIPv6 = IpFilterRuleType.REJECT;
        }
        else if (numAcceptIPv5 > 0 && numRejectIPv5 == 0) {
            this.ipFilterRuleTypeIPv6 = IpFilterRuleType.ACCEPT;
        }
        else {
            this.ipFilterRuleTypeIPv6 = null;
        }
        this.ipv4Rules = sortAndFilter(unsortedIPv4Rules);
        this.ipv6Rules = sortAndFilter(unsortedIPv6Rules);
    }
    
    @Override
    protected boolean accept(final ChannelHandlerContext ctx, final InetSocketAddress remoteAddress) {
        if (remoteAddress.getAddress() instanceof Inet4Address) {
            final int indexOf = Collections.binarySearch((List<? extends InetSocketAddress>)this.ipv4Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE);
            if (indexOf >= 0) {
                if (this.ipFilterRuleTypeIPv4 == null) {
                    return this.ipv4Rules.get(indexOf).ruleType() == IpFilterRuleType.ACCEPT;
                }
                return this.ipFilterRuleTypeIPv4 == IpFilterRuleType.ACCEPT;
            }
        }
        else {
            final int indexOf = Collections.binarySearch((List<? extends InetSocketAddress>)this.ipv6Rules, remoteAddress, IpSubnetFilterRuleComparator.INSTANCE);
            if (indexOf >= 0) {
                if (this.ipFilterRuleTypeIPv6 == null) {
                    return this.ipv6Rules.get(indexOf).ruleType() == IpFilterRuleType.ACCEPT;
                }
                return this.ipFilterRuleTypeIPv6 == IpFilterRuleType.ACCEPT;
            }
        }
        return this.acceptIfNotFound;
    }
    
    private static List<IpSubnetFilterRule> sortAndFilter(final List<IpSubnetFilterRule> rules) {
        Collections.sort(rules);
        final Iterator<IpSubnetFilterRule> iterator = rules.iterator();
        final List<IpSubnetFilterRule> toKeep = new ArrayList<IpSubnetFilterRule>();
        IpSubnetFilterRule parentRule = iterator.hasNext() ? iterator.next() : null;
        if (parentRule != null) {
            toKeep.add(parentRule);
        }
        while (iterator.hasNext()) {
            final IpSubnetFilterRule childRule = iterator.next();
            if (!parentRule.matches(new InetSocketAddress(childRule.getIpAddress(), 1))) {
                toKeep.add(childRule);
                parentRule = childRule;
            }
        }
        return toKeep;
    }
}
