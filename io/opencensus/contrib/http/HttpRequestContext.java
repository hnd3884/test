package io.opencensus.contrib.http;

import com.google.common.base.Preconditions;
import io.opencensus.tags.TagContext;
import java.util.concurrent.atomic.AtomicLong;
import io.opencensus.trace.Span;
import io.opencensus.tags.TagMetadata;
import com.google.common.annotations.VisibleForTesting;

public class HttpRequestContext
{
    @VisibleForTesting
    static final long INVALID_STARTTIME = -1L;
    static final TagMetadata METADATA_NO_PROPAGATION;
    @VisibleForTesting
    final long requestStartTime;
    @VisibleForTesting
    final Span span;
    @VisibleForTesting
    AtomicLong sentMessageSize;
    @VisibleForTesting
    AtomicLong receiveMessageSize;
    @VisibleForTesting
    AtomicLong sentSeqId;
    @VisibleForTesting
    AtomicLong receviedSeqId;
    @VisibleForTesting
    final TagContext tagContext;
    
    HttpRequestContext(final Span span, final TagContext tagContext) {
        this.sentMessageSize = new AtomicLong();
        this.receiveMessageSize = new AtomicLong();
        this.sentSeqId = new AtomicLong();
        this.receviedSeqId = new AtomicLong();
        Preconditions.checkNotNull((Object)span, (Object)"span");
        Preconditions.checkNotNull((Object)tagContext, (Object)"tagContext");
        this.span = span;
        this.tagContext = tagContext;
        this.requestStartTime = System.nanoTime();
    }
    
    static {
        METADATA_NO_PROPAGATION = TagMetadata.create(TagMetadata.TagTtl.NO_PROPAGATION);
    }
}
