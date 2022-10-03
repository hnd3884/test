package org.openjsse.sun.security.util;

import java.util.regex.PatternSyntaxException;
import sun.security.action.GetPropertyAction;
import java.security.InvalidParameterException;
import sun.security.util.Debug;

public final class SecurityProviderConstants
{
    private static final Debug debug;
    public static final int DEF_DSA_KEY_SIZE;
    public static final int DEF_RSA_KEY_SIZE;
    public static final int DEF_RSASSA_PSS_KEY_SIZE;
    public static final int DEF_DH_KEY_SIZE;
    public static final int DEF_EC_KEY_SIZE;
    private static final String KEY_LENGTH_PROP = "jdk.security.defaultKeySize";
    
    private SecurityProviderConstants() {
    }
    
    public static final int getDefDSASubprimeSize(final int primeSize) {
        if (primeSize <= 1024) {
            return 160;
        }
        if (primeSize == 2048) {
            return 224;
        }
        if (primeSize == 3072) {
            return 256;
        }
        throw new InvalidParameterException("Invalid DSA Prime Size: " + primeSize);
    }
    
    static {
        debug = Debug.getInstance("jca", "ProviderConfig");
        final String keyLengthStr = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
        int dsaKeySize = 2048;
        int rsaSsaPssKeySize;
        int rsaKeySize = rsaSsaPssKeySize = 2048;
        int dhKeySize = 2048;
        int ecKeySize = 256;
        if (keyLengthStr != null) {
            try {
                final String[] split;
                final String[] pairs = split = keyLengthStr.split(",");
                for (final String p : split) {
                    final String[] algoAndValue = p.split(":");
                    Label_0361: {
                        if (algoAndValue.length != 2) {
                            if (SecurityProviderConstants.debug != null) {
                                SecurityProviderConstants.debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + p);
                            }
                        }
                        else {
                            final String algoName = algoAndValue[0].trim().toUpperCase();
                            int value = -1;
                            try {
                                value = Integer.parseInt(algoAndValue[1].trim());
                            }
                            catch (final NumberFormatException nfe) {
                                if (SecurityProviderConstants.debug != null) {
                                    SecurityProviderConstants.debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + p);
                                }
                                break Label_0361;
                            }
                            if (algoName.equals("DSA")) {
                                dsaKeySize = value;
                            }
                            else if (algoName.equals("RSA")) {
                                rsaKeySize = value;
                            }
                            else if (algoName.equals("RSASSA-PSS")) {
                                rsaSsaPssKeySize = value;
                            }
                            else if (algoName.equals("DH")) {
                                dhKeySize = value;
                            }
                            else if (algoName.equals("EC")) {
                                ecKeySize = value;
                            }
                            else {
                                if (SecurityProviderConstants.debug != null) {
                                    SecurityProviderConstants.debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + p);
                                }
                                break Label_0361;
                            }
                            if (SecurityProviderConstants.debug != null) {
                                SecurityProviderConstants.debug.println("Overriding default " + algoName + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + value);
                            }
                        }
                    }
                }
            }
            catch (final PatternSyntaxException pse) {
                if (SecurityProviderConstants.debug != null) {
                    SecurityProviderConstants.debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + pse);
                }
            }
        }
        DEF_DSA_KEY_SIZE = dsaKeySize;
        DEF_RSA_KEY_SIZE = rsaKeySize;
        DEF_RSASSA_PSS_KEY_SIZE = rsaSsaPssKeySize;
        DEF_DH_KEY_SIZE = dhKeySize;
        DEF_EC_KEY_SIZE = ecKeySize;
    }
}
