package sun.security.provider;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.io.Serializable;
import java.security.SecureRandomSpi;

public final class SecureRandom extends SecureRandomSpi implements Serializable
{
    private static final long serialVersionUID = 3581829991155417889L;
    private static final int DIGEST_SIZE = 20;
    private transient MessageDigest digest;
    private byte[] state;
    private byte[] remainder;
    private int remCount;
    
    public SecureRandom() {
        this.init(null);
    }
    
    private SecureRandom(final byte[] array) {
        this.init(array);
    }
    
    private void init(final byte[] array) {
        try {
            this.digest = MessageDigest.getInstance("SHA", "SUN");
        }
        catch (final NoSuchProviderException | NoSuchAlgorithmException ex) {
            try {
                this.digest = MessageDigest.getInstance("SHA");
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new InternalError("internal error: SHA-1 not available.", ex2);
            }
        }
        if (array != null) {
            this.engineSetSeed(array);
        }
    }
    
    public byte[] engineGenerateSeed(final int n) {
        final byte[] array = new byte[n];
        SeedGenerator.generateSeed(array);
        return array;
    }
    
    public synchronized void engineSetSeed(final byte[] array) {
        if (this.state != null) {
            this.digest.update(this.state);
            for (int i = 0; i < this.state.length; ++i) {
                this.state[i] = 0;
            }
        }
        this.state = this.digest.digest(array);
    }
    
    private static void updateState(final byte[] array, final byte[] array2) {
        int n = 1;
        boolean b = false;
        for (int i = 0; i < array.length; ++i) {
            final int n2 = array[i] + array2[i] + n;
            final byte b2 = (byte)n2;
            b |= (array[i] != b2);
            array[i] = b2;
            n = n2 >> 8;
        }
        if (!b) {
            final int n3 = 0;
            ++array[n3];
        }
    }
    
    public synchronized void engineNextBytes(final byte[] array) {
        int i = 0;
        byte[] remainder = this.remainder;
        if (this.state == null) {
            final byte[] array2 = new byte[20];
            SeederHolder.seeder.engineNextBytes(array2);
            this.state = this.digest.digest(array2);
        }
        int remCount = this.remCount;
        if (remCount > 0) {
            final int n = (array.length - i < 20 - remCount) ? (array.length - i) : (20 - remCount);
            for (int j = 0; j < n; ++j) {
                array[j] = remainder[remCount];
                remainder[remCount++] = 0;
            }
            this.remCount += n;
            i += n;
        }
        while (i < array.length) {
            this.digest.update(this.state);
            remainder = this.digest.digest();
            updateState(this.state, remainder);
            final int n2 = (array.length - i > 20) ? 20 : (array.length - i);
            for (int k = 0; k < n2; ++k) {
                array[i++] = remainder[k];
                remainder[k] = 0;
            }
            this.remCount += n2;
        }
        this.remainder = remainder;
        this.remCount %= 20;
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        try {
            this.digest = MessageDigest.getInstance("SHA", "SUN");
        }
        catch (final NoSuchProviderException | NoSuchAlgorithmException ex) {
            try {
                this.digest = MessageDigest.getInstance("SHA");
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new InternalError("internal error: SHA-1 not available.", ex2);
            }
        }
    }
    
    private static class SeederHolder
    {
        private static final SecureRandom seeder;
        
        static {
            seeder = new SecureRandom(SeedGenerator.getSystemEntropy(), null);
            final byte[] array = new byte[20];
            SeedGenerator.generateSeed(array);
            SeederHolder.seeder.engineSetSeed(array);
        }
    }
}
