package io.netty.handler.codec.haproxy;

import io.netty.util.ResourceLeakDetectorFactory;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import io.netty.util.NetUtil;
import java.util.Collection;
import java.util.ArrayList;
import io.netty.util.CharsetUtil;
import io.netty.util.ByteProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;
import io.netty.util.ResourceLeakTracker;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.AbstractReferenceCounted;

public final class HAProxyMessage extends AbstractReferenceCounted
{
    private static final ResourceLeakDetector<HAProxyMessage> leakDetector;
    private final ResourceLeakTracker<HAProxyMessage> leak;
    private final HAProxyProtocolVersion protocolVersion;
    private final HAProxyCommand command;
    private final HAProxyProxiedProtocol proxiedProtocol;
    private final String sourceAddress;
    private final String destinationAddress;
    private final int sourcePort;
    private final int destinationPort;
    private final List<HAProxyTLV> tlvs;
    
    private HAProxyMessage(final HAProxyProtocolVersion protocolVersion, final HAProxyCommand command, final HAProxyProxiedProtocol proxiedProtocol, final String sourceAddress, final String destinationAddress, final String sourcePort, final String destinationPort) {
        this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, portStringToInt(sourcePort), portStringToInt(destinationPort));
    }
    
    public HAProxyMessage(final HAProxyProtocolVersion protocolVersion, final HAProxyCommand command, final HAProxyProxiedProtocol proxiedProtocol, final String sourceAddress, final String destinationAddress, final int sourcePort, final int destinationPort) {
        this(protocolVersion, command, proxiedProtocol, sourceAddress, destinationAddress, sourcePort, destinationPort, Collections.emptyList());
    }
    
    public HAProxyMessage(final HAProxyProtocolVersion protocolVersion, final HAProxyCommand command, final HAProxyProxiedProtocol proxiedProtocol, final String sourceAddress, final String destinationAddress, final int sourcePort, final int destinationPort, final List<? extends HAProxyTLV> tlvs) {
        ObjectUtil.checkNotNull(protocolVersion, "protocolVersion");
        ObjectUtil.checkNotNull(proxiedProtocol, "proxiedProtocol");
        ObjectUtil.checkNotNull(tlvs, "tlvs");
        final HAProxyProxiedProtocol.AddressFamily addrFamily = proxiedProtocol.addressFamily();
        checkAddress(sourceAddress, addrFamily);
        checkAddress(destinationAddress, addrFamily);
        checkPort(sourcePort, addrFamily);
        checkPort(destinationPort, addrFamily);
        this.protocolVersion = protocolVersion;
        this.command = command;
        this.proxiedProtocol = proxiedProtocol;
        this.sourceAddress = sourceAddress;
        this.destinationAddress = destinationAddress;
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.tlvs = Collections.unmodifiableList(tlvs);
        this.leak = HAProxyMessage.leakDetector.track(this);
    }
    
    static HAProxyMessage decodeHeader(final ByteBuf header) {
        ObjectUtil.checkNotNull(header, "header");
        if (header.readableBytes() < 16) {
            throw new HAProxyProtocolException("incomplete header: " + header.readableBytes() + " bytes (expected: 16+ bytes)");
        }
        header.skipBytes(12);
        final byte verCmdByte = header.readByte();
        HAProxyProtocolVersion ver;
        try {
            ver = HAProxyProtocolVersion.valueOf(verCmdByte);
        }
        catch (final IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (ver != HAProxyProtocolVersion.V2) {
            throw new HAProxyProtocolException("version 1 unsupported: 0x" + Integer.toHexString(verCmdByte));
        }
        HAProxyCommand cmd;
        try {
            cmd = HAProxyCommand.valueOf(verCmdByte);
        }
        catch (final IllegalArgumentException e2) {
            throw new HAProxyProtocolException(e2);
        }
        if (cmd == HAProxyCommand.LOCAL) {
            return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.LOCAL);
        }
        HAProxyProxiedProtocol protAndFam;
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf(header.readByte());
        }
        catch (final IllegalArgumentException e3) {
            throw new HAProxyProtocolException(e3);
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return unknownMsg(HAProxyProtocolVersion.V2, HAProxyCommand.PROXY);
        }
        final int addressInfoLen = header.readUnsignedShort();
        int srcPort = 0;
        int dstPort = 0;
        final HAProxyProxiedProtocol.AddressFamily addressFamily = protAndFam.addressFamily();
        String srcAddress;
        String dstAddress;
        if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_UNIX) {
            if (addressInfoLen < 216 || header.readableBytes() < 216) {
                throw new HAProxyProtocolException("incomplete UNIX socket address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 216+ bytes)");
            }
            int startIdx = header.readerIndex();
            int addressEnd = header.forEachByte(startIdx, 108, ByteProcessor.FIND_NUL);
            int addressLen;
            if (addressEnd == -1) {
                addressLen = 108;
            }
            else {
                addressLen = addressEnd - startIdx;
            }
            srcAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
            startIdx += 108;
            addressEnd = header.forEachByte(startIdx, 108, ByteProcessor.FIND_NUL);
            if (addressEnd == -1) {
                addressLen = 108;
            }
            else {
                addressLen = addressEnd - startIdx;
            }
            dstAddress = header.toString(startIdx, addressLen, CharsetUtil.US_ASCII);
            header.readerIndex(startIdx + 108);
        }
        else {
            int addressLen;
            if (addressFamily == HAProxyProxiedProtocol.AddressFamily.AF_IPv4) {
                if (addressInfoLen < 12 || header.readableBytes() < 12) {
                    throw new HAProxyProtocolException("incomplete IPv4 address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 12+ bytes)");
                }
                addressLen = 4;
            }
            else {
                if (addressFamily != HAProxyProxiedProtocol.AddressFamily.AF_IPv6) {
                    throw new HAProxyProtocolException("unable to parse address information (unknown address family: " + addressFamily + ')');
                }
                if (addressInfoLen < 36 || header.readableBytes() < 36) {
                    throw new HAProxyProtocolException("incomplete IPv6 address information: " + Math.min(addressInfoLen, header.readableBytes()) + " bytes (expected: 36+ bytes)");
                }
                addressLen = 16;
            }
            srcAddress = ipBytesToString(header, addressLen);
            dstAddress = ipBytesToString(header, addressLen);
            srcPort = header.readUnsignedShort();
            dstPort = header.readUnsignedShort();
        }
        final List<HAProxyTLV> tlvs = readTlvs(header);
        return new HAProxyMessage(ver, cmd, protAndFam, srcAddress, dstAddress, srcPort, dstPort, tlvs);
    }
    
    private static List<HAProxyTLV> readTlvs(final ByteBuf header) {
        HAProxyTLV haProxyTLV = readNextTLV(header);
        if (haProxyTLV == null) {
            return Collections.emptyList();
        }
        final List<HAProxyTLV> haProxyTLVs = new ArrayList<HAProxyTLV>(4);
        do {
            haProxyTLVs.add(haProxyTLV);
            if (haProxyTLV instanceof HAProxySSLTLV) {
                haProxyTLVs.addAll(((HAProxySSLTLV)haProxyTLV).encapsulatedTLVs());
            }
        } while ((haProxyTLV = readNextTLV(header)) != null);
        return haProxyTLVs;
    }
    
    private static HAProxyTLV readNextTLV(final ByteBuf header) {
        if (header.readableBytes() < 4) {
            return null;
        }
        final byte typeAsByte = header.readByte();
        final HAProxyTLV.Type type = HAProxyTLV.Type.typeForByteValue(typeAsByte);
        final int length = header.readUnsignedShort();
        switch (type) {
            case PP2_TYPE_SSL: {
                final ByteBuf rawContent = header.retainedSlice(header.readerIndex(), length);
                final ByteBuf byteBuf = header.readSlice(length);
                final byte client = byteBuf.readByte();
                final int verify = byteBuf.readInt();
                if (byteBuf.readableBytes() >= 4) {
                    final List<HAProxyTLV> encapsulatedTlvs = new ArrayList<HAProxyTLV>(4);
                    do {
                        final HAProxyTLV haProxyTLV = readNextTLV(byteBuf);
                        if (haProxyTLV == null) {
                            break;
                        }
                        encapsulatedTlvs.add(haProxyTLV);
                    } while (byteBuf.readableBytes() >= 4);
                    return new HAProxySSLTLV(verify, client, encapsulatedTlvs, rawContent);
                }
                return new HAProxySSLTLV(verify, client, Collections.emptyList(), rawContent);
            }
            case PP2_TYPE_ALPN:
            case PP2_TYPE_AUTHORITY:
            case PP2_TYPE_SSL_VERSION:
            case PP2_TYPE_SSL_CN:
            case PP2_TYPE_NETNS:
            case OTHER: {
                return new HAProxyTLV(type, typeAsByte, header.readRetainedSlice(length));
            }
            default: {
                return null;
            }
        }
    }
    
    static HAProxyMessage decodeHeader(final String header) {
        if (header == null) {
            throw new HAProxyProtocolException("header");
        }
        final String[] parts = header.split(" ");
        final int numParts = parts.length;
        if (numParts < 2) {
            throw new HAProxyProtocolException("invalid header: " + header + " (expected: 'PROXY' and proxied protocol values)");
        }
        if (!"PROXY".equals(parts[0])) {
            throw new HAProxyProtocolException("unknown identifier: " + parts[0]);
        }
        HAProxyProxiedProtocol protAndFam;
        try {
            protAndFam = HAProxyProxiedProtocol.valueOf(parts[1]);
        }
        catch (final IllegalArgumentException e) {
            throw new HAProxyProtocolException(e);
        }
        if (protAndFam != HAProxyProxiedProtocol.TCP4 && protAndFam != HAProxyProxiedProtocol.TCP6 && protAndFam != HAProxyProxiedProtocol.UNKNOWN) {
            throw new HAProxyProtocolException("unsupported v1 proxied protocol: " + parts[1]);
        }
        if (protAndFam == HAProxyProxiedProtocol.UNKNOWN) {
            return unknownMsg(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY);
        }
        if (numParts != 6) {
            throw new HAProxyProtocolException("invalid TCP4/6 header: " + header + " (expected: 6 parts)");
        }
        try {
            return new HAProxyMessage(HAProxyProtocolVersion.V1, HAProxyCommand.PROXY, protAndFam, parts[2], parts[3], parts[4], parts[5]);
        }
        catch (final RuntimeException e2) {
            throw new HAProxyProtocolException("invalid HAProxy message", e2);
        }
    }
    
    private static HAProxyMessage unknownMsg(final HAProxyProtocolVersion version, final HAProxyCommand command) {
        return new HAProxyMessage(version, command, HAProxyProxiedProtocol.UNKNOWN, null, null, 0, 0);
    }
    
    private static String ipBytesToString(final ByteBuf header, final int addressLen) {
        final StringBuilder sb = new StringBuilder();
        final int ipv4Len = 4;
        final int ipv6Len = 8;
        if (addressLen == 4) {
            for (int i = 0; i < 4; ++i) {
                sb.append(header.readByte() & 0xFF);
                sb.append('.');
            }
        }
        else {
            for (int i = 0; i < 8; ++i) {
                sb.append(Integer.toHexString(header.readUnsignedShort()));
                sb.append(':');
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
    
    private static int portStringToInt(final String value) {
        int port;
        try {
            port = Integer.parseInt(value);
        }
        catch (final NumberFormatException e) {
            throw new IllegalArgumentException("invalid port: " + value, e);
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("invalid port: " + value + " (expected: 1 ~ 65535)");
        }
        return port;
    }
    
    private static void checkAddress(final String address, final HAProxyProxiedProtocol.AddressFamily addrFamily) {
        ObjectUtil.checkNotNull(addrFamily, "addrFamily");
        switch (addrFamily) {
            case AF_UNSPEC: {
                if (address != null) {
                    throw new IllegalArgumentException("unable to validate an AF_UNSPEC address: " + address);
                }
                return;
            }
            case AF_UNIX: {
                ObjectUtil.checkNotNull(address, "address");
                if (address.getBytes(CharsetUtil.US_ASCII).length > 108) {
                    throw new IllegalArgumentException("invalid AF_UNIX address: " + address);
                }
                return;
            }
            default: {
                ObjectUtil.checkNotNull(address, "address");
                switch (addrFamily) {
                    case AF_IPv4: {
                        if (!NetUtil.isValidIpV4Address(address)) {
                            throw new IllegalArgumentException("invalid IPv4 address: " + address);
                        }
                        break;
                    }
                    case AF_IPv6: {
                        if (!NetUtil.isValidIpV6Address(address)) {
                            throw new IllegalArgumentException("invalid IPv6 address: " + address);
                        }
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unexpected addrFamily: " + addrFamily);
                    }
                }
            }
        }
    }
    
    private static void checkPort(final int port, final HAProxyProxiedProtocol.AddressFamily addrFamily) {
        switch (addrFamily) {
            case AF_IPv4:
            case AF_IPv6: {
                if (port < 0 || port > 65535) {
                    throw new IllegalArgumentException("invalid port: " + port + " (expected: 0 ~ 65535)");
                }
                break;
            }
            case AF_UNSPEC:
            case AF_UNIX: {
                if (port != 0) {
                    throw new IllegalArgumentException("port cannot be specified with addrFamily: " + addrFamily);
                }
                break;
            }
            default: {
                throw new IllegalArgumentException("unexpected addrFamily: " + addrFamily);
            }
        }
    }
    
    public HAProxyProtocolVersion protocolVersion() {
        return this.protocolVersion;
    }
    
    public HAProxyCommand command() {
        return this.command;
    }
    
    public HAProxyProxiedProtocol proxiedProtocol() {
        return this.proxiedProtocol;
    }
    
    public String sourceAddress() {
        return this.sourceAddress;
    }
    
    public String destinationAddress() {
        return this.destinationAddress;
    }
    
    public int sourcePort() {
        return this.sourcePort;
    }
    
    public int destinationPort() {
        return this.destinationPort;
    }
    
    public List<HAProxyTLV> tlvs() {
        return this.tlvs;
    }
    
    int tlvNumBytes() {
        int tlvNumBytes = 0;
        for (int i = 0; i < this.tlvs.size(); ++i) {
            tlvNumBytes += this.tlvs.get(i).totalNumBytes();
        }
        return tlvNumBytes;
    }
    
    @Override
    public HAProxyMessage touch() {
        this.tryRecord();
        return (HAProxyMessage)super.touch();
    }
    
    @Override
    public HAProxyMessage touch(final Object hint) {
        if (this.leak != null) {
            this.leak.record(hint);
        }
        return this;
    }
    
    @Override
    public HAProxyMessage retain() {
        this.tryRecord();
        return (HAProxyMessage)super.retain();
    }
    
    @Override
    public HAProxyMessage retain(final int increment) {
        this.tryRecord();
        return (HAProxyMessage)super.retain(increment);
    }
    
    @Override
    public boolean release() {
        this.tryRecord();
        return super.release();
    }
    
    @Override
    public boolean release(final int decrement) {
        this.tryRecord();
        return super.release(decrement);
    }
    
    private void tryRecord() {
        if (this.leak != null) {
            this.leak.record();
        }
    }
    
    @Override
    protected void deallocate() {
        try {
            for (final HAProxyTLV tlv : this.tlvs) {
                tlv.release();
            }
        }
        finally {
            final ResourceLeakTracker<HAProxyMessage> leak = this.leak;
            if (leak != null) {
                final boolean closed = leak.close(this);
                assert closed;
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(256).append(StringUtil.simpleClassName(this)).append("(protocolVersion: ").append(this.protocolVersion).append(", command: ").append(this.command).append(", proxiedProtocol: ").append(this.proxiedProtocol).append(", sourceAddress: ").append(this.sourceAddress).append(", destinationAddress: ").append(this.destinationAddress).append(", sourcePort: ").append(this.sourcePort).append(", destinationPort: ").append(this.destinationPort).append(", tlvs: [");
        if (!this.tlvs.isEmpty()) {
            for (final HAProxyTLV tlv : this.tlvs) {
                sb.append(tlv).append(", ");
            }
            sb.setLength(sb.length() - 2);
        }
        sb.append("])");
        return sb.toString();
    }
    
    static {
        leakDetector = ResourceLeakDetectorFactory.instance().newResourceLeakDetector(HAProxyMessage.class);
    }
}
