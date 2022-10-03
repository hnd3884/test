package sun.security.ssl;

import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Arrays;
import javax.crypto.ShortBufferException;
import java.security.AccessController;
import java.security.Security;
import java.security.PrivilegedAction;
import java.util.AbstractMap;
import javax.crypto.BadPaddingException;
import java.nio.ByteBuffer;
import javax.crypto.Cipher;
import java.security.GeneralSecurityException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

enum SSLCipher
{
    B_NULL("NULL", CipherType.NULL_CIPHER, 0, 0, 0, 0, true, true, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new NullReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_NONE), new AbstractMap.SimpleImmutableEntry(new NullReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_13) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new NullWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_NONE), new AbstractMap.SimpleImmutableEntry(new NullWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_13) }), 
    B_RC4_40("RC4", CipherType.STREAM_CIPHER, 5, 16, 0, 0, true, true, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }), 
    B_RC2_40("RC2", CipherType.BLOCK_CIPHER, 5, 16, 8, 0, false, true, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }), 
    B_DES_40("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 5, 8, 8, 0, true, true, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10) }), 
    B_RC4_128("RC4", CipherType.STREAM_CIPHER, 16, 16, 0, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new StreamWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_12) }), 
    B_DES("DES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 8, 8, 8, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_11) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_11) }), 
    B_3DES("DESede/CBC/NoPadding", CipherType.BLOCK_CIPHER, 24, 24, 8, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }), 
    B_IDEA("IDEA", CipherType.BLOCK_CIPHER, 16, 16, 8, 0, false, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(null, ProtocolVersion.PROTOCOLS_TO_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(null, ProtocolVersion.PROTOCOLS_TO_12) }), 
    B_AES_128("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 16, 16, 16, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }), 
    B_AES_256("AES/CBC/NoPadding", CipherType.BLOCK_CIPHER, 32, 32, 16, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockReadCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T10BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_TO_10), new AbstractMap.SimpleImmutableEntry(new T11BlockWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_11_12) }), 
    B_AES_128_GCM("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 16, 16, 12, 4, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12GcmReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12GcmWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12) }), 
    B_AES_256_GCM("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 32, 32, 12, 4, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12GcmReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12GcmWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12) }), 
    B_AES_128_GCM_IV("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 16, 16, 12, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }), 
    B_AES_256_GCM_IV("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 32, 32, 12, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) });
    
    final String description;
    final String transformation;
    final String algorithm;
    final boolean allowed;
    final int keySize;
    final int expandedKeySize;
    final int ivSize;
    final int fixedIvSize;
    final boolean exportable;
    final CipherType cipherType;
    final int tagSize = 16;
    private final boolean isAvailable;
    private final Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[] readCipherGenerators;
    private final Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[] writeCipherGenerators;
    private static final HashMap<String, Long> cipherLimits;
    static final String[] tag;
    
    private SSLCipher(final String transformation, final CipherType cipherType, final int keySize, final int expandedKeySize, final int ivSize, final int fixedIvSize, final boolean allowed, final boolean exportable, final Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[] readCipherGenerators, final Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[] writeCipherGenerators) {
        this.transformation = transformation;
        this.algorithm = transformation.split("/")[0];
        this.cipherType = cipherType;
        this.description = this.algorithm + "/" + (keySize << 3);
        this.keySize = keySize;
        this.ivSize = ivSize;
        this.fixedIvSize = fixedIvSize;
        this.allowed = allowed;
        this.expandedKeySize = expandedKeySize;
        this.exportable = exportable;
        this.isAvailable = (allowed && isUnlimited(keySize, transformation) && isTransformationAvailable(transformation));
        this.readCipherGenerators = readCipherGenerators;
        this.writeCipherGenerators = writeCipherGenerators;
    }
    
    private static boolean isTransformationAvailable(final String s) {
        if (s.equals("NULL")) {
            return true;
        }
        try {
            JsseJce.getCipher(s);
            return true;
        }
        catch (final NoSuchAlgorithmException ex) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Transformation " + s + " is not available.", new Object[0]);
            }
            return false;
        }
    }
    
    SSLReadCipher createReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SecretKey secretKey, final IvParameterSpec ivParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
        if (this.readCipherGenerators.length == 0) {
            return null;
        }
        ReadCipherGenerator readCipherGenerator = null;
        for (final Map.Entry<ReadCipherGenerator, ProtocolVersion[]> entry : this.readCipherGenerators) {
            final ProtocolVersion[] array = entry.getValue();
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                if (protocolVersion == array[j]) {
                    readCipherGenerator = entry.getKey();
                }
            }
        }
        if (readCipherGenerator != null) {
            return readCipherGenerator.createCipher(this, authenticator, protocolVersion, this.transformation, secretKey, ivParameterSpec, secureRandom);
        }
        return null;
    }
    
    SSLWriteCipher createWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SecretKey secretKey, final IvParameterSpec ivParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
        if (this.writeCipherGenerators.length == 0) {
            return null;
        }
        WriteCipherGenerator writeCipherGenerator = null;
        for (final Map.Entry<WriteCipherGenerator, ProtocolVersion[]> entry : this.writeCipherGenerators) {
            final ProtocolVersion[] array = entry.getValue();
            for (int length2 = array.length, j = 0; j < length2; ++j) {
                if (protocolVersion == array[j]) {
                    writeCipherGenerator = entry.getKey();
                }
            }
        }
        if (writeCipherGenerator != null) {
            return writeCipherGenerator.createCipher(this, authenticator, protocolVersion, this.transformation, secretKey, ivParameterSpec, secureRandom);
        }
        return null;
    }
    
    boolean isAvailable() {
        return this.isAvailable;
    }
    
    private static boolean isUnlimited(final int n, final String s) {
        final int n2 = n * 8;
        if (n2 > 128) {
            try {
                if (Cipher.getMaxAllowedKeyLength(s) < n2) {
                    return false;
                }
            }
            catch (final Exception ex) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
    
    private static void addMac(final Authenticator.MAC mac, final ByteBuffer byteBuffer, final byte b) {
        if (mac.macAlg().size != 0) {
            final int position = byteBuffer.position();
            final byte[] compute = mac.compute(b, byteBuffer, false);
            byteBuffer.limit(byteBuffer.limit() + compute.length);
            byteBuffer.put(compute);
            byteBuffer.position(position);
        }
    }
    
    private static void checkStreamMac(final Authenticator.MAC mac, final ByteBuffer byteBuffer, final byte b, final byte[] array) throws BadPaddingException {
        final int size = mac.macAlg().size;
        if (size != 0) {
            if (byteBuffer.remaining() - size < 0) {
                throw new BadPaddingException("bad record");
            }
            if (checkMacTags(b, byteBuffer, mac, array, false)) {
                throw new BadPaddingException("bad record MAC");
            }
        }
    }
    
    private static void checkCBCMac(final Authenticator.MAC mac, final ByteBuffer byteBuffer, final byte b, final int n, final byte[] array) throws BadPaddingException {
        Object o = null;
        final int size = mac.macAlg().size;
        final int position = byteBuffer.position();
        if (size != 0) {
            int n2 = byteBuffer.remaining() - size;
            if (n2 < 0) {
                o = new BadPaddingException("bad record");
                n2 = n - size;
                byteBuffer.limit(position + n);
            }
            if (checkMacTags(b, byteBuffer, mac, array, false) && o == null) {
                o = new BadPaddingException("bad record MAC");
            }
            checkMacTags(b, ByteBuffer.allocate(calculateRemainingLen(mac, n, n2) + mac.macAlg().size), mac, array, true);
        }
        if (o != null) {
            throw o;
        }
    }
    
    private static boolean checkMacTags(final byte b, final ByteBuffer byteBuffer, final Authenticator.MAC mac, final byte[] array, final boolean b2) {
        final int size = mac.macAlg().size;
        final int position = byteBuffer.position();
        final int limit = byteBuffer.limit();
        final int n = limit - size;
        byteBuffer.limit(n);
        final byte[] compute = mac.compute(b, byteBuffer, array, b2);
        if (compute == null || size != compute.length) {
            throw new RuntimeException("Internal MAC error");
        }
        byteBuffer.position(n);
        byteBuffer.limit(limit);
        try {
            return compareMacTags(byteBuffer, compute)[0] != 0;
        }
        finally {
            byteBuffer.position(position);
            byteBuffer.limit(n);
        }
    }
    
    private static int[] compareMacTags(final ByteBuffer byteBuffer, final byte[] array) {
        final int[] array2 = { 0, 0 };
        for (int length = array.length, i = 0; i < length; ++i) {
            if (byteBuffer.get() != array[i]) {
                final int[] array3 = array2;
                final int n = 0;
                ++array3[n];
            }
            else {
                final int[] array4 = array2;
                final int n2 = 1;
                ++array4[n2];
            }
        }
        return array2;
    }
    
    private static int calculateRemainingLen(final Authenticator.MAC mac, int n, int n2) {
        final int hashBlockSize = mac.macAlg().hashBlockSize;
        final int minimalPaddingSize = mac.macAlg().minimalPaddingSize;
        n += 13 - (hashBlockSize - minimalPaddingSize);
        n2 += 13 - (hashBlockSize - minimalPaddingSize);
        return 1 + (int)(Math.ceil(n / (1.0 * hashBlockSize)) - Math.ceil(n2 / (1.0 * hashBlockSize))) * hashBlockSize;
    }
    
    private static int addPadding(final ByteBuffer byteBuffer, final int n) {
        final int remaining = byteBuffer.remaining();
        final int position = byteBuffer.position();
        int n2 = remaining + 1;
        if (n2 % n != 0) {
            final int n3 = n2 + (n - 1);
            n2 = n3 - n3 % n;
        }
        final byte b = (byte)(n2 - remaining);
        byteBuffer.limit(n2 + position);
        byte b2 = 0;
        int n4 = position + remaining;
        while (b2 < b) {
            byteBuffer.put(n4++, (byte)(b - 1));
            ++b2;
        }
        byteBuffer.position(n4);
        byteBuffer.limit(n4);
        return n2;
    }
    
    private static int removePadding(final ByteBuffer byteBuffer, final int n, final int n2, final ProtocolVersion protocolVersion) throws BadPaddingException {
        final int remaining = byteBuffer.remaining();
        final int position = byteBuffer.position();
        final int n3 = byteBuffer.get(position + remaining - 1) & 0xFF;
        final int n4 = remaining - (n3 + 1);
        if (n4 - n < 0) {
            checkPadding(byteBuffer.duplicate(), (byte)(n3 & 0xFF));
            throw new BadPaddingException("Invalid Padding length: " + n3);
        }
        final int[] checkPadding = checkPadding((ByteBuffer)byteBuffer.duplicate().position(position + n4), (byte)(n3 & 0xFF));
        if (protocolVersion.useTLS10PlusSpec()) {
            if (checkPadding[0] != 0) {
                throw new BadPaddingException("Invalid TLS padding data");
            }
        }
        else if (n3 > n2) {
            throw new BadPaddingException("Padding length (" + n3 + ") of SSLv3 message should not be bigger than the block size (" + n2 + ")");
        }
        byteBuffer.limit(position + n4);
        return n4;
    }
    
    private static int[] checkPadding(final ByteBuffer byteBuffer, final byte b) {
        if (!byteBuffer.hasRemaining()) {
            throw new RuntimeException("hasRemaining() must be positive");
        }
        final int[] array = { 0, 0 };
        byteBuffer.mark();
        int i = 0;
        while (i <= 256) {
            while (byteBuffer.hasRemaining() && i <= 256) {
                if (byteBuffer.get() != b) {
                    final int[] array2 = array;
                    final int n = 0;
                    ++array2[n];
                }
                else {
                    final int[] array3 = array;
                    final int n2 = 1;
                    ++array3[n2];
                }
                ++i;
            }
            byteBuffer.reset();
        }
        return array;
    }
    
    static {
        cipherLimits = new HashMap<String, Long>();
        tag = new String[] { "KEYUPDATE" };
        final String s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("jdk.tls.keyLimits");
            }
        });
        if (s != null) {
            for (final String s2 : s.split(",")) {
                final String[] split2 = s2.trim().toUpperCase().split(" ");
                Label_1870: {
                    if (split2[1].contains(SSLCipher.tag[0])) {
                        final int n = 0;
                        final int index = split2[2].indexOf("^");
                        long long1;
                        try {
                            if (index >= 0) {
                                long1 = (long)Math.pow(2.0, Integer.parseInt(split2[2].substring(index + 1)));
                            }
                            else {
                                long1 = Long.parseLong(split2[2]);
                            }
                            if (long1 < 1L || long1 > 4611686018427387904L) {
                                throw new NumberFormatException("Length exceeded limits");
                            }
                        }
                        catch (final NumberFormatException ex) {
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                                SSLLogger.fine("jdk.tls.keyLimits:  " + ex.getMessage() + ":  " + s2, new Object[0]);
                            }
                            break Label_1870;
                        }
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                            SSLLogger.fine("jdk.tls.keyLimits:  entry = " + s2 + ". " + split2[0] + ":" + SSLCipher.tag[n] + " = " + long1, new Object[0]);
                        }
                        SSLCipher.cipherLimits.put(split2[0] + ":" + SSLCipher.tag[n], long1);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.fine("jdk.tls.keyLimits:  Unknown action:  " + s2, new Object[0]);
                    }
                }
            }
        }
    }
    
    abstract static class SSLReadCipher
    {
        final Authenticator authenticator;
        final ProtocolVersion protocolVersion;
        boolean keyLimitEnabled;
        long keyLimitCountdown;
        SecretKey baseSecret;
        
        SSLReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
            this.keyLimitEnabled = false;
            this.keyLimitCountdown = 0L;
            this.authenticator = authenticator;
            this.protocolVersion = protocolVersion;
        }
        
        static final SSLReadCipher nullTlsReadCipher() {
            try {
                return SSLCipher.B_NULL.createReadCipher(Authenticator.nullTlsMac(), ProtocolVersion.NONE, null, null, null);
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Cannot create NULL SSLCipher", ex);
            }
        }
        
        abstract Plaintext decrypt(final byte p0, final ByteBuffer p1, final byte[] p2) throws GeneralSecurityException;
        
        void dispose() {
        }
        
        abstract int estimateFragmentSize(final int p0, final int p1);
        
        boolean isNullCipher() {
            return false;
        }
        
        public boolean atKeyLimit() {
            if (this.keyLimitCountdown >= 0L) {
                return false;
            }
            this.keyLimitEnabled = false;
            return true;
        }
    }
    
    abstract static class SSLWriteCipher
    {
        final Authenticator authenticator;
        final ProtocolVersion protocolVersion;
        boolean keyLimitEnabled;
        long keyLimitCountdown;
        SecretKey baseSecret;
        
        SSLWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
            this.keyLimitEnabled = false;
            this.keyLimitCountdown = 0L;
            this.authenticator = authenticator;
            this.protocolVersion = protocolVersion;
        }
        
        abstract int encrypt(final byte p0, final ByteBuffer p1);
        
        static final SSLWriteCipher nullTlsWriteCipher() {
            try {
                return SSLCipher.B_NULL.createWriteCipher(Authenticator.nullTlsMac(), ProtocolVersion.NONE, null, null, null);
            }
            catch (final GeneralSecurityException ex) {
                throw new RuntimeException("Cannot create NULL SSL write Cipher", ex);
            }
        }
        
        void dispose() {
        }
        
        abstract int getExplicitNonceSize();
        
        abstract int calculateFragmentSize(final int p0, final int p1);
        
        abstract int calculatePacketSize(final int p0, final int p1);
        
        boolean isCBCMode() {
            return false;
        }
        
        boolean isNullCipher() {
            return false;
        }
        
        public boolean atKeyLimit() {
            if (this.keyLimitCountdown >= 0L) {
                return false;
            }
            this.keyLimitEnabled = false;
            return true;
        }
    }
    
    private static final class NullReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new NullReadCipher(authenticator, protocolVersion);
        }
        
        static final class NullReadCipher extends SSLReadCipher
        {
            NullReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
                super(authenticator, protocolVersion);
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    checkStreamMac(mac, byteBuffer, b, array);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
            
            @Override
            boolean isNullCipher() {
                return true;
            }
        }
    }
    
    private static final class NullWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new NullWriteCipher(authenticator, protocolVersion);
        }
        
        static final class NullWriteCipher extends SSLWriteCipher
        {
            NullWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
                super(authenticator, protocolVersion);
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    addMac(mac, byteBuffer, b);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final int remaining = byteBuffer.remaining();
                byteBuffer.position(byteBuffer.limit());
                return remaining;
            }
            
            @Override
            int getExplicitNonceSize() {
                return 0;
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                return n - n2 - ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                return n + n2 + ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
            
            @Override
            boolean isNullCipher() {
                return true;
            }
        }
    }
    
    private static final class StreamReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new StreamReadCipher(authenticator, protocolVersion, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class StreamReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            StreamReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(s)).init(2, key, algorithmParameterSpec, secureRandom);
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                final int remaining = byteBuffer.remaining();
                final int position = byteBuffer.position();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (remaining != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex);
                }
                byteBuffer.position(position);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", byteBuffer.duplicate());
                }
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    checkStreamMac(mac, byteBuffer, b, array);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
        }
    }
    
    private static final class StreamWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new StreamWriteCipher(authenticator, protocolVersion, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class StreamWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            
            StreamWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(s)).init(1, key, algorithmParameterSpec, secureRandom);
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    addMac(mac, byteBuffer, b);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.finest("Padded plaintext before ENCRYPTION", byteBuffer.duplicate());
                }
                final int remaining = byteBuffer.remaining();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (remaining != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex);
                }
                return remaining;
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int getExplicitNonceSize() {
                return 0;
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                return n - n2 - ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                return n + n2 + ((Authenticator.MAC)this.authenticator).macAlg().size;
            }
        }
    }
    
    private static final class T10BlockReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new BlockReadCipher(authenticator, protocolVersion, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class BlockReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            BlockReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(s)).init(2, key, algorithmParameterSpec, secureRandom);
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                BadPaddingException ex = null;
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                final int remaining = byteBuffer.remaining();
                final int size = mac.macAlg().size;
                if (size != 0 && !this.sanityCheck(size, byteBuffer.remaining())) {
                    ex = new BadPaddingException("ciphertext sanity check failed");
                }
                final int remaining2 = byteBuffer.remaining();
                final int position = byteBuffer.position();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (remaining2 != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex2) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex2);
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext after DECRYPTION", byteBuffer.duplicate().position(position));
                }
                final int blockSize = this.cipher.getBlockSize();
                byteBuffer.position(position);
                try {
                    removePadding(byteBuffer, size, blockSize, this.protocolVersion);
                }
                catch (final BadPaddingException ex3) {
                    if (ex == null) {
                        ex = ex3;
                    }
                }
                try {
                    if (size != 0) {
                        checkCBCMac(mac, byteBuffer, b, remaining, array);
                    }
                    else {
                        this.authenticator.increaseSequenceNumber();
                    }
                }
                catch (final BadPaddingException ex4) {
                    if (ex == null) {
                        ex = ex4;
                    }
                }
                if (ex != null) {
                    throw ex;
                }
                return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - ((Authenticator.MAC)this.authenticator).macAlg().size - 1;
            }
            
            private boolean sanityCheck(final int n, final int n2) {
                final int blockSize = this.cipher.getBlockSize();
                if (n2 % blockSize == 0) {
                    final int n3 = n + 1;
                    return n2 >= ((n3 >= blockSize) ? n3 : blockSize);
                }
                return false;
            }
        }
    }
    
    private static final class T10BlockWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new BlockWriteCipher(authenticator, protocolVersion, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class BlockWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            
            BlockWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(s)).init(1, key, algorithmParameterSpec, secureRandom);
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final int position = byteBuffer.position();
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    addMac(mac, byteBuffer, b);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final int access$1600 = addPadding(byteBuffer, this.cipher.getBlockSize());
                byteBuffer.position(position);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext before ENCRYPTION", byteBuffer.duplicate());
                }
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (access$1600 != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex);
                }
                return access$1600;
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int getExplicitNonceSize() {
                return 0;
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                final int size = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                final int n3 = n - n2;
                int n4 = n3 - n3 % blockSize;
                return --n4 - size;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                final int size = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int n3 = n + size + 1;
                if (n3 % blockSize != 0) {
                    final int n4 = n3 + (blockSize - 1);
                    n3 = n4 - n4 % blockSize;
                }
                return n2 + n3;
            }
            
            @Override
            boolean isCBCMode() {
                return true;
            }
        }
    }
    
    private static final class T11BlockReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new BlockReadCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class BlockReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            BlockReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                if (algorithmParameterSpec == null) {
                    algorithmParameterSpec = new IvParameterSpec(new byte[sslCipher.ivSize]);
                }
                this.cipher.init(2, key, algorithmParameterSpec, secureRandom);
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                BadPaddingException ex = null;
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                final int remaining = byteBuffer.remaining();
                final int size = mac.macAlg().size;
                if (size != 0 && !this.sanityCheck(size, byteBuffer.remaining())) {
                    ex = new BadPaddingException("ciphertext sanity check failed");
                }
                final int remaining2 = byteBuffer.remaining();
                final int position = byteBuffer.position();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (remaining2 != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex2) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex2);
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext after DECRYPTION", byteBuffer.duplicate().position(position));
                }
                byteBuffer.position(position + this.cipher.getBlockSize());
                final int position2 = byteBuffer.position();
                final int blockSize = this.cipher.getBlockSize();
                byteBuffer.position(position2);
                try {
                    removePadding(byteBuffer, size, blockSize, this.protocolVersion);
                }
                catch (final BadPaddingException ex3) {
                    if (ex == null) {
                        ex = ex3;
                    }
                }
                try {
                    if (size != 0) {
                        checkCBCMac(mac, byteBuffer, b, remaining, array);
                    }
                    else {
                        this.authenticator.increaseSequenceNumber();
                    }
                }
                catch (final BadPaddingException ex4) {
                    if (ex == null) {
                        ex = ex4;
                    }
                }
                if (ex != null) {
                    throw ex;
                }
                return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - this.cipher.getBlockSize() - ((Authenticator.MAC)this.authenticator).macAlg().size - 1;
            }
            
            private boolean sanityCheck(final int n, final int n2) {
                final int blockSize = this.cipher.getBlockSize();
                if (n2 % blockSize == 0) {
                    final int n3 = n + 1;
                    return n2 >= ((n3 >= blockSize) ? n3 : blockSize) + blockSize;
                }
                return false;
            }
        }
    }
    
    private static final class T11BlockWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new BlockWriteCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class BlockWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final SecureRandom random;
            
            BlockWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                this.random = random;
                if (algorithmParameterSpec == null) {
                    algorithmParameterSpec = new IvParameterSpec(new byte[sslCipher.ivSize]);
                }
                this.cipher.init(1, key, algorithmParameterSpec, random);
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final int position = byteBuffer.position();
                final Authenticator.MAC mac = (Authenticator.MAC)this.authenticator;
                if (mac.macAlg().size != 0) {
                    addMac(mac, byteBuffer, b);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final byte[] array = new byte[this.cipher.getBlockSize()];
                this.random.nextBytes(array);
                final int n = position - array.length;
                byteBuffer.position(n);
                byteBuffer.put(array);
                byteBuffer.position(n);
                final int access$1600 = addPadding(byteBuffer, this.cipher.getBlockSize());
                byteBuffer.position(n);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext before ENCRYPTION", byteBuffer.duplicate());
                }
                final ByteBuffer duplicate = byteBuffer.duplicate();
                try {
                    if (access$1600 != this.cipher.update(duplicate, byteBuffer)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (byteBuffer.position() != duplicate.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException ex) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex);
                }
                return access$1600;
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int getExplicitNonceSize() {
                return this.cipher.getBlockSize();
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                final int size = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                final int n3 = n - n2 - blockSize;
                int n4 = n3 - n3 % blockSize;
                return --n4 - size;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                final int size = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int n3 = n + size + 1;
                if (n3 % blockSize != 0) {
                    final int n4 = n3 + (blockSize - 1);
                    n3 = n4 - n4 % blockSize;
                }
                return n2 + blockSize + n3;
            }
            
            @Override
            boolean isCBCMode() {
                return true;
            }
        }
    }
    
    private static final class T12GcmReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new GcmReadCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class GcmReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] fixedIv;
            private final int recordIvSize;
            private final SecureRandom random;
            
            GcmReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.fixedIv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                this.recordIvSize = sslCipher.ivSize - sslCipher.fixedIvSize;
                this.random = random;
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                if (byteBuffer.remaining() < this.recordIvSize + this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + byteBuffer.remaining() + "). Needs to be more than or equal to IV size (" + this.recordIvSize + ") + tag size (" + this.tagSize + ")");
                }
                final byte[] copy = Arrays.copyOf(this.fixedIv, this.fixedIv.length + this.recordIvSize);
                byteBuffer.get(copy, this.fixedIv.length, this.recordIvSize);
                final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(this.tagSize * 8, copy);
                try {
                    this.cipher.init(2, this.key, gcmParameterSpec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ex) {
                    throw new RuntimeException("invalid key or spec in GCM mode", (Throwable)ex);
                }
                this.cipher.updateAAD(this.authenticator.acquireAuthenticationBytes(b, byteBuffer.remaining() - this.tagSize, array));
                final int position = byteBuffer.position();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                int doFinal;
                try {
                    doFinal = this.cipher.doFinal(duplicate, byteBuffer);
                }
                catch (final IllegalBlockSizeException ex2) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ex2.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException ex3) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex3);
                }
                byteBuffer.position(position);
                byteBuffer.limit(position + doFinal);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", byteBuffer.duplicate());
                }
                return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - this.recordIvSize - this.tagSize;
            }
        }
    }
    
    private static final class T12GcmWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new GcmWriteCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        private static final class GcmWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] fixedIv;
            private final int recordIvSize;
            private final SecureRandom random;
            
            GcmWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.fixedIv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                this.recordIvSize = sslCipher.ivSize - sslCipher.fixedIvSize;
                this.random = random;
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final byte[] sequenceNumber = this.authenticator.sequenceNumber();
                final byte[] copy = Arrays.copyOf(this.fixedIv, this.fixedIv.length + sequenceNumber.length);
                System.arraycopy(sequenceNumber, 0, copy, this.fixedIv.length, sequenceNumber.length);
                final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(this.tagSize * 8, copy);
                try {
                    this.cipher.init(1, this.key, gcmParameterSpec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ex) {
                    throw new RuntimeException("invalid key or spec in GCM mode", (Throwable)ex);
                }
                this.cipher.updateAAD(this.authenticator.acquireAuthenticationBytes(b, byteBuffer.remaining(), null));
                byteBuffer.position(byteBuffer.position() - sequenceNumber.length);
                byteBuffer.put(sequenceNumber);
                final int position = byteBuffer.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", byteBuffer.duplicate());
                }
                final ByteBuffer duplicate = byteBuffer.duplicate();
                final int outputSize = this.cipher.getOutputSize(duplicate.remaining());
                if (outputSize > byteBuffer.remaining()) {
                    byteBuffer.limit(position + outputSize);
                }
                int doFinal;
                try {
                    doFinal = this.cipher.doFinal(duplicate, byteBuffer);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ex2) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), (Throwable)ex2);
                }
                if (doFinal != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                return doFinal + sequenceNumber.length;
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int getExplicitNonceSize() {
                return this.recordIvSize;
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                return n - n2 - this.recordIvSize - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                return n + n2 + this.recordIvSize + this.tagSize;
            }
        }
    }
    
    private static final class T13GcmReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new GcmReadCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        static final class GcmReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            GcmReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(s.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyLimit read side: algorithm = " + s.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public Plaintext decrypt(final byte b, final ByteBuffer byteBuffer, final byte[] array) throws GeneralSecurityException {
                if (b == ContentType.CHANGE_CIPHER_SPEC.id) {
                    return new Plaintext(b, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
                }
                if (byteBuffer.remaining() <= this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + byteBuffer.remaining() + "). Needs to be more than tag size (" + this.tagSize + ")");
                }
                byte[] sequenceNumber = array;
                if (sequenceNumber == null) {
                    sequenceNumber = this.authenticator.sequenceNumber();
                }
                final byte[] array2 = this.iv.clone();
                final int n = array2.length - sequenceNumber.length;
                for (int i = 0; i < sequenceNumber.length; ++i) {
                    final byte[] array3 = array2;
                    final int n2 = n + i;
                    array3[n2] ^= sequenceNumber[i];
                }
                final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(this.tagSize * 8, array2);
                try {
                    this.cipher.init(2, this.key, gcmParameterSpec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ex) {
                    throw new RuntimeException("invalid key or spec in GCM mode", (Throwable)ex);
                }
                this.cipher.updateAAD(this.authenticator.acquireAuthenticationBytes(b, byteBuffer.remaining(), sequenceNumber));
                final int position = byteBuffer.position();
                final ByteBuffer duplicate = byteBuffer.duplicate();
                int doFinal;
                try {
                    doFinal = this.cipher.doFinal(duplicate, byteBuffer);
                }
                catch (final IllegalBlockSizeException ex2) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ex2.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException ex3) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), ex3);
                }
                byteBuffer.position(position);
                byteBuffer.limit(position + doFinal);
                int n3;
                for (n3 = byteBuffer.limit() - 1; n3 > 0 && byteBuffer.get(n3) == 0; --n3) {}
                if (n3 < position + 1) {
                    throw new BadPaddingException("Incorrect inner plaintext: no content type");
                }
                final byte value = byteBuffer.get(n3);
                byteBuffer.limit(n3);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", byteBuffer.duplicate());
                }
                if (this.keyLimitEnabled) {
                    this.keyLimitCountdown -= doFinal;
                }
                return new Plaintext(value, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, byteBuffer.slice());
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int estimateFragmentSize(final int n, final int n2) {
                return n - n2 - this.tagSize;
            }
        }
    }
    
    private static final class T13GcmWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom secureRandom) throws GeneralSecurityException {
            return new GcmWriteCipher(authenticator, protocolVersion, sslCipher, s, key, algorithmParameterSpec, secureRandom);
        }
        
        private static final class GcmWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            GcmWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String s, final Key key, final AlgorithmParameterSpec algorithmParameterSpec, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(s);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)algorithmParameterSpec).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(s.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyLimit write side: algorithm = " + s.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public int encrypt(final byte b, final ByteBuffer byteBuffer) {
                final byte[] sequenceNumber = this.authenticator.sequenceNumber();
                final byte[] array = this.iv.clone();
                final int n = array.length - sequenceNumber.length;
                for (int i = 0; i < sequenceNumber.length; ++i) {
                    final byte[] array2 = array;
                    final int n2 = n + i;
                    array2[n2] ^= sequenceNumber[i];
                }
                final GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(this.tagSize * 8, array);
                try {
                    this.cipher.init(1, this.key, gcmParameterSpec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ex) {
                    throw new RuntimeException("invalid key or spec in GCM mode", (Throwable)ex);
                }
                final int outputSize = this.cipher.getOutputSize(byteBuffer.remaining());
                this.cipher.updateAAD(this.authenticator.acquireAuthenticationBytes(b, outputSize, sequenceNumber));
                final int position = byteBuffer.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", byteBuffer.duplicate());
                }
                final ByteBuffer duplicate = byteBuffer.duplicate();
                if (outputSize > byteBuffer.remaining()) {
                    byteBuffer.limit(position + outputSize);
                }
                int doFinal;
                try {
                    doFinal = this.cipher.doFinal(duplicate, byteBuffer);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ex2) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), (Throwable)ex2);
                }
                if (doFinal != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                if (this.keyLimitEnabled) {
                    this.keyLimitCountdown -= doFinal;
                }
                return doFinal;
            }
            
            @Override
            void dispose() {
                if (this.cipher != null) {
                    try {
                        this.cipher.doFinal();
                    }
                    catch (final Exception ex) {}
                }
            }
            
            @Override
            int getExplicitNonceSize() {
                return 0;
            }
            
            @Override
            int calculateFragmentSize(final int n, final int n2) {
                return n - n2 - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int n, final int n2) {
                return n + n2 + this.tagSize;
            }
        }
    }
    
    interface WriteCipherGenerator
    {
        SSLWriteCipher createCipher(final SSLCipher p0, final Authenticator p1, final ProtocolVersion p2, final String p3, final Key p4, final AlgorithmParameterSpec p5, final SecureRandom p6) throws GeneralSecurityException;
    }
    
    interface ReadCipherGenerator
    {
        SSLReadCipher createCipher(final SSLCipher p0, final Authenticator p1, final ProtocolVersion p2, final String p3, final Key p4, final AlgorithmParameterSpec p5, final SecureRandom p6) throws GeneralSecurityException;
    }
}
