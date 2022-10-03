package com.unboundid.util.ssl.cert;

import com.unboundid.util.StaticUtils;
import com.unboundid.util.ThreadSafetyLevel;
import com.unboundid.util.ThreadSafety;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum PKCS8PrivateKeyVersion
{
    V1(0, "v1"), 
    V2(1, "v2");
    
    private final int intValue;
    private final String name;
    
    private PKCS8PrivateKeyVersion(final int intValue, final String name) {
        this.intValue = intValue;
        this.name = name;
    }
    
    int getIntValue() {
        return this.intValue;
    }
    
    public String getName() {
        return this.name;
    }
    
    static PKCS8PrivateKeyVersion valueOf(final int intValue) {
        for (final PKCS8PrivateKeyVersion v : values()) {
            if (v.intValue == intValue) {
                return v;
            }
        }
        return null;
    }
    
    public static PKCS8PrivateKeyVersion forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "1":
            case "v1": {
                return PKCS8PrivateKeyVersion.V1;
            }
            case "2":
            case "v2": {
                return PKCS8PrivateKeyVersion.V2;
            }
            default: {
                return null;
            }
        }
    }
}
