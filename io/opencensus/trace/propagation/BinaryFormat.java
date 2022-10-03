package io.opencensus.trace.propagation;

import io.opencensus.internal.Utils;
import java.text.ParseException;
import io.opencensus.trace.SpanContext;

public abstract class BinaryFormat
{
    static final NoopBinaryFormat NOOP_BINARY_FORMAT;
    
    @Deprecated
    public byte[] toBinaryValue(final SpanContext spanContext) {
        return this.toByteArray(spanContext);
    }
    
    public byte[] toByteArray(final SpanContext spanContext) {
        return this.toBinaryValue(spanContext);
    }
    
    @Deprecated
    public SpanContext fromBinaryValue(final byte[] bytes) throws ParseException {
        try {
            return this.fromByteArray(bytes);
        }
        catch (final SpanContextParseException e) {
            throw new ParseException(e.toString(), 0);
        }
    }
    
    public SpanContext fromByteArray(final byte[] bytes) throws SpanContextParseException {
        try {
            return this.fromBinaryValue(bytes);
        }
        catch (final ParseException e) {
            throw new SpanContextParseException("Error while parsing.", e);
        }
    }
    
    static BinaryFormat getNoopBinaryFormat() {
        return BinaryFormat.NOOP_BINARY_FORMAT;
    }
    
    static {
        NOOP_BINARY_FORMAT = new NoopBinaryFormat();
    }
    
    private static final class NoopBinaryFormat extends BinaryFormat
    {
        @Override
        public byte[] toByteArray(final SpanContext spanContext) {
            Utils.checkNotNull(spanContext, "spanContext");
            return new byte[0];
        }
        
        @Override
        public SpanContext fromByteArray(final byte[] bytes) {
            Utils.checkNotNull(bytes, "bytes");
            return SpanContext.INVALID;
        }
    }
}
