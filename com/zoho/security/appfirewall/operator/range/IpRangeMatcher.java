package com.zoho.security.appfirewall.operator.range;

import java.math.BigInteger;
import java.net.InetAddress;
import java.util.logging.Level;
import java.net.Inet6Address;
import java.net.Inet4Address;
import com.adventnet.iam.security.IPUtil;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class IpRangeMatcher implements RangeMatcher
{
    private static final Logger LOGGER;
    final List<RangeMatcher> rangeMatchers;
    
    private IpRangeMatcher() {
        this.rangeMatchers = new ArrayList<RangeMatcher>();
    }
    
    private IpRangeMatcher(final RangeMatcher rangeMatcher) {
        this.rangeMatchers = new ArrayList<RangeMatcher>();
        this.addRangeMatcher(rangeMatcher);
    }
    
    private void addRangeMatcher(final RangeMatcher rangematcher) {
        this.rangeMatchers.add(rangematcher);
    }
    
    @Override
    public boolean matches(final String address) {
        for (final RangeMatcher rangeMatcher : this.rangeMatchers) {
            final boolean matches = rangeMatcher.matches(address);
            if (matches) {
                return true;
            }
        }
        return false;
    }
    
    public static IpRangeMatcher newIpRangeMatcher(final String startAddress, final String endAddress) {
        final RangeMatcher rangeMatcher = newIpRangeMatcherInternal(startAddress, endAddress);
        if (rangeMatcher != null) {
            return new IpRangeMatcher(rangeMatcher);
        }
        return null;
    }
    
    private static RangeMatcher newIpRangeMatcherInternal(final String startAddress, final String endAddress) {
        final InetAddress startInetAddress = IPUtil.parseAddress(startAddress);
        final InetAddress endInetAddress = IPUtil.parseAddress(endAddress);
        if (startInetAddress instanceof Inet4Address && endInetAddress instanceof Inet4Address) {
            return new Ipv4RangeMatcher(startInetAddress, endInetAddress);
        }
        if (startInetAddress instanceof Inet6Address && endInetAddress instanceof Inet6Address) {
            return new Ipv6RangeMatcher(startInetAddress, endInetAddress);
        }
        IpRangeMatcher.LOGGER.log(Level.WARNING, "Invalid formate addresses are passed. start address is: {0} and end address is: {1}", new Object[] { startAddress, endAddress });
        return null;
    }
    
    public static IpRangeMatcher newIpv4RangeMatcher(final String startAddress, final String endAddress) {
        final InetAddress startInetAddress = IPUtil.parseAddress(startAddress);
        final InetAddress endInetAddress = IPUtil.parseAddress(endAddress);
        if (startInetAddress instanceof Inet6Address || endInetAddress instanceof Inet6Address) {
            IpRangeMatcher.LOGGER.log(Level.WARNING, "Passed address argument is in the formate of IPv6. start address is: {0} and end address is: {1}", new Object[] { startAddress, endAddress });
            return null;
        }
        return new IpRangeMatcher(new Ipv4RangeMatcher(startInetAddress, endInetAddress));
    }
    
    public static IpRangeMatcher newIpv6RangeMatcher(final String startAddress, final String endAddress) {
        final InetAddress startInetAddress = IPUtil.parseAddress(startAddress);
        final InetAddress endInetAddress = IPUtil.parseAddress(endAddress);
        if (startInetAddress instanceof Inet4Address || endInetAddress instanceof Inet4Address) {
            IpRangeMatcher.LOGGER.log(Level.SEVERE, "Passed address argument is in the formate of IPv4. start address is: {0} and end address is: {1}", new Object[] { startAddress, endAddress });
            return null;
        }
        return new IpRangeMatcher(new Ipv6RangeMatcher(startInetAddress, endInetAddress));
    }
    
    public static IpRangeMatcher newSubnetMatcher(final String address) {
        return new IpRangeMatcher(new SubnetMatcher(address));
    }
    
    public static IpRangeMatcher newSubnetMatcher(final String address, final int cidr) {
        return new IpRangeMatcher(new SubnetMatcher(IPUtil.parseAddress(address), cidr));
    }
    
    public static IpRangeMatcher newInstance(final String address) {
        if (address != null && !address.isEmpty()) {
            final IpRangeMatcher ipRangeMatcher = new IpRangeMatcher();
            for (final String addr : address.split("\\|")) {
                RangeMatcher rangeMatcher = null;
                Label_0155: {
                    if (addr.indexOf(45) != -1) {
                        final String endAddressAsString = addr.substring(addr.indexOf(45) + 1).trim();
                        final String startAddressAsString = addr.substring(0, addr.indexOf(45)).trim();
                        if (endAddressAsString.isEmpty()) {
                            break Label_0155;
                        }
                        if (startAddressAsString.isEmpty()) {
                            break Label_0155;
                        }
                        rangeMatcher = newIpRangeMatcherInternal(startAddressAsString, endAddressAsString);
                    }
                    else if (addr.indexOf(47) != -1) {
                        rangeMatcher = newSubnetMatcher(addr);
                    }
                    if (rangeMatcher != null) {
                        ipRangeMatcher.addRangeMatcher(rangeMatcher);
                    }
                }
            }
            return ipRangeMatcher;
        }
        return null;
    }
    
    static {
        LOGGER = Logger.getLogger(IpRangeMatcher.class.getName());
    }
    
    private static final class Ipv4RangeMatcher implements RangeMatcher
    {
        private final long start;
        private final long end;
        
        Ipv4RangeMatcher(final InetAddress startAddress, final InetAddress endAddress) {
            this.start = IPUtil.IPv4_TO_LONG(startAddress.getAddress());
            this.end = IPUtil.IPv4_TO_LONG(endAddress.getAddress());
        }
        
        @Override
        public boolean matches(final String address) {
            final InetAddress inetAddress = IPUtil.parseAddress(address);
            if (inetAddress instanceof Inet6Address) {
                return false;
            }
            final long ip = IPUtil.IPv4_TO_LONG(IPUtil.parseAddress(address).getAddress());
            return IPUtil.IS_IP_IN_RANGE(ip, this.start, this.end);
        }
    }
    
    private static final class Ipv6RangeMatcher implements RangeMatcher
    {
        private final BigInteger start;
        private final BigInteger end;
        
        Ipv6RangeMatcher(final InetAddress startAddress, final InetAddress endAddress) {
            this.start = IPUtil.IPv6_TO_BIGINT(startAddress.getAddress());
            this.end = IPUtil.IPv6_TO_BIGINT(endAddress.getAddress());
        }
        
        @Override
        public boolean matches(final String address) {
            final InetAddress inetAddress = IPUtil.parseAddress(address);
            if (inetAddress instanceof Inet4Address) {
                return false;
            }
            final BigInteger ip = IPUtil.IPv6_TO_BIGINT(inetAddress.getAddress());
            return ip.compareTo(this.start) >= 0 && ip.compareTo(this.end) <= 0;
        }
    }
    
    private static final class SubnetMatcher implements RangeMatcher
    {
        private final byte[] cidrAddress;
        private final int nMaskFullBytes;
        private final byte lastByte;
        
        SubnetMatcher(final String ipAddress) {
            this(IPUtil.parseAddress(IPUtil.REMOVE_CIDR(ipAddress)), IPUtil.GET_CIDR_VALUE(ipAddress));
        }
        
        SubnetMatcher(final InetAddress ipAddress, final int cidr) {
            this.nMaskFullBytes = cidr / 8;
            this.lastByte = (byte)(65280 >> (cidr & 0x7));
            this.cidrAddress = ipAddress.getAddress();
            if (this.cidrAddress.length * 8 < cidr) {
                throw new IllegalArgumentException(String.format("IP address %s is too short for bitmask of length %d", ipAddress, cidr));
            }
        }
        
        @Override
        public boolean matches(final String address) {
            final byte[] remAddr = IPUtil.parseAddress(address).getAddress();
            return IPUtil.isSubnetMatches(this.cidrAddress, remAddr, this.nMaskFullBytes, this.lastByte);
        }
    }
}
