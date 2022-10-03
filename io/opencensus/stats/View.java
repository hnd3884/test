package io.opencensus.stats;

import io.opencensus.common.Duration;
import io.opencensus.common.Function;
import io.opencensus.internal.StringUtils;
import java.util.Collections;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import io.opencensus.tags.TagKey;
import java.util.Comparator;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class View
{
    static final int NAME_MAX_LENGTH = 255;
    private static final Comparator<TagKey> TAG_KEY_COMPARATOR;
    
    View() {
    }
    
    public abstract Name getName();
    
    public abstract String getDescription();
    
    public abstract Measure getMeasure();
    
    public abstract Aggregation getAggregation();
    
    public abstract List<TagKey> getColumns();
    
    @Deprecated
    public abstract AggregationWindow getWindow();
    
    @Deprecated
    public static View create(final Name name, final String description, final Measure measure, final Aggregation aggregation, final List<TagKey> columns, final AggregationWindow window) {
        Utils.checkArgument(new HashSet(columns).size() == columns.size(), (Object)"Columns have duplicate.");
        final List<TagKey> tagKeys = new ArrayList<TagKey>(columns);
        Collections.sort(tagKeys, View.TAG_KEY_COMPARATOR);
        return new AutoValue_View(name, description, measure, aggregation, Collections.unmodifiableList((List<? extends TagKey>)tagKeys), window);
    }
    
    public static View create(final Name name, final String description, final Measure measure, final Aggregation aggregation, final List<TagKey> columns) {
        Utils.checkArgument(new HashSet(columns).size() == columns.size(), (Object)"Columns have duplicate.");
        return create(name, description, measure, aggregation, columns, AggregationWindow.Cumulative.create());
    }
    
    static {
        TAG_KEY_COMPARATOR = new Comparator<TagKey>() {
            @Override
            public int compare(final TagKey key1, final TagKey key2) {
                return key1.getName().compareToIgnoreCase(key2.getName());
            }
        };
    }
    
    @Immutable
    public abstract static class Name
    {
        Name() {
        }
        
        public abstract String asString();
        
        public static Name create(final String name) {
            Utils.checkArgument(StringUtils.isPrintableString(name) && name.length() <= 255, (Object)"Name should be a ASCII string with a length no greater than 255 characters.");
            return new AutoValue_View_Name(name);
        }
    }
    
    @Deprecated
    @Immutable
    public abstract static class AggregationWindow
    {
        private AggregationWindow() {
        }
        
        public abstract <T> T match(final Function<? super Cumulative, T> p0, final Function<? super Interval, T> p1, final Function<? super AggregationWindow, T> p2);
        
        @Deprecated
        @Immutable
        public abstract static class Cumulative extends AggregationWindow
        {
            private static final Cumulative CUMULATIVE;
            
            Cumulative() {
            }
            
            public static Cumulative create() {
                return Cumulative.CUMULATIVE;
            }
            
            @Override
            public final <T> T match(final Function<? super Cumulative, T> p0, final Function<? super Interval, T> p1, final Function<? super AggregationWindow, T> defaultFunction) {
                return p0.apply(this);
            }
            
            static {
                CUMULATIVE = new AutoValue_View_AggregationWindow_Cumulative();
            }
        }
        
        @Deprecated
        @Immutable
        public abstract static class Interval extends AggregationWindow
        {
            private static final Duration ZERO;
            
            Interval() {
            }
            
            public abstract Duration getDuration();
            
            public static Interval create(final Duration duration) {
                Utils.checkArgument(duration.compareTo(Interval.ZERO) > 0, (Object)"Duration must be positive");
                return new AutoValue_View_AggregationWindow_Interval(duration);
            }
            
            @Override
            public final <T> T match(final Function<? super Cumulative, T> p0, final Function<? super Interval, T> p1, final Function<? super AggregationWindow, T> defaultFunction) {
                return p1.apply(this);
            }
            
            static {
                ZERO = Duration.create(0L, 0);
            }
        }
    }
}
