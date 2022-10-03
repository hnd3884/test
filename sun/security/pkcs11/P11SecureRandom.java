package sun.security.pkcs11;

import java.io.IOException;
import java.io.ObjectInputStream;
import sun.security.pkcs11.wrapper.PKCS11Exception;
import java.security.NoSuchAlgorithmException;
import java.security.ProviderException;
import java.security.SecureRandom;
import java.security.SecureRandomSpi;

final class P11SecureRandom extends SecureRandomSpi
{
    private static final long serialVersionUID = -8939510236124553291L;
    private final Token token;
    private volatile SecureRandom mixRandom;
    private byte[] mixBuffer;
    private int buffered;
    private static final long MAX_IBUFFER_TIME = 100L;
    private static final int IBUFFER_SIZE = 32;
    private transient byte[] iBuffer;
    private transient int ibuffered;
    private transient long lastRead;
    
    P11SecureRandom(final Token token) {
        this.iBuffer = new byte[32];
        this.ibuffered = 0;
        this.lastRead = 0L;
        this.token = token;
    }
    
    @Override
    protected synchronized void engineSetSeed(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("seed must not be null");
        }
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.token.p11.C_SeedRandom(opSession.id(), array);
        }
        catch (final PKCS11Exception ex) {
            final SecureRandom mixRandom = this.mixRandom;
            if (mixRandom != null) {
                mixRandom.setSeed(array);
            }
            else {
                try {
                    this.mixBuffer = new byte[20];
                    final SecureRandom instance = SecureRandom.getInstance("SHA1PRNG");
                    instance.setSeed(array);
                    this.mixRandom = instance;
                }
                catch (final NoSuchAlgorithmException ex2) {
                    throw new ProviderException(ex2);
                }
            }
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    @Override
    protected void engineNextBytes(final byte[] array) {
        if (array == null || array.length == 0) {
            return;
        }
        if (array.length <= 32) {
            int i = 0;
            synchronized (this.iBuffer) {
                while (i < array.length) {
                    final long currentTimeMillis = System.currentTimeMillis();
                    if (this.ibuffered == 0 || currentTimeMillis - this.lastRead >= 100L) {
                        this.lastRead = currentTimeMillis;
                        this.implNextBytes(this.iBuffer);
                        this.ibuffered = 32;
                    }
                    while (i < array.length && this.ibuffered > 0) {
                        array[i++] = this.iBuffer[32 - this.ibuffered--];
                    }
                }
            }
        }
        else {
            this.implNextBytes(array);
        }
    }
    
    @Override
    protected byte[] engineGenerateSeed(final int n) {
        final byte[] array = new byte[n];
        this.engineNextBytes(array);
        return array;
    }
    
    private void mix(final byte[] array) {
        final SecureRandom mixRandom = this.mixRandom;
        if (mixRandom == null) {
            return;
        }
        synchronized (this) {
            int n = 0;
            int length = array.length;
            while (length-- > 0) {
                if (this.buffered == 0) {
                    mixRandom.nextBytes(this.mixBuffer);
                    this.buffered = this.mixBuffer.length;
                }
                final int n2 = n++;
                array[n2] ^= this.mixBuffer[this.mixBuffer.length - this.buffered];
                --this.buffered;
            }
        }
    }
    
    private void implNextBytes(final byte[] array) {
        Session opSession = null;
        try {
            opSession = this.token.getOpSession();
            this.token.p11.C_GenerateRandom(opSession.id(), array);
            this.mix(array);
        }
        catch (final PKCS11Exception ex) {
            throw new ProviderException("nextBytes() failed", ex);
        }
        finally {
            this.token.releaseSession(opSession);
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        this.iBuffer = new byte[32];
        this.ibuffered = 0;
        this.lastRead = 0L;
    }
}
