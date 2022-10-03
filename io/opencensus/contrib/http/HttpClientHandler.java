package io.opencensus.contrib.http;

import io.opencensus.tags.TagContext;
import io.opencensus.tags.TagValue;
import io.opencensus.contrib.http.util.HttpMeasureConstants;
import java.util.concurrent.TimeUnit;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.SpanContext;
import javax.annotation.Nullable;
import io.opencensus.trace.Span;
import io.opencensus.tags.Tags;
import io.opencensus.stats.Stats;
import com.google.common.base.Preconditions;
import io.opencensus.tags.Tagger;
import io.opencensus.stats.StatsRecorder;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.propagation.TextFormat;

public class HttpClientHandler<Q, P, C> extends AbstractHttpHandler<Q, P>
{
    private final TextFormat.Setter<C> setter;
    private final TextFormat textFormat;
    private final Tracer tracer;
    private final StatsRecorder statsRecorder;
    private final Tagger tagger;
    
    public HttpClientHandler(final Tracer tracer, final HttpExtractor<Q, P> extractor, final TextFormat textFormat, final TextFormat.Setter<C> setter) {
        super(extractor);
        Preconditions.checkNotNull((Object)setter, (Object)"setter");
        Preconditions.checkNotNull((Object)textFormat, (Object)"textFormat");
        Preconditions.checkNotNull((Object)tracer, (Object)"tracer");
        this.setter = setter;
        this.textFormat = textFormat;
        this.tracer = tracer;
        this.statsRecorder = Stats.getStatsRecorder();
        this.tagger = Tags.getTagger();
    }
    
    public HttpRequestContext handleStart(@Nullable Span parent, final C carrier, final Q request) {
        Preconditions.checkNotNull((Object)carrier, (Object)"carrier");
        Preconditions.checkNotNull((Object)request, (Object)"request");
        if (parent == null) {
            parent = this.tracer.getCurrentSpan();
        }
        final String spanName = this.getSpanName(request, (HttpExtractor<Q, P>)this.extractor);
        final SpanBuilder builder = this.tracer.spanBuilderWithExplicitParent(spanName, parent);
        final Span span = builder.setSpanKind(Span.Kind.CLIENT).startSpan();
        if (span.getOptions().contains(Span.Options.RECORD_EVENTS)) {
            this.addSpanRequestAttributes(span, request, (HttpExtractor<Q, P>)this.extractor);
        }
        final SpanContext spanContext = span.getContext();
        if (!spanContext.equals((Object)SpanContext.INVALID)) {
            this.textFormat.inject(spanContext, (Object)carrier, (TextFormat.Setter)this.setter);
        }
        return this.getNewContext(span, this.tagger.getCurrentTagContext());
    }
    
    public void handleEnd(final HttpRequestContext context, @Nullable final Q request, @Nullable final P response, @Nullable final Throwable error) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        final int httpCode = this.extractor.getStatusCode((P)response);
        this.recordStats(context, request, httpCode);
        this.spanEnd(context.span, httpCode, error);
    }
    
    private void recordStats(final HttpRequestContext context, @Nullable final Q request, final int httpCode) {
        final double requestLatency = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - context.requestStartTime);
        final String methodStr = (request == null) ? "" : this.extractor.getMethod((Q)request);
        final String host = (request == null) ? "null_request" : this.extractor.getHost((Q)request);
        final TagContext startCtx = this.tagger.toBuilder(context.tagContext).put(HttpMeasureConstants.HTTP_CLIENT_HOST, TagValue.create((host == null) ? "null_host" : host), HttpRequestContext.METADATA_NO_PROPAGATION).put(HttpMeasureConstants.HTTP_CLIENT_METHOD, TagValue.create((methodStr == null) ? "" : methodStr), HttpRequestContext.METADATA_NO_PROPAGATION).put(HttpMeasureConstants.HTTP_CLIENT_STATUS, TagValue.create((httpCode == 0) ? "error" : Integer.toString(httpCode)), HttpRequestContext.METADATA_NO_PROPAGATION).build();
        this.statsRecorder.newMeasureMap().put(HttpMeasureConstants.HTTP_CLIENT_ROUNDTRIP_LATENCY, requestLatency).put(HttpMeasureConstants.HTTP_CLIENT_SENT_BYTES, context.sentMessageSize.get()).put(HttpMeasureConstants.HTTP_CLIENT_RECEIVED_BYTES, context.receiveMessageSize.get()).record(startCtx);
    }
}
