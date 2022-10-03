package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SSLException;
import java.util.Arrays;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import sun.misc.HexDumpEncoder;
import java.io.ByteArrayOutputStream;

class OutputRecord extends ByteArrayOutputStream implements Record
{
    private HandshakeHash handshakeHash;
    private int lastHashed;
    private boolean firstMessage;
    private final byte contentType;
    private int headerOffset;
    ProtocolVersion protocolVersion;
    private ProtocolVersion helloVersion;
    static final Debug debug;
    private static int[] V3toV2CipherMap1;
    private static int[] V3toV2CipherMap3;
    
    OutputRecord(final byte type, final int size) {
        super(size);
        this.protocolVersion = ProtocolVersion.DEFAULT;
        this.helloVersion = ProtocolVersion.DEFAULT_HELLO;
        this.firstMessage = true;
        this.count = 261;
        this.contentType = type;
        this.lastHashed = this.count;
        this.headerOffset = 256;
    }
    
    OutputRecord(final byte type) {
        this(type, recordSize(type));
    }
    
    private static int recordSize(final byte type) {
        if (type == 20 || type == 21) {
            return 539;
        }
        return 16921;
    }
    
    synchronized void setVersion(final ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
    
    synchronized void setHelloVersion(final ProtocolVersion helloVersion) {
        this.helloVersion = helloVersion;
    }
    
    @Override
    public synchronized void reset() {
        super.reset();
        this.count = 261;
        this.lastHashed = this.count;
        this.headerOffset = 256;
    }
    
    void setHandshakeHash(final HandshakeHash handshakeHash) {
        assert this.contentType == 22;
        this.handshakeHash = handshakeHash;
    }
    
    void doHashes() {
        final int len = this.count - this.lastHashed;
        if (len > 0) {
            this.hashInternal(this.buf, this.lastHashed, len);
            this.lastHashed = this.count;
        }
    }
    
    private void hashInternal(final byte[] buf, final int offset, final int len) {
        if (OutputRecord.debug != null && Debug.isOn("data")) {
            try {
                final HexDumpEncoder hd = new HexDumpEncoder();
                System.out.println("[write] MD5 and SHA1 hashes:  len = " + len);
                hd.encodeBuffer(new ByteArrayInputStream(buf, this.lastHashed, len), System.out);
            }
            catch (final IOException ex) {}
        }
        this.handshakeHash.update(buf, this.lastHashed, len);
        this.lastHashed = this.count;
    }
    
    boolean isEmpty() {
        return this.count == 261;
    }
    
    boolean isAlert(final byte description) {
        return this.count > 262 && this.contentType == 21 && this.buf[262] == description;
    }
    
    void encrypt(final Authenticator authenticator, final CipherBox box) throws IOException {
        if (this.contentType == 22) {
            this.doHashes();
        }
        if (authenticator instanceof MAC) {
            final MAC signer = (MAC)authenticator;
            if (signer.MAClen() != 0) {
                final byte[] hash = signer.compute(this.contentType, this.buf, 261, this.count - 261, false);
                this.write(hash);
            }
        }
        if (!box.isNullCipher()) {
            if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && (box.isCBCMode() || box.isAEADMode())) {
                final byte[] nonce = box.createExplicitNonce(authenticator, this.contentType, this.count - 261);
                final int offset = 261 - nonce.length;
                System.arraycopy(nonce, 0, this.buf, offset, nonce.length);
                this.headerOffset = offset - 5;
            }
            else {
                this.headerOffset = 256;
            }
            int offset2 = 261;
            if (!box.isAEADMode()) {
                offset2 = this.headerOffset + 5;
            }
            this.count = offset2 + box.encrypt(this.buf, offset2, this.count - offset2);
        }
    }
    
    final int availableDataBytes() {
        final int dataSize = this.count - 261;
        return 16384 - dataSize;
    }
    
    private void ensureCapacity(final int minCapacity) {
        if (minCapacity > this.buf.length) {
            this.buf = Arrays.copyOf(this.buf, minCapacity);
        }
    }
    
    final byte contentType() {
        return this.contentType;
    }
    
