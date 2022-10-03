package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum RSAPrivateKeyVersion
{
    TWO_PRIME(0, "two-prime"), 
    MULTI(1, "multi");
    
    private final int intValue;
    private final String name;
    
    private RSAPrivateKeyVersion(final int intValue, final String name) {
        this.intValue = intValue;
        this.name = name;
    }
    
    int getIntValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    static RSAPrivateKeyVersion valueOf(final int intValue) {
        for (final RSAPrivateKeyVersion v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static RSAPrivateKeyVersion forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "twoprime":
            case "two-prime":
            case "two_prime": {
                return RSAPrivateKeyVersion.TWO_PRIME;
            }
            case "multi": {
                return RSAPrivateKeyVersion.MULTI;
            }
            default: {
                return null;
            }
        }
    }
}
