package org.bouncycastle.pqc.crypto.xmss;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class WOTSPlusOid implements XMSSOid
{
    private static final Map<String, WOTSPlusOid> oidLookupTable;
    private final int oid;
    private final String stringRepresentation;
    
    private WOTSPlusOid(final int oid, final String stringRepresentation) {
        this.oid = oid;
        this.stringRepresentation = stringRepresentation;
    }
    
    protected static WOTSPlusOid lookup(final String s, final int n, final int n2, final int n3) {
        if (s == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return WOTSPlusOid.oidLookupTable.get(createKey(s, n, n2, n3));
    }
    
    private static String createKey(final String s, final int n, final int n2, final int n3) {
        if (s == null) {
            throw new NullPointerException("algorithmName == null");
        }
        return s + "-" + n + "-" + n2 + "-" + n3;
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
        hashMap.put(createKey("SHA-256", 32, 16, 67), new WOTSPlusOid(16777217, "WOTSP_SHA2-256_W16"));
        hashMap.put(createKey("SHA-512", 64, 16, 131), new WOTSPlusOid(33554434, "WOTSP_SHA2-512_W16"));
        hashMap.put(createKey("SHAKE128", 32, 16, 67), new WOTSPlusOid(50331651, "WOTSP_SHAKE128_W16"));
        hashMap.put(createKey("SHAKE256", 64, 16, 131), new WOTSPlusOid(67108868, "WOTSP_SHAKE256_W16"));
        oidLookupTable = Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
}
