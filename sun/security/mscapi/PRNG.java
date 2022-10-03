package sun.security.mscapi;

import java.security.ProviderException;
import java.io.Serializable;
import java.security.SecureRandomSpi;

public final class PRNG extends SecureRandomSpi implements Serializable
{
    private static final long serialVersionUID = 4129268715132691532L;
    
    private static native byte[] generateSeed(final int p0, final byte[] p1);
    
    @Override
    protected void engineSetSeed(final byte[] array) {
        if (array != null) {
            generateSeed(-1, array);
        }
    }
    
    @Override
    protected void engineNextBytes(final byte[] array) {
        if (array != null && generateSeed(0, array) == null) {
            throw new ProviderException("Error generating random bytes");
        }
    }
    
    @Override
    protected byte[] engineGenerateSeed(final int n) {
        final byte[] generateSeed = generateSeed(n, null);
        if (generateSeed == null) {
            throw new ProviderException("Error generating seed bytes");
        }
        return generateSeed;
    }
}
