package sun.security.ssl;

import sun.security.util.MessageDigestSpi2;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

final class HandshakeHash
{
    private TranscriptHash transcriptHash;
    private LinkedList<byte[]> reserves;
    private boolean hasBeenUsed;
    
    HandshakeHash() {
        this.transcriptHash = new CacheOnlyHash();
        this.reserves = new LinkedList<byte[]>();
        this.hasBeenUsed = false;
    }
    
    void determine(final ProtocolVersion protocolVersion, final CipherSuite cipherSuite) {
        if (!(this.transcriptHash instanceof CacheOnlyHash)) {
            throw new IllegalStateException("Not expected instance of transcript hash");
        }
        final CacheOnlyHash cacheOnlyHash = (CacheOnlyHash)this.transcriptHash;
        if (protocolVersion.useTLS13PlusSpec()) {
            this.transcriptHash = new T13HandshakeHash(cipherSuite);
        }
        else if (protocolVersion.useTLS12PlusSpec()) {
            this.transcriptHash = new T12HandshakeHash(cipherSuite);
        }
        else if (protocolVersion.useTLS10PlusSpec()) {
            this.transcriptHash = new T10HandshakeHash(cipherSuite);
        }
        else {
            this.transcriptHash = new S30HandshakeHash(cipherSuite);
        }
        final byte[] byteArray = cacheOnlyHash.baos.toByteArray();
        if (byteArray.length != 0) {
            this.transcriptHash.update(byteArray, 0, byteArray.length);
        }
    }
    
    HandshakeHash copy() {
        if (this.transcriptHash instanceof CacheOnlyHash) {
            final HandshakeHash handshakeHash = new HandshakeHash();
            handshakeHash.transcriptHash = ((CacheOnlyHash)this.transcriptHash).copy();
            handshakeHash.reserves = new LinkedList<byte[]>(this.reserves);
            handshakeHash.hasBeenUsed = this.hasBeenUsed;
            return handshakeHash;
        }
        throw new IllegalStateException("Hash does not support copying");
    }
    
    void receive(final byte[] array) {
        this.reserves.add(Arrays.copyOf(array, array.length));
    }
    
    void receive(final ByteBuffer byteBuffer, final int n) {
        if (byteBuffer.hasArray()) {
            final int n2 = byteBuffer.position() + byteBuffer.arrayOffset();
            this.reserves.add(Arrays.copyOfRange(byteBuffer.array(), n2, n2 + n));
        }
        else {
            final int position = byteBuffer.position();
            final byte[] array = new byte[n];
            byteBuffer.get(array);
            byteBuffer.position(position);
            this.reserves.add(Arrays.copyOf(array, array.length));
        }
    }
    
    void receive(final ByteBuffer byteBuffer) {
        this.receive(byteBuffer, byteBuffer.remaining());
    }
    
    void push(final byte[] array) {
        this.reserves.push(Arrays.copyOf(array, array.length));
    }
    
    byte[] removeLastReceived() {
        return this.reserves.removeLast();
    }
    
    void deliver(final byte[] array) {
        this.update();
        this.transcriptHash.update(array, 0, array.length);
    }
    
    void deliver(final byte[] array, final int n, final int n2) {
        this.update();
        this.transcriptHash.update(array, n, n2);
    }
    
    void deliver(final ByteBuffer byteBuffer) {
        this.update();
        if (byteBuffer.hasArray()) {
            this.transcriptHash.update(byteBuffer.array(), byteBuffer.position() + byteBuffer.arrayOffset(), byteBuffer.remaining());
        }
        else {
            final int position = byteBuffer.position();
            final byte[] array = new byte[byteBuffer.remaining()];
            byteBuffer.get(array);
            byteBuffer.position(position);
            this.transcriptHash.update(array, 0, array.length);
        }
    }
    
    void utilize() {
        if (this.hasBeenUsed) {
            return;
        }
        if (this.reserves.size() != 0) {
            final byte[] array = this.reserves.remove();
            this.transcriptHash.update(array, 0, array.length);
            this.hasBeenUsed = true;
        }
    }
    
    void consume() {
        if (this.hasBeenUsed) {
            this.hasBeenUsed = false;
            return;
        }
        if (this.reserves.size() != 0) {
            final byte[] array = this.reserves.remove();
            this.transcriptHash.update(array, 0, array.length);
        }
    }
    
