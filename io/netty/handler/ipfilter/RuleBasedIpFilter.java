package io.netty.handler.ipfilter;

import java.net.SocketAddress;
import java.util.Iterator;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import io.netty.util.internal.ObjectUtil;
import java.util.List;
import io.netty.channel.ChannelHandler;
import java.net.InetSocketAddress;

@ChannelHandler.Sharable
public class RuleBasedIpFilter extends AbstractRemoteAddressFilter<InetSocketAddress>
{
    private final boolean acceptIfNotFound;
    private final List<IpFilterRule> rules;
    
    public RuleBasedIpFilter(final IpFilterRule... rules) {
        this(true, rules);
    }
    
    public RuleBasedIpFilter(final boolean acceptIfNotFound, final IpFilterRule... rules) {
        ObjectUtil.checkNotNull(rules, "rules");
        this.acceptIfNotFound = acceptIfNotFound;
        this.rules = new ArrayList<IpFilterRule>(rules.length);
        for (final IpFilterRule rule : rules) {
            if (rule != null) {
                this.rules.add(rule);
            }
        }
    }
    
    @Override
    protected boolean accept(final ChannelHandlerContext ctx, final InetSocketAddress remoteAddress) throws Exception {
        for (final IpFilterRule rule : this.rules) {
            if (rule.matches(remoteAddress)) {
                return rule.ruleType() == IpFilterRuleType.ACCEPT;
            }
        }
        return this.acceptIfNotFound;
    }
}
