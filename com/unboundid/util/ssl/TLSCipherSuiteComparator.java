package com.unboundid.util.ssl;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;
import com.unboundid.util.NotMutable;
import java.io.Serializable;
import java.util.Comparator;

@NotMutable
@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public final class TLSCipherSuiteComparator implements Comparator<String>, Serializable
{
    private static final TLSCipherSuiteComparator INSTANCE;
    private static final long serialVersionUID = 7719643162516590858L;
    
    private TLSCipherSuiteComparator() {
    }
    
    public static TLSCipherSuiteComparator getInstance() {
        return TLSCipherSuiteComparator.INSTANCE;
    }
    
    @Override
    public int compare(final String s1, final String s2) {
        final String cipherSuiteName1 = StaticUtils.toUpperCase(s1).replace('-', '_');
        final String cipherSuiteName2 = StaticUtils.toUpperCase(s2).replace('-', '_');
        final int scsvOrder = getSCSVOrder(cipherSuiteName1, cipherSuiteName2);
        if (scsvOrder != 0) {
            return scsvOrder;
        }
        final int prefixOrder = getPrefixOrder(cipherSuiteName1, cipherSuiteName2);
        if (prefixOrder != 0) {
            return prefixOrder;
        }
        final int blockCipherOrder = getBlockCipherOrder(cipherSuiteName1, cipherSuiteName2);
        if (blockCipherOrder != 0) {
            return blockCipherOrder;
        }
        final int digestOrder = getDigestOrder(cipherSuiteName1, cipherSuiteName2);
        if (digestOrder != 0) {
            return digestOrder;
        }
        return s1.compareTo(s2);
    }
    
    private static int getSCSVOrder(final String cipherSuiteName1, final String cipherSuiteName2) {
        if (cipherSuiteName1.endsWith("_SCSV")) {
            if (cipherSuiteName2.endsWith("_SCSV")) {
                return 0;
            }
            return 1;
        }
        else {
            if (cipherSuiteName2.endsWith("_SCSV")) {
                return -1;
            }
            return 0;
        }
    }
    
    private static int getPrefixOrder(final String cipherSuiteName1, final String cipherSuiteName2) {
        final int prefixValue1 = getPrefixValue(cipherSuiteName1);
        final int prefixValue2 = getPrefixValue(cipherSuiteName2);
        return prefixValue1 - prefixValue2;
    }
    
    private static int getPrefixValue(final String cipherSuiteName) {
        if (cipherSuiteName.startsWith("TLS_AES_")) {
            return 1;
        }
        if (cipherSuiteName.startsWith("TLS_CHACHA20_")) {
            return 2;
        }
        if (cipherSuiteName.startsWith("TLS_ECDHE_")) {
            return 3;
        }
        if (cipherSuiteName.startsWith("TLS_DHE_")) {
            return 4;
        }
        if (cipherSuiteName.startsWith("TLS_RSA_")) {
            return 5;
        }
        if (cipherSuiteName.startsWith("TLS_")) {
            return 6;
        }
        if (cipherSuiteName.startsWith("SSL_")) {
            return 7;
        }
        return 8;
    }
    
    private static int getBlockCipherOrder(final String cipherSuiteName1, final String cipherSuiteName2) {
        final int blockCipherValue1 = getBlockCipherValue(cipherSuiteName1);
        final int blockCipherValue2 = getBlockCipherValue(cipherSuiteName2);
        return blockCipherValue1 - blockCipherValue2;
    }
    
    private static int getBlockCipherValue(final String cipherSuiteName) {
        if (cipherSuiteName.contains("_AES_256_GCM")) {
            return 1;
        }
        if (cipherSuiteName.contains("_AES_128_GCM")) {
            return 2;
        }
        if (cipherSuiteName.contains("_AES") && cipherSuiteName.contains("_GCM")) {
            return 3;
        }
        if (cipherSuiteName.contains("_AES_256")) {
            return 4;
        }
        if (cipherSuiteName.contains("_AES_128")) {
            return 5;
        }
        if (cipherSuiteName.contains("_AES")) {
            return 6;
        }
        if (cipherSuiteName.contains("_CHACHA20")) {
            return 7;
        }
        if (cipherSuiteName.contains("_GCM")) {
            return 8;
        }
        return 9;
    }
    
    private static int getDigestOrder(final String cipherSuiteName1, final String cipherSuiteName2) {
        final int digestValue1 = getDigestValue(cipherSuiteName1);
        final int digestValue2 = getDigestValue(cipherSuiteName2);
        return digestValue1 - digestValue2;
    }
    
    private static int getDigestValue(final String cipherSuiteName) {
        if (cipherSuiteName.endsWith("_SHA512")) {
            return 1;
        }
        if (cipherSuiteName.endsWith("_SHA384")) {
            return 2;
        }
        if (cipherSuiteName.endsWith("_SHA256")) {
            return 3;
        }
        if (cipherSuiteName.endsWith("_SHA")) {
            return 4;
        }
        return 5;
    }
    
    @Override
    public boolean equals(final Object o) {
        return o != null && o instanceof TLSCipherSuiteComparator;
    }
    
    @Override
    public int hashCode() {
        return 0;
    }
    
    static {
        INSTANCE = new TLSCipherSuiteComparator();
    }
}
