package io.netty.handler.ipfilter;

import java.math.BigInteger;
import io.netty.util.NetUtil;
import java.net.InetSocketAddress;
import java.net.Inet6Address;
import java.net.Inet4Address;
import io.netty.util.internal.ObjectUtil;
import java.net.InetAddress;
import java.net.UnknownHostException;
import io.netty.util.internal.SocketUtils;

public final class IpSubnetFilterRule implements IpFilterRule, Comparable<IpSubnetFilterRule>
{
    private final IpFilterRule filterRule;
    private final String ipAddress;
    
    public IpSubnetFilterRule(final String ipAddress, final int cidrPrefix, final IpFilterRuleType ruleType) {
        try {
            this.ipAddress = ipAddress;
            this.filterRule = selectFilterRule(SocketUtils.addressByName(ipAddress), cidrPrefix, ruleType);
        }
        catch (final UnknownHostException e) {
            throw new IllegalArgumentException("ipAddress", e);
        }
    }
    
    public IpSubnetFilterRule(final InetAddress ipAddress, final int cidrPrefix, final IpFilterRuleType ruleType) {
        this.ipAddress = ipAddress.getHostAddress();
        this.filterRule = selectFilterRule(ipAddress, cidrPrefix, ruleType);
    }
    
    private static IpFilterRule selectFilterRule(final InetAddress ipAddress, final int cidrPrefix, final IpFilterRuleType ruleType) {
        ObjectUtil.checkNotNull(ipAddress, "ipAddress");
        ObjectUtil.checkNotNull(ruleType, "ruleType");
        if (ipAddress instanceof Inet4Address) {
            return new Ip4SubnetFilterRule((Inet4Address)ipAddress, cidrPrefix, ruleType);
        }
        if (ipAddress instanceof Inet6Address) {
            return new Ip6SubnetFilterRule((Inet6Address)ipAddress, cidrPrefix, ruleType);
        }
        throw new IllegalArgumentException("Only IPv4 and IPv6 addresses are supported");
    }
    
    @Override
    public boolean matches(final InetSocketAddress remoteAddress) {
        return this.filterRule.matches(remoteAddress);
    }
    
    @Override
    public IpFilterRuleType ruleType() {
        return this.filterRule.ruleType();
    }
    
    String getIpAddress() {
        return this.ipAddress;
    }
    
    IpFilterRule getFilterRule() {
        return this.filterRule;
    }
    
    @Override
    public int compareTo(final IpSubnetFilterRule ipSubnetFilterRule) {
        if (this.filterRule instanceof Ip4SubnetFilterRule) {
            return compareInt(((Ip4SubnetFilterRule)this.filterRule).networkAddress, ((Ip4SubnetFilterRule)ipSubnetFilterRule.filterRule).networkAddress);
        }
        return ((Ip6SubnetFilterRule)this.filterRule).networkAddress.compareTo(((Ip6SubnetFilterRule)ipSubnetFilterRule.filterRule).networkAddress);
    }
    
    int compareTo(final InetSocketAddress inetSocketAddress) {
        if (this.filterRule instanceof Ip4SubnetFilterRule) {
            final Ip4SubnetFilterRule ip4SubnetFilterRule = (Ip4SubnetFilterRule)this.filterRule;
            return compareInt(ip4SubnetFilterRule.networkAddress, NetUtil.ipv4AddressToInt((Inet4Address)inetSocketAddress.getAddress()) & ip4SubnetFilterRule.subnetMask);
        }
        final Ip6SubnetFilterRule ip6SubnetFilterRule = (Ip6SubnetFilterRule)this.filterRule;
        return ip6SubnetFilterRule.networkAddress.compareTo(ipToInt((Inet6Address)inetSocketAddress.getAddress()).and(ip6SubnetFilterRule.networkAddress));
    }
    
    private static int compareInt(final int x, final int y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }
    
    static final class Ip4SubnetFilterRule implements IpFilterRule
    {
        private final int networkAddress;
        private final int subnetMask;
        private final IpFilterRuleType ruleType;
        
        private Ip4SubnetFilterRule(final Inet4Address ipAddress, final int cidrPrefix, final IpFilterRuleType ruleType) {
            if (cidrPrefix < 0 || cidrPrefix > 32) {
                throw new IllegalArgumentException(String.format("IPv4 requires the subnet prefix to be in range of [0,32]. The prefix was: %d", cidrPrefix));
            }
            this.subnetMask = prefixToSubnetMask(cidrPrefix);
            this.networkAddress = (NetUtil.ipv4AddressToInt(ipAddress) & this.subnetMask);
            this.ruleType = ruleType;
        }
        
        @Override
        public boolean matches(final InetSocketAddress remoteAddress) {
            final InetAddress inetAddress = remoteAddress.getAddress();
            if (inetAddress instanceof Inet4Address) {
                final int ipAddress = NetUtil.ipv4AddressToInt((Inet4Address)inetAddress);
                return (ipAddress & this.subnetMask) == this.networkAddress;
            }
            return false;
        }
        
        @Override
        public IpFilterRuleType ruleType() {
            return this.ruleType;
        }
        
        private static int prefixToSubnetMask(final int cidrPrefix) {
            return (int)(-1L << 32 - cidrPrefix);
        }
    }
    
    static final class Ip6SubnetFilterRule implements IpFilterRule
    {
        private static final BigInteger MINUS_ONE;
        private final BigInteger networkAddress;
        private final BigInteger subnetMask;
        private final IpFilterRuleType ruleType;
        
        private Ip6SubnetFilterRule(final Inet6Address ipAddress, final int cidrPrefix, final IpFilterRuleType ruleType) {
            if (cidrPrefix < 0 || cidrPrefix > 128) {
                throw new IllegalArgumentException(String.format("IPv6 requires the subnet prefix to be in range of [0,128]. The prefix was: %d", cidrPrefix));
            }
            this.subnetMask = prefixToSubnetMask(cidrPrefix);
            this.networkAddress = ipToInt(ipAddress).and(this.subnetMask);
            this.ruleType = ruleType;
        }
        
        @Override
        public boolean matches(final InetSocketAddress remoteAddress) {
            final InetAddress inetAddress = remoteAddress.getAddress();
            if (inetAddress instanceof Inet6Address) {
                final BigInteger ipAddress = ipToInt((Inet6Address)inetAddress);
                return ipAddress.and(this.subnetMask).equals(this.subnetMask) || ipAddress.and(this.subnetMask).equals(this.networkAddress);
            }
            return false;
        }
        
        @Override
        public IpFilterRuleType ruleType() {
            return this.ruleType;
        }
        
        private static BigInteger ipToInt(final Inet6Address ipAddress) {
            final byte[] octets = ipAddress.getAddress();
            assert octets.length == 16;
            return new BigInteger(octets);
        }
        
        private static BigInteger prefixToSubnetMask(final int cidrPrefix) {
            return Ip6SubnetFilterRule.MINUS_ONE.shiftLeft(128 - cidrPrefix);
        }
        
        static {
            MINUS_ONE = BigInteger.valueOf(-1L);
        }
    }
}
