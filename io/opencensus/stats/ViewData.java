package io.opencensus.stats;

import java.util.Iterator;
import io.opencensus.common.Functions;
import io.opencensus.common.Duration;
import io.opencensus.common.Function;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import io.opencensus.common.Timestamp;
import io.opencensus.tags.TagValue;
import java.util.List;
import java.util.Map;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ViewData
{
    ViewData() {
    }
    
    public abstract View getView();
    
    public abstract Map<List<TagValue>, AggregationData> getAggregationMap();
    
    @Deprecated
    public abstract AggregationWindowData getWindowData();
    
    public abstract Timestamp getStart();
    
    public abstract Timestamp getEnd();
    
    @Deprecated
    public static ViewData create(final View view, final Map<? extends List<TagValue>, ? extends AggregationData> map, final AggregationWindowData windowData) {
        checkWindow(view.getWindow(), windowData);
        final Map<List<TagValue>, AggregationData> deepCopy = new HashMap<List<TagValue>, AggregationData>();
        for (final Map.Entry<? extends List<TagValue>, ? extends AggregationData> entry : map.entrySet()) {
            checkAggregation(view.getAggregation(), (AggregationData)entry.getValue(), view.getMeasure());
            deepCopy.put(Collections.unmodifiableList((List<? extends TagValue>)new ArrayList<TagValue>((Collection<? extends TagValue>)entry.getKey())), (AggregationData)entry.getValue());
        }
        return windowData.match(new Function<AggregationWindowData.CumulativeData, ViewData>() {
            @Override
            public ViewData apply(final AggregationWindowData.CumulativeData arg) {
                return createInternal(view, Collections.unmodifiableMap((Map<?, ?>)deepCopy), arg, arg.getStart(), arg.getEnd());
            }
        }, new Function<AggregationWindowData.IntervalData, ViewData>() {
            @Override
            public ViewData apply(final AggregationWindowData.IntervalData arg) {
                final Duration duration = ((View.AggregationWindow.Interval)view.getWindow()).getDuration();
                return createInternal(view, Collections.unmodifiableMap((Map<?, ?>)deepCopy), arg, arg.getEnd().addDuration(Duration.create(-duration.getSeconds(), -duration.getNanos())), arg.getEnd());
            }
        }, Functions.throwAssertionError());
    }
    
    public static ViewData create(final View view, final Map<? extends List<TagValue>, ? extends AggregationData> map, final Timestamp start, final Timestamp end) {
        final Map<List<TagValue>, AggregationData> deepCopy = new HashMap<List<TagValue>, AggregationData>();
        for (final Map.Entry<? extends List<TagValue>, ? extends AggregationData> entry : map.entrySet()) {
            checkAggregation(view.getAggregation(), (AggregationData)entry.getValue(), view.getMeasure());
            deepCopy.put(Collections.unmodifiableList((List<? extends TagValue>)new ArrayList<TagValue>((Collection<? extends TagValue>)entry.getKey())), (AggregationData)entry.getValue());
        }
        return createInternal(view, Collections.unmodifiableMap((Map<? extends List<TagValue>, ? extends AggregationData>)deepCopy), AggregationWindowData.CumulativeData.create(start, end), start, end);
    }
    
    private static ViewData createInternal(final View view, final Map<List<TagValue>, AggregationData> aggregationMap, final AggregationWindowData window, final Timestamp start, final Timestamp end) {
        final Map<List<TagValue>, AggregationData> map = aggregationMap;
        return new AutoValue_ViewData(view, map, window, start, end);
    }
    
    private static void checkWindow(final View.AggregationWindow window, final AggregationWindowData windowData) {
        window.match((Function<? super View.AggregationWindow.Cumulative, Object>)new Function<View.AggregationWindow.Cumulative, Void>() {
            @Override
            public Void apply(final View.AggregationWindow.Cumulative arg) {
                throwIfWindowMismatch(windowData instanceof AggregationWindowData.CumulativeData, arg, windowData);
                return null;
            }
        }, (Function<? super View.AggregationWindow.Interval, Object>)new Function<View.AggregationWindow.Interval, Void>() {
            @Override
            public Void apply(final View.AggregationWindow.Interval arg) {
                throwIfWindowMismatch(windowData instanceof AggregationWindowData.IntervalData, arg, windowData);
                return null;
            }
        }, Functions.throwAssertionError());
    }
    
    private static void throwIfWindowMismatch(final boolean isValid, final View.AggregationWindow window, final AggregationWindowData windowData) {
        if (!isValid) {
            throw new IllegalArgumentException(createErrorMessageForWindow(window, windowData));
        }
    }
    
    private static String createErrorMessageForWindow(final View.AggregationWindow window, final AggregationWindowData windowData) {
        return "AggregationWindow and AggregationWindowData types mismatch. AggregationWindow: " + window.getClass().getSimpleName() + " AggregationWindowData: " + windowData.getClass().getSimpleName();
    }
    
    private static void checkAggregation(final Aggregation aggregation, final AggregationData aggregationData, final Measure measure) {
        aggregation.match((Function<? super Aggregation.Sum, Object>)new Function<Aggregation.Sum, Void>() {
            @Override
            public Void apply(final Aggregation.Sum arg) {
                measure.match((Function<? super Measure.MeasureDouble, Object>)new Function<Measure.MeasureDouble, Void>() {
                    @Override
                    public Void apply(final Measure.MeasureDouble arg) {
                        throwIfAggregationMismatch(aggregationData instanceof AggregationData.SumDataDouble, aggregation, aggregationData);
                        return null;
                    }
                }, (Function<? super Measure.MeasureLong, Object>)new Function<Measure.MeasureLong, Void>() {
                    @Override
                    public Void apply(final Measure.MeasureLong arg) {
                        throwIfAggregationMismatch(aggregationData instanceof AggregationData.SumDataLong, aggregation, aggregationData);
                        return null;
                    }
                }, Functions.throwAssertionError());
                return null;
            }
        }, (Function<? super Aggregation.Count, Object>)new Function<Aggregation.Count, Void>() {
            @Override
            public Void apply(final Aggregation.Count arg) {
                throwIfAggregationMismatch(aggregationData instanceof AggregationData.CountData, aggregation, aggregationData);
                return null;
            }
        }, (Function<? super Aggregation.Distribution, Object>)new Function<Aggregation.Distribution, Void>() {
            @Override
            public Void apply(final Aggregation.Distribution arg) {
                throwIfAggregationMismatch(aggregationData instanceof AggregationData.DistributionData, aggregation, aggregationData);
                return null;
            }
        }, (Function<? super Aggregation.LastValue, Object>)new Function<Aggregation.LastValue, Void>() {
            @Override
            public Void apply(final Aggregation.LastValue arg) {
                measure.match((Function<? super Measure.MeasureDouble, Object>)new Function<Measure.MeasureDouble, Void>() {
                    @Override
                    public Void apply(final Measure.MeasureDouble arg) {
                        throwIfAggregationMismatch(aggregationData instanceof AggregationData.LastValueDataDouble, aggregation, aggregationData);
                        return null;
                    }
                }, (Function<? super Measure.MeasureLong, Object>)new Function<Measure.MeasureLong, Void>() {
                    @Override
                    public Void apply(final Measure.MeasureLong arg) {
                        throwIfAggregationMismatch(aggregationData instanceof AggregationData.LastValueDataLong, aggregation, aggregationData);
                        return null;
                    }
                }, Functions.throwAssertionError());
                return null;
            }
        }, (Function<? super Aggregation, Object>)new Function<Aggregation, Void>() {
            @Override
            public Void apply(final Aggregation arg) {
                if (arg instanceof Aggregation.Mean) {
                    throwIfAggregationMismatch(aggregationData instanceof AggregationData.MeanData, aggregation, aggregationData);
                    return null;
                }
                throw new AssertionError();
            }
        });
    }
    
    private static void throwIfAggregationMismatch(final boolean isValid, final Aggregation aggregation, final AggregationData aggregationData) {
        if (!isValid) {
            throw new IllegalArgumentException(createErrorMessageForAggregation(aggregation, aggregationData));
        }
    }
    
    private static String createErrorMessageForAggregation(final Aggregation aggregation, final AggregationData aggregationData) {
        return "Aggregation and AggregationData types mismatch. Aggregation: " + aggregation.getClass().getSimpleName() + " AggregationData: " + aggregationData.getClass().getSimpleName();
    }
    
    @Deprecated
    @Immutable
    public abstract static class AggregationWindowData
    {
        private AggregationWindowData() {
        }
        
        public abstract <T> T match(final Function<? super CumulativeData, T> p0, final Function<? super IntervalData, T> p1, final Function<? super AggregationWindowData, T> p2);
        
        @Deprecated
        @Immutable
        public abstract static class CumulativeData extends AggregationWindowData
        {
            CumulativeData() {
            }
            
            public abstract Timestamp getStart();
            
            public abstract Timestamp getEnd();
            
            @Override
            public final <T> T match(final Function<? super CumulativeData, T> p0, final Function<? super IntervalData, T> p1, final Function<? super AggregationWindowData, T> defaultFunction) {
                return p0.apply(this);
            }
            
            public static CumulativeData create(final Timestamp start, final Timestamp end) {
                if (start.compareTo(end) > 0) {
                    throw new IllegalArgumentException("Start time is later than end time.");
                }
                return new AutoValue_ViewData_AggregationWindowData_CumulativeData(start, end);
            }
        }
        
        @Deprecated
        @Immutable
        public abstract static class IntervalData extends AggregationWindowData
        {
            IntervalData() {
            }
            
            public abstract Timestamp getEnd();
            
            @Override
            public final <T> T match(final Function<? super CumulativeData, T> p0, final Function<? super IntervalData, T> p1, final Function<? super AggregationWindowData, T> defaultFunction) {
                return p1.apply(this);
            }
            
            public static IntervalData create(final Timestamp end) {
                return new AutoValue_ViewData_AggregationWindowData_IntervalData(end);
            }
        }
    }
}
