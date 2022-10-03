package io.opencensus.contrib.http;

import io.opencensus.tags.TagContext;
import io.opencensus.tags.TagValue;
import io.opencensus.contrib.http.util.HttpMeasureConstants;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.Link;
import io.opencensus.trace.Span;
import io.opencensus.trace.propagation.SpanContextParseException;
import io.opencensus.tags.Tags;
import io.opencensus.stats.Stats;
import com.google.common.base.Preconditions;
import io.opencensus.tags.Tagger;
import io.opencensus.stats.StatsRecorder;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.propagation.TextFormat;

public class HttpServerHandler<Q, P, C> extends AbstractHttpHandler<Q, P>
{
    private final TextFormat.Getter<C> getter;
    private final TextFormat textFormat;
    private final Tracer tracer;
    private final Boolean publicEndpoint;
    private final StatsRecorder statsRecorder;
    private final Tagger tagger;
    
    public HttpServerHandler(final Tracer tracer, final HttpExtractor<Q, P> extractor, final TextFormat textFormat, final TextFormat.Getter<C> getter, final Boolean publicEndpoint) {
        super(extractor);
        Preconditions.checkNotNull((Object)tracer, (Object)"tracer");
        Preconditions.checkNotNull((Object)textFormat, (Object)"textFormat");
        Preconditions.checkNotNull((Object)getter, (Object)"getter");
        Preconditions.checkNotNull((Object)publicEndpoint, (Object)"publicEndpoint");
        this.tracer = tracer;
        this.textFormat = textFormat;
        this.getter = getter;
        this.publicEndpoint = publicEndpoint;
        this.statsRecorder = Stats.getStatsRecorder();
        this.tagger = Tags.getTagger();
    }
    
    public HttpRequestContext handleStart(final C carrier, final Q request) {
        Preconditions.checkNotNull((Object)carrier, (Object)"carrier");
        Preconditions.checkNotNull((Object)request, (Object)"request");
        SpanBuilder spanBuilder = null;
        final String spanName = this.getSpanName(request, (HttpExtractor<Q, P>)this.extractor);
        SpanContext spanContext = null;
        try {
            spanContext = this.textFormat.extract((Object)carrier, (TextFormat.Getter)this.getter);
        }
        catch (final SpanContextParseException ex) {}
        if (spanContext == null || this.publicEndpoint) {
            spanBuilder = this.tracer.spanBuilder(spanName);
        }
        else {
            spanBuilder = this.tracer.spanBuilderWithRemoteParent(spanName, spanContext);
        }
        final Span span = spanBuilder.setSpanKind(Span.Kind.SERVER).startSpan();
        if (this.publicEndpoint && spanContext != null) {
            span.addLink(Link.fromSpanContext(spanContext, Link.Type.PARENT_LINKED_SPAN));
        }
        if (span.getOptions().contains(Span.Options.RECORD_EVENTS)) {
            this.addSpanRequestAttributes(span, request, (HttpExtractor<Q, P>)this.extractor);
        }
        return this.getNewContext(span, this.tagger.getCurrentTagContext());
    }
    
    public void handleEnd(final HttpRequestContext context, final Q request, @Nullable final P response, @Nullable final Throwable error) {
        Preconditions.checkNotNull((Object)context, (Object)"context");
        Preconditions.checkNotNull((Object)request, (Object)"request");
        final int httpCode = this.extractor.getStatusCode((P)response);
        this.recordStats(context, request, httpCode);
        this.spanEnd(context.span, httpCode, error);
    }
    
    private void recordStats(final HttpRequestContext context, final Q request, final int httpCode) {
        final double requestLatency = (double)TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - context.requestStartTime);
        final String methodStr = this.extractor.getMethod((Q)request);
        final String routeStr = this.extractor.getRoute((Q)request);
        final TagContext startCtx = this.tagger.toBuilder(context.tagContext).put(HttpMeasureConstants.HTTP_SERVER_METHOD, TagValue.create((methodStr == null) ? "" : methodStr), HttpRequestContext.METADATA_NO_PROPAGATION).put(HttpMeasureConstants.HTTP_SERVER_ROUTE, TagValue.create((routeStr == null) ? "" : routeStr), HttpRequestContext.METADATA_NO_PROPAGATION).put(HttpMeasureConstants.HTTP_SERVER_STATUS, TagValue.create((httpCode == 0) ? "error" : Integer.toString(httpCode)), HttpRequestContext.METADATA_NO_PROPAGATION).build();
        this.statsRecorder.newMeasureMap().put(HttpMeasureConstants.HTTP_SERVER_LATENCY, requestLatency).put(HttpMeasureConstants.HTTP_SERVER_RECEIVED_BYTES, context.receiveMessageSize.get()).put(HttpMeasureConstants.HTTP_SERVER_SENT_BYTES, context.sentMessageSize.get()).record(startCtx);
    }
}
