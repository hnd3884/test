package sun.security.ssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.security.AlgorithmParameters;
import java.util.Set;
import java.util.EnumSet;
import java.security.CryptoPrimitive;

public enum ProtocolVersion
{
    TLS13(772, "TLSv1.3"), 
    TLS12(771, "TLSv1.2"), 
    TLS11(770, "TLSv1.1"), 
    TLS10(769, "TLSv1"), 
    SSL30(768, "SSLv3"), 
    SSL20Hello(2, "SSLv2Hello"), 
    NONE(-1, "NONE");
    
    final int id;
    final String name;
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
    
    private ProtocolVersion(final int id, final String name) {
        this.id = id;
        this.name = name;
        this.major = (byte)(id >>> 8 & 0xFF);
        this.minor = (byte)(id & 0xFF);
        this.isAvailable = SSLAlgorithmConstraints.DEFAULT_SSL_ONLY.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), name, null);
    }
    
    static ProtocolVersion valueOf(final byte b, final byte b2) {
        for (final ProtocolVersion protocolVersion : values()) {
            if (protocolVersion.major == b && protocolVersion.minor == b2) {
                return protocolVersion;
            }
        }
        return null;
    }
    
    static ProtocolVersion valueOf(final int n) {
        for (final ProtocolVersion protocolVersion : values()) {
            if (protocolVersion.id == n) {
                return protocolVersion;
            }
        }
        return null;
    }
    
    static String nameOf(final byte b, final byte b2) {
        for (final ProtocolVersion protocolVersion : values()) {
            if (protocolVersion.major == b && protocolVersion.minor == b2) {
                return protocolVersion.name;
            }
        }
        return "TLS-" + b + "." + b2;
    }
    
    static String nameOf(final int n) {
        return nameOf((byte)(n >>> 8 & 0xFF), (byte)(n & 0xFF));
    }
    
    static ProtocolVersion nameOf(final String s) {
        for (final ProtocolVersion protocolVersion : values()) {
            if (protocolVersion.name.equals(s)) {
                return protocolVersion;
            }
        }
        return null;
    }
    
    static boolean isNegotiable(final byte b, final byte b2, final boolean b3) {
        final int n = (b & 0xFF) << 8 | (b2 & 0xFF);
        return n >= ProtocolVersion.SSL30.id || (b3 && n == ProtocolVersion.SSL20Hello.id);
    }
    
    static String[] toStringArray(final List<ProtocolVersion> list) {
        if (list != null && !list.isEmpty()) {
            final String[] array = new String[list.size()];
            int n = 0;
            final Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                array[n++] = ((ProtocolVersion)iterator.next()).name;
            }
            return array;
        }
        return new String[0];
    }
    
    static String[] toStringArray(final int[] array) {
        if (array != null && array.length != 0) {
            final String[] array2 = new String[array.length];
            int n = 0;
            for (int length = array.length, i = 0; i < length; ++i) {
                array2[n++] = nameOf(array[i]);
            }
            return array2;
        }
        return new String[0];
    }
    
    static List<ProtocolVersion> namesOf(final String[] array) {
        if (array == null || array.length == 0) {
            return Collections.emptyList();
        }
        final ArrayList list = new ArrayList(array.length);
        for (final String s : array) {
            final ProtocolVersion name = nameOf(s);
            if (name == null) {
                throw new IllegalArgumentException("Unsupported protocol" + s);
            }
            list.add(name);
        }
        return (List<ProtocolVersion>)Collections.unmodifiableList((List<?>)list);
    }
    
    static boolean useTLS12PlusSpec(final String s) {
        final ProtocolVersion name = nameOf(s);
        return name != null && name != ProtocolVersion.NONE && name.id >= ProtocolVersion.TLS12.id;
    }
    
    int compare(final ProtocolVersion protocolVersion) {
        if (this == protocolVersion) {
            return 0;
        }
        if (this == ProtocolVersion.NONE) {
            return -1;
        }
        if (protocolVersion == ProtocolVersion.NONE) {
            return 1;
        }
        return this.id - protocolVersion.id;
    }
    
    boolean useTLS13PlusSpec() {
        return this.id >= ProtocolVersion.TLS13.id;
    }
    
    boolean useTLS12PlusSpec() {
        return this.id >= ProtocolVersion.TLS12.id;
    }
    
    boolean useTLS11PlusSpec() {
        return this.id >= ProtocolVersion.TLS11.id;
    }
    
    boolean useTLS10PlusSpec() {
        return this.id >= ProtocolVersion.TLS10.id;
    }
    
    static boolean useTLS10PlusSpec(final int n) {
        return n >= ProtocolVersion.TLS10.id;
    }
    
    static boolean useTLS13PlusSpec(final int n) {
        return n >= ProtocolVersion.TLS13.id;
    }
    
    static ProtocolVersion selectedFrom(final List<ProtocolVersion> list, final int n) {
        ProtocolVersion none = ProtocolVersion.NONE;
        for (final ProtocolVersion protocolVersion : list) {
            if (protocolVersion.id == n) {
                return protocolVersion;
            }
            if (protocolVersion.id >= n || protocolVersion.id <= none.id) {
                continue;
            }
            none = protocolVersion;
        }
        return none;
    }
    
    static {
        PROTOCOLS_TO_10 = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_13 = new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_OF_NONE = new ProtocolVersion[] { ProtocolVersion.NONE };
        PROTOCOLS_OF_30 = new ProtocolVersion[] { ProtocolVersion.SSL30 };
        PROTOCOLS_OF_11 = new ProtocolVersion[] { ProtocolVersion.TLS11 };
        PROTOCOLS_OF_12 = new ProtocolVersion[] { ProtocolVersion.TLS12 };
        PROTOCOLS_OF_13 = new ProtocolVersion[] { ProtocolVersion.TLS13 };
        PROTOCOLS_10_11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
        PROTOCOLS_11_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11 };
        PROTOCOLS_12_13 = new ProtocolVersion[] { ProtocolVersion.TLS13, ProtocolVersion.TLS12 };
        PROTOCOLS_10_12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10 };
        PROTOCOLS_TO_TLS12 = new ProtocolVersion[] { ProtocolVersion.TLS12, ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_TLS11 = new ProtocolVersion[] { ProtocolVersion.TLS11, ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_TO_TLS10 = new ProtocolVersion[] { ProtocolVersion.TLS10, ProtocolVersion.SSL30 };
        PROTOCOLS_EMPTY = new ProtocolVersion[0];
    }
}
