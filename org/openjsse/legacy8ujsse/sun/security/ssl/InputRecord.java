package org.openjsse.legacy8ujsse.sun.security.ssl;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLException;
import java.io.EOFException;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLProtocolException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import sun.misc.HexDumpEncoder;
import javax.crypto.BadPaddingException;
import java.io.ByteArrayInputStream;

class InputRecord extends ByteArrayInputStream implements Record
{
    private HandshakeHash handshakeHash;
    private int lastHashed;
    boolean formatVerified;
    private boolean isClosed;
    private boolean appDataValid;
    private ProtocolVersion helloVersion;
    static final Debug debug;
    private int exlen;
    private byte[] v2Buf;
    private static final byte[] v2NoCipher;
    
    InputRecord() {
        super(new byte[16921]);
        this.formatVerified = true;
        this.setHelloVersion(ProtocolVersion.DEFAULT_HELLO);
        this.pos = 5;
        this.count = 5;
        this.lastHashed = this.count;
        this.exlen = 0;
        this.v2Buf = null;
    }
    
    void setHelloVersion(final ProtocolVersion helloVersion) {
        this.helloVersion = helloVersion;
    }
    
    ProtocolVersion getHelloVersion() {
        return this.helloVersion;
    }
    
    void enableFormatChecks() {
        this.formatVerified = false;
    }
    
    boolean isAppDataValid() {
        return this.appDataValid;
    }
    
    void setAppDataValid(final boolean value) {
        this.appDataValid = value;
    }
    
    byte contentType() {
        return this.buf[0];
    }
    
    void setHandshakeHash(final HandshakeHash handshakeHash) {
        this.handshakeHash = handshakeHash;
    }
    
    HandshakeHash getHandshakeHash() {
        return this.handshakeHash;
    }
    
    void decrypt(final Authenticator authenticator, final CipherBox box) throws BadPaddingException {
        BadPaddingException reservedBPE = null;
        final int tagLen = (authenticator instanceof MAC) ? ((MAC)authenticator).MAClen() : 0;
        final int cipheredLength = this.count - 5;
        if (!box.isNullCipher()) {
            try {
                final int nonceSize = box.applyExplicitNonce(authenticator, this.contentType(), this.buf, 5, cipheredLength);
                this.pos = 5 + nonceSize;
                this.lastHashed = this.pos;
                int offset = 5;
                if (box.isAEADMode()) {
                    offset += nonceSize;
                }
                this.count = offset + box.decrypt(this.buf, offset, this.count - offset, tagLen);
            }
            catch (final BadPaddingException bpe) {
                reservedBPE = bpe;
            }
        }
        if (authenticator instanceof MAC && tagLen != 0) {
            final MAC signer = (MAC)authenticator;
            int macOffset = this.count - tagLen;
            int contentLen = macOffset - this.pos;
            if (contentLen < 0) {
                if (reservedBPE == null) {
                    reservedBPE = new BadPaddingException("bad record");
                }
                macOffset = 5 + cipheredLength - tagLen;
                contentLen = macOffset - 5;
            }
            this.count -= tagLen;
            if (checkMacTags(this.contentType(), this.buf, this.pos, contentLen, signer, false) && reservedBPE == null) {
                reservedBPE = new BadPaddingException("bad record MAC");
            }
            if (box.isCBCMode()) {
                final int remainingLen = calculateRemainingLen(signer, cipheredLength, contentLen);
                if (remainingLen > this.buf.length) {
                    throw new RuntimeException("Internal buffer capacity error");
                }
                checkMacTags(this.contentType(), this.buf, 0, remainingLen, signer, true);
            }
        }
        if (reservedBPE != null) {
            throw reservedBPE;
        }
    }
    
