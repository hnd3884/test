package org.bouncycastle.jcajce.provider.asymmetric.util;

public class PrimeCertaintyCalculator
{
    private PrimeCertaintyCalculator() {
    }
    
    public static int getDefaultCertainty(final int n) {
        return (n <= 1024) ? 80 : (96 + 16 * ((n - 1) / 1024));
    }
}
