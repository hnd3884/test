package org.bouncycastle.crypto.tls;

public class AlertLevel
{
    public static final short warning = 1;
    public static final short fatal = 2;
    
    public static String getName(final short n) {
        switch (n) {
            case 1: {
                return "warning";
            }
            case 2: {
                return "fatal";
            }
            default: {
                return "UNKNOWN";
            }
        }
    }
    
    public static String getText(final short n) {
        return getName(n) + "(" + n + ")";
    }
}
