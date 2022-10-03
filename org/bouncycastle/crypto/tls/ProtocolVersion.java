package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Strings;
import java.io.IOException;

public final class ProtocolVersion
{
    public static final ProtocolVersion SSLv3;
    public static final ProtocolVersion TLSv10;
    public static final ProtocolVersion TLSv11;
    public static final ProtocolVersion TLSv12;
    public static final ProtocolVersion DTLSv10;
    public static final ProtocolVersion DTLSv12;
    private int version;
    private String name;
    
    private ProtocolVersion(final int n, final String name) {
        this.version = (n & 0xFFFF);
        this.name = name;
    }
    
    public int getFullVersion() {
        return this.version;
    }
    
    public int getMajorVersion() {
        return this.version >> 8;
    }
    
    public int getMinorVersion() {
        return this.version & 0xFF;
    }
    
    public boolean isDTLS() {
        return this.getMajorVersion() == 254;
    }
    
    public boolean isSSL() {
        return this == ProtocolVersion.SSLv3;
    }
    
    public boolean isTLS() {
        return this.getMajorVersion() == 3;
    }
    
    public ProtocolVersion getEquivalentTLSVersion() {
        if (!this.isDTLS()) {
            return this;
        }
        if (this == ProtocolVersion.DTLSv10) {
            return ProtocolVersion.TLSv11;
        }
        return ProtocolVersion.TLSv12;
    }
    
    public boolean isEqualOrEarlierVersionOf(final ProtocolVersion protocolVersion) {
        if (this.getMajorVersion() != protocolVersion.getMajorVersion()) {
            return false;
        }
        final int n = protocolVersion.getMinorVersion() - this.getMinorVersion();
        return this.isDTLS() ? (n <= 0) : (n >= 0);
    }
    
    public boolean isLaterVersionOf(final ProtocolVersion protocolVersion) {
        if (this.getMajorVersion() != protocolVersion.getMajorVersion()) {
            return false;
        }
        final int n = protocolVersion.getMinorVersion() - this.getMinorVersion();
        return this.isDTLS() ? (n > 0) : (n < 0);
    }
    
    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof ProtocolVersion && this.equals((ProtocolVersion)o));
    }
    
    public boolean equals(final ProtocolVersion protocolVersion) {
        return protocolVersion != null && this.version == protocolVersion.version;
    }
    
    @Override
    public int hashCode() {
        return this.version;
    }
    
    public static ProtocolVersion get(final int n, final int n2) throws IOException {
        switch (n) {
            case 3: {
                switch (n2) {
                    case 0: {
                        return ProtocolVersion.SSLv3;
                    }
                    case 1: {
                        return ProtocolVersion.TLSv10;
                    }
                    case 2: {
                        return ProtocolVersion.TLSv11;
                    }
                    case 3: {
                        return ProtocolVersion.TLSv12;
                    }
                    default: {
                        return getUnknownVersion(n, n2, "TLS");
                    }
                }
                break;
            }
            case 254: {
                switch (n2) {
                    case 255: {
                        return ProtocolVersion.DTLSv10;
                    }
                    case 254: {
                        throw new TlsFatalAlert((short)47);
                    }
                    case 253: {
                        return ProtocolVersion.DTLSv12;
                    }
                    default: {
                        return getUnknownVersion(n, n2, "DTLS");
                    }
                }
                break;
            }
            default: {
                throw new TlsFatalAlert((short)47);
            }
        }
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    private static ProtocolVersion getUnknownVersion(final int n, final int n2, final String s) throws IOException {
        TlsUtils.checkUint8(n);
        TlsUtils.checkUint8(n2);
        final int n3 = n << 8 | n2;
        return new ProtocolVersion(n3, s + " 0x" + Strings.toUpperCase(Integer.toHexString(0x10000 | n3).substring(1)));
    }
    
    static {
        SSLv3 = new ProtocolVersion(768, "SSL 3.0");
        TLSv10 = new ProtocolVersion(769, "TLS 1.0");
        TLSv11 = new ProtocolVersion(770, "TLS 1.1");
        TLSv12 = new ProtocolVersion(771, "TLS 1.2");
        DTLSv10 = new ProtocolVersion(65279, "DTLS 1.0");
        DTLSv12 = new ProtocolVersion(65277, "DTLS 1.2");
    }
}
