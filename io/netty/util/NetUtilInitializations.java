package io.netty.util;

import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Iterator;
import java.util.Enumeration;
import java.util.List;
import java.net.SocketException;
import io.netty.util.internal.SocketUtils;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.net.Inet6Address;
import io.netty.util.internal.PlatformDependent;
import java.net.InetAddress;
import java.net.Inet4Address;
import io.netty.util.internal.logging.InternalLogger;

final class NetUtilInitializations
{
    private static final InternalLogger logger;
    
    private NetUtilInitializations() {
    }
    
    static Inet4Address createLocalhost4() {
        final byte[] LOCALHOST4_BYTES = { 127, 0, 0, 1 };
        Inet4Address localhost4 = null;
        try {
            localhost4 = (Inet4Address)InetAddress.getByAddress("localhost", LOCALHOST4_BYTES);
        }
        catch (final Exception e) {
            PlatformDependent.throwException(e);
        }
        return localhost4;
    }
    
    static Inet6Address createLocalhost6() {
        final byte[] LOCALHOST6_BYTES = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 };
        Inet6Address localhost6 = null;
        try {
            localhost6 = (Inet6Address)InetAddress.getByAddress("localhost", LOCALHOST6_BYTES);
        }
        catch (final Exception e) {
            PlatformDependent.throwException(e);
        }
        return localhost6;
    }
    
    static NetworkIfaceAndInetAddress determineLoopback(final Inet4Address localhost4, final Inet6Address localhost6) {
        final List<NetworkInterface> ifaces = new ArrayList<NetworkInterface>();
        try {
            final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    final NetworkInterface iface = interfaces.nextElement();
                    if (SocketUtils.addressesFromNetworkInterface(iface).hasMoreElements()) {
                        ifaces.add(iface);
                    }
                }
            }
        }
        catch (final SocketException e) {
            NetUtilInitializations.logger.warn("Failed to retrieve the list of available network interfaces", e);
        }
        NetworkInterface loopbackIface = null;
        InetAddress loopbackAddr = null;
    Label_0164:
        for (final NetworkInterface iface2 : ifaces) {
            final Enumeration<InetAddress> i = SocketUtils.addressesFromNetworkInterface(iface2);
            while (i.hasMoreElements()) {
                final InetAddress addr = i.nextElement();
                if (addr.isLoopbackAddress()) {
                    loopbackIface = iface2;
                    loopbackAddr = addr;
                    break Label_0164;
                }
            }
        }
        if (loopbackIface == null) {
            try {
                for (final NetworkInterface iface2 : ifaces) {
                    if (iface2.isLoopback()) {
                        final Enumeration<InetAddress> i = SocketUtils.addressesFromNetworkInterface(iface2);
                        if (i.hasMoreElements()) {
                            loopbackIface = iface2;
                            loopbackAddr = i.nextElement();
                            break;
                        }
                        continue;
                    }
                }
                if (loopbackIface == null) {
                    NetUtilInitializations.logger.warn("Failed to find the loopback interface");
                }
            }
            catch (final SocketException e2) {
                NetUtilInitializations.logger.warn("Failed to find the loopback interface", e2);
            }
        }
        if (loopbackIface != null) {
            NetUtilInitializations.logger.debug("Loopback interface: {} ({}, {})", loopbackIface.getName(), loopbackIface.getDisplayName(), loopbackAddr.getHostAddress());
        }
        else if (loopbackAddr == null) {
            try {
                if (NetworkInterface.getByInetAddress(localhost6) != null) {
                    NetUtilInitializations.logger.debug("Using hard-coded IPv6 localhost address: {}", localhost6);
                    loopbackAddr = localhost6;
                }
            }
            catch (final Exception ex) {}
            finally {
                if (loopbackAddr == null) {
                    NetUtilInitializations.logger.debug("Using hard-coded IPv4 localhost address: {}", localhost4);
                    loopbackAddr = localhost4;
                }
            }
        }
        return new NetworkIfaceAndInetAddress(loopbackIface, loopbackAddr);
    }
    
    static {
        logger = InternalLoggerFactory.getInstance(NetUtilInitializations.class);
    }
    
    static final class NetworkIfaceAndInetAddress
    {
        private final NetworkInterface iface;
        private final InetAddress address;
        
        NetworkIfaceAndInetAddress(final NetworkInterface iface, final InetAddress address) {
            this.iface = iface;
            this.address = address;
        }
        
        public NetworkInterface iface() {
            return this.iface;
        }
        
        public InetAddress address() {
            return this.address;
        }
    }
}
