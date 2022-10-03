package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum X509CertificateVersion
{
    V1(0, "v1"), 
    V2(1, "v2"), 
    V3(2, "v3");
    
    private final int intValue;
    private final String name;
    
    private X509CertificateVersion(final int intValue, final String name) {
        this.intValue = intValue;
        this.name = name;
    }
    
    int getIntValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    static X509CertificateVersion valueOf(final int intValue) {
        for (final X509CertificateVersion v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static X509CertificateVersion forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "1":
            case "v1": {
                return X509CertificateVersion.V1;
            }
            case "2":
            case "v2": {
                return X509CertificateVersion.V2;
            }
            case "3":
            case "v3": {
                return X509CertificateVersion.V3;
            }
            default: {
                return null;
            }
        }
    }
}
