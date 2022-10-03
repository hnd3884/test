package sun.security.util;

import java.util.regex.PatternSyntaxException;
import sun.security.action.GetPropertyAction;
import java.security.InvalidParameterException;

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
    
    public static final int getDefDSASubprimeSize(final int n) {
        if (n <= 1024) {
            return 160;
        }
        if (n == 2048) {
            return 224;
        }
        if (n == 3072) {
            return 256;
        }
        throw new InvalidParameterException("Invalid DSA Prime Size: " + n);
    }
    
    static {
        debug = Debug.getInstance("jca", "ProviderConfig");
        final String privilegedGetProperty = GetPropertyAction.privilegedGetProperty("jdk.security.defaultKeySize");
        int def_DSA_KEY_SIZE = 2048;
        int def_RSASSA_PSS_KEY_SIZE;
        int def_RSA_KEY_SIZE = def_RSASSA_PSS_KEY_SIZE = 2048;
        int def_DH_KEY_SIZE = 2048;
        int def_EC_KEY_SIZE = 256;
        if (privilegedGetProperty != null) {
            try {
                for (final String s : privilegedGetProperty.split(",")) {
                    final String[] split2 = s.split(":");
                    Label_0361: {
                        if (split2.length != 2) {
                            if (SecurityProviderConstants.debug != null) {
                                SecurityProviderConstants.debug.println("Ignoring invalid pair in jdk.security.defaultKeySize property: " + s);
                            }
                        }
                        else {
                            final String upperCase = split2[0].trim().toUpperCase();
                            int int1;
                            try {
                                int1 = Integer.parseInt(split2[1].trim());
                            }
                            catch (final NumberFormatException ex) {
                                if (SecurityProviderConstants.debug != null) {
                                    SecurityProviderConstants.debug.println("Ignoring invalid value in jdk.security.defaultKeySize property: " + s);
                                }
                                break Label_0361;
                            }
                            if (upperCase.equals("DSA")) {
                                def_DSA_KEY_SIZE = int1;
                            }
                            else if (upperCase.equals("RSA")) {
                                def_RSA_KEY_SIZE = int1;
                            }
                            else if (upperCase.equals("RSASSA-PSS")) {
                                def_RSASSA_PSS_KEY_SIZE = int1;
                            }
                            else if (upperCase.equals("DH")) {
                                def_DH_KEY_SIZE = int1;
                            }
                            else if (upperCase.equals("EC")) {
                                def_EC_KEY_SIZE = int1;
                            }
                            else {
                                if (SecurityProviderConstants.debug != null) {
                                    SecurityProviderConstants.debug.println("Ignoring unsupported algo in jdk.security.defaultKeySize property: " + s);
                                }
                                break Label_0361;
                            }
                            if (SecurityProviderConstants.debug != null) {
                                SecurityProviderConstants.debug.println("Overriding default " + upperCase + " keysize with value from " + "jdk.security.defaultKeySize" + " property: " + int1);
                            }
                        }
                    }
                }
            }
            catch (final PatternSyntaxException ex2) {
                if (SecurityProviderConstants.debug != null) {
                    SecurityProviderConstants.debug.println("Unexpected exception while parsing jdk.security.defaultKeySize property: " + ex2);
                }
            }
        }
        DEF_DSA_KEY_SIZE = def_DSA_KEY_SIZE;
        DEF_RSA_KEY_SIZE = def_RSA_KEY_SIZE;
        DEF_RSASSA_PSS_KEY_SIZE = def_RSASSA_PSS_KEY_SIZE;
        DEF_DH_KEY_SIZE = def_DH_KEY_SIZE;
        DEF_EC_KEY_SIZE = def_EC_KEY_SIZE;
    }
}
