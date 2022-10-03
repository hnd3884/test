package com.google.api.client.http;

import java.util.Collection;
import com.google.common.collect.ImmutableList;
import java.util.logging.Level;
import io.opencensus.contrib.http.util.HttpPropagationUtil;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Status;
import io.opencensus.trace.EndSpanOptions;
import io.opencensus.trace.BlankSpan;
import com.google.api.client.util.Preconditions;
import io.opencensus.trace.Span;
import com.google.common.annotations.VisibleForTesting;
import javax.annotation.Nullable;
import io.opencensus.trace.propagation.TextFormat;
import java.util.concurrent.atomic.AtomicLong;
import io.opencensus.trace.Tracer;
import java.util.logging.Logger;
import com.google.api.client.util.Beta;

@Beta
public class OpenCensusUtils
{
    private static final Logger logger;
    public static final String SPAN_NAME_HTTP_REQUEST_EXECUTE;
    private static final Tracer tracer;
    private static final AtomicLong idGenerator;
    private static volatile boolean isRecordEvent;
    @Nullable
    @VisibleForTesting
    static volatile TextFormat propagationTextFormat;
    @Nullable
    @VisibleForTesting
    static volatile TextFormat.Setter propagationTextFormatSetter;
    
    public static void setPropagationTextFormat(@Nullable final TextFormat textFormat) {
        OpenCensusUtils.propagationTextFormat = textFormat;
    }
    
    public static void setPropagationTextFormatSetter(@Nullable final TextFormat.Setter textFormatSetter) {
        OpenCensusUtils.propagationTextFormatSetter = textFormatSetter;
    }
    
    public static void setIsRecordEvent(final boolean recordEvent) {
        OpenCensusUtils.isRecordEvent = recordEvent;
    }
    
    public static Tracer getTracer() {
        return OpenCensusUtils.tracer;
    }
    
    public static boolean isRecordEvent() {
        return OpenCensusUtils.isRecordEvent;
    }
    
    public static void propagateTracingContext(final Span span, final HttpHeaders headers) {
        Preconditions.checkArgument(span != null, (Object)"span should not be null.");
        Preconditions.checkArgument(headers != null, (Object)"headers should not be null.");
        if (OpenCensusUtils.propagationTextFormat != null && OpenCensusUtils.propagationTextFormatSetter != null && !span.equals(BlankSpan.INSTANCE)) {
            OpenCensusUtils.propagationTextFormat.inject(span.getContext(), (Object)headers, OpenCensusUtils.propagationTextFormatSetter);
        }
    }
    
    public static EndSpanOptions getEndSpanOptions(@Nullable final Integer statusCode) {
        final EndSpanOptions.Builder builder = EndSpanOptions.builder();
        if (statusCode == null) {
            builder.setStatus(Status.UNKNOWN);
        }
        else if (!HttpStatusCodes.isSuccess(statusCode)) {
            switch (statusCode) {
                case 400: {
                    builder.setStatus(Status.INVALID_ARGUMENT);
                    break;
                }
                case 401: {
                    builder.setStatus(Status.UNAUTHENTICATED);
                    break;
                }
                case 403: {
                    builder.setStatus(Status.PERMISSION_DENIED);
                    break;
                }
                case 404: {
                    builder.setStatus(Status.NOT_FOUND);
                    break;
                }
                case 412: {
                    builder.setStatus(Status.FAILED_PRECONDITION);
                    break;
                }
                case 500: {
                    builder.setStatus(Status.UNAVAILABLE);
                    break;
                }
                default: {
                    builder.setStatus(Status.UNKNOWN);
                    break;
                }
            }
        }
        else {
            builder.setStatus(Status.OK);
        }
        return builder.build();
    }
    
    public static void recordSentMessageEvent(final Span span, final long size) {
        recordMessageEvent(span, size, MessageEvent.Type.SENT);
    }
    
    public static void recordReceivedMessageEvent(final Span span, final long size) {
        recordMessageEvent(span, size, MessageEvent.Type.RECEIVED);
    }
    
    @VisibleForTesting
    static void recordMessageEvent(final Span span, long size, final MessageEvent.Type eventType) {
        Preconditions.checkArgument(span != null, (Object)"span should not be null.");
        if (size < 0L) {
            size = 0L;
        }
        final MessageEvent event = MessageEvent.builder(eventType, OpenCensusUtils.idGenerator.getAndIncrement()).setUncompressedMessageSize(size).build();
        span.addMessageEvent(event);
    }
    
    private OpenCensusUtils() {
    }
    
    static {
        logger = Logger.getLogger(OpenCensusUtils.class.getName());
        SPAN_NAME_HTTP_REQUEST_EXECUTE = "Sent." + HttpRequest.class.getName() + ".execute";
        tracer = Tracing.getTracer();
        idGenerator = new AtomicLong();
        OpenCensusUtils.isRecordEvent = true;
        OpenCensusUtils.propagationTextFormat = null;
        OpenCensusUtils.propagationTextFormatSetter = null;
        try {
            OpenCensusUtils.propagationTextFormat = HttpPropagationUtil.getCloudTraceFormat();
            OpenCensusUtils.propagationTextFormatSetter = new TextFormat.Setter<HttpHeaders>() {
                public void put(final HttpHeaders carrier, final String key, final String value) {
                    carrier.set(key, value);
                }
            };
        }
        catch (final Exception e) {
            OpenCensusUtils.logger.log(Level.WARNING, "Cannot initialize default OpenCensus HTTP propagation text format.", e);
        }
        try {
            Tracing.getExportComponent().getSampledSpanStore().registerSpanNamesForCollection((Collection)ImmutableList.of((Object)OpenCensusUtils.SPAN_NAME_HTTP_REQUEST_EXECUTE));
        }
        catch (final Exception e) {
            OpenCensusUtils.logger.log(Level.WARNING, "Cannot register default OpenCensus span names for collection.", e);
        }
    }
}
