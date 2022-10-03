package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class DefaultXMSSOid implements XMSSOid
{
    private static final Map<String, DefaultXMSSOid> oidLookupTable;
    private final int oid;
    private final String stringRepresentation;
    
    private DefaultXMSSOid(final int oid, final String stringRepresentation) {
        this.oid = oid;
        this.stringRepresentation = stringRepresentation;
    }
    
    public static DefaultXMSSOid lookup(final String s, final int n, final int n2, final int n3, final int n4) {
        if (s == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return DefaultXMSSOid.oidLookupTable.get(createKey(s, n, n2, n3, n4));
    }
    
    private static String createKey(final String s, final int n, final int n2, final int n3, final int n4) {
        if (s == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return s + "-" + n + "-" + n2 + "-" + n3 + "-" + n4;
    }
    
    public int getOid() {
        return this.oid;
    }
    
    @Override
    public String toString() {
        return this.stringRepresentation;
    }
    
    static {
        final HashMap hashMap = new HashMap();
        hashMap.put(createKey("SHA-256", 32, 16, 67, 10), new DefaultXMSSOid(16777217, "XMSS_SHA2-256_W16_H10"));
        hashMap.put(createKey("SHA-256", 32, 16, 67, 16), new DefaultXMSSOid(33554434, "XMSS_SHA2-256_W16_H16"));
        hashMap.put(createKey("SHA-256", 32, 16, 67, 20), new DefaultXMSSOid(50331651, "XMSS_SHA2-256_W16_H20"));
        hashMap.put(createKey("SHA-512", 64, 16, 131, 10), new DefaultXMSSOid(67108868, "XMSS_SHA2-512_W16_H10"));
        hashMap.put(createKey("SHA-512", 64, 16, 131, 16), new DefaultXMSSOid(83886085, "XMSS_SHA2-512_W16_H16"));
        hashMap.put(createKey("SHA-512", 64, 16, 131, 20), new DefaultXMSSOid(100663302, "XMSS_SHA2-512_W16_H20"));
        hashMap.put(createKey("SHAKE128", 32, 16, 67, 10), new DefaultXMSSOid(117440519, "XMSS_SHAKE128_W16_H10"));
        hashMap.put(createKey("SHAKE128", 32, 16, 67, 16), new DefaultXMSSOid(134217736, "XMSS_SHAKE128_W16_H16"));
        hashMap.put(createKey("SHAKE128", 32, 16, 67, 20), new DefaultXMSSOid(150994953, "XMSS_SHAKE128_W16_H20"));
        hashMap.put(createKey("SHAKE256", 64, 16, 131, 10), new DefaultXMSSOid(167772170, "XMSS_SHAKE256_W16_H10"));
        hashMap.put(createKey("SHAKE256", 64, 16, 131, 16), new DefaultXMSSOid(184549387, "XMSS_SHAKE256_W16_H16"));
        hashMap.put(createKey("SHAKE256", 64, 16, 131, 20), new DefaultXMSSOid(201326604, "XMSS_SHAKE256_W16_H20"));
        oidLookupTable = Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
}
