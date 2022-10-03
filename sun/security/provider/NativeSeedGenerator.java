package sun.security.provider;

import java.io.IOException;

class NativeSeedGenerator extends SeedGenerator
{
    NativeSeedGenerator(final String s) throws IOException {
        if (!nativeGenerateSeed(new byte[2])) {
            throw new IOException("Required native CryptoAPI features not  available on this machine");
        }
    }
    
    private static native boolean nativeGenerateSeed(final byte[] p0);
    
    @Override
    void getSeedBytes(final byte[] array) {
        if (!nativeGenerateSeed(array)) {
            throw new InternalError("Unexpected CryptoAPI failure generating seed");
        }
    }
}
