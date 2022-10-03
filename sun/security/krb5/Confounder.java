package sun.security.krb5;

import java.security.SecureRandom;

public final class Confounder
{
    private static SecureRandom srand;
    
    private Confounder() {
    }
    
    public static byte[] bytes(final int n) {
        final byte[] array = new byte[n];
        Confounder.srand.nextBytes(array);
        return array;
    }
    
    public static int intValue() {
        return Confounder.srand.nextInt();
    }
    
    public static long longValue() {
        return Confounder.srand.nextLong();
    }
    
    static {
        Confounder.srand = new SecureRandom();
    }
}
