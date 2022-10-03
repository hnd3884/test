package io.netty.handler.codec.dns;

import io.netty.handler.codec.CorruptedFrameException;
import io.netty.buffer.ByteBuf;
import java.net.SocketAddress;
import io.netty.channel.AddressedEnvelope;
import io.netty.util.internal.StringUtil;

final class DnsMessageUtil
{
    static StringBuilder appendQuery(final StringBuilder buf, final DnsQuery query) {
        appendQueryHeader(buf, query);
        appendAllRecords(buf, query);
        return buf;
    }
    
    static StringBuilder appendResponse(final StringBuilder buf, final DnsResponse response) {
        appendResponseHeader(buf, response);
        appendAllRecords(buf, response);
        return buf;
    }
    
    static StringBuilder appendRecordClass(final StringBuilder buf, int dnsClass) {
        String name = null;
        switch (dnsClass &= 0xFFFF) {
            case 1: {
                name = "IN";
                break;
            }
            case 2: {
                name = "CSNET";
                break;
            }
            case 3: {
                name = "CHAOS";
                break;
            }
            case 4: {
                name = "HESIOD";
                break;
            }
            case 254: {
                name = "NONE";
                break;
            }
            case 255: {
                name = "ANY";
                break;
            }
            default: {
                name = null;
                break;
            }
        }
        if (name != null) {
            buf.append(name);
        }
        else {
            buf.append("UNKNOWN(").append(dnsClass).append(')');
        }
        return buf;
    }
    
    private static void appendQueryHeader(final StringBuilder buf, final DnsQuery msg) {
        buf.append(StringUtil.simpleClassName(msg)).append('(');
        appendAddresses(buf, msg).append(msg.id()).append(", ").append(msg.opCode());
        if (msg.isRecursionDesired()) {
            buf.append(", RD");
        }
        if (msg.z() != 0) {
            buf.append(", Z: ").append(msg.z());
        }
        buf.append(')');
    }
    
    private static void appendResponseHeader(final StringBuilder buf, final DnsResponse msg) {
        buf.append(StringUtil.simpleClassName(msg)).append('(');
        appendAddresses(buf, msg).append(msg.id()).append(", ").append(msg.opCode()).append(", ").append(msg.code()).append(',');
        boolean hasComma = true;
        if (msg.isRecursionDesired()) {
            hasComma = false;
            buf.append(" RD");
        }
        if (msg.isAuthoritativeAnswer()) {
            hasComma = false;
            buf.append(" AA");
        }
        if (msg.isTruncated()) {
            hasComma = false;
            buf.append(" TC");
        }
        if (msg.isRecursionAvailable()) {
            hasComma = false;
            buf.append(" RA");
        }
        if (msg.z() != 0) {
            if (!hasComma) {
                buf.append(',');
            }
            buf.append(" Z: ").append(msg.z());
        }
        if (hasComma) {
            buf.setCharAt(buf.length() - 1, ')');
        }
        else {
            buf.append(')');
        }
    }
    
    private static StringBuilder appendAddresses(final StringBuilder buf, final DnsMessage msg) {
        if (!(msg instanceof AddressedEnvelope)) {
            return buf;
        }
        final AddressedEnvelope<?, SocketAddress> envelope = (AddressedEnvelope<?, SocketAddress>)msg;
        SocketAddress addr = envelope.sender();
        if (addr != null) {
            buf.append("from: ").append(addr).append(", ");
        }
        addr = envelope.recipient();
        if (addr != null) {
            buf.append("to: ").append(addr).append(", ");
        }
        return buf;
    }
    
    private static void appendAllRecords(final StringBuilder buf, final DnsMessage msg) {
        appendRecords(buf, msg, DnsSection.QUESTION);
        appendRecords(buf, msg, DnsSection.ANSWER);
        appendRecords(buf, msg, DnsSection.AUTHORITY);
        appendRecords(buf, msg, DnsSection.ADDITIONAL);
    }
    
    private static void appendRecords(final StringBuilder buf, final DnsMessage message, final DnsSection section) {
        for (int count = message.count(section), i = 0; i < count; ++i) {
            buf.append(StringUtil.NEWLINE).append('\t').append(message.recordAt(section, i));
        }
    }
    
