package io.netty.handler.codec.http2;

import java.util.Arrays;
import io.netty.handler.codec.ValueConverter;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.util.AsciiString;
import java.util.List;

final class HpackStaticTable
{
    static final int NOT_FOUND = -1;
    private static final List<HpackHeaderField> STATIC_TABLE;
    private static final CharSequenceMap<Integer> STATIC_INDEX_BY_NAME;
    private static final int MAX_SAME_NAME_FIELD_INDEX;
    static final int length;
    
    private static HpackHeaderField newEmptyHeaderField(final String name) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.EMPTY_STRING);
    }
    
    private static HpackHeaderField newHeaderField(final String name, final String value) {
        return new HpackHeaderField(AsciiString.cached(name), AsciiString.cached(value));
    }
    
    static HpackHeaderField getEntry(final int index) {
        return HpackStaticTable.STATIC_TABLE.get(index - 1);
    }
    
    static int getIndex(final CharSequence name) {
        final Integer index = HpackStaticTable.STATIC_INDEX_BY_NAME.get(name);
        if (index == null) {
            return -1;
        }
        return index;
    }
    
    static int getIndexInsensitive(final CharSequence name, final CharSequence value) {
        int index = getIndex(name);
        if (index == -1) {
            return -1;
        }
        HpackHeaderField entry = getEntry(index);
        if (HpackUtil.equalsVariableTime(value, entry.value)) {
            return index;
        }
        ++index;
        while (index <= HpackStaticTable.MAX_SAME_NAME_FIELD_INDEX) {
            entry = getEntry(index);
            if (!HpackUtil.equalsVariableTime(name, entry.name)) {
                return -1;
            }
            if (HpackUtil.equalsVariableTime(value, entry.value)) {
                return index;
            }
            ++index;
        }
        return -1;
    }
    
    private static CharSequenceMap<Integer> createMap() {
        final int length = HpackStaticTable.STATIC_TABLE.size();
        final CharSequenceMap<Integer> ret = new CharSequenceMap<Integer>(true, (ValueConverter<Integer>)UnsupportedValueConverter.instance(), length);
        for (int index = length; index > 0; --index) {
            final HpackHeaderField entry = getEntry(index);
            final CharSequence name = entry.name;
            ret.set(name, Integer.valueOf(index));
        }
        return ret;
    }
    
    private static int maxSameNameFieldIndex() {
        final int length = HpackStaticTable.STATIC_TABLE.size();
        HpackHeaderField cursor = getEntry(length);
        for (int index = length - 1; index > 0; --index) {
            final HpackHeaderField entry = getEntry(index);
            if (HpackUtil.equalsVariableTime(entry.name, cursor.name)) {
                return index + 1;
            }
            cursor = entry;
        }
        return length;
    }
    
    private HpackStaticTable() {
    }
    
    static {
        STATIC_TABLE = Arrays.asList(newEmptyHeaderField(":authority"), newHeaderField(":method", "GET"), newHeaderField(":method", "POST"), newHeaderField(":path", "/"), newHeaderField(":path", "/index.html"), newHeaderField(":scheme", "http"), newHeaderField(":scheme", "https"), newHeaderField(":status", "200"), newHeaderField(":status", "204"), newHeaderField(":status", "206"), newHeaderField(":status", "304"), newHeaderField(":status", "400"), newHeaderField(":status", "404"), newHeaderField(":status", "500"), newEmptyHeaderField("accept-charset"), newHeaderField("accept-encoding", "gzip, deflate"), newEmptyHeaderField("accept-language"), newEmptyHeaderField("accept-ranges"), newEmptyHeaderField("accept"), newEmptyHeaderField("access-control-allow-origin"), newEmptyHeaderField("age"), newEmptyHeaderField("allow"), newEmptyHeaderField("authorization"), newEmptyHeaderField("cache-control"), newEmptyHeaderField("content-disposition"), newEmptyHeaderField("content-encoding"), newEmptyHeaderField("content-language"), newEmptyHeaderField("content-length"), newEmptyHeaderField("content-location"), newEmptyHeaderField("content-range"), newEmptyHeaderField("content-type"), newEmptyHeaderField("cookie"), newEmptyHeaderField("date"), newEmptyHeaderField("etag"), newEmptyHeaderField("expect"), newEmptyHeaderField("expires"), newEmptyHeaderField("from"), newEmptyHeaderField("host"), newEmptyHeaderField("if-match"), newEmptyHeaderField("if-modified-since"), newEmptyHeaderField("if-none-match"), newEmptyHeaderField("if-range"), newEmptyHeaderField("if-unmodified-since"), newEmptyHeaderField("last-modified"), newEmptyHeaderField("link"), newEmptyHeaderField("location"), newEmptyHeaderField("max-forwards"), newEmptyHeaderField("proxy-authenticate"), newEmptyHeaderField("proxy-authorization"), newEmptyHeaderField("range"), newEmptyHeaderField("referer"), newEmptyHeaderField("refresh"), newEmptyHeaderField("retry-after"), newEmptyHeaderField("server"), newEmptyHeaderField("set-cookie"), newEmptyHeaderField("strict-transport-security"), newEmptyHeaderField("transfer-encoding"), newEmptyHeaderField("user-agent"), newEmptyHeaderField("vary"), newEmptyHeaderField("via"), newEmptyHeaderField("www-authenticate"));
        STATIC_INDEX_BY_NAME = createMap();
        MAX_SAME_NAME_FIELD_INDEX = maxSameNameFieldIndex();
        length = HpackStaticTable.STATIC_TABLE.size();
    }
}
