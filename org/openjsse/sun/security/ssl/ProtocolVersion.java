package org.openjsse.sun.security.ssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;

enum ProtocolVersion
{
    TLS13(772, "TLSv1.3", false), 
    TLS12(771, "TLSv1.2", false), 
    TLS11(770, "TLSv1.1", false), 
    TLS10(769, "TLSv1", false), 
    SSL30(768, "SSLv3", false), 
    SSL20Hello(2, "SSLv2Hello", false), 
    DTLS12(65277, "DTLSv1.2", true), 
    DTLS10(65279, "DTLSv1.0", true), 
    NONE(-1, "NONE", false);
    
    final int id;
    final String name;
    final boolean isDTLS;
    final byte major;
    final byte minor;
    final boolean isAvailable;
    static final int LIMIT_MAX_VALUE = 65535;
    static final int LIMIT_MIN_VALUE = 0;
    static final ProtocolVersion[] PROTOCOLS_TO_10;
    static final ProtocolVersion[] PROTOCOLS_TO_11;
    static final ProtocolVersion[] PROTOCOLS_TO_12;
    static final ProtocolVersion[] PROTOCOLS_TO_13;
    static final ProtocolVersion[] PROTOCOLS_OF_NONE;
    static final ProtocolVersion[] PROTOCOLS_OF_30;
    static final ProtocolVersion[] PROTOCOLS_OF_11;
    static final ProtocolVersion[] PROTOCOLS_OF_12;
    static final ProtocolVersion[] PROTOCOLS_OF_13;
    static final ProtocolVersion[] PROTOCOLS_10_11;
    static final ProtocolVersion[] PROTOCOLS_11_12;
    static final ProtocolVersion[] PROTOCOLS_12_13;
    static final ProtocolVersion[] PROTOCOLS_10_12;
    static final ProtocolVersion[] PROTOCOLS_TO_TLS12;
    static final ProtocolVersion[] PROTOCOLS_TO_TLS11;
    static final ProtocolVersion[] PROTOCOLS_TO_TLS10;
    static final ProtocolVersion[] PROTOCOLS_EMPTY;
    
