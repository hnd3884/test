package io.opencensus.stats;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Collection;
import io.opencensus.common.Function;
import io.opencensus.common.Functions;
import java.util.Collections;
import java.util.HashMap;
import javax.annotation.Nullable;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;
import java.util.Map;
import io.opencensus.common.Timestamp;
import java.util.logging.Level;
import io.opencensus.tags.TagContext;
import java.util.logging.Logger;
import javax.annotation.concurrent.Immutable;
import io.opencensus.internal.Utils;
import javax.annotation.concurrent.ThreadSafe;

final class NoopStats
{
    private NoopStats() {
    }
    
    static StatsComponent newNoopStatsComponent() {
        return new NoopStatsComponent();
    }
    
    static StatsRecorder getNoopStatsRecorder() {
        return NoopStatsRecorder.INSTANCE;
    }
    
    static MeasureMap newNoopMeasureMap() {
        return new NoopMeasureMap();
    }
    
    static ViewManager newNoopViewManager() {
        return new NoopViewManager();
    }
    
    @ThreadSafe
    private static final class NoopStatsComponent extends StatsComponent
    {
        private final ViewManager viewManager;
        private volatile boolean isRead;
        
        private NoopStatsComponent() {
            this.viewManager = NoopStats.newNoopViewManager();
        }
        
        @Override
        public ViewManager getViewManager() {
            return this.viewManager;
        }
        
        @Override
        public StatsRecorder getStatsRecorder() {
            return NoopStats.getNoopStatsRecorder();
        }
        
        @Override
        public StatsCollectionState getState() {
            this.isRead = true;
            return StatsCollectionState.DISABLED;
        }
        
        @Deprecated
        @Override
        public void setState(final StatsCollectionState state) {
            Utils.checkNotNull(state, "state");
            Utils.checkState(!this.isRead, "State was already read, cannot set state.");
        }
    }
    
    @Immutable
    private static final class NoopStatsRecorder extends StatsRecorder
    {
        static final StatsRecorder INSTANCE;
        
        @Override
        public MeasureMap newMeasureMap() {
            return NoopStats.newNoopMeasureMap();
        }
        
        static {
            INSTANCE = new NoopStatsRecorder();
        }
    }
    
    private static final class NoopMeasureMap extends MeasureMap
    {
        private static final Logger logger;
        private boolean hasUnsupportedValues;
        
        @Override
        public MeasureMap put(final Measure.MeasureDouble measure, final double value) {
            if (value < 0.0) {
                this.hasUnsupportedValues = true;
            }
            return this;
        }
        
        @Override
        public MeasureMap put(final Measure.MeasureLong measure, final long value) {
            if (value < 0L) {
                this.hasUnsupportedValues = true;
            }
            return this;
        }
        
        @Override
        public void record() {
        }
        
        @Override
        public void record(final TagContext tags) {
            Utils.checkNotNull(tags, "tags");
            if (this.hasUnsupportedValues) {
                NoopMeasureMap.logger.log(Level.WARNING, "Dropping values, value to record must be non-negative.");
            }
        }
        
        static {
            logger = Logger.getLogger(NoopMeasureMap.class.getName());
        }
    }
    
    @ThreadSafe
    private static final class NoopViewManager extends ViewManager
    {
        private static final Timestamp ZERO_TIMESTAMP;
        @GuardedBy("registeredViews")
        private final Map<View.Name, View> registeredViews;
        @Nullable
        private volatile Set<View> exportedViews;
        
        private NoopViewManager() {
            this.registeredViews = new HashMap<View.Name, View>();
        }
        
        @Override
        public void registerView(final View newView) {
            Utils.checkNotNull(newView, "newView");
            synchronized (this.registeredViews) {
                this.exportedViews = null;
                final View existing = this.registeredViews.get(newView.getName());
                Utils.checkArgument(existing == null || newView.equals(existing), (Object)"A different view with the same name already exists.");
                if (existing == null) {
                    this.registeredViews.put(newView.getName(), newView);
                }
            }
        }
        
        @Nullable
        @Override
        public ViewData getView(final View.Name name) {
            Utils.checkNotNull(name, "name");
            synchronized (this.registeredViews) {
                final View view = this.registeredViews.get(name);
                if (view == null) {
                    return null;
                }
                return ViewData.create(view, Collections.emptyMap(), view.getWindow().match((Function<? super View.AggregationWindow.Cumulative, ViewData.AggregationWindowData>)Functions.returnConstant((T)ViewData.AggregationWindowData.CumulativeData.create(NoopViewManager.ZERO_TIMESTAMP, NoopViewManager.ZERO_TIMESTAMP)), (Function<? super View.AggregationWindow.Interval, ViewData.AggregationWindowData>)Functions.returnConstant((T)ViewData.AggregationWindowData.IntervalData.create(NoopViewManager.ZERO_TIMESTAMP)), Functions.throwAssertionError()));
            }
        }
        
        @Override
        public Set<View> getAllExportedViews() {
            Set<View> views = this.exportedViews;
            if (views == null) {
                synchronized (this.registeredViews) {
                    views = (this.exportedViews = filterExportedViews(this.registeredViews.values()));
                }
            }
            return views;
        }
        
        private static Set<View> filterExportedViews(final Collection<View> allViews) {
            final Set<View> views = new HashSet<View>();
            for (final View view : allViews) {
                if (view.getWindow() instanceof View.AggregationWindow.Interval) {
                    continue;
                }
                views.add(view);
            }
            return Collections.unmodifiableSet((Set<? extends View>)views);
        }
        
        static {
            ZERO_TIMESTAMP = Timestamp.create(0L, 0);
        }
    }
}
