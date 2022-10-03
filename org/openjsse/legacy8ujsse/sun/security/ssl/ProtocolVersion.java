package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Collections;
import java.security.AlgorithmParameters;
import java.util.EnumSet;
import java.security.CryptoPrimitive;
import java.util.HashSet;
import java.util.Set;

public final class ProtocolVersion implements Comparable<ProtocolVersion>
{
    static final int LIMIT_MAX_VALUE = 65535;
    static final int LIMIT_MIN_VALUE = 0;
    static final ProtocolVersion NONE;
    static final ProtocolVersion SSL20Hello;
    static final ProtocolVersion SSL30;
    static final ProtocolVersion TLS10;
    static final ProtocolVersion TLS11;
    static final ProtocolVersion TLS12;
    private static final boolean FIPS;
    static final ProtocolVersion MIN;
    static final ProtocolVersion MAX;
    static final ProtocolVersion DEFAULT;
    static final ProtocolVersion DEFAULT_HELLO;
    static final Set<ProtocolVersion> availableProtocols;
    public final int v;
    public final byte major;
    public final byte minor;
    final String name;
    
    private ProtocolVersion(final int v, final String name) {
        this.v = v;
        this.name = name;
        this.major = (byte)(v >>> 8);
        this.minor = (byte)(v & 0xFF);
    }
    
    private static ProtocolVersion valueOf(final int v) {
        if (v == ProtocolVersion.SSL30.v) {
            return ProtocolVersion.SSL30;
        }
        if (v == ProtocolVersion.TLS10.v) {
            return ProtocolVersion.TLS10;
        }
        if (v == ProtocolVersion.TLS11.v) {
            return ProtocolVersion.TLS11;
        }
        if (v == ProtocolVersion.TLS12.v) {
            return ProtocolVersion.TLS12;
        }
        if (v == ProtocolVersion.SSL20Hello.v) {
            return ProtocolVersion.SSL20Hello;
        }
        final int major = v >>> 8 & 0xFF;
        final int minor = v & 0xFF;
        return new ProtocolVersion(v, "Unknown-" + major + "." + minor);
    }
    
    public static ProtocolVersion valueOf(final int major, final int minor) {
        return valueOf((major & 0xFF) << 8 | (minor & 0xFF));
    }
    
    static ProtocolVersion valueOf(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Protocol cannot be null");
        }
        if (ProtocolVersion.FIPS && (name.equals(ProtocolVersion.SSL30.name) || name.equals(ProtocolVersion.SSL20Hello.name))) {
            throw new IllegalArgumentException("Only TLS 1.0 or later allowed in FIPS mode");
        }
        if (name.equals(ProtocolVersion.SSL30.name)) {
            return ProtocolVersion.SSL30;
        }
        if (name.equals(ProtocolVersion.TLS10.name)) {
            return ProtocolVersion.TLS10;
        }
        if (name.equals(ProtocolVersion.TLS11.name)) {
            return ProtocolVersion.TLS11;
        }
        if (name.equals(ProtocolVersion.TLS12.name)) {
            return ProtocolVersion.TLS12;
        }
        if (name.equals(ProtocolVersion.SSL20Hello.name)) {
            return ProtocolVersion.SSL20Hello;
        }
        throw new IllegalArgumentException(name);
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    
    @Override
    public int compareTo(final ProtocolVersion protocolVersion) {
        return this.v - protocolVersion.v;
    }
    
    static {
        NONE = new ProtocolVersion(-1, "NONE");
        SSL20Hello = new ProtocolVersion(2, "SSLv2Hello");
        SSL30 = new ProtocolVersion(768, "SSLv3");
        TLS10 = new ProtocolVersion(769, "TLSv1");
        TLS11 = new ProtocolVersion(770, "TLSv1.1");
        TLS12 = new ProtocolVersion(771, "TLSv1.2");
        FIPS = Legacy8uJSSE.isFIPS();
        MIN = (ProtocolVersion.FIPS ? ProtocolVersion.TLS10 : ProtocolVersion.SSL30);
        MAX = ProtocolVersion.TLS12;
        DEFAULT = ProtocolVersion.TLS12;
        DEFAULT_HELLO = (ProtocolVersion.FIPS ? ProtocolVersion.TLS10 : ProtocolVersion.SSL30);
        final Set<ProtocolVersion> protocols = new HashSet<ProtocolVersion>(5);
        final ProtocolVersion[] array;
        final ProtocolVersion[] pvs = array = new ProtocolVersion[] { ProtocolVersion.SSL20Hello, ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12 };
        for (final ProtocolVersion p : array) {
            if (SSLAlgorithmConstraints.DEFAULT_SSL_ONLY.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), p.name, null)) {
                protocols.add(p);
            }
        }
        availableProtocols = Collections.unmodifiableSet((Set<? extends ProtocolVersion>)protocols);
    }
}
