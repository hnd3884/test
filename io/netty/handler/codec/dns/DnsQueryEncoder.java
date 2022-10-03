package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.util.internal.ObjectUtil;

final class DnsQueryEncoder
{
    private final DnsRecordEncoder recordEncoder;
    
    DnsQueryEncoder(final DnsRecordEncoder recordEncoder) {
        this.recordEncoder = ObjectUtil.checkNotNull(recordEncoder, "recordEncoder");
    }
    
    void encode(final DnsQuery query, final ByteBuf out) throws Exception {
        encodeHeader(query, out);
        this.encodeQuestions(query, out);
        this.encodeRecords(query, DnsSection.ADDITIONAL, out);
    }
    
    private static void encodeHeader(final DnsQuery query, final ByteBuf buf) {
        buf.writeShort(query.id());
        int flags = 0;
        flags |= (query.opCode().byteValue() & 0xFF) << 14;
        if (query.isRecursionDesired()) {
            flags |= 0x100;
        }
        buf.writeShort(flags);
        buf.writeShort(query.count(DnsSection.QUESTION));
        buf.writeShort(0);
        buf.writeShort(0);
        buf.writeShort(query.count(DnsSection.ADDITIONAL));
    }
    
    private void encodeQuestions(final DnsQuery query, final ByteBuf buf) throws Exception {
        for (int count = query.count(DnsSection.QUESTION), i = 0; i < count; ++i) {
            this.recordEncoder.encodeQuestion(query.recordAt(DnsSection.QUESTION, i), buf);
        }
    }
    
    private void encodeRecords(final DnsQuery query, final DnsSection section, final ByteBuf buf) throws Exception {
        for (int count = query.count(section), i = 0; i < count; ++i) {
            this.recordEncoder.encodeRecord(query.recordAt(section, i), buf);
        }
    }
}
