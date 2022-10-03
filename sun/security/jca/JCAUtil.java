package sun.security.jca;

import java.security.SecureRandom;

public final class JCAUtil
{
    private static final int ARRAY_SIZE = 4096;
    
    private JCAUtil() {
    }
    
    public static int getTempArraySize(final int n) {
        return Math.min(4096, n);
    }
    
    public static SecureRandom getSecureRandom() {
        return CachedSecureRandomHolder.instance;
    }
    
    private static class CachedSecureRandomHolder
    {
        public static SecureRandom instance;
        
        static {
            CachedSecureRandomHolder.instance = new SecureRandom();
        }
    }
}
