package org.bouncycastle.crypto.tls;

public class HashAlgorithm
{
    public static final short none = 0;
    public static final short md5 = 1;
    public static final short sha1 = 2;
    public static final short sha224 = 3;
    public static final short sha256 = 4;
    public static final short sha384 = 5;
    public static final short sha512 = 6;
    
    public static String getName(final short n) {
        switch (n) {
            case 0: {
                return "none";
            }
            case 1: {
                return "md5";
            }
            case 2: {
                return "sha1";
            }
            case 3: {
                return "sha224";
            }
            case 4: {
                return "sha256";
            }
            case 5: {
                return "sha384";
            }
            case 6: {
                return "sha512";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    public static String getText(final short n) {
        return getName(n) + "(" + n + ")";
    }
    
    public static boolean isPrivate(final short n) {
        return 224 <= n && n <= 255;
    }
}
