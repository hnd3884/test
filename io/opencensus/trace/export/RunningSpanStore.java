package io.opencensus.trace.export;

import java.util.Collections;
import java.util.HashMap;
import io.opencensus.internal.Utils;
import java.util.Map;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class RunningSpanStore
{
    private static final RunningSpanStore NOOP_RUNNING_SPAN_STORE;
    
    protected RunningSpanStore() {
    }
    
    static RunningSpanStore getNoopRunningSpanStore() {
        return RunningSpanStore.NOOP_RUNNING_SPAN_STORE;
    }
    
    public abstract Summary getSummary();
    
    public abstract Collection<SpanData> getRunningSpans(final Filter p0);
    
    public abstract void setMaxNumberOfSpans(final int p0);
    
    static {
        NOOP_RUNNING_SPAN_STORE = new NoopRunningSpanStore();
    }
    
    @Immutable
    public abstract static class Summary
    {
        Summary() {
        }
        
        public static Summary create(final Map<String, PerSpanNameSummary> perSpanNameSummary) {
            return new AutoValue_RunningSpanStore_Summary(Collections.unmodifiableMap((Map<? extends String, ? extends PerSpanNameSummary>)new HashMap<String, PerSpanNameSummary>(Utils.checkNotNull(perSpanNameSummary, "perSpanNameSummary"))));
        }
        
        public abstract Map<String, PerSpanNameSummary> getPerSpanNameSummary();
    }
    
    @Immutable
    public abstract static class PerSpanNameSummary
    {
        PerSpanNameSummary() {
        }
        
        public static PerSpanNameSummary create(final int numRunningSpans) {
            Utils.checkArgument(numRunningSpans >= 0, (Object)"Negative numRunningSpans.");
            return new AutoValue_RunningSpanStore_PerSpanNameSummary(numRunningSpans);
        }
        
        public abstract int getNumRunningSpans();
    }
    
    @Immutable
    public abstract static class Filter
    {
        Filter() {
        }
        
        public static Filter create(final String spanName, final int maxSpansToReturn) {
            Utils.checkArgument(maxSpansToReturn >= 0, (Object)"Negative maxSpansToReturn.");
            return new AutoValue_RunningSpanStore_Filter(spanName, maxSpansToReturn);
        }
        
        public abstract String getSpanName();
        
        public abstract int getMaxSpansToReturn();
    }
    
    private static final class NoopRunningSpanStore extends RunningSpanStore
    {
        private static final Summary EMPTY_SUMMARY;
        
        @Override
        public Summary getSummary() {
            return NoopRunningSpanStore.EMPTY_SUMMARY;
        }
        
        @Override
        public Collection<SpanData> getRunningSpans(final Filter filter) {
            Utils.checkNotNull(filter, "filter");
            return (Collection<SpanData>)Collections.emptyList();
        }
        
        @Override
        public void setMaxNumberOfSpans(final int maxNumberOfSpans) {
            Utils.checkArgument(maxNumberOfSpans >= 0, (Object)"Invalid negative maxNumberOfElements");
        }
        
        static {
            EMPTY_SUMMARY = Summary.create(Collections.emptyMap());
        }
    }
}
