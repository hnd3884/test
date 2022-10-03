package io.netty.handler.codec.dns;

import io.netty.handler.codec.CorruptedFrameException;
import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;
import java.net.SocketAddress;

abstract class DnsResponseDecoder<A extends SocketAddress>
{
    private final DnsRecordDecoder recordDecoder;
    
    DnsResponseDecoder(final DnsRecordDecoder recordDecoder) {
        this.recordDecoder = ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
    }
    
    final DnsResponse decode(final A sender, final A recipient, final ByteBuf buffer) throws Exception {
        final int id = buffer.readUnsignedShort();
        final int flags = buffer.readUnsignedShort();
        if (flags >> 15 == 0) {
            throw new CorruptedFrameException("not a response");
        }
        final DnsResponse response = this.newResponse(sender, recipient, id, DnsOpCode.valueOf((byte)(flags >> 11 & 0xF)), DnsResponseCode.valueOf((byte)(flags & 0xF)));
        response.setRecursionDesired((flags >> 8 & 0x1) == 0x1);
        response.setAuthoritativeAnswer((flags >> 10 & 0x1) == 0x1);
        response.setTruncated((flags >> 9 & 0x1) == 0x1);
        response.setRecursionAvailable((flags >> 7 & 0x1) == 0x1);
        response.setZ(flags >> 4 & 0x7);
        boolean success = false;
        try {
            final int questionCount = buffer.readUnsignedShort();
            final int answerCount = buffer.readUnsignedShort();
            final int authorityRecordCount = buffer.readUnsignedShort();
            final int additionalRecordCount = buffer.readUnsignedShort();
            this.decodeQuestions(response, buffer, questionCount);
            if (!this.decodeRecords(response, DnsSection.ANSWER, buffer, answerCount)) {
                success = true;
                return response;
            }
            if (!this.decodeRecords(response, DnsSection.AUTHORITY, buffer, authorityRecordCount)) {
                success = true;
                return response;
            }
            this.decodeRecords(response, DnsSection.ADDITIONAL, buffer, additionalRecordCount);
            success = true;
            return response;
        }
        finally {
            if (!success) {
                response.release();
            }
        }
    }
    
    protected abstract DnsResponse newResponse(final A p0, final A p1, final int p2, final DnsOpCode p3, final DnsResponseCode p4) throws Exception;
    
    private void decodeQuestions(final DnsResponse response, final ByteBuf buf, final int questionCount) throws Exception {
        for (int i = questionCount; i > 0; --i) {
            response.addRecord(DnsSection.QUESTION, (DnsRecord)this.recordDecoder.decodeQuestion(buf));
        }
    }
    
    private boolean decodeRecords(final DnsResponse response, final DnsSection section, final ByteBuf buf, final int count) throws Exception {
        for (int i = count; i > 0; --i) {
            final DnsRecord r = this.recordDecoder.decodeRecord(buf);
            if (r == null) {
                return false;
            }
            response.addRecord(section, r);
        }
        return true;
    }
}
