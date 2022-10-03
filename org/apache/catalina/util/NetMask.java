package org.apache.catalina.util;

import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import org.apache.tomcat.util.res.StringManager;

public final class NetMask
{
    private static final StringManager sm;
    private final String expression;
    private final byte[] netaddr;
    private final int nrBytes;
    private final int lastByteShift;
    private final boolean foundPort;
    private final Pattern portPattern;
    
    public NetMask(final String input) {
        this.expression = input;
        final int portIdx = input.indexOf(59);
        String nonPortPart;
        if (portIdx == -1) {
            this.foundPort = false;
            nonPortPart = input;
            this.portPattern = null;
        }
        else {
            this.foundPort = true;
            nonPortPart = input.substring(0, portIdx);
            try {
                this.portPattern = Pattern.compile(input.substring(portIdx + 1));
            }
            catch (final PatternSyntaxException e) {
                throw new IllegalArgumentException(NetMask.sm.getString("netmask.invalidPort", new Object[] { input }), e);
            }
        }
        final int idx = nonPortPart.indexOf(47);
        if (idx == -1) {
            try {
                this.netaddr = InetAddress.getByName(nonPortPart).getAddress();
            }
            catch (final UnknownHostException e2) {
                throw new IllegalArgumentException(NetMask.sm.getString("netmask.invalidAddress", new Object[] { nonPortPart }));
            }
            this.nrBytes = this.netaddr.length;
            this.lastByteShift = 0;
            return;
        }
        final String addressPart = nonPortPart.substring(0, idx);
        final String cidrPart = nonPortPart.substring(idx + 1);
        try {
            this.netaddr = InetAddress.getByName(addressPart).getAddress();
        }
        catch (final UnknownHostException e3) {
            throw new IllegalArgumentException(NetMask.sm.getString("netmask.invalidAddress", new Object[] { addressPart }));
        }
        final int addrlen = this.netaddr.length * 8;
        int cidr;
        try {
            cidr = Integer.parseInt(cidrPart);
        }
        catch (final NumberFormatException e4) {
            throw new IllegalArgumentException(NetMask.sm.getString("netmask.cidrNotNumeric", new Object[] { cidrPart }));
        }
        if (cidr < 0) {
            throw new IllegalArgumentException(NetMask.sm.getString("netmask.cidrNegative", new Object[] { cidrPart }));
        }
        if (cidr > addrlen) {
            throw new IllegalArgumentException(NetMask.sm.getString("netmask.cidrTooBig", new Object[] { cidrPart, addrlen }));
        }
        this.nrBytes = cidr / 8;
        final int remainder = cidr % 8;
        this.lastByteShift = ((remainder == 0) ? 0 : (8 - remainder));
    }
    
    public boolean matches(final InetAddress addr, final int port) {
        if (!this.foundPort) {
            return false;
        }
        final String portString = Integer.toString(port);
        return this.portPattern.matcher(portString).matches() && this.matches(addr, true);
    }
    
    public boolean matches(final InetAddress addr) {
        return this.matches(addr, false);
    }
    
    public boolean matches(final InetAddress addr, final boolean checkedPort) {
        if (!checkedPort && this.foundPort) {
            return false;
        }
        final byte[] candidate = addr.getAddress();
        if (candidate.length != this.netaddr.length) {
            return false;
        }
        int i;
        for (i = 0; i < this.nrBytes; ++i) {
            if (this.netaddr[i] != candidate[i]) {
                return false;
            }
        }
        if (this.lastByteShift == 0) {
            return true;
        }
        final int lastByte = this.netaddr[i] ^ candidate[i];
        return lastByte >> this.lastByteShift == 0;
    }
    
    @Override
    public String toString() {
        return this.expression;
    }
    
    static {
        sm = StringManager.getManager((Class)NetMask.class);
    }
}
