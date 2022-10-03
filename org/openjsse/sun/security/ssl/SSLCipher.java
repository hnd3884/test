package org.openjsse.sun.security.ssl;

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
    B_AES_256_GCM_IV("AES/GCM/NoPadding", CipherType.AEAD_CIPHER, 32, 32, 12, 0, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T13GcmWriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }), 
    B_CC20_P1305("ChaCha20-Poly1305", CipherType.AEAD_CIPHER, 32, 32, 12, 12, true, false, (Map.Entry<ReadCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12CC20P1305ReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(new T13CC20P1305ReadCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) }, (Map.Entry<WriteCipherGenerator, ProtocolVersion[]>[])new Map.Entry[] { new AbstractMap.SimpleImmutableEntry(new T12CC20P1305WriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_12), new AbstractMap.SimpleImmutableEntry(new T13CC20P1305WriteCipherGenerator(), ProtocolVersion.PROTOCOLS_OF_13) });
    
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
        final String[] splits = transformation.split("/");
        this.algorithm = splits[0];
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
    
    private static boolean isTransformationAvailable(final String transformation) {
        if (transformation.equals("NULL")) {
            return true;
        }
        try {
            JsseJce.getCipher(transformation);
            return true;
        }
        catch (final NoSuchAlgorithmException e) {
            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                SSLLogger.fine("Transformation " + transformation + " is not available.", new Object[0]);
            }
            return false;
        }
    }
    
    SSLReadCipher createReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SecretKey key, final IvParameterSpec iv, final SecureRandom random) throws GeneralSecurityException {
        if (this.writeCipherGenerators.length == 0) {
            return null;
        }
        ReadCipherGenerator wcg = null;
        for (final Map.Entry<ReadCipherGenerator, ProtocolVersion[]> me : this.readCipherGenerators) {
            for (final ProtocolVersion pv : me.getValue()) {
                if (protocolVersion == pv) {
                    wcg = me.getKey();
                }
            }
        }
        if (wcg != null) {
            return wcg.createCipher(this, authenticator, protocolVersion, this.transformation, key, iv, random);
        }
        return null;
    }
    
    SSLWriteCipher createWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SecretKey key, final IvParameterSpec iv, final SecureRandom random) throws GeneralSecurityException {
        if (this.readCipherGenerators.length == 0) {
            return null;
        }
        WriteCipherGenerator rcg = null;
        for (final Map.Entry<WriteCipherGenerator, ProtocolVersion[]> me : this.writeCipherGenerators) {
            for (final ProtocolVersion pv : me.getValue()) {
                if (protocolVersion == pv) {
                    rcg = me.getKey();
                }
            }
        }
        if (rcg != null) {
            return rcg.createCipher(this, authenticator, protocolVersion, this.transformation, key, iv, random);
        }
        return null;
    }
    
    boolean isAvailable() {
        return this.isAvailable;
    }
    
    private static boolean isUnlimited(final int keySize, final String transformation) {
        final int keySizeInBits = keySize * 8;
        if (keySizeInBits > 128) {
            try {
                if (Cipher.getMaxAllowedKeyLength(transformation) < keySizeInBits) {
                    return false;
                }
            }
            catch (final Exception e) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.description;
    }
    
    private static void addMac(final Authenticator.MAC signer, final ByteBuffer destination, final byte contentType) {
        if (signer.macAlg().size != 0) {
            final int dstContent = destination.position();
            final byte[] hash = signer.compute(contentType, destination, false);
            destination.limit(destination.limit() + hash.length);
            destination.put(hash);
            destination.position(dstContent);
        }
    }
    
    private static void checkStreamMac(final Authenticator.MAC signer, final ByteBuffer bb, final byte contentType, final byte[] sequence) throws BadPaddingException {
        final int tagLen = signer.macAlg().size;
        if (tagLen != 0) {
            final int contentLen = bb.remaining() - tagLen;
            if (contentLen < 0) {
                throw new BadPaddingException("bad record");
            }
            if (checkMacTags(contentType, bb, signer, sequence, false)) {
                throw new BadPaddingException("bad record MAC");
            }
        }
    }
    
    private static void checkCBCMac(final Authenticator.MAC signer, final ByteBuffer bb, final byte contentType, final int cipheredLength, final byte[] sequence) throws BadPaddingException {
        BadPaddingException reservedBPE = null;
        final int tagLen = signer.macAlg().size;
        final int pos = bb.position();
        if (tagLen != 0) {
            int contentLen = bb.remaining() - tagLen;
            if (contentLen < 0) {
                reservedBPE = new BadPaddingException("bad record");
                contentLen = cipheredLength - tagLen;
                bb.limit(pos + cipheredLength);
            }
            if (checkMacTags(contentType, bb, signer, sequence, false) && reservedBPE == null) {
                reservedBPE = new BadPaddingException("bad record MAC");
            }
            int remainingLen = calculateRemainingLen(signer, cipheredLength, contentLen);
            remainingLen += signer.macAlg().size;
            final ByteBuffer temporary = ByteBuffer.allocate(remainingLen);
            checkMacTags(contentType, temporary, signer, sequence, true);
        }
        if (reservedBPE != null) {
            throw reservedBPE;
        }
    }
    
    private static boolean checkMacTags(final byte contentType, final ByteBuffer bb, final Authenticator.MAC signer, final byte[] sequence, final boolean isSimulated) {
        final int tagLen = signer.macAlg().size;
        final int position = bb.position();
        final int lim = bb.limit();
        final int macOffset = lim - tagLen;
        bb.limit(macOffset);
        final byte[] hash = signer.compute(contentType, bb, sequence, isSimulated);
        if (hash == null || tagLen != hash.length) {
            throw new RuntimeException("Internal MAC error");
        }
        bb.position(macOffset);
        bb.limit(lim);
        try {
            final int[] results = compareMacTags(bb, hash);
            return results[0] != 0;
        }
        finally {
            bb.position(position);
            bb.limit(macOffset);
        }
    }
    
    private static int[] compareMacTags(final ByteBuffer bb, final byte[] tag) {
        final int[] results = { 0, 0 };
        for (final byte t : tag) {
            if (bb.get() != t) {
                final int[] array = results;
                final int n = 0;
                ++array[n];
            }
            else {
                final int[] array2 = results;
                final int n2 = 1;
                ++array2[n2];
            }
        }
        return results;
    }
    
    private static int calculateRemainingLen(final Authenticator.MAC signer, int fullLen, int usedLen) {
        final int blockLen = signer.macAlg().hashBlockSize;
        final int minimalPaddingLen = signer.macAlg().minimalPaddingSize;
        fullLen += 13 - (blockLen - minimalPaddingLen);
        usedLen += 13 - (blockLen - minimalPaddingLen);
        return 1 + (int)(Math.ceil(fullLen / (1.0 * blockLen)) - Math.ceil(usedLen / (1.0 * blockLen))) * blockLen;
    }
    
    private static int addPadding(final ByteBuffer bb, final int blockSize) {
        final int len = bb.remaining();
        int offset = bb.position();
        int newlen = len + 1;
        if (newlen % blockSize != 0) {
            newlen += blockSize - 1;
            newlen -= newlen % blockSize;
        }
        final byte pad = (byte)(newlen - len);
        bb.limit(newlen + offset);
        int i = 0;
        offset += len;
        while (i < pad) {
            bb.put(offset++, (byte)(pad - 1));
            ++i;
        }
        bb.position(offset);
        bb.limit(offset);
        return newlen;
    }
    
    private static int removePadding(final ByteBuffer bb, final int tagLen, final int blockSize, final ProtocolVersion protocolVersion) throws BadPaddingException {
        final int len = bb.remaining();
        final int offset = bb.position();
        final int padOffset = offset + len - 1;
        final int padLen = bb.get(padOffset) & 0xFF;
        final int newLen = len - (padLen + 1);
        if (newLen - tagLen < 0) {
            checkPadding(bb.duplicate(), (byte)(padLen & 0xFF));
            throw new BadPaddingException("Invalid Padding length: " + padLen);
        }
        final int[] results = checkPadding((ByteBuffer)bb.duplicate().position(offset + newLen), (byte)(padLen & 0xFF));
        if (protocolVersion.useTLS10PlusSpec()) {
            if (results[0] != 0) {
                throw new BadPaddingException("Invalid TLS padding data");
            }
        }
        else if (padLen > blockSize) {
            throw new BadPaddingException("Padding length (" + padLen + ") of SSLv3 message should not be bigger than the block size (" + blockSize + ")");
        }
        bb.limit(offset + newLen);
        return newLen;
    }
    
    private static int[] checkPadding(final ByteBuffer bb, final byte pad) {
        if (!bb.hasRemaining()) {
            throw new RuntimeException("hasRemaining() must be positive");
        }
        final int[] results = { 0, 0 };
        bb.mark();
        int i = 0;
        while (i <= 256) {
            while (bb.hasRemaining() && i <= 256) {
                if (bb.get() != pad) {
                    final int[] array = results;
                    final int n = 0;
                    ++array[n];
                }
                else {
                    final int[] array2 = results;
                    final int n2 = 1;
                    ++array2[n2];
                }
                ++i;
            }
            bb.reset();
        }
        return results;
    }
    
    static {
        cipherLimits = new HashMap<String, Long>();
        tag = new String[] { "KEYUPDATE" };
        final long max = 4611686018427387904L;
        final String prop = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction<String>() {
            @Override
            public String run() {
                return Security.getProperty("jdk.tls.keyLimits");
            }
        });
        if (prop != null) {
            final String[] split;
            final String[] propvalue = split = prop.split(",");
            for (final String entry : split) {
                final String[] values = entry.trim().toUpperCase().split(" ");
                Label_2008: {
                    if (values[1].contains(SSLCipher.tag[0])) {
                        final int index = 0;
                        final int i = values[2].indexOf("^");
                        long size;
                        try {
                            if (i >= 0) {
                                size = (long)Math.pow(2.0, Integer.parseInt(values[2].substring(i + 1)));
                            }
                            else {
                                size = Long.parseLong(values[2]);
                            }
                            if (size < 1L || size > 4611686018427387904L) {
                                throw new NumberFormatException("Length exceeded limits");
                            }
                        }
                        catch (final NumberFormatException e) {
                            if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                                SSLLogger.fine("jdk.tls.keyLimits:  " + e.getMessage() + ":  " + entry, new Object[0]);
                            }
                            break Label_2008;
                        }
                        if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                            SSLLogger.fine("jdk.tls.keyLimits:  entry = " + entry + ". " + values[0] + ":" + SSLCipher.tag[index] + " = " + size, new Object[0]);
                        }
                        SSLCipher.cipherLimits.put(values[0] + ":" + SSLCipher.tag[index], size);
                    }
                    else if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                        SSLLogger.fine("jdk.tls.keyLimits:  Unknown action:  " + entry, new Object[0]);
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
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Cannot create NULL SSLCipher", gse);
            }
        }
        
        static final SSLReadCipher nullDTlsReadCipher() {
            try {
                return SSLCipher.B_NULL.createReadCipher(Authenticator.nullDtlsMac(), ProtocolVersion.NONE, null, null, null);
            }
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Cannot create NULL SSLCipher", gse);
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
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Cannot create NULL SSL write Cipher", gse);
            }
        }
        
        static final SSLWriteCipher nullDTlsWriteCipher() {
            try {
                return SSLCipher.B_NULL.createWriteCipher(Authenticator.nullDtlsMac(), ProtocolVersion.NONE, null, null, null);
            }
            catch (final GeneralSecurityException gse) {
                throw new RuntimeException("Cannot create NULL SSL write Cipher", gse);
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
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new NullReadCipher(authenticator, protocolVersion);
        }
        
        static final class NullReadCipher extends SSLReadCipher
        {
            NullReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
                super(authenticator, protocolVersion);
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    checkStreamMac(signer, bb, contentType, sequence);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
            }
            
            @Override
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return packetSize - headerSize - macLen;
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
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new NullWriteCipher(authenticator, protocolVersion);
        }
        
        static final class NullWriteCipher extends SSLWriteCipher
        {
            NullWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion) {
                super(authenticator, protocolVersion);
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    addMac(signer, bb, contentType);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final int len = bb.remaining();
                bb.position(bb.limit());
                return len;
            }
            
            @Override
            int getExplicitNonceSize() {
                return 0;
            }
            
            @Override
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return packetLimit - headerSize - macLen;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return fragmentSize + headerSize + macLen;
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
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new StreamReadCipher(authenticator, protocolVersion, algorithm, key, params, random);
        }
        
        static final class StreamReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            StreamReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(algorithm)).init(2, key, params, random);
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                final int len = bb.remaining();
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                bb.position(pos);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", bb.duplicate());
                }
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    checkStreamMac(signer, bb, contentType, sequence);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return packetSize - headerSize - macLen;
            }
        }
    }
    
    private static final class StreamWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new StreamWriteCipher(authenticator, protocolVersion, algorithm, key, params, random);
        }
        
        static final class StreamWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            
            StreamWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(algorithm)).init(1, key, params, random);
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    addMac(signer, bb, contentType);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.finest("Padded plaintext before ENCRYPTION", bb.duplicate());
                }
                final int len = bb.remaining();
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return packetLimit - headerSize - macLen;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return fragmentSize + headerSize + macLen;
            }
        }
    }
    
    private static final class T10BlockReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new BlockReadCipher(authenticator, protocolVersion, algorithm, key, params, random);
        }
        
        static final class BlockReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            BlockReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(algorithm)).init(2, key, params, random);
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                BadPaddingException reservedBPE = null;
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                final int cipheredLength = bb.remaining();
                final int tagLen = signer.macAlg().size;
                if (tagLen != 0 && !this.sanityCheck(tagLen, bb.remaining())) {
                    reservedBPE = new BadPaddingException("ciphertext sanity check failed");
                }
                final int len = bb.remaining();
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext after DECRYPTION", bb.duplicate().position(pos));
                }
                final int blockSize = this.cipher.getBlockSize();
                bb.position(pos);
                try {
                    removePadding(bb, tagLen, blockSize, this.protocolVersion);
                }
                catch (final BadPaddingException bpe) {
                    if (reservedBPE == null) {
                        reservedBPE = bpe;
                    }
                }
                try {
                    if (tagLen != 0) {
                        checkCBCMac(signer, bb, contentType, cipheredLength, sequence);
                    }
                    else {
                        this.authenticator.increaseSequenceNumber();
                    }
                }
                catch (final BadPaddingException bpe) {
                    if (reservedBPE == null) {
                        reservedBPE = bpe;
                    }
                }
                if (reservedBPE != null) {
                    throw reservedBPE;
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                return packetSize - headerSize - macLen - 1;
            }
            
            private boolean sanityCheck(final int tagLen, final int fragmentLen) {
                final int blockSize = this.cipher.getBlockSize();
                if (fragmentLen % blockSize == 0) {
                    int minimal = tagLen + 1;
                    minimal = ((minimal >= blockSize) ? minimal : blockSize);
                    return fragmentLen >= minimal;
                }
                return false;
            }
        }
    }
    
    private static final class T10BlockWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new BlockWriteCipher(authenticator, protocolVersion, algorithm, key, params, random);
        }
        
        static final class BlockWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            
            BlockWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                (this.cipher = JsseJce.getCipher(algorithm)).init(1, key, params, random);
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final int pos = bb.position();
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    addMac(signer, bb, contentType);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final int blockSize = this.cipher.getBlockSize();
                final int len = addPadding(bb, blockSize);
                bb.position(pos);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int fragLen = packetLimit - headerSize;
                fragLen -= fragLen % blockSize;
                fragLen = --fragLen - macLen;
                return fragLen;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int paddedLen = fragmentSize + macLen + 1;
                if (paddedLen % blockSize != 0) {
                    paddedLen += blockSize - 1;
                    paddedLen -= paddedLen % blockSize;
                }
                return headerSize + paddedLen;
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
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new BlockReadCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class BlockReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            
            BlockReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                if (params == null) {
                    params = new IvParameterSpec(new byte[sslCipher.ivSize]);
                }
                this.cipher.init(2, key, params, random);
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                BadPaddingException reservedBPE = null;
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                final int cipheredLength = bb.remaining();
                final int tagLen = signer.macAlg().size;
                if (tagLen != 0 && !this.sanityCheck(tagLen, bb.remaining())) {
                    reservedBPE = new BadPaddingException("ciphertext sanity check failed");
                }
                final int len = bb.remaining();
                int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext after DECRYPTION", bb.duplicate().position(pos));
                }
                bb.position(pos + this.cipher.getBlockSize());
                pos = bb.position();
                final int blockSize = this.cipher.getBlockSize();
                bb.position(pos);
                try {
                    removePadding(bb, tagLen, blockSize, this.protocolVersion);
                }
                catch (final BadPaddingException bpe) {
                    if (reservedBPE == null) {
                        reservedBPE = bpe;
                    }
                }
                try {
                    if (tagLen != 0) {
                        checkCBCMac(signer, bb, contentType, cipheredLength, sequence);
                    }
                    else {
                        this.authenticator.increaseSequenceNumber();
                    }
                }
                catch (final BadPaddingException bpe) {
                    if (reservedBPE == null) {
                        reservedBPE = bpe;
                    }
                }
                if (reservedBPE != null) {
                    throw reservedBPE;
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int nonceSize = this.cipher.getBlockSize();
                return packetSize - headerSize - nonceSize - macLen - 1;
            }
            
            private boolean sanityCheck(final int tagLen, final int fragmentLen) {
                final int blockSize = this.cipher.getBlockSize();
                if (fragmentLen % blockSize == 0) {
                    int minimal = tagLen + 1;
                    minimal = ((minimal >= blockSize) ? minimal : blockSize);
                    minimal += blockSize;
                    return fragmentLen >= minimal;
                }
                return false;
            }
        }
    }
    
    private static final class T11BlockWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new BlockWriteCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class BlockWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final SecureRandom random;
            
            BlockWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                this.random = random;
                if (params == null) {
                    params = new IvParameterSpec(new byte[sslCipher.ivSize]);
                }
                this.cipher.init(1, key, params, random);
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                int pos = bb.position();
                final Authenticator.MAC signer = (Authenticator.MAC)this.authenticator;
                if (signer.macAlg().size != 0) {
                    addMac(signer, bb, contentType);
                }
                else {
                    this.authenticator.increaseSequenceNumber();
                }
                final byte[] nonce = new byte[this.cipher.getBlockSize()];
                this.random.nextBytes(nonce);
                pos -= nonce.length;
                bb.position(pos);
                bb.put(nonce);
                bb.position(pos);
                final int blockSize = this.cipher.getBlockSize();
                final int len = addPadding(bb, blockSize);
                bb.position(pos);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Padded plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                try {
                    if (len != this.cipher.update(dup, bb)) {
                        throw new RuntimeException("Unexpected number of plaintext bytes");
                    }
                    if (bb.position() != dup.position()) {
                        throw new RuntimeException("Unexpected ByteBuffer position");
                    }
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int fragLen = packetLimit - headerSize - blockSize;
                fragLen -= fragLen % blockSize;
                fragLen = --fragLen - macLen;
                return fragLen;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                final int macLen = ((Authenticator.MAC)this.authenticator).macAlg().size;
                final int blockSize = this.cipher.getBlockSize();
                int paddedLen = fragmentSize + macLen + 1;
                if (paddedLen % blockSize != 0) {
                    paddedLen += blockSize - 1;
                    paddedLen -= paddedLen % blockSize;
                }
                return headerSize + blockSize + paddedLen;
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
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new GcmReadCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class GcmReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] fixedIv;
            private final int recordIvSize;
            private final SecureRandom random;
            
            GcmReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.fixedIv = ((IvParameterSpec)params).getIV();
                this.recordIvSize = sslCipher.ivSize - sslCipher.fixedIvSize;
                this.random = random;
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                if (bb.remaining() < this.recordIvSize + this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + bb.remaining() + "). Needs to be more than or equal to IV size (" + this.recordIvSize + ") + tag size (" + this.tagSize + ")");
                }
                final byte[] iv = Arrays.copyOf(this.fixedIv, this.fixedIv.length + this.recordIvSize);
                bb.get(iv, this.fixedIv.length, this.recordIvSize);
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, iv);
                try {
                    this.cipher.init(2, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining() - this.tagSize, sequence);
                this.cipher.updateAAD(aad);
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                int len;
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ibse.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                bb.position(pos);
                bb.limit(pos + len);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", bb.duplicate());
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                return packetSize - headerSize - this.recordIvSize - this.tagSize;
            }
        }
    }
    
    private static final class T12GcmWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new GcmWriteCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        private static final class GcmWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] fixedIv;
            private final int recordIvSize;
            private final SecureRandom random;
            
            GcmWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.fixedIv = ((IvParameterSpec)params).getIV();
                this.recordIvSize = sslCipher.ivSize - sslCipher.fixedIvSize;
                this.random = random;
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final byte[] nonce = this.authenticator.sequenceNumber();
                final byte[] iv = Arrays.copyOf(this.fixedIv, this.fixedIv.length + nonce.length);
                System.arraycopy(nonce, 0, iv, this.fixedIv.length, nonce.length);
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, iv);
                try {
                    this.cipher.init(1, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining(), null);
                this.cipher.updateAAD(aad);
                bb.position(bb.position() - nonce.length);
                bb.put(nonce);
                final int pos = bb.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                final int outputSize = this.cipher.getOutputSize(dup.remaining());
                if (outputSize > bb.remaining()) {
                    bb.limit(pos + outputSize);
                }
                int len;
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
                }
                if (len != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                return len + nonce.length;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                return packetLimit - headerSize - this.recordIvSize - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                return fragmentSize + headerSize + this.recordIvSize + this.tagSize;
            }
        }
    }
    
    private static final class T13GcmReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new GcmReadCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class GcmReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            GcmReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(algorithm.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyLimit read side: algorithm = " + algorithm.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public Plaintext decrypt(byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                if (contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                    return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
                }
                if (bb.remaining() <= this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + bb.remaining() + "). Needs to be more than tag size (" + this.tagSize + ")");
                }
                byte[] sn = sequence;
                if (sn == null) {
                    sn = this.authenticator.sequenceNumber();
                }
                final byte[] nonce = this.iv.clone();
                final int offset = nonce.length - sn.length;
                for (int i = 0; i < sn.length; ++i) {
                    final byte[] array = nonce;
                    final int n = offset + i;
                    array[n] ^= sn[i];
                }
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, nonce);
                try {
                    this.cipher.init(2, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining(), sn);
                this.cipher.updateAAD(aad);
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                int len;
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ibse.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                bb.position(pos);
                bb.limit(pos + len);
                int j;
                for (j = bb.limit() - 1; j > 0 && bb.get(j) == 0; --j) {}
                if (j < pos + 1) {
                    throw new BadPaddingException("Incorrect inner plaintext: no content type");
                }
                contentType = bb.get(j);
                bb.limit(j);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", bb.duplicate());
                }
                if (this.keyLimitEnabled) {
                    this.keyLimitCountdown -= len;
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                return packetSize - headerSize - this.tagSize;
            }
        }
    }
    
    private static final class T13GcmWriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new GcmWriteCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        private static final class GcmWriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            GcmWriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(algorithm.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("KeyLimit write side: algorithm = " + algorithm.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final byte[] sn = this.authenticator.sequenceNumber();
                final byte[] nonce = this.iv.clone();
                final int offset = nonce.length - sn.length;
                for (int i = 0; i < sn.length; ++i) {
                    final byte[] array = nonce;
                    final int n = offset + i;
                    array[n] ^= sn[i];
                }
                final GCMParameterSpec spec = new GCMParameterSpec(this.tagSize * 8, nonce);
                try {
                    this.cipher.init(1, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in GCM mode", ikae);
                }
                final int outputSize = this.cipher.getOutputSize(bb.remaining());
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, outputSize, sn);
                this.cipher.updateAAD(aad);
                final int pos = bb.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                if (outputSize > bb.remaining()) {
                    bb.limit(pos + outputSize);
                }
                int len;
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
                }
                if (len != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                if (this.keyLimitEnabled) {
                    this.keyLimitCountdown -= len;
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                return packetLimit - headerSize - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                return fragmentSize + headerSize + this.tagSize;
            }
        }
    }
    
    private static final class T12CC20P1305ReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new CC20P1305ReadCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class CC20P1305ReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            CC20P1305ReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
            }
            
            public Plaintext decrypt(final byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                if (bb.remaining() <= this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + bb.remaining() + "). Needs to be more than tag size (" + this.tagSize + ")");
                }
                byte[] sn = sequence;
                if (sn == null) {
                    sn = this.authenticator.sequenceNumber();
                }
                final byte[] nonce = new byte[this.iv.length];
                System.arraycopy(sn, 0, nonce, nonce.length - sn.length, sn.length);
                for (int i = 0; i < nonce.length; ++i) {
                    final byte[] array = nonce;
                    final int n = i;
                    array[n] ^= this.iv[i];
                }
                final AlgorithmParameterSpec spec = new IvParameterSpec(nonce);
                try {
                    this.cipher.init(2, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in AEAD mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining() - this.tagSize, sequence);
                this.cipher.updateAAD(aad);
                int len = bb.remaining();
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ibse.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                bb.position(pos);
                bb.limit(pos + len);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", bb.duplicate());
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                return packetSize - headerSize - this.tagSize;
            }
        }
    }
    
    private static final class T12CC20P1305WriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new CC20P1305WriteCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        private static final class CC20P1305WriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            CC20P1305WriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(algorithm.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("algorithm = " + algorithm.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final byte[] sn = this.authenticator.sequenceNumber();
                final byte[] nonce = new byte[this.iv.length];
                System.arraycopy(sn, 0, nonce, nonce.length - sn.length, sn.length);
                for (int i = 0; i < nonce.length; ++i) {
                    final byte[] array = nonce;
                    final int n = i;
                    array[n] ^= this.iv[i];
                }
                final AlgorithmParameterSpec spec = new IvParameterSpec(nonce);
                try {
                    this.cipher.init(1, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in AEAD mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining(), null);
                this.cipher.updateAAD(aad);
                int len = bb.remaining();
                final int pos = bb.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                final int outputSize = this.cipher.getOutputSize(dup.remaining());
                if (outputSize > bb.remaining()) {
                    bb.limit(pos + outputSize);
                }
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
                }
                if (len != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                return packetLimit - headerSize - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                return fragmentSize + headerSize + this.tagSize;
            }
        }
    }
    
    private static final class T13CC20P1305ReadCipherGenerator implements ReadCipherGenerator
    {
        @Override
        public SSLReadCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new CC20P1305ReadCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        static final class CC20P1305ReadCipher extends SSLReadCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            CC20P1305ReadCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
            }
            
            public Plaintext decrypt(byte contentType, final ByteBuffer bb, final byte[] sequence) throws GeneralSecurityException {
                if (contentType == ContentType.CHANGE_CIPHER_SPEC.id) {
                    return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
                }
                if (bb.remaining() <= this.tagSize) {
                    throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + bb.remaining() + "). Needs to be more than tag size (" + this.tagSize + ")");
                }
                byte[] sn = sequence;
                if (sn == null) {
                    sn = this.authenticator.sequenceNumber();
                }
                final byte[] nonce = new byte[this.iv.length];
                System.arraycopy(sn, 0, nonce, nonce.length - sn.length, sn.length);
                for (int i = 0; i < nonce.length; ++i) {
                    final byte[] array = nonce;
                    final int n = i;
                    array[n] ^= this.iv[i];
                }
                final AlgorithmParameterSpec spec = new IvParameterSpec(nonce);
                try {
                    this.cipher.init(2, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in AEAD mode", ikae);
                }
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, bb.remaining(), sn);
                this.cipher.updateAAD(aad);
                int len = bb.remaining();
                final int pos = bb.position();
                final ByteBuffer dup = bb.duplicate();
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode \"" + ibse.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
                }
                catch (final ShortBufferException sbe) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName(), sbe);
                }
                bb.position(pos);
                bb.limit(pos + len);
                int j;
                for (j = bb.limit() - 1; j > 0 && bb.get(j) == 0; --j) {}
                if (j < pos + 1) {
                    throw new BadPaddingException("Incorrect inner plaintext: no content type");
                }
                contentType = bb.get(j);
                bb.limit(j);
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext after DECRYPTION", bb.duplicate());
                }
                return new Plaintext(contentType, ProtocolVersion.NONE.major, ProtocolVersion.NONE.minor, -1, -1L, bb.slice());
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
            int estimateFragmentSize(final int packetSize, final int headerSize) {
                return packetSize - headerSize - this.tagSize;
            }
        }
    }
    
    private static final class T13CC20P1305WriteCipherGenerator implements WriteCipherGenerator
    {
        @Override
        public SSLWriteCipher createCipher(final SSLCipher sslCipher, final Authenticator authenticator, final ProtocolVersion protocolVersion, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
            return new CC20P1305WriteCipher(authenticator, protocolVersion, sslCipher, algorithm, key, params, random);
        }
        
        private static final class CC20P1305WriteCipher extends SSLWriteCipher
        {
            private final Cipher cipher;
            private final int tagSize;
            private final Key key;
            private final byte[] iv;
            private final SecureRandom random;
            
            CC20P1305WriteCipher(final Authenticator authenticator, final ProtocolVersion protocolVersion, final SSLCipher sslCipher, final String algorithm, final Key key, final AlgorithmParameterSpec params, final SecureRandom random) throws GeneralSecurityException {
                super(authenticator, protocolVersion);
                this.cipher = JsseJce.getCipher(algorithm);
                sslCipher.getClass();
                this.tagSize = 16;
                this.key = key;
                this.iv = ((IvParameterSpec)params).getIV();
                this.random = random;
                this.keyLimitCountdown = SSLCipher.cipherLimits.getOrDefault(algorithm.toUpperCase() + ":" + SSLCipher.tag[0], 0L);
                if (SSLLogger.isOn && SSLLogger.isOn("ssl")) {
                    SSLLogger.fine("algorithm = " + algorithm.toUpperCase() + ":" + SSLCipher.tag[0] + "\ncountdown value = " + this.keyLimitCountdown, new Object[0]);
                }
                if (this.keyLimitCountdown > 0L) {
                    this.keyLimitEnabled = true;
                }
            }
            
            public int encrypt(final byte contentType, final ByteBuffer bb) {
                final byte[] sn = this.authenticator.sequenceNumber();
                final byte[] nonce = new byte[this.iv.length];
                System.arraycopy(sn, 0, nonce, nonce.length - sn.length, sn.length);
                for (int i = 0; i < nonce.length; ++i) {
                    final byte[] array = nonce;
                    final int n = i;
                    array[n] ^= this.iv[i];
                }
                final AlgorithmParameterSpec spec = new IvParameterSpec(nonce);
                try {
                    this.cipher.init(1, this.key, spec, this.random);
                }
                catch (final InvalidKeyException | InvalidAlgorithmParameterException ikae) {
                    throw new RuntimeException("invalid key or spec in AEAD mode", ikae);
                }
                final int outputSize = this.cipher.getOutputSize(bb.remaining());
                final byte[] aad = this.authenticator.acquireAuthenticationBytes(contentType, outputSize, sn);
                this.cipher.updateAAD(aad);
                int len = bb.remaining();
                final int pos = bb.position();
                if (SSLLogger.isOn && SSLLogger.isOn("plaintext")) {
                    SSLLogger.fine("Plaintext before ENCRYPTION", bb.duplicate());
                }
                final ByteBuffer dup = bb.duplicate();
                if (outputSize > bb.remaining()) {
                    bb.limit(pos + outputSize);
                }
                try {
                    len = this.cipher.doFinal(dup, bb);
                }
                catch (final IllegalBlockSizeException | BadPaddingException | ShortBufferException ibse) {
                    throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), ibse);
                }
                if (len != outputSize) {
                    throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
                }
                if (this.keyLimitEnabled) {
                    this.keyLimitCountdown -= len;
                }
                return len;
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
            int calculateFragmentSize(final int packetLimit, final int headerSize) {
                return packetLimit - headerSize - this.tagSize;
            }
            
            @Override
            int calculatePacketSize(final int fragmentSize, final int headerSize) {
                return fragmentSize + headerSize + this.tagSize;
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