    void write(final OutputStream s, final boolean holdRecord, final ByteArrayOutputStream heldRecordBuffer) throws IOException {
        if (this.count == 261) {
            return;
        }
        final int length = this.count - this.headerOffset - 5;
        if (length < 0) {
            throw new SSLException("output record size too small: " + length);
        }
        if (OutputRecord.debug != null && (Debug.isOn("record") || Debug.isOn("handshake")) && ((OutputRecord.debug != null && Debug.isOn("record")) || this.contentType() == 20)) {
            System.out.println(Thread.currentThread().getName() + ", WRITE: " + this.protocolVersion + " " + InputRecord.contentName(this.contentType()) + ", length = " + length);
        }
        if (this.firstMessage && this.useV2Hello()) {
            final byte[] v3Msg = new byte[length - 4];
            System.arraycopy(this.buf, 265, v3Msg, 0, v3Msg.length);
            this.headerOffset = 0;
            this.V3toV2ClientHello(v3Msg);
            this.handshakeHash.reset();
            this.lastHashed = 2;
            this.doHashes();
            if (OutputRecord.debug != null && Debug.isOn("record")) {
                System.out.println(Thread.currentThread().getName() + ", WRITE: SSLv2 client hello message, length = " + (this.count - 2));
            }
        }
        else {
            this.buf[this.headerOffset + 0] = this.contentType;
            this.buf[this.headerOffset + 1] = this.protocolVersion.major;
            this.buf[this.headerOffset + 2] = this.protocolVersion.minor;
            this.buf[this.headerOffset + 3] = (byte)(length >> 8);
            this.buf[this.headerOffset + 4] = (byte)length;
        }
        this.firstMessage = false;
        int debugOffset = 0;
        if (holdRecord) {
            this.writeBuffer(heldRecordBuffer, this.buf, this.headerOffset, this.count - this.headerOffset, debugOffset);
        }
        else {
            if (heldRecordBuffer != null && heldRecordBuffer.size() > 0) {
                final int heldLen = heldRecordBuffer.size();
                final int newCount = this.count + heldLen - this.headerOffset;
                this.ensureCapacity(newCount);
                System.arraycopy(this.buf, this.headerOffset, this.buf, heldLen, this.count - this.headerOffset);
                System.arraycopy(heldRecordBuffer.toByteArray(), 0, this.buf, 0, heldLen);
                this.count = newCount;
                this.headerOffset = 0;
                heldRecordBuffer.reset();
                debugOffset = heldLen;
            }
            this.writeBuffer(s, this.buf, this.headerOffset, this.count - this.headerOffset, debugOffset);
        }
        this.reset();
    }
    
    void writeBuffer(final OutputStream s, final byte[] buf, final int off, final int len, final int debugOffset) throws IOException {
        s.write(buf, off, len);
        s.flush();
        if (OutputRecord.debug != null && Debug.isOn("packet")) {
            try {
                final HexDumpEncoder hd = new HexDumpEncoder();
                System.out.println("[Raw write]: length = " + (len - debugOffset));
                hd.encodeBuffer(new ByteArrayInputStream(buf, off + debugOffset, len - debugOffset), System.out);
            }
            catch (final IOException ex) {}
        }
    }
    
    private boolean useV2Hello() {
        return this.firstMessage && this.helloVersion == ProtocolVersion.SSL20Hello && this.contentType == 22 && this.buf[this.headerOffset + 5] == 1 && this.buf[299] == 0;
    }
    
    private void V3toV2ClientHello(final byte[] v3Msg) throws SSLException {
        final int v3SessionIdLenOffset = 34;
        final int v3SessionIdLen = v3Msg[v3SessionIdLenOffset];
        final int v3CipherSpecLenOffset = v3SessionIdLenOffset + 1 + v3SessionIdLen;
        final int v3CipherSpecLen = ((v3Msg[v3CipherSpecLenOffset] & 0xFF) << 8) + (v3Msg[v3CipherSpecLenOffset + 1] & 0xFF);
        final int cipherSpecs = v3CipherSpecLen / 2;
        int v3CipherSpecOffset = v3CipherSpecLenOffset + 2;
        int v2CipherSpecLen = 0;
        this.count = 11;
        boolean containsRenegoInfoSCSV = false;
        for (int i = 0; i < cipherSpecs; ++i) {
            final byte byte1 = v3Msg[v3CipherSpecOffset++];
            final byte byte2 = v3Msg[v3CipherSpecOffset++];
            v2CipherSpecLen += this.V3toV2CipherSuite(byte1, byte2);
            if (!containsRenegoInfoSCSV && byte1 == 0 && byte2 == -1) {
                containsRenegoInfoSCSV = true;
            }
        }
        if (!containsRenegoInfoSCSV) {
            v2CipherSpecLen += this.V3toV2CipherSuite((byte)0, (byte)(-1));
        }
        this.buf[2] = 1;
        this.buf[3] = v3Msg[0];
        this.buf[4] = v3Msg[1];
        this.buf[5] = (byte)(v2CipherSpecLen >>> 8);
        this.buf[6] = (byte)v2CipherSpecLen;
        this.buf[7] = 0;
        this.buf[8] = 0;
        this.buf[9] = 0;
        this.buf[10] = 32;
        System.arraycopy(v3Msg, 2, this.buf, this.count, 32);
        this.count += 32;
        this.count -= 2;
        this.buf[0] = (byte)(this.count >>> 8);
        final byte[] buf = this.buf;
        final int n = 0;
        buf[n] |= (byte)128;
        this.buf[1] = (byte)this.count;
        this.count += 2;
    }
    
    private int V3toV2CipherSuite(final byte byte1, final byte byte2) {
        this.buf[this.count++] = 0;
        this.buf[this.count++] = byte1;
        this.buf[this.count++] = byte2;
        if ((byte2 & 0xFF) > 10 || OutputRecord.V3toV2CipherMap1[byte2] == -1) {
            return 3;
        }
        this.buf[this.count++] = (byte)OutputRecord.V3toV2CipherMap1[byte2];
        this.buf[this.count++] = 0;
        this.buf[this.count++] = (byte)OutputRecord.V3toV2CipherMap3[byte2];
        return 6;
    }
    
    static {
        debug = Debug.getInstance("ssl");
        OutputRecord.V3toV2CipherMap1 = new int[] { -1, -1, -1, 2, 1, -1, 4, 5, -1, 6, 7 };
        OutputRecord.V3toV2CipherMap3 = new int[] { -1, -1, -1, 128, 128, -1, 128, 128, -1, 64, 192 };
    }
}
