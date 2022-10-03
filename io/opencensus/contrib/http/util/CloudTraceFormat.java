package io.opencensus.contrib.http.util;

import java.util.Collections;
import java.nio.ByteBuffer;
import io.opencensus.trace.SpanId;
import com.google.common.primitives.UnsignedInts;
import io.opencensus.trace.TraceId;
import io.opencensus.trace.propagation.SpanContextParseException;
import com.google.common.primitives.UnsignedLongs;
import com.google.common.base.Preconditions;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.Tracestate;
import io.opencensus.trace.TraceOptions;
import java.util.List;
import io.opencensus.trace.propagation.TextFormat;

final class CloudTraceFormat extends TextFormat
{
    static final String HEADER_NAME = "X-Cloud-Trace-Context";
    static final List<String> FIELDS;
    static final char SPAN_ID_DELIMITER = '/';
    static final String TRACE_OPTION_DELIMITER = ";o=";
    static final String SAMPLED = "1";
    static final String NOT_SAMPLED = "0";
    static final TraceOptions OPTIONS_SAMPLED;
    static final TraceOptions OPTIONS_NOT_SAMPLED;
    static final int TRACE_ID_SIZE = 32;
    static final int TRACE_OPTION_DELIMITER_SIZE;
    static final int SPAN_ID_START_POS = 33;
    static final int MIN_HEADER_SIZE = 34;
    static final int CLOUD_TRACE_IS_SAMPLED = 1;
    private static final Tracestate TRACESTATE_DEFAULT;
    
    public List<String> fields() {
        return CloudTraceFormat.FIELDS;
    }
    
    public <C> void inject(final SpanContext spanContext, final C carrier, final TextFormat.Setter<C> setter) {
        Preconditions.checkNotNull((Object)spanContext, (Object)"spanContext");
        Preconditions.checkNotNull((Object)setter, (Object)"setter");
        Preconditions.checkNotNull((Object)carrier, (Object)"carrier");
        final StringBuilder builder = new StringBuilder().append(spanContext.getTraceId().toLowerBase16()).append('/').append(UnsignedLongs.toString(spanIdToLong(spanContext.getSpanId()))).append(";o=").append(spanContext.getTraceOptions().isSampled() ? "1" : "0");
        setter.put((Object)carrier, "X-Cloud-Trace-Context", builder.toString());
    }
    
    public <C> SpanContext extract(final C carrier, final TextFormat.Getter<C> getter) throws SpanContextParseException {
        Preconditions.checkNotNull((Object)carrier, (Object)"carrier");
        Preconditions.checkNotNull((Object)getter, (Object)"getter");
        try {
            final String headerStr = getter.get((Object)carrier, "X-Cloud-Trace-Context");
            if (headerStr == null || headerStr.length() < 34) {
                throw new SpanContextParseException("Missing or too short header: X-Cloud-Trace-Context");
            }
            Preconditions.checkArgument(headerStr.charAt(32) == '/', (Object)"Invalid TRACE_ID size");
            final TraceId traceId = TraceId.fromLowerBase16(headerStr.subSequence(0, 32));
            final int traceOptionsPos = headerStr.indexOf(";o=", 32);
            final CharSequence spanIdStr = headerStr.subSequence(33, (traceOptionsPos < 0) ? headerStr.length() : traceOptionsPos);
            final SpanId spanId = longToSpanId(UnsignedLongs.parseUnsignedLong(spanIdStr.toString(), 10));
            TraceOptions traceOptions = CloudTraceFormat.OPTIONS_NOT_SAMPLED;
            if (traceOptionsPos > 0) {
                final String traceOptionsStr = headerStr.substring(traceOptionsPos + CloudTraceFormat.TRACE_OPTION_DELIMITER_SIZE);
                if ((UnsignedInts.parseUnsignedInt(traceOptionsStr, 10) & 0x1) != 0x0) {
                    traceOptions = CloudTraceFormat.OPTIONS_SAMPLED;
                }
            }
            return SpanContext.create(traceId, spanId, traceOptions, CloudTraceFormat.TRACESTATE_DEFAULT);
        }
        catch (final IllegalArgumentException e) {
            throw new SpanContextParseException("Invalid input", (Throwable)e);
        }
    }
    
    private static SpanId longToSpanId(final long x) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(x);
        return SpanId.fromBytes(buffer.array());
    }
    
    private static long spanIdToLong(final SpanId spanId) {
        final ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(spanId.getBytes());
        return buffer.getLong(0);
    }
    
    static {
        FIELDS = Collections.singletonList("X-Cloud-Trace-Context");
        OPTIONS_SAMPLED = TraceOptions.builder().setIsSampled(true).build();
        OPTIONS_NOT_SAMPLED = TraceOptions.DEFAULT;
        TRACE_OPTION_DELIMITER_SIZE = ";o=".length();
        TRACESTATE_DEFAULT = Tracestate.builder().build();
    }
}