    static DnsQuery decodeDnsQuery(final DnsRecordDecoder decoder, final ByteBuf buf, final DnsQueryFactory supplier) throws Exception {
        final DnsQuery query = newQuery(buf, supplier);
        boolean success = false;
        try {
            final int questionCount = buf.readUnsignedShort();
            final int answerCount = buf.readUnsignedShort();
            final int authorityRecordCount = buf.readUnsignedShort();
            final int additionalRecordCount = buf.readUnsignedShort();
            decodeQuestions(decoder, query, buf, questionCount);
            decodeRecords(decoder, query, DnsSection.ANSWER, buf, answerCount);
            decodeRecords(decoder, query, DnsSection.AUTHORITY, buf, authorityRecordCount);
            decodeRecords(decoder, query, DnsSection.ADDITIONAL, buf, additionalRecordCount);
            success = true;
            return query;
        }
        finally {
            if (!success) {
                query.release();
            }
        }
    }
    
    private static DnsQuery newQuery(final ByteBuf buf, final DnsQueryFactory supplier) {
        final int id = buf.readUnsignedShort();
        final int flags = buf.readUnsignedShort();
        if (flags >> 15 == 1) {
            throw new CorruptedFrameException("not a query");
        }
        final DnsQuery query = supplier.newQuery(id, DnsOpCode.valueOf((byte)(flags >> 11 & 0xF)));
        query.setRecursionDesired((flags >> 8 & 0x1) == 0x1);
        query.setZ(flags >> 4 & 0x7);
        return query;
    }
    
    private static void decodeQuestions(final DnsRecordDecoder decoder, final DnsQuery query, final ByteBuf buf, final int questionCount) throws Exception {
        for (int i = questionCount; i > 0; --i) {
            query.addRecord(DnsSection.QUESTION, (DnsRecord)decoder.decodeQuestion(buf));
        }
    }
    
    private static void decodeRecords(final DnsRecordDecoder decoder, final DnsQuery query, final DnsSection section, final ByteBuf buf, final int count) throws Exception {
        for (int i = count; i > 0; --i) {
            final DnsRecord r = decoder.decodeRecord(buf);
            if (r == null) {
                break;
            }
            query.addRecord(section, r);
        }
    }
    
    static void encodeDnsResponse(final DnsRecordEncoder encoder, final DnsResponse response, final ByteBuf buf) throws Exception {
        boolean success = false;
        try {
            encodeHeader(response, buf);
            encodeQuestions(encoder, response, buf);
            encodeRecords(encoder, response, DnsSection.ANSWER, buf);
            encodeRecords(encoder, response, DnsSection.AUTHORITY, buf);
            encodeRecords(encoder, response, DnsSection.ADDITIONAL, buf);
            success = true;
        }
        finally {
            if (!success) {
                buf.release();
            }
        }
    }
    
    private static void encodeHeader(final DnsResponse response, final ByteBuf buf) {
        buf.writeShort(response.id());
        int flags = 32768;
        flags |= (response.opCode().byteValue() & 0xFF) << 11;
        if (response.isAuthoritativeAnswer()) {
            flags |= 0x400;
        }
        if (response.isTruncated()) {
            flags |= 0x200;
        }
        if (response.isRecursionDesired()) {
            flags |= 0x100;
        }
        if (response.isRecursionAvailable()) {
            flags |= 0x80;
        }
        flags |= response.z() << 4;
        flags |= response.code().intValue();
        buf.writeShort(flags);
        buf.writeShort(response.count(DnsSection.QUESTION));
        buf.writeShort(response.count(DnsSection.ANSWER));
        buf.writeShort(response.count(DnsSection.AUTHORITY));
        buf.writeShort(response.count(DnsSection.ADDITIONAL));
    }
    
    private static void encodeQuestions(final DnsRecordEncoder encoder, final DnsResponse response, final ByteBuf buf) throws Exception {
        for (int count = response.count(DnsSection.QUESTION), i = 0; i < count; ++i) {
            encoder.encodeQuestion(response.recordAt(DnsSection.QUESTION, i), buf);
        }
    }
    
    private static void encodeRecords(final DnsRecordEncoder encoder, final DnsResponse response, final DnsSection section, final ByteBuf buf) throws Exception {
        for (int count = response.count(section), i = 0; i < count; ++i) {
            encoder.encodeRecord(response.recordAt(section, i), buf);
        }
    }
    
    private DnsMessageUtil() {
    }
    
    interface DnsQueryFactory
    {
        DnsQuery newQuery(final int p0, final DnsOpCode p1);
    }
}
