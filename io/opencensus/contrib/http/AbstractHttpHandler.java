package io.opencensus.contrib.http;

import io.opencensus.tags.TagContext;
import io.opencensus.contrib.http.util.HttpTraceUtil;
import io.opencensus.trace.AttributeValue;
import javax.annotation.Nullable;
import io.opencensus.trace.MessageEvent;
import io.opencensus.trace.Span;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;

abstract class AbstractHttpHandler<Q, P>
{
    @VisibleForTesting
    final HttpExtractor<Q, P> extractor;
    
    AbstractHttpHandler(final HttpExtractor<Q, P> extractor) {
        Preconditions.checkNotNull((Object)extractor, (Object)"extractor");
        this.extractor = extractor;
    }
    
    static void recordMessageEvent(final Span span, final long id, final MessageEvent.Type type, final long uncompressedMessageSize, final long compressedMessageSize) {
        final MessageEvent messageEvent = MessageEvent.builder(type, id).setUncompressedMessageSize(uncompressedMessageSize).setCompressedMessageSize(compressedMessageSize).build();
        span.addMessageEvent(messageEvent);
    }
    
    private static void putAttributeIfNotEmptyOrNull(final Span span, final String key, @Nullable final String value) {
        if (value != null && !value.isEmpty()) {
            span.putAttribute(key, AttributeValue.stringAttributeValue(value));
        }
    }
    
    public final void handleMessageSent(final HttpRequestContext context, final long bytes) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        context.sentMessageSize.addAndGet(bytes);
        if (context.span.getOptions().contains(Span.Options.RECORD_EVENTS)) {
            recordMessageEvent(context.span, context.sentSeqId.addAndGet(1L), MessageEvent.Type.SENT, bytes, 0L);
        }
    }
    
    public final void handleMessageReceived(final HttpRequestContext context, final long bytes) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        context.receiveMessageSize.addAndGet(bytes);
        if (context.span.getOptions().contains(Span.Options.RECORD_EVENTS)) {
            recordMessageEvent(context.span, context.receviedSeqId.addAndGet(1L), MessageEvent.Type.RECEIVED, bytes, 0L);
        }
    }
    
    void spanEnd(final Span span, final int httpStatus, @Nullable final Throwable error) {
        if (span.getOptions().contains(Span.Options.RECORD_EVENTS)) {
            span.putAttribute("http.status_code", AttributeValue.longAttributeValue((long)httpStatus));
            span.setStatus(HttpTraceUtil.parseResponseStatus(httpStatus, error));
        }
        span.end();
    }
    
    final String getSpanName(final Q request, final HttpExtractor<Q, P> extractor) {
        String path = extractor.getPath(request);
        if (path == null) {
            path = "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
    
    final void addSpanRequestAttributes(final Span span, final Q request, final HttpExtractor<Q, P> extractor) {
        putAttributeIfNotEmptyOrNull(span, "http.user_agent", extractor.getUserAgent(request));
        putAttributeIfNotEmptyOrNull(span, "http.host", extractor.getHost(request));
        putAttributeIfNotEmptyOrNull(span, "http.method", extractor.getMethod(request));
        putAttributeIfNotEmptyOrNull(span, "http.path", extractor.getPath(request));
        putAttributeIfNotEmptyOrNull(span, "http.route", extractor.getRoute(request));
        putAttributeIfNotEmptyOrNull(span, "http.url", extractor.getUrl(request));
    }
    
    public Span getSpanFromContext(final HttpRequestContext context) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        return context.span;
    }
    
    HttpRequestContext getNewContext(final Span span, final TagContext tagContext) {
        return new HttpRequestContext(span, tagContext);
    }
}
