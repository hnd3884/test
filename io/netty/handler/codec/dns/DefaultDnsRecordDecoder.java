package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;

public class DefaultDnsRecordDecoder implements DnsRecordDecoder
{
    static final String ROOT = ".";
    
    protected DefaultDnsRecordDecoder() {
    }
    
    @Override
    public final DnsQuestion decodeQuestion(final ByteBuf in) throws Exception {
        final String name = decodeName(in);
        final DnsRecordType type = DnsRecordType.valueOf(in.readUnsignedShort());
        final int qClass = in.readUnsignedShort();
        return new DefaultDnsQuestion(name, type, qClass);
    }
    
    @Override
    public final <T extends DnsRecord> T decodeRecord(final ByteBuf in) throws Exception {
        final int startOffset = in.readerIndex();
        final String name = decodeName(in);
        final int endOffset = in.writerIndex();
        if (endOffset - in.readerIndex() < 10) {
            in.readerIndex(startOffset);
            return null;
        }
        final DnsRecordType type = DnsRecordType.valueOf(in.readUnsignedShort());
        final int aClass = in.readUnsignedShort();
        final long ttl = in.readUnsignedInt();
        final int length = in.readUnsignedShort();
        final int offset = in.readerIndex();
        if (endOffset - offset < length) {
            in.readerIndex(startOffset);
            return null;
        }
        final T record = (T)this.decodeRecord(name, type, aClass, ttl, in, offset, length);
        in.readerIndex(offset + length);
        return record;
    }
    
    protected DnsRecord decodeRecord(final String name, final DnsRecordType type, final int dnsClass, final long timeToLive, final ByteBuf in, final int offset, final int length) throws Exception {
        if (type == DnsRecordType.PTR) {
            return new DefaultDnsPtrRecord(name, dnsClass, timeToLive, this.decodeName0(in.duplicate().setIndex(offset, offset + length)));
        }
        if (type == DnsRecordType.CNAME || type == DnsRecordType.NS) {
            return new DefaultDnsRawRecord(name, type, dnsClass, timeToLive, DnsCodecUtil.decompressDomainName(in.duplicate().setIndex(offset, offset + length)));
        }
        return new DefaultDnsRawRecord(name, type, dnsClass, timeToLive, in.retainedDuplicate().setIndex(offset, offset + length));
    }
    
    protected String decodeName0(final ByteBuf in) {
        return decodeName(in);
    }
    
    public static String decodeName(final ByteBuf in) {
        return DnsCodecUtil.decodeDomainName(in);
    }
}