    void update() {
        while (this.reserves.size() != 0) {
            final byte[] array = this.reserves.remove();
            this.transcriptHash.update(array, 0, array.length);
        }
        this.hasBeenUsed = false;
    }
    
    byte[] digest() {
        return this.transcriptHash.digest();
    }
    
    void finish() {
        this.transcriptHash = new CacheOnlyHash();
        this.reserves = new LinkedList<byte[]>();
        this.hasBeenUsed = false;
    }
    
    byte[] archived() {
        return this.transcriptHash.archived();
    }
    
    byte[] digest(final String s) {
        return ((T10HandshakeHash)this.transcriptHash).digest(s);
    }
    
    byte[] digest(final String s, final SecretKey secretKey) {
        return ((S30HandshakeHash)this.transcriptHash).digest(s, secretKey);
    }
    
    byte[] digest(final boolean b, final SecretKey secretKey) {
        return ((S30HandshakeHash)this.transcriptHash).digest(b, secretKey);
    }
    
    public boolean isHashable(final byte b) {
        return b != SSLHandshake.HELLO_REQUEST.id;
    }
    
    private static final class CacheOnlyHash implements TranscriptHash
    {
        private final ByteArrayOutputStream baos;
        
        CacheOnlyHash() {
            this.baos = new ByteArrayOutputStream();
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.baos.write(array, n, n2);
        }
        
        @Override
        public byte[] digest() {
            throw new IllegalStateException("Not expected call to handshake hash digest");
        }
        
        @Override
        public byte[] archived() {
            return this.baos.toByteArray();
        }
        
        CacheOnlyHash copy() {
            final CacheOnlyHash cacheOnlyHash = new CacheOnlyHash();
            try {
                this.baos.writeTo(cacheOnlyHash.baos);
            }
            catch (final IOException ex) {
                throw new RuntimeException("unable to to clone hash state");
            }
            return cacheOnlyHash;
        }
    }
    
    static final class S30HandshakeHash implements TranscriptHash
    {
        static final byte[] MD5_pad1;
        static final byte[] MD5_pad2;
        static final byte[] SHA_pad1;
        static final byte[] SHA_pad2;
        private static final byte[] SSL_CLIENT;
        private static final byte[] SSL_SERVER;
        private final MessageDigest mdMD5;
        private final MessageDigest mdSHA;
        private final TranscriptHash md5;
        private final TranscriptHash sha;
        private final ByteArrayOutputStream baos;
        
        S30HandshakeHash(final CipherSuite cipherSuite) {
            this.mdMD5 = JsseJce.getMessageDigest("MD5");
            this.mdSHA = JsseJce.getMessageDigest("SHA");
            boolean b = false;
            if (this.mdMD5 instanceof Cloneable) {
                this.md5 = new CloneableHash(this.mdMD5);
            }
            else {
                b = true;
                this.md5 = new NonCloneableHash(this.mdMD5);
            }
            if (this.mdSHA instanceof Cloneable) {
                this.sha = new CloneableHash(this.mdSHA);
            }
            else {
                b = true;
                this.sha = new NonCloneableHash(this.mdSHA);
            }
            if (b) {
                this.baos = null;
            }
            else {
                this.baos = new ByteArrayOutputStream();
            }
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.md5.update(array, n, n2);
            this.sha.update(array, n, n2);
            if (this.baos != null) {
                this.baos.write(array, n, n2);
            }
        }
        
        @Override
        public byte[] digest() {
            final byte[] array = new byte[36];
            System.arraycopy(this.md5.digest(), 0, array, 0, 16);
            System.arraycopy(this.sha.digest(), 0, array, 16, 20);
            return array;
        }
        
        @Override
        public byte[] archived() {
            if (this.baos != null) {
                return this.baos.toByteArray();
            }
            if (this.md5 instanceof NonCloneableHash) {
                return this.md5.archived();
            }
            return this.sha.archived();
        }
        
        byte[] digest(final boolean b, final SecretKey secretKey) {
            final MessageDigest cloneMd5 = this.cloneMd5();
            final MessageDigest cloneSha = this.cloneSha();
            if (b) {
                cloneMd5.update(S30HandshakeHash.SSL_CLIENT);
                cloneSha.update(S30HandshakeHash.SSL_CLIENT);
            }
            else {
                cloneMd5.update(S30HandshakeHash.SSL_SERVER);
                cloneSha.update(S30HandshakeHash.SSL_SERVER);
            }
            updateDigest(cloneMd5, S30HandshakeHash.MD5_pad1, S30HandshakeHash.MD5_pad2, secretKey);
            updateDigest(cloneSha, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, secretKey);
            final byte[] array = new byte[36];
            System.arraycopy(cloneMd5.digest(), 0, array, 0, 16);
            System.arraycopy(cloneSha.digest(), 0, array, 16, 20);
            return array;
        }
        
