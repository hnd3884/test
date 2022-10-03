package sun.security.ssl;

import java.util.Arrays;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

final class RandomCookie
{
    final byte[] randomBytes;
    private static final byte[] hrrRandomBytes;
    private static final byte[] t12Protection;
    private static final byte[] t11Protection;
    static final RandomCookie hrrRandom;
    
    RandomCookie(final SecureRandom secureRandom) {
        secureRandom.nextBytes(this.randomBytes = new byte[32]);
    }
    
    RandomCookie(final HandshakeContext handshakeContext) {
        this.randomBytes = new byte[32];
        handshakeContext.sslContext.getSecureRandom().nextBytes(this.randomBytes);
        Object o = null;
        if (handshakeContext.maximumActiveProtocol.useTLS13PlusSpec()) {
            if (!handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                if (handshakeContext.negotiatedProtocol.useTLS12PlusSpec()) {
                    o = RandomCookie.t12Protection;
                }
                else {
                    o = RandomCookie.t11Protection;
                }
            }
        }
        else if (handshakeContext.maximumActiveProtocol.useTLS12PlusSpec() && !handshakeContext.negotiatedProtocol.useTLS12PlusSpec()) {
            o = RandomCookie.t11Protection;
        }
        if (o != null) {
            System.arraycopy(o, 0, this.randomBytes, this.randomBytes.length - o.length, o.length);
        }
    }
    
    RandomCookie(final ByteBuffer byteBuffer) throws IOException {
        byteBuffer.get(this.randomBytes = new byte[32]);
    }
    
    private RandomCookie(final byte[] array) {
        System.arraycopy(array, 0, this.randomBytes = new byte[32], 0, 32);
    }
    
    @Override
    public String toString() {
        return "random_bytes = {" + Utilities.toHexString(this.randomBytes) + "}";
    }
    
    boolean isHelloRetryRequest() {
        return Arrays.equals(RandomCookie.hrrRandomBytes, this.randomBytes);
    }
    
    boolean isVersionDowngrade(final HandshakeContext handshakeContext) {
        if (handshakeContext.maximumActiveProtocol.useTLS13PlusSpec()) {
            if (!handshakeContext.negotiatedProtocol.useTLS13PlusSpec()) {
                return this.isT12Downgrade() || this.isT11Downgrade();
            }
        }
        else if (handshakeContext.maximumActiveProtocol.useTLS12PlusSpec() && !handshakeContext.negotiatedProtocol.useTLS12PlusSpec()) {
            return this.isT11Downgrade();
        }
        return false;
    }
    
    private boolean isT12Downgrade() {
        return Utilities.equals(this.randomBytes, 24, 32, RandomCookie.t12Protection, 0, 8);
    }
    
    private boolean isT11Downgrade() {
        return Utilities.equals(this.randomBytes, 24, 32, RandomCookie.t11Protection, 0, 8);
    }
    
    static {
        hrrRandomBytes = new byte[] { -49, 33, -83, 116, -27, -102, 97, 17, -66, 29, -116, 2, 30, 101, -72, -111, -62, -94, 17, 22, 122, -69, -116, 94, 7, -98, 9, -30, -56, -88, 51, -100 };
        t12Protection = new byte[] { 68, 79, 87, 78, 71, 82, 68, 1 };
        t11Protection = new byte[] { 68, 79, 87, 78, 71, 82, 68, 0 };
        hrrRandom = new RandomCookie(RandomCookie.hrrRandomBytes);
    }
}
