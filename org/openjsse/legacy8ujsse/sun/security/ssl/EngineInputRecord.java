package org.openjsse.legacy8ujsse.sun.security.ssl;

import sun.misc.HexDumpEncoder;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.crypto.BadPaddingException;
import javax.net.ssl.SSLException;
import java.nio.ByteBuffer;

final class EngineInputRecord extends InputRecord
{
    private SSLEngineImpl engine;
    private static ByteBuffer tmpBB;
    private boolean internalData;
    
    EngineInputRecord(final SSLEngineImpl engine) {
        this.engine = engine;
    }
    
    @Override
    byte contentType() {
        if (this.internalData) {
            return super.contentType();
        }
        return 23;
    }
    
    int bytesInCompletePacket(final ByteBuffer buf) throws SSLException {
        if (buf.remaining() < 5) {
            return -1;
        }
        final int pos = buf.position();
        final byte byteZero = buf.get(pos);
        int len = 0;
        if (this.formatVerified || byteZero == 22 || byteZero == 21) {
            final ProtocolVersion recordVersion = ProtocolVersion.valueOf(buf.get(pos + 1), buf.get(pos + 2));
            InputRecord.checkRecordVersion(recordVersion, false);
            this.formatVerified = true;
            len = ((buf.get(pos + 3) & 0xFF) << 8) + (buf.get(pos + 4) & 0xFF) + 5;
        }
        else {
            final boolean isShort = (byteZero & 0x80) != 0x0;
            if (!isShort || (buf.get(pos + 2) != 1 && buf.get(pos + 2) != 4)) {
                throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }
            final ProtocolVersion recordVersion2 = ProtocolVersion.valueOf(buf.get(pos + 3), buf.get(pos + 4));
            InputRecord.checkRecordVersion(recordVersion2, true);
            final int mask = isShort ? 127 : 63;
            len = ((byteZero & mask) << 8) + (buf.get(pos + 1) & 0xFF) + (isShort ? 2 : 3);
        }
        return len;
    }
    
    ByteBuffer decrypt(final Authenticator authenticator, final CipherBox box, final ByteBuffer bb) throws BadPaddingException {
        if (this.internalData) {
            this.decrypt(authenticator, box);
            return EngineInputRecord.tmpBB;
        }
        BadPaddingException reservedBPE = null;
        final int tagLen = (authenticator instanceof MAC) ? ((MAC)authenticator).MAClen() : 0;
        final int cipheredLength = bb.remaining();
        if (!box.isNullCipher()) {
            try {
                final int nonceSize = box.applyExplicitNonce(authenticator, this.contentType(), bb);
                if (box.isAEADMode()) {
                    bb.position(bb.position() + nonceSize);
                }
                box.decrypt(bb, tagLen);
                bb.position(nonceSize);
            }
            catch (final BadPaddingException bpe) {
                reservedBPE = bpe;
            }
        }
        if (authenticator instanceof MAC && tagLen != 0) {
            final MAC signer = (MAC)authenticator;
            int macOffset = bb.limit() - tagLen;
            if (bb.remaining() < tagLen) {
                if (reservedBPE == null) {
                    reservedBPE = new BadPaddingException("bad record");
                }
                macOffset = cipheredLength - tagLen;
                bb.limit(cipheredLength);
            }
            if (checkMacTags(this.contentType(), bb, signer, false) && reservedBPE == null) {
                reservedBPE = new BadPaddingException("bad record MAC");
            }
            if (box.isCBCMode()) {
                final int remainingLen = InputRecord.calculateRemainingLen(signer, cipheredLength, macOffset);
                if (remainingLen > this.buf.length) {
                    throw new RuntimeException("Internal buffer capacity error");
                }
                InputRecord.checkMacTags(this.contentType(), this.buf, 0, remainingLen, signer, true);
            }
            bb.limit(macOffset);
        }
        if (reservedBPE != null) {
            throw reservedBPE;
        }
        return bb.slice();
    }
    
    private static boolean checkMacTags(final byte contentType, final ByteBuffer bb, final MAC signer, final boolean isSimulated) {
        final int position = bb.position();
        final int tagLen = signer.MAClen();
        final int lim = bb.limit();
        final int macData = lim - tagLen;
        bb.limit(macData);
        final byte[] hash = signer.compute(contentType, bb, isSimulated);
        if (hash == null || tagLen != hash.length) {
            throw new RuntimeException("Internal MAC error");
        }
        bb.position(macData);
        bb.limit(lim);
        try {
            final int[] results = compareMacTags(bb, hash);
            return results[0] != 0;
        }
        finally {
            bb.position(position);
            bb.limit(macData);
        }
    }
    
    private static int[] compareMacTags(final ByteBuffer bb, final byte[] tag) {
        final int[] results = { 0, 0 };
        for (int i = 0; i < tag.length; ++i) {
            if (bb.get() != tag[i]) {
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
    
    @Override
    void writeBuffer(final OutputStream s, final byte[] buf, final int off, final int len) throws IOException {
        final ByteBuffer netBB = (ByteBuffer)ByteBuffer.allocate(len).put(buf, 0, len).flip();
        this.engine.writer.putOutboundDataSync(netBB);
    }
    
    ByteBuffer read(final ByteBuffer srcBB) throws IOException {
        if (!this.formatVerified || srcBB.get(srcBB.position()) != 23) {
            this.internalData = true;
            this.read(new ByteBufferInputStream(srcBB), null);
            return EngineInputRecord.tmpBB;
        }
        this.internalData = false;
        final int srcPos = srcBB.position();
        final int srcLim = srcBB.limit();
        final ProtocolVersion recordVersion = ProtocolVersion.valueOf(srcBB.get(srcPos + 1), srcBB.get(srcPos + 2));
        InputRecord.checkRecordVersion(recordVersion, false);
        final int len = this.bytesInCompletePacket(srcBB);
        assert len > 0;
        if (EngineInputRecord.debug != null && Debug.isOn("packet")) {
            try {
                final HexDumpEncoder hd = new HexDumpEncoder();
                final ByteBuffer bb = srcBB.duplicate();
                bb.limit(srcPos + len);
                System.out.println("[Raw read (bb)]: length = " + len);
                hd.encodeBuffer(bb, System.out);
            }
            catch (final IOException ex) {}
        }
        srcBB.position(srcPos + 5);
        srcBB.limit(srcPos + len);
        final ByteBuffer bb2 = srcBB.slice();
        srcBB.position(srcBB.limit());
        srcBB.limit(srcLim);
        return bb2;
    }
    
    static {
        EngineInputRecord.tmpBB = ByteBuffer.allocate(0);
    }
}