        byte[] digest(final String s, final SecretKey secretKey) {
            if ("RSA".equalsIgnoreCase(s)) {
                final MessageDigest cloneMd5 = this.cloneMd5();
                final MessageDigest cloneSha = this.cloneSha();
                updateDigest(cloneMd5, S30HandshakeHash.MD5_pad1, S30HandshakeHash.MD5_pad2, secretKey);
                updateDigest(cloneSha, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, secretKey);
                final byte[] array = new byte[36];
                System.arraycopy(cloneMd5.digest(), 0, array, 0, 16);
                System.arraycopy(cloneSha.digest(), 0, array, 16, 20);
                return array;
            }
            final MessageDigest cloneSha2 = this.cloneSha();
            updateDigest(cloneSha2, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, secretKey);
            return cloneSha2.digest();
        }
        
        private static byte[] genPad(final int n, final int n2) {
            final byte[] array = new byte[n2];
            Arrays.fill(array, (byte)n);
            return array;
        }
        
        private MessageDigest cloneMd5() {
            if (this.mdMD5 instanceof Cloneable) {
                try {
                    return (MessageDigest)this.mdMD5.clone();
                }
                catch (final CloneNotSupportedException ex) {
                    throw new RuntimeException("MessageDigest does no support clone operation");
                }
            }
            final MessageDigest messageDigest = JsseJce.getMessageDigest("MD5");
            messageDigest.update(this.md5.archived());
            return messageDigest;
        }
        
        private MessageDigest cloneSha() {
            if (this.mdSHA instanceof Cloneable) {
                try {
                    return (MessageDigest)this.mdSHA.clone();
                }
                catch (final CloneNotSupportedException ex) {
                    throw new RuntimeException("MessageDigest does no support clone operation");
                }
            }
            final MessageDigest messageDigest = JsseJce.getMessageDigest("SHA");
            messageDigest.update(this.sha.archived());
            return messageDigest;
        }
        
        private static void updateDigest(final MessageDigest messageDigest, final byte[] array, final byte[] array2, final SecretKey secretKey) {
            final byte[] array3 = (byte[])("RAW".equals(secretKey.getFormat()) ? secretKey.getEncoded() : null);
            if (array3 != null) {
                messageDigest.update(array3);
            }
            else {
                digestKey(messageDigest, secretKey);
            }
            messageDigest.update(array);
            final byte[] digest = messageDigest.digest();
            if (array3 != null) {
                messageDigest.update(array3);
            }
            else {
                digestKey(messageDigest, secretKey);
            }
            messageDigest.update(array2);
            messageDigest.update(digest);
        }
        
        private static void digestKey(final MessageDigest messageDigest, final SecretKey secretKey) {
            try {
                if (!(messageDigest instanceof MessageDigestSpi2)) {
                    throw new Exception("Digest does not support implUpdate(SecretKey)");
                }
                ((MessageDigestSpi2)messageDigest).engineUpdate(secretKey);
            }
            catch (final Exception ex) {
                throw new RuntimeException("Could not obtain encoded key and MessageDigest cannot digest key", ex);
            }
        }
        
        static {
            MD5_pad1 = genPad(54, 48);
            MD5_pad2 = genPad(92, 48);
            SHA_pad1 = genPad(54, 40);
            SHA_pad2 = genPad(92, 40);
            SSL_CLIENT = new byte[] { 67, 76, 78, 84 };
            SSL_SERVER = new byte[] { 83, 82, 86, 82 };
        }
    }
    
    static final class T10HandshakeHash implements TranscriptHash
    {
        private final TranscriptHash md5;
        private final TranscriptHash sha;
        private final ByteArrayOutputStream baos;
        
