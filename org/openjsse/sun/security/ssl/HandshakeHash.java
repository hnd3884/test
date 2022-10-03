package org.openjsse.sun.security.ssl;

import org.openjsse.sun.security.util.MessageDigestSpi2;
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
        final CacheOnlyHash coh = (CacheOnlyHash)this.transcriptHash;
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
        final byte[] reserved = coh.baos.toByteArray();
        if (reserved.length != 0) {
            this.transcriptHash.update(reserved, 0, reserved.length);
        }
    }
    
    HandshakeHash copy() {
        if (this.transcriptHash instanceof CacheOnlyHash) {
            final HandshakeHash result = new HandshakeHash();
            result.transcriptHash = ((CacheOnlyHash)this.transcriptHash).copy();
            result.reserves = new LinkedList<byte[]>(this.reserves);
            result.hasBeenUsed = this.hasBeenUsed;
            return result;
        }
        throw new IllegalStateException("Hash does not support copying");
    }
    
    void receive(final byte[] input) {
        this.reserves.add(Arrays.copyOf(input, input.length));
    }
    
    void receive(final ByteBuffer input, final int length) {
        if (input.hasArray()) {
            final int from = input.position() + input.arrayOffset();
            final int to = from + length;
            this.reserves.add(Arrays.copyOfRange(input.array(), from, to));
        }
        else {
            final int inPos = input.position();
            final byte[] holder = new byte[length];
            input.get(holder);
            input.position(inPos);
            this.reserves.add(Arrays.copyOf(holder, holder.length));
        }
    }
    
    void receive(final ByteBuffer input) {
        this.receive(input, input.remaining());
    }
    
    void push(final byte[] input) {
        this.reserves.push(Arrays.copyOf(input, input.length));
    }
    
    byte[] removeLastReceived() {
        return this.reserves.removeLast();
    }
    
    void deliver(final byte[] input) {
        this.update();
        this.transcriptHash.update(input, 0, input.length);
    }
    
    void deliver(final byte[] input, final int offset, final int length) {
        this.update();
        this.transcriptHash.update(input, offset, length);
    }
    
    void deliver(final ByteBuffer input) {
        this.update();
        if (input.hasArray()) {
            this.transcriptHash.update(input.array(), input.position() + input.arrayOffset(), input.remaining());
        }
        else {
            final int inPos = input.position();
            final byte[] holder = new byte[input.remaining()];
            input.get(holder);
            input.position(inPos);
            this.transcriptHash.update(holder, 0, holder.length);
        }
    }
    
    void utilize() {
        if (this.hasBeenUsed) {
            return;
        }
        if (this.reserves.size() != 0) {
            final byte[] holder = this.reserves.remove();
            this.transcriptHash.update(holder, 0, holder.length);
            this.hasBeenUsed = true;
        }
    }
    
    void consume() {
        if (this.hasBeenUsed) {
            this.hasBeenUsed = false;
            return;
        }
        if (this.reserves.size() != 0) {
            final byte[] holder = this.reserves.remove();
            this.transcriptHash.update(holder, 0, holder.length);
        }
    }
    
    void update() {
        while (this.reserves.size() != 0) {
            final byte[] holder = this.reserves.remove();
            this.transcriptHash.update(holder, 0, holder.length);
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
    
    byte[] digest(final String algorithm) {
        final T10HandshakeHash hh = (T10HandshakeHash)this.transcriptHash;
        return hh.digest(algorithm);
    }
    
    byte[] digest(final String algorithm, final SecretKey masterSecret) {
        final S30HandshakeHash hh = (S30HandshakeHash)this.transcriptHash;
        return hh.digest(algorithm, masterSecret);
    }
    
    byte[] digest(final boolean useClientLabel, final SecretKey masterSecret) {
        final S30HandshakeHash hh = (S30HandshakeHash)this.transcriptHash;
        return hh.digest(useClientLabel, masterSecret);
    }
    
    public boolean isHashable(final byte handshakeType) {
        return handshakeType != SSLHandshake.HELLO_REQUEST.id && handshakeType != SSLHandshake.HELLO_VERIFY_REQUEST.id;
    }
    
    private static final class CacheOnlyHash implements TranscriptHash
    {
        private final ByteArrayOutputStream baos;
        
        CacheOnlyHash() {
            this.baos = new ByteArrayOutputStream();
        }
        
        @Override
        public void update(final byte[] input, final int offset, final int length) {
            this.baos.write(input, offset, length);
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
            final CacheOnlyHash result = new CacheOnlyHash();
            try {
                this.baos.writeTo(result.baos);
            }
            catch (final IOException ex) {
                throw new RuntimeException("unable to to clone hash state");
            }
            return result;
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
            boolean hasArchived = false;
            if (this.mdMD5 instanceof Cloneable) {
                this.md5 = new CloneableHash(this.mdMD5);
            }
            else {
                hasArchived = true;
                this.md5 = new NonCloneableHash(this.mdMD5);
            }
            if (this.mdSHA instanceof Cloneable) {
                this.sha = new CloneableHash(this.mdSHA);
            }
            else {
                hasArchived = true;
                this.sha = new NonCloneableHash(this.mdSHA);
            }
            if (hasArchived) {
                this.baos = null;
            }
            else {
                this.baos = new ByteArrayOutputStream();
            }
        }
        
        @Override
        public void update(final byte[] input, final int offset, final int length) {
            this.md5.update(input, offset, length);
            this.sha.update(input, offset, length);
            if (this.baos != null) {
                this.baos.write(input, offset, length);
            }
        }
        
        @Override
        public byte[] digest() {
            final byte[] digest = new byte[36];
            System.arraycopy(this.md5.digest(), 0, digest, 0, 16);
            System.arraycopy(this.sha.digest(), 0, digest, 16, 20);
            return digest;
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
        
        byte[] digest(final boolean useClientLabel, final SecretKey masterSecret) {
            final MessageDigest md5Clone = this.cloneMd5();
            final MessageDigest shaClone = this.cloneSha();
            if (useClientLabel) {
                md5Clone.update(S30HandshakeHash.SSL_CLIENT);
                shaClone.update(S30HandshakeHash.SSL_CLIENT);
            }
            else {
                md5Clone.update(S30HandshakeHash.SSL_SERVER);
                shaClone.update(S30HandshakeHash.SSL_SERVER);
            }
            updateDigest(md5Clone, S30HandshakeHash.MD5_pad1, S30HandshakeHash.MD5_pad2, masterSecret);
            updateDigest(shaClone, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, masterSecret);
            final byte[] digest = new byte[36];
            System.arraycopy(md5Clone.digest(), 0, digest, 0, 16);
            System.arraycopy(shaClone.digest(), 0, digest, 16, 20);
            return digest;
        }
        
        byte[] digest(final String algorithm, final SecretKey masterSecret) {
            if ("RSA".equalsIgnoreCase(algorithm)) {
                final MessageDigest md5Clone = this.cloneMd5();
                final MessageDigest shaClone = this.cloneSha();
                updateDigest(md5Clone, S30HandshakeHash.MD5_pad1, S30HandshakeHash.MD5_pad2, masterSecret);
                updateDigest(shaClone, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, masterSecret);
                final byte[] digest = new byte[36];
                System.arraycopy(md5Clone.digest(), 0, digest, 0, 16);
                System.arraycopy(shaClone.digest(), 0, digest, 16, 20);
                return digest;
            }
            final MessageDigest shaClone2 = this.cloneSha();
            updateDigest(shaClone2, S30HandshakeHash.SHA_pad1, S30HandshakeHash.SHA_pad2, masterSecret);
            return shaClone2.digest();
        }
        
        private static byte[] genPad(final int b, final int count) {
            final byte[] padding = new byte[count];
            Arrays.fill(padding, (byte)b);
            return padding;
        }
        
        private MessageDigest cloneMd5() {
            if (this.mdMD5 instanceof Cloneable) {
                try {
                    final MessageDigest md5Clone = (MessageDigest)this.mdMD5.clone();
                    return md5Clone;
                }
                catch (final CloneNotSupportedException ex) {
                    throw new RuntimeException("MessageDigest does no support clone operation");
                }
            }
            final MessageDigest md5Clone = JsseJce.getMessageDigest("MD5");
            md5Clone.update(this.md5.archived());
            return md5Clone;
        }
        
        private MessageDigest cloneSha() {
            if (this.mdSHA instanceof Cloneable) {
                try {
                    final MessageDigest shaClone = (MessageDigest)this.mdSHA.clone();
                    return shaClone;
                }
                catch (final CloneNotSupportedException ex) {
                    throw new RuntimeException("MessageDigest does no support clone operation");
                }
            }
            final MessageDigest shaClone = JsseJce.getMessageDigest("SHA");
            shaClone.update(this.sha.archived());
            return shaClone;
        }
        
        private static void updateDigest(final MessageDigest md, final byte[] pad1, final byte[] pad2, final SecretKey masterSecret) {
            final byte[] keyBytes = (byte[])("RAW".equals(masterSecret.getFormat()) ? masterSecret.getEncoded() : null);
            if (keyBytes != null) {
                md.update(keyBytes);
            }
            else {
                digestKey(md, masterSecret);
            }
            md.update(pad1);
            final byte[] temp = md.digest();
            if (keyBytes != null) {
                md.update(keyBytes);
            }
            else {
                digestKey(md, masterSecret);
            }
            md.update(pad2);
            md.update(temp);
        }
        
        private static void digestKey(final MessageDigest md, final SecretKey key) {
            try {
                if (!(md instanceof MessageDigestSpi2)) {
                    throw new Exception("Digest does not support implUpdate(SecretKey)");
                }
                ((MessageDigestSpi2)md).engineUpdate(key);
            }
            catch (final Exception e) {
                throw new RuntimeException("Could not obtain encoded key and MessageDigest cannot digest key", e);
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
            final MessageDigest mdMD5 = JsseJce.getMessageDigest("MD5");
            final MessageDigest mdSHA = JsseJce.getMessageDigest("SHA");
            boolean hasArchived = false;
            if (mdMD5 instanceof Cloneable) {
                this.md5 = new CloneableHash(mdMD5);
            }
            else {
                hasArchived = true;
                this.md5 = new NonCloneableHash(mdMD5);
            }
            if (mdSHA instanceof Cloneable) {
                this.sha = new CloneableHash(mdSHA);
            }
            else {
                hasArchived = true;
                this.sha = new NonCloneableHash(mdSHA);
            }
            if (hasArchived) {
                this.baos = null;
            }
            else {
                this.baos = new ByteArrayOutputStream();
            }
        }
        
        @Override
        public void update(final byte[] input, final int offset, final int length) {
            this.md5.update(input, offset, length);
            this.sha.update(input, offset, length);
            if (this.baos != null) {
                this.baos.write(input, offset, length);
            }
        }
        
        @Override
        public byte[] digest() {
            final byte[] digest = new byte[36];
            System.arraycopy(this.md5.digest(), 0, digest, 0, 16);
            System.arraycopy(this.sha.digest(), 0, digest, 16, 20);
            return digest;
        }
        
        byte[] digest(final String algorithm) {
            if ("RSA".equalsIgnoreCase(algorithm)) {
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
            final MessageDigest md = JsseJce.getMessageDigest(cipherSuite.hashAlg.name);
            if (md instanceof Cloneable) {
                this.transcriptHash = new CloneableHash(md);
                this.baos = new ByteArrayOutputStream();
            }
            else {
                this.transcriptHash = new NonCloneableHash(md);
                this.baos = null;
            }
        }
        
        @Override
        public void update(final byte[] input, final int offset, final int length) {
            this.transcriptHash.update(input, offset, length);
            if (this.baos != null) {
                this.baos.write(input, offset, length);
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
            final MessageDigest md = JsseJce.getMessageDigest(cipherSuite.hashAlg.name);
            if (md instanceof Cloneable) {
                this.transcriptHash = new CloneableHash(md);
            }
            else {
                this.transcriptHash = new NonCloneableHash(md);
            }
        }
        
        @Override
        public void update(final byte[] input, final int offset, final int length) {
            this.transcriptHash.update(input, offset, length);
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
        public void update(final byte[] input, final int offset, final int length) {
            this.md.update(input, offset, length);
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
        public void update(final byte[] input, final int offset, final int length) {
            this.baos.write(input, offset, length);
        }
        
        @Override
        public byte[] digest() {
            final byte[] bytes = this.baos.toByteArray();
            this.md.reset();
            return this.md.digest(bytes);
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
