package io.opencensus.contrib.http.util;

import com.google.common.collect.UnmodifiableIterator;
import io.opencensus.stats.ViewManager;
import io.opencensus.stats.Stats;
import com.google.common.annotations.VisibleForTesting;
import io.opencensus.stats.View;
import com.google.common.collect.ImmutableSet;

public final class HttpViews
{
    @VisibleForTesting
    static final ImmutableSet<View> HTTP_SERVER_VIEWS_SET;
    @VisibleForTesting
    static final ImmutableSet<View> HTTP_CLIENT_VIEWS_SET;
    
    private HttpViews() {
    }
    
    public static final void registerAllClientViews() {
        registerAllClientViews(Stats.getViewManager());
    }
    
    @VisibleForTesting
    static void registerAllClientViews(final ViewManager viewManager) {
        for (final View view : HttpViews.HTTP_CLIENT_VIEWS_SET) {
            viewManager.registerView(view);
        }
    }
    
    public static final void registerAllServerViews() {
        registerAllServerViews(Stats.getViewManager());
    }
    
    @VisibleForTesting
    static void registerAllServerViews(final ViewManager viewManager) {
        for (final View view : HttpViews.HTTP_SERVER_VIEWS_SET) {
            viewManager.registerView(view);
        }
    }
    
    public static final void registerAllViews() {
        registerAllViews(Stats.getViewManager());
    }
    
    @VisibleForTesting
    static void registerAllViews(final ViewManager viewManager) {
        registerAllClientViews(viewManager);
        registerAllServerViews(viewManager);
    }
    
    static {
        HTTP_SERVER_VIEWS_SET = ImmutableSet.of((Object)HttpViewConstants.HTTP_SERVER_COMPLETED_COUNT_VIEW, (Object)HttpViewConstants.HTTP_SERVER_SENT_BYTES_VIEW, (Object)HttpViewConstants.HTTP_SERVER_RECEIVED_BYTES_VIEW, (Object)HttpViewConstants.HTTP_SERVER_LATENCY_VIEW);
        HTTP_CLIENT_VIEWS_SET = ImmutableSet.of((Object)HttpViewConstants.HTTP_CLIENT_COMPLETED_COUNT_VIEW, (Object)HttpViewConstants.HTTP_CLIENT_RECEIVED_BYTES_VIEW, (Object)HttpViewConstants.HTTP_CLIENT_SENT_BYTES_VIEW, (Object)HttpViewConstants.HTTP_CLIENT_ROUNDTRIP_LATENCY_VIEW);
    }
}