        T10HandshakeHash(final CipherSuite cipherSuite) {
            final MessageDigest messageDigest = JsseJce.getMessageDigest("MD5");
            final MessageDigest messageDigest2 = JsseJce.getMessageDigest("SHA");
            boolean b = false;
            if (messageDigest instanceof Cloneable) {
                this.md5 = new CloneableHash(messageDigest);
            }
            else {
                b = true;
                this.md5 = new NonCloneableHash(messageDigest);
            }
            if (messageDigest2 instanceof Cloneable) {
                this.sha = new CloneableHash(messageDigest2);
            }
            else {
                b = true;
                this.sha = new NonCloneableHash(messageDigest2);
            }
            if (b) {
                this.baos = null;
            }
            else {
                this.baos = new ByteArrayOutputStream();
            }
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.md5.update(array, n, n2);
            this.sha.update(array, n, n2);
            if (this.baos != null) {
                this.baos.write(array, n, n2);
            }
        }
        
        @Override
        public byte[] digest() {
            final byte[] array = new byte[36];
            System.arraycopy(this.md5.digest(), 0, array, 0, 16);
            System.arraycopy(this.sha.digest(), 0, array, 16, 20);
            return array;
        }
        
        byte[] digest(final String s) {
            if ("RSA".equalsIgnoreCase(s)) {
                return this.digest();
            }
            return this.sha.digest();
        }
        
        @Override
        public byte[] archived() {
            if (this.baos != null) {
                return this.baos.toByteArray();
            }
            if (this.md5 instanceof NonCloneableHash) {
                return this.md5.archived();
            }
            return this.sha.archived();
        }
    }
    
    static final class T12HandshakeHash implements TranscriptHash
    {
        private final TranscriptHash transcriptHash;
        private final ByteArrayOutputStream baos;
        
        T12HandshakeHash(final CipherSuite cipherSuite) {
            final MessageDigest messageDigest = JsseJce.getMessageDigest(cipherSuite.hashAlg.name);
            if (messageDigest instanceof Cloneable) {
                this.transcriptHash = new CloneableHash(messageDigest);
                this.baos = new ByteArrayOutputStream();
            }
            else {
                this.transcriptHash = new NonCloneableHash(messageDigest);
                this.baos = null;
            }
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.transcriptHash.update(array, n, n2);
            if (this.baos != null) {
                this.baos.write(array, n, n2);
            }
        }
        
        @Override
        public byte[] digest() {
            return this.transcriptHash.digest();
        }
        
        @Override
        public byte[] archived() {
            if (this.baos != null) {
                return this.baos.toByteArray();
            }
            return this.transcriptHash.archived();
        }
    }
    
    static final class T13HandshakeHash implements TranscriptHash
    {
        private final TranscriptHash transcriptHash;
        
        T13HandshakeHash(final CipherSuite cipherSuite) {
            final MessageDigest messageDigest = JsseJce.getMessageDigest(cipherSuite.hashAlg.name);
            if (messageDigest instanceof Cloneable) {
                this.transcriptHash = new CloneableHash(messageDigest);
            }
            else {
                this.transcriptHash = new NonCloneableHash(messageDigest);
            }
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.transcriptHash.update(array, n, n2);
        }
        
        @Override
        public byte[] digest() {
            return this.transcriptHash.digest();
        }
        
        @Override
        public byte[] archived() {
            throw new UnsupportedOperationException("TLS 1.3 does not require archived.");
        }
    }
    
    static final class CloneableHash implements TranscriptHash
    {
        private final MessageDigest md;
        
        CloneableHash(final MessageDigest md) {
            this.md = md;
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.md.update(array, n, n2);
        }
        
        @Override
        public byte[] digest() {
            try {
                return ((MessageDigest)this.md.clone()).digest();
            }
            catch (final CloneNotSupportedException ex) {
                return new byte[0];
            }
        }
        
        @Override
        public byte[] archived() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    static final class NonCloneableHash implements TranscriptHash
    {
        private final MessageDigest md;
        private final ByteArrayOutputStream baos;
        
        NonCloneableHash(final MessageDigest md) {
            this.baos = new ByteArrayOutputStream();
            this.md = md;
        }
        
        @Override
        public void update(final byte[] array, final int n, final int n2) {
            this.baos.write(array, n, n2);
        }
        
        @Override
        public byte[] digest() {
            final byte[] byteArray = this.baos.toByteArray();
            this.md.reset();
            return this.md.digest(byteArray);
        }
        
        @Override
        public byte[] archived() {
            return this.baos.toByteArray();
        }
    }
    
    interface TranscriptHash
    {
        void update(final byte[] p0, final int p1, final int p2);
        
        byte[] digest();
        
        byte[] archived();
    }
}