    static boolean checkMacTags(final byte contentType, final byte[] buffer, final int offset, final int contentLen, final MAC signer, final boolean isSimulated) {
        final int tagLen = signer.MAClen();
        final byte[] hash = signer.compute(contentType, buffer, offset, contentLen, isSimulated);
        if (hash == null || tagLen != hash.length) {
            throw new RuntimeException("Internal MAC error");
        }
        final int[] results = compareMacTags(buffer, offset + contentLen, hash);
        return results[0] != 0;
    }
    
    private static int[] compareMacTags(final byte[] buffer, final int offset, final byte[] tag) {
        final int[] results = { 0, 0 };
        for (int i = 0; i < tag.length; ++i) {
            if (buffer[offset + i] != tag[i]) {
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
    
    static int calculateRemainingLen(final MAC signer, int fullLen, int usedLen) {
        final int blockLen = signer.hashBlockLen();
        final int minimalPaddingLen = signer.minimalPaddingLen();
        fullLen += 13 - (blockLen - minimalPaddingLen);
        usedLen += 13 - (blockLen - minimalPaddingLen);
        return 1 + (int)(Math.ceil(fullLen / (1.0 * blockLen)) - Math.ceil(usedLen / (1.0 * blockLen))) * signer.hashBlockLen();
    }
    
    void ignore(final int bytes) {
        if (bytes > 0) {
            this.pos += bytes;
            this.lastHashed = this.pos;
        }
    }
    
    void doHashes() {
        final int len = this.pos - this.lastHashed;
        if (len > 0) {
            this.hashInternal(this.buf, this.lastHashed, len);
            this.lastHashed = this.pos;
        }
    }
    
    private void hashInternal(final byte[] databuf, final int offset, final int len) {
        if (InputRecord.debug != null && Debug.isOn("data")) {
            try {
                final HexDumpEncoder hd = new HexDumpEncoder();
                System.out.println("[read] MD5 and SHA1 hashes:  len = " + len);
                hd.encodeBuffer(new ByteArrayInputStream(databuf, offset, len), System.out);
            }
            catch (final IOException ex) {}
        }
        this.handshakeHash.update(databuf, offset, len);
    }
    
    void queueHandshake(final InputRecord r) throws IOException {
        this.doHashes();
        if (this.pos > 5) {
            final int len = this.count - this.pos;
            if (len != 0) {
                System.arraycopy(this.buf, this.pos, this.buf, 5, len);
            }
            this.pos = 5;
            this.lastHashed = this.pos;
            this.count = 5 + len;
        }
        int len = r.available() + this.count;
        if (this.buf.length < len) {
            final byte[] newbuf = new byte[len];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        System.arraycopy(r.buf, r.pos, this.buf, this.count, len - this.count);
        this.count = len;
        len = r.lastHashed - r.pos;
        if (this.pos == 5) {
            this.lastHashed += len;
            r.pos = r.count;
            return;
        }
        throw new SSLProtocolException("?? confused buffer hashing ??");
    }
    
    @Override
    public void close() {
        this.appDataValid = false;
        this.isClosed = true;
        this.mark = 0;
        this.pos = 0;
        this.count = 0;
    }
    
    private int readFully(final InputStream s, final byte[] b, final int off, final int len) throws IOException {
        int n;
        int readLen;
        for (n = 0; n < len; n += readLen, this.exlen += readLen) {
            readLen = s.read(b, off + n, len - n);
            if (readLen < 0) {
                return readLen;
            }
            if (InputRecord.debug != null && Debug.isOn("packet")) {
                try {
                    final HexDumpEncoder hd = new HexDumpEncoder();
                    final ByteBuffer bb = ByteBuffer.wrap(b, off + n, readLen);
                    System.out.println("[Raw read]: length = " + bb.remaining());
                    hd.encodeBuffer(bb, System.out);
                }
                catch (final IOException ex) {}
            }
        }
        return n;
    }
    
    void read(final InputStream s, final OutputStream o) throws IOException {
        if (this.isClosed) {
            return;
        }
        if (this.exlen < 5) {
            final int really = this.readFully(s, this.buf, this.exlen, 5 - this.exlen);
            if (really < 0) {
                throw new EOFException("SSL peer shut down incorrectly");
            }
            this.pos = 5;
            this.count = 5;
            this.lastHashed = this.pos;
        }
        if (!this.formatVerified) {
            this.formatVerified = true;
            if (this.buf[0] != 22 && this.buf[0] != 21) {
                this.handleUnknownRecord(s, o);
            }
            else {
                this.readV3Record(s, o);
            }
        }
        else {
            this.readV3Record(s, o);
        }
    }
    
    static void checkRecordVersion(final ProtocolVersion version, final boolean allowSSL20Hello) throws SSLException {
        if ((version.v < ProtocolVersion.MIN.v || (version.major & 0xFF) > (ProtocolVersion.MAX.major & 0xFF)) && (!allowSSL20Hello || version.v != ProtocolVersion.SSL20Hello.v)) {
            throw new SSLException("Unsupported record version " + version);
        }
    }
    
    private void readV3Record(final InputStream s, final OutputStream o) throws IOException {
        final ProtocolVersion recordVersion = ProtocolVersion.valueOf(this.buf[1], this.buf[2]);
        checkRecordVersion(recordVersion, false);
        final int contentLen = ((this.buf[3] & 0xFF) << 8) + (this.buf[4] & 0xFF);
        if (contentLen < 0 || contentLen > 33300) {
            throw new SSLProtocolException("Bad InputRecord size, count = " + contentLen + ", buf.length = " + this.buf.length);
        }
        if (contentLen > this.buf.length - 5) {
            final byte[] newbuf = new byte[contentLen + 5];
            System.arraycopy(this.buf, 0, newbuf, 0, 5);
            this.buf = newbuf;
        }
        if (this.exlen < contentLen + 5) {
            final int really = this.readFully(s, this.buf, this.exlen, contentLen + 5 - this.exlen);
            if (really < 0) {
                throw new SSLException("SSL peer shut down incorrectly");
            }
        }
        this.count = contentLen + 5;
        this.exlen = 0;
        if (InputRecord.debug != null && Debug.isOn("record")) {
            if (this.count < 0 || this.count > 16916) {
                System.out.println(Thread.currentThread().getName() + ", Bad InputRecord size, count = " + this.count);
            }
            System.out.println(Thread.currentThread().getName() + ", READ: " + recordVersion + " " + contentName(this.contentType()) + ", length = " + this.available());
        }
    }
    
    private void handleUnknownRecord(final InputStream s, final OutputStream o) throws IOException {
        if ((this.buf[0] & 0x80) != 0x0 && this.buf[2] == 1) {
            if (this.helloVersion != ProtocolVersion.SSL20Hello) {
                throw new SSLHandshakeException("SSLv2Hello is disabled");
            }
            final ProtocolVersion recordVersion = ProtocolVersion.valueOf(this.buf[3], this.buf[4]);
            if (recordVersion == ProtocolVersion.SSL20Hello) {
                try {
                    this.writeBuffer(o, InputRecord.v2NoCipher, 0, InputRecord.v2NoCipher.length);
                }
                catch (final Exception ex) {}
                throw new SSLException("Unsupported SSL v2.0 ClientHello");
            }
            final int len = ((this.buf[0] & 0x7F) << 8) + (this.buf[1] & 0xFF) - 3;
            if (this.v2Buf == null) {
                this.v2Buf = new byte[len];
            }
            if (this.exlen < len + 5) {
                final int really = this.readFully(s, this.v2Buf, this.exlen - 5, len + 5 - this.exlen);
                if (really < 0) {
                    throw new EOFException("SSL peer shut down incorrectly");
                }
            }
            this.exlen = 0;
            this.hashInternal(this.buf, 2, 3);
            this.hashInternal(this.v2Buf, 0, len);
            this.V2toV3ClientHello(this.v2Buf);
            this.v2Buf = null;
            this.lastHashed = this.count;
            if (InputRecord.debug != null && Debug.isOn("record")) {
                System.out.println(Thread.currentThread().getName() + ", READ:  SSL v2, contentType = " + contentName(this.contentType()) + ", translated length = " + this.available());
            }
        }
        else {
            if ((this.buf[0] & 0x80) != 0x0 && this.buf[2] == 4) {
                throw new SSLException("SSL V2.0 servers are not supported.");
            }
            for (int i = 0; i < InputRecord.v2NoCipher.length; ++i) {
                if (this.buf[i] != InputRecord.v2NoCipher[i]) {
                    throw new SSLException("Unrecognized SSL message, plaintext connection?");
                }
            }
            throw new SSLException("SSL V2.0 servers are not supported.");
        }
    }
    
    void writeBuffer(final OutputStream s, final byte[] buf, final int off, final int len) throws IOException {
        s.write(buf, 0, len);
        s.flush();
    }
    
    private void V2toV3ClientHello(final byte[] v2Msg) throws SSLException {
        this.buf[0] = 22;
        this.buf[1] = this.buf[3];
        this.buf[2] = this.buf[4];
        this.buf[5] = 1;
        this.buf[9] = this.buf[1];
        this.buf[10] = this.buf[2];
        this.count = 11;
        final int cipherSpecLen = ((v2Msg[0] & 0xFF) << 8) + (v2Msg[1] & 0xFF);
        final int sessionIdLen = ((v2Msg[2] & 0xFF) << 8) + (v2Msg[3] & 0xFF);
        final int nonceLen = ((v2Msg[4] & 0xFF) << 8) + (v2Msg[5] & 0xFF);
        int offset = 6 + cipherSpecLen + sessionIdLen;
        if (nonceLen < 32) {
            for (int i = 0; i < 32 - nonceLen; ++i) {
                this.buf[this.count++] = 0;
            }
            System.arraycopy(v2Msg, offset, this.buf, this.count, nonceLen);
            this.count += nonceLen;
        }
        else {
            System.arraycopy(v2Msg, offset + (nonceLen - 32), this.buf, this.count, 32);
            this.count += 32;
        }
        offset -= sessionIdLen;
        this.buf[this.count++] = (byte)sessionIdLen;
        System.arraycopy(v2Msg, offset, this.buf, this.count, sessionIdLen);
        this.count += sessionIdLen;
        offset -= cipherSpecLen;
        int j = this.count + 2;
        for (int i = 0; i < cipherSpecLen; i += 3) {
            if (v2Msg[offset + i] == 0) {
                this.buf[j++] = v2Msg[offset + i + 1];
                this.buf[j++] = v2Msg[offset + i + 2];
            }
        }
        j -= this.count + 2;
        this.buf[this.count++] = (byte)(j >>> 8);
        this.buf[this.count++] = (byte)j;
        this.count += j;
        this.buf[this.count++] = 1;
        this.buf[this.count++] = 0;
        this.buf[3] = (byte)(this.count - 5);
        this.buf[4] = (byte)(this.count - 5 >>> 8);
        this.buf[6] = 0;
        this.buf[7] = (byte)(this.count - 5 - 4 >>> 8);
        this.buf[8] = (byte)(this.count - 5 - 4);
        this.pos = 5;
    }
    
    static String contentName(final int contentType) {
        switch (contentType) {
            case 20: {
                return "Change Cipher Spec";
            }
            case 21: {
                return "Alert";
            }
            case 22: {
                return "Handshake";
            }
            case 23: {
                return "Application Data";
            }
            default: {
                return "contentType = " + contentType;
            }
        }
    }
    
    static {
        debug = Debug.getInstance("ssl");
        v2NoCipher = new byte[] { -128, 3, 0, 0, 1 };
    }
}
