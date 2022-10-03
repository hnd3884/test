package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PKCS10CertificateSigningRequestVersion
{
    V1(0, "v1");
    
    private final int intValue;
    private final String name;
    
    private PKCS10CertificateSigningRequestVersion(final int intValue, final String name) {
        this.intValue = intValue;
        this.name = name;
    }
    
    int getIntValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    static PKCS10CertificateSigningRequestVersion valueOf(final int intValue) {
        for (final PKCS10CertificateSigningRequestVersion v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static PKCS10CertificateSigningRequestVersion forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "1":
            case "v1": {
                return PKCS10CertificateSigningRequestVersion.V1;
            }
            default: {
                return null;
            }
        }
    }
}
