package org.openjsse.util;

import java.security.spec.AlgorithmParameterSpec;
import java.security.interfaces.RSAKey;

public class RSAKeyUtil
{
    public static AlgorithmParameterSpec getParams(final RSAKey rsaKey) {
        return rsaKey.getParams();
    }
    
    private RSAKeyUtil() {
    }
}
