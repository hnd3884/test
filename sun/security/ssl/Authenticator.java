package sun.security.ssl;

import java.security.Key;
import javax.crypto.Mac;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.SecretKey;

abstract class Authenticator
{
    protected final byte[] block;
    
    private Authenticator(final byte[] block) {
        this.block = block;
    }
    
    static Authenticator valueOf(final ProtocolVersion protocolVersion) {
        if (protocolVersion.useTLS13PlusSpec()) {
            return new TLS13Authenticator(protocolVersion);
        }
        if (protocolVersion.useTLS10PlusSpec()) {
            return new TLS10Authenticator(protocolVersion);
        }
        return new SSL30Authenticator();
    }
    
    static <T extends Authenticator & MAC> T valueOf(final ProtocolVersion protocolVersion, final CipherSuite.MacAlg macAlg, final SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        if (protocolVersion.useTLS13PlusSpec()) {
            throw new RuntimeException("No MacAlg used in TLS 1.3");
        }
        if (protocolVersion.useTLS10PlusSpec()) {
            return (T)new TLS10Mac(protocolVersion, macAlg, secretKey);
        }
        return (T)new SSL30Mac(protocolVersion, macAlg, secretKey);
    }
    
    static Authenticator nullTlsMac() {
        return new SSLNullMac();
    }
    
    abstract boolean seqNumOverflow();
    
    abstract boolean seqNumIsHuge();
    
    final byte[] sequenceNumber() {
        return Arrays.copyOf(this.block, 8);
    }
    
    final void increaseSequenceNumber() {
        for (int i = 7; i >= 0; --i) {
            final byte[] block = this.block;
            final int n = i;
            if (++block[n] != 0) {
                break;
            }
        }
    }
    
    byte[] acquireAuthenticationBytes(final byte b, final int n, final byte[] array) {
        throw new UnsupportedOperationException("Used by AEAD algorithms only");
    }
    
    static final long toLong(final byte[] array) {
        if (array != null && array.length == 8) {
            return ((long)array[0] & 0xFFL) << 56 | ((long)array[1] & 0xFFL) << 48 | ((long)array[2] & 0xFFL) << 40 | ((long)array[3] & 0xFFL) << 32 | ((long)array[4] & 0xFFL) << 24 | ((long)array[5] & 0xFFL) << 16 | ((long)array[6] & 0xFFL) << 8 | ((long)array[7] & 0xFFL);
        }
        return -1L;
    }
    
    private static class SSLAuthenticator extends Authenticator
    {
        private SSLAuthenticator(final byte[] array) {
            super(array, null);
        }
        
        @Override
        boolean seqNumOverflow() {
            return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1 && this.block[2] == -1 && this.block[3] == -1 && this.block[4] == -1 && this.block[5] == -1 && this.block[6] == -1;
        }
        
        @Override
        boolean seqNumIsHuge() {
            return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1 && this.block[2] == -1 && this.block[3] == -1;
        }
    }
    
    private static class SSLNullAuthenticator extends SSLAuthenticator
    {
        private SSLNullAuthenticator() {
            super(new byte[8]);
        }
    }
    
    private static class SSL30Authenticator extends SSLAuthenticator
    {
        private static final int BLOCK_SIZE = 11;
        
        private SSL30Authenticator() {
            super(new byte[11]);
        }
        
        @Override
        byte[] acquireAuthenticationBytes(final byte b, final int n, final byte[] array) {
            final byte[] array2 = this.block.clone();
            this.increaseSequenceNumber();
            array2[8] = b;
            array2[9] = (byte)(n >> 8);
            array2[10] = (byte)n;
            return array2;
        }
    }
    
    private static class TLS10Authenticator extends SSLAuthenticator
    {
        private static final int BLOCK_SIZE = 13;
        
        private TLS10Authenticator(final ProtocolVersion protocolVersion) {
            super(new byte[13]);
            this.block[9] = protocolVersion.major;
            this.block[10] = protocolVersion.minor;
        }
        
        @Override
        byte[] acquireAuthenticationBytes(final byte b, final int n, final byte[] array) {
            final byte[] array2 = this.block.clone();
            if (array != null) {
                if (array.length != 8) {
                    throw new RuntimeException("Insufficient explicit sequence number bytes");
                }
                System.arraycopy(array, 0, array2, 0, array.length);
            }
            else {
                this.increaseSequenceNumber();
            }
            array2[8] = b;
            array2[11] = (byte)(n >> 8);
            array2[12] = (byte)n;
            return array2;
        }
    }
    
    private static final class TLS13Authenticator extends SSLAuthenticator
    {
        private static final int BLOCK_SIZE = 13;
        