    private ProtocolVersion(final int id, final String name, final boolean isDTLS) {
        this.id = id;
        this.name = name;
        this.isDTLS = isDTLS;
        this.major = (byte)(id >>> 8 & 0xFF);
        this.minor = (byte)(id & 0xFF);
        this.isAvailable = SSLAlgorithmConstraints.DEFAULT_SSL_ONLY.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), name, null);
    }
    
    static ProtocolVersion valueOf(final byte major, final byte minor) {
        for (final ProtocolVersion pv : values()) {
            if (pv.major == major && pv.minor == minor) {
                return pv;
            }
        }
        return null;
    }
    
    static ProtocolVersion valueOf(final int id) {
        for (final ProtocolVersion pv : values()) {
            if (pv.id == id) {
                return pv;
            }
        }
        return null;
    }
    
    static String nameOf(final byte major, final byte minor) {
        for (final ProtocolVersion pv : values()) {
            if (pv.major == major && pv.minor == minor) {
                return pv.name;
            }
        }
        return "(D)TLS-" + major + "." + minor;
    }
    
    static String nameOf(final int id) {
        return nameOf((byte)(id >>> 8 & 0xFF), (byte)(id & 0xFF));
    }
    
    static ProtocolVersion nameOf(final String name) {
        for (final ProtocolVersion pv : values()) {
            if (pv.name.equals(name)) {
                return pv;
            }
        }
        return null;
    }
    
    static boolean isNegotiable(final byte major, final byte minor, final boolean isDTLS, final boolean allowSSL20Hello) {
        final int v = (major & 0xFF) << 8 | (minor & 0xFF);
        if (isDTLS) {
            return v <= ProtocolVersion.DTLS10.id;
        }
        return v >= ProtocolVersion.SSL30.id || (allowSSL20Hello && v == ProtocolVersion.SSL20Hello.id);
    }
    
    static String[] toStringArray(final List<ProtocolVersion> protocolVersions) {
        if (protocolVersions != null && !protocolVersions.isEmpty()) {
            final String[] protocolNames = new String[protocolVersions.size()];
            int i = 0;
            for (final ProtocolVersion pv : protocolVersions) {
                protocolNames[i++] = pv.name;
            }
            return protocolNames;
        }
        return new String[0];
    }
    
    static String[] toStringArray(final int[] protocolVersions) {
        if (protocolVersions != null && protocolVersions.length != 0) {
            final String[] protocolNames = new String[protocolVersions.length];
            int i = 0;
            for (final int pv : protocolVersions) {
                protocolNames[i++] = nameOf(pv);
            }
            return protocolNames;
        }
        return new String[0];
    }
    
    static List<ProtocolVersion> namesOf(final String[] protocolNames) {
        if (protocolNames == null || protocolNames.length == 0) {
            return Collections.emptyList();
        }
        final List<ProtocolVersion> pvs = new ArrayList<ProtocolVersion>(protocolNames.length);
        for (final String pn : protocolNames) {
            final ProtocolVersion pv = nameOf(pn);
            if (pv == null) {
                throw new IllegalArgumentException("Unsupported protocol" + pn);
            }
            pvs.add(pv);
        }
        return Collections.unmodifiableList((List<? extends ProtocolVersion>)pvs);
    }
    
    static boolean useTLS12PlusSpec(final String name) {
        final ProtocolVersion pv = nameOf(name);
        return pv != null && pv != ProtocolVersion.NONE && (pv.isDTLS ? (pv.id <= ProtocolVersion.DTLS12.id) : (pv.id >= ProtocolVersion.TLS12.id));
    }
    
    int compare(final ProtocolVersion that) {
        if (this == that) {
            return 0;
        }
        if (this == ProtocolVersion.NONE) {
            return -1;
        }
        if (that == ProtocolVersion.NONE) {
            return 1;
        }
        if (this.isDTLS) {
            return that.id - this.id;
        }
        return this.id - that.id;
    }
    
    boolean useTLS13PlusSpec() {
        return this.isDTLS ? (this.id < ProtocolVersion.DTLS12.id) : (this.id >= ProtocolVersion.TLS13.id);
    }
    
    boolean useTLS12PlusSpec() {
        return this.isDTLS ? (this.id <= ProtocolVersion.DTLS12.id) : (this.id >= ProtocolVersion.TLS12.id);
    }
    
    boolean useTLS11PlusSpec() {
        return this.isDTLS || this.id >= ProtocolVersion.TLS11.id;
    }
    
    boolean useTLS10PlusSpec() {
        return this.isDTLS || this.id >= ProtocolVersion.TLS10.id;
    }
    
    static boolean useTLS10PlusSpec(final int id, final boolean isDTLS) {
        return isDTLS || id >= ProtocolVersion.TLS10.id;
    }
    
    static boolean useTLS13PlusSpec(final int id, final boolean isDTLS) {
        return isDTLS ? (id < ProtocolVersion.DTLS12.id) : (id >= ProtocolVersion.TLS13.id);
    }
    
    static ProtocolVersion selectedFrom(final List<ProtocolVersion> listedVersions, final int suggestedVersion) {
        ProtocolVersion selectedVersion = ProtocolVersion.NONE;
        for (final ProtocolVersion pv : listedVersions) {
            if (pv.id == suggestedVersion) {
                return pv;
            }
            if (pv.isDTLS) {
                if (pv.id <= suggestedVersion || pv.id >= selectedVersion.id) {
                    continue;
                }
                selectedVersion = pv;
            }
            else {
                if (pv.id >= suggestedVersion || pv.id <= selectedVersion.id) {
                    continue;
                }
                selectedVersion = pv;
            }
        }
        return selectedVersion;
    }
    
    static {
        PROTOCOLS_TO_10 = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.DTLS10 };
        PROTOCOLS_TO_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 };
        PROTOCOLS_TO_13 = new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30, ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 };
        PROTOCOLS_OF_NONE = new ProtocolVersion[] { ProtocolVersion.NONE };
        PROTOCOLS_OF_30 = new ProtocolVersion[] { ProtocolVersion.SSL30 };
        PROTOCOLS_OF_11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.DTLS10 };
        PROTOCOLS_OF_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.DTLS12 };
        PROTOCOLS_OF_13 = new ProtocolVersion[] { ProtocolVersion.TLS13 };
        PROTOCOLS_10_11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.DTLS10 };
        PROTOCOLS_11_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 };
        PROTOCOLS_12_13 = new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.DTLS12 };
        PROTOCOLS_10_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.DTLS12, ProtocolVersion.DTLS10 };
        PROTOCOLS_TO_TLS12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_TLS11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_TLS10 = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_EMPTY = new ProtocolVersion[0];
    }
}
