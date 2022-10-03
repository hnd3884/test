package cryptix.jce.provider.random;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.security.SecureRandomSpi;

public final class DevRandom extends SecureRandomSpi
{
    private static final String RANDOM_DEV_NAME = "/dev/urandom";
    private static final File RANDOM_DEV;
    private static FileInputStream randomStream;
    
    private static void getRandomBytes(final byte[] bytes) throws IOException {
        int offset = 0;
        int count;
        for (int todo = bytes.length; todo > 0; todo -= count) {
            synchronized (DevRandom.randomStream) {
                if ((count = DevRandom.randomStream.read(bytes, offset, todo)) == -1) {
                    throw new IOException("EOF");
                }
                monitorexit(DevRandom.randomStream);
            }
            offset += count;
        }
    }
    
    protected void engineSetSeed(final byte[] seed) {
    }
    
    protected void engineNextBytes(final byte[] bytes) {
        try {
            getRandomBytes(bytes);
        }
        catch (final IOException e) {
            throw new RuntimeException("Cannot read from randomness device: " + e);
        }
    }
    
    protected byte[] engineGenerateSeed(final int numBytes) {
        final byte[] seed = new byte[numBytes];
        this.engineNextBytes(seed);
        return seed;
    }
    
    public static boolean isAvailable() {
        return DevRandom.randomStream != null;
    }
    
    public DevRandom() {
        if (DevRandom.randomStream == null) {
            throw new InternalError("randomStream == null");
        }
    }
    
    static {
        RANDOM_DEV = new File("/dev/urandom");
        DevRandom.randomStream = null;
        try {
            DevRandom.randomStream = new FileInputStream(DevRandom.RANDOM_DEV);
            final byte[] test_bytes = new byte[2500];
            getRandomBytes(test_bytes);
            if (!StatisticalTests.looksRandom(test_bytes)) {
                System.out.println("CryptixRandom Provider:Output of /dev/urandom doesn't look random, this may indicate a serious security problem!");
                DevRandom.randomStream.close();
                DevRandom.randomStream = null;
            }
        }
        catch (final IOException e) {
            DevRandom.randomStream = null;
        }
    }
}