        private TLS13Authenticator(final ProtocolVersion protocolVersion) {
            super(new byte[13]);
            this.block[9] = ProtocolVersion.TLS12.major;
            this.block[10] = ProtocolVersion.TLS12.minor;
        }
        
        @Override
        byte[] acquireAuthenticationBytes(final byte b, final int n, final byte[] array) {
            final byte[] copyOfRange = Arrays.copyOfRange(this.block, 8, 13);
            this.increaseSequenceNumber();
            copyOfRange[0] = b;
            copyOfRange[3] = (byte)(n >> 8);
            copyOfRange[4] = (byte)(n & 0xFF);
            return copyOfRange;
        }
    }
    
    interface MAC
    {
        CipherSuite.MacAlg macAlg();
        
        byte[] compute(final byte p0, final ByteBuffer p1, final byte[] p2, final boolean p3);
        
        default byte[] compute(final byte b, final ByteBuffer byteBuffer, final boolean b2) {
            return this.compute(b, byteBuffer, null, b2);
        }
    }
    
    private class MacImpl implements MAC
    {
        private final CipherSuite.MacAlg macAlg;
        private final Mac mac;
        
        private MacImpl() {
            this.macAlg = CipherSuite.MacAlg.M_NULL;
            this.mac = null;
        }
        
        private MacImpl(final ProtocolVersion protocolVersion, final CipherSuite.MacAlg macAlg, final SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
            if (macAlg == null) {
                throw new RuntimeException("Null MacAlg");
            }
            final boolean b = protocolVersion.id < ProtocolVersion.TLS10.id;
            String s = null;
            switch (macAlg) {
                case M_MD5: {
                    s = (b ? "SslMacMD5" : "HmacMD5");
                    break;
                }
                case M_SHA: {
                    s = (b ? "SslMacSHA1" : "HmacSHA1");
                    break;
                }
                case M_SHA256: {
                    s = "HmacSHA256";
                    break;
                }
                case M_SHA384: {
                    s = "HmacSHA384";
                    break;
                }
                default: {
                    throw new RuntimeException("Unknown MacAlg " + macAlg);
                }
            }
            final Mac mac = JsseJce.getMac(s);
            mac.init(secretKey);
            this.macAlg = macAlg;
            this.mac = mac;
        }
        
        @Override
        public CipherSuite.MacAlg macAlg() {
            return this.macAlg;
        }
        
        @Override
        public byte[] compute(final byte b, final ByteBuffer byteBuffer, final byte[] array, final boolean b2) {
            if (this.macAlg.size == 0) {
                return new byte[0];
            }
            if (!b2) {
                this.mac.update(Authenticator.this.acquireAuthenticationBytes(b, byteBuffer.remaining(), array));
            }
            this.mac.update(byteBuffer);
            return this.mac.doFinal();
        }
    }
    
    private static final class SSLNullMac extends SSLNullAuthenticator implements MAC
    {
        private final MacImpl macImpl;
        
        public SSLNullMac() {
            this.macImpl = new MacImpl();
        }
        
        @Override
        public CipherSuite.MacAlg macAlg() {
            return this.macImpl.macAlg;
        }
        
        @Override
        public byte[] compute(final byte b, final ByteBuffer byteBuffer, final byte[] array, final boolean b2) {
            return this.macImpl.compute(b, byteBuffer, array, b2);
        }
    }
    
    private static final class SSL30Mac extends SSL30Authenticator implements MAC
    {
        private final MacImpl macImpl;
        
        public SSL30Mac(final ProtocolVersion protocolVersion, final CipherSuite.MacAlg macAlg, final SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
            this.macImpl = new MacImpl(protocolVersion, macAlg, secretKey);
        }
        
        @Override
        public CipherSuite.MacAlg macAlg() {
            return this.macImpl.macAlg;
        }
        
        @Override
        public byte[] compute(final byte b, final ByteBuffer byteBuffer, final byte[] array, final boolean b2) {
            return this.macImpl.compute(b, byteBuffer, array, b2);
        }
    }
    
    private static final class TLS10Mac extends TLS10Authenticator implements MAC
    {
        private final MacImpl macImpl;
        
        public TLS10Mac(final ProtocolVersion protocolVersion, final CipherSuite.MacAlg macAlg, final SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
            super(protocolVersion);
            this.macImpl = new MacImpl(protocolVersion, macAlg, secretKey);
        }
        
        @Override
        public CipherSuite.MacAlg macAlg() {
            return this.macImpl.macAlg;
        }
        
        @Override
        public byte[] compute(final byte b, final ByteBuffer byteBuffer, final byte[] array, final boolean b2) {
            return this.macImpl.compute(b, byteBuffer, array, b2);
        }
    }
}
