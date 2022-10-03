package sun.security.ssl;

import java.security.MessageDigest;
import java.util.Arrays;
import java.security.SecureRandom;
import java.io.IOException;

abstract class HelloCookieManager
{
    abstract byte[] createCookie(final ServerHandshakeContext p0, final ClientHello.ClientHelloMessage p1) throws IOException;
    
    abstract boolean isCookieValid(final ServerHandshakeContext p0, final ClientHello.ClientHelloMessage p1, final byte[] p2) throws IOException;
    
    static class Builder
    {
        final SecureRandom secureRandom;
        private volatile T13HelloCookieManager t13HelloCookieManager;
        
        Builder(final SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
        }
        
        HelloCookieManager valueOf(final ProtocolVersion protocolVersion) {
            if (!protocolVersion.useTLS13PlusSpec()) {
                return null;
            }
            if (this.t13HelloCookieManager != null) {
                return this.t13HelloCookieManager;
            }
            synchronized (this) {
                if (this.t13HelloCookieManager == null) {
                    this.t13HelloCookieManager = new T13HelloCookieManager(this.secureRandom);
                }
            }
            return this.t13HelloCookieManager;
        }
    }
    
    private static final class T13HelloCookieManager extends HelloCookieManager
    {
        final SecureRandom secureRandom;
        private int cookieVersion;
        private final byte[] cookieSecret;
        private final byte[] legacySecret;
        
        T13HelloCookieManager(final SecureRandom secureRandom) {
            this.secureRandom = secureRandom;
            this.cookieVersion = secureRandom.nextInt();
            this.cookieSecret = new byte[64];
            this.legacySecret = new byte[64];
            secureRandom.nextBytes(this.cookieSecret);
            System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 64);
        }
        
        @Override
        byte[] createCookie(final ServerHandshakeContext serverHandshakeContext, final ClientHello.ClientHelloMessage clientHelloMessage) throws IOException {
            final int cookieVersion;
            final byte[] cookieSecret;
            synchronized (this) {
                cookieVersion = this.cookieVersion;
                cookieSecret = this.cookieSecret;
                if ((this.cookieVersion & 0xFFFFFF) == 0x0) {
                    System.arraycopy(this.cookieSecret, 0, this.legacySecret, 0, 64);
                    this.secureRandom.nextBytes(this.cookieSecret);
                }
                ++this.cookieVersion;
            }
            final MessageDigest messageDigest = JsseJce.getMessageDigest(serverHandshakeContext.negotiatedCipherSuite.hashAlg.name);
            messageDigest.update(clientHelloMessage.getHeaderBytes());
            final byte[] digest = messageDigest.digest(cookieSecret);
            serverHandshakeContext.handshakeHash.update();
            final byte[] digest2 = serverHandshakeContext.handshakeHash.digest();
            final byte[] array = { (byte)(serverHandshakeContext.negotiatedCipherSuite.id >> 8 & 0xFF), (byte)(serverHandshakeContext.negotiatedCipherSuite.id & 0xFF), (byte)(cookieVersion >> 24 & 0xFF) };
            final byte[] copy = Arrays.copyOf(array, array.length + digest.length + digest2.length);
            System.arraycopy(digest, 0, copy, array.length, digest.length);
            System.arraycopy(digest2, 0, copy, array.length + digest.length, digest2.length);
            return copy;
        }
        
        @Override
        boolean isCookieValid(final ServerHandshakeContext serverHandshakeContext, final ClientHello.ClientHelloMessage clientHelloMessage, final byte[] array) throws IOException {
            if (array == null || array.length <= 32) {
                return false;
            }
            final CipherSuite value = CipherSuite.valueOf((array[0] & 0xFF) << 8 | (array[1] & 0xFF));
            if (value == null || value.hashAlg == null || value.hashAlg.hashLength == 0) {
                return false;
            }
            final int hashLength = value.hashAlg.hashLength;
            if (array.length != 3 + hashLength * 2) {
                return false;
            }
            final byte[] copyOfRange = Arrays.copyOfRange(array, 3, 3 + hashLength);
            final byte[] copyOfRange2 = Arrays.copyOfRange(array, 3 + hashLength, array.length);
            byte[] array2;
            synchronized (this) {
                if ((byte)(this.cookieVersion >> 24 & 0xFF) == array[2]) {
                    array2 = this.cookieSecret;
                }
                else {
                    array2 = this.legacySecret;
                }
            }
            final MessageDigest messageDigest = JsseJce.getMessageDigest(value.hashAlg.name);
            messageDigest.update(clientHelloMessage.getHeaderBytes());
            if (!Arrays.equals(messageDigest.digest(array2), copyOfRange)) {
                return false;
            }
            serverHandshakeContext.handshakeHash.push(ServerHello.hrrReproducer.produce(serverHandshakeContext, clientHelloMessage));
            final byte[] array3 = new byte[4 + hashLength];
            array3[0] = SSLHandshake.MESSAGE_HASH.id;
            array3[2] = (array3[1] = 0);
            array3[3] = (byte)(hashLength & 0xFF);
            System.arraycopy(copyOfRange2, 0, array3, 4, hashLength);
            serverHandshakeContext.handshakeHash.push(array3);
            return true;
        }
    }
}
