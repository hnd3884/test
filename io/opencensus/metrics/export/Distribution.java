package io.opencensus.metrics.export;

import io.opencensus.metrics.data.Exemplar;
import io.opencensus.common.Function;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Collection;
import io.opencensus.internal.Utils;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class Distribution
{
    Distribution() {
    }
    
    public static Distribution create(final long count, final double sum, final double sumOfSquaredDeviations, final BucketOptions bucketOptions, final List<Bucket> buckets) {
        Utils.checkArgument(count >= 0L, (Object)"count should be non-negative.");
        Utils.checkArgument(sumOfSquaredDeviations >= 0.0, (Object)"sum of squared deviations should be non-negative.");
        if (count == 0L) {
            Utils.checkArgument(sum == 0.0, (Object)"sum should be 0 if count is 0.");
            Utils.checkArgument(sumOfSquaredDeviations == 0.0, (Object)"sum of squared deviations should be 0 if count is 0.");
        }
        Utils.checkNotNull(bucketOptions, "bucketOptions");
        final List<Bucket> bucketsCopy = Collections.unmodifiableList((List<? extends Bucket>)new ArrayList<Bucket>(Utils.checkNotNull(buckets, "buckets")));
        Utils.checkListElementNotNull(bucketsCopy, "bucket");
        return new AutoValue_Distribution(count, sum, sumOfSquaredDeviations, bucketOptions, bucketsCopy);
    }
    
    public abstract long getCount();
    
    public abstract double getSum();
    
    public abstract double getSumOfSquaredDeviations();
    
    @Nullable
    public abstract BucketOptions getBucketOptions();
    
    public abstract List<Bucket> getBuckets();
    
    @Immutable
    public abstract static class BucketOptions
    {
        private BucketOptions() {
        }
        
        public static BucketOptions explicitOptions(final List<Double> bucketBoundaries) {
            return create(bucketBoundaries);
        }
        
        public abstract <T> T match(final Function<? super ExplicitOptions, T> p0, final Function<? super BucketOptions, T> p1);
        
        @Immutable
        public abstract static class ExplicitOptions extends BucketOptions
        {
            ExplicitOptions() {
            }
            
            @Override
            public final <T> T match(final Function<? super ExplicitOptions, T> explicitFunction, final Function<? super BucketOptions, T> defaultFunction) {
                return explicitFunction.apply(this);
            }
            
            private static ExplicitOptions create(final List<Double> bucketBoundaries) {
                Utils.checkNotNull(bucketBoundaries, "bucketBoundaries");
                final List<Double> bucketBoundariesCopy = Collections.unmodifiableList((List<? extends Double>)new ArrayList<Double>(bucketBoundaries));
                checkBucketBoundsAreSorted(bucketBoundariesCopy);
                return new AutoValue_Distribution_BucketOptions_ExplicitOptions(bucketBoundariesCopy);
            }
            
            private static void checkBucketBoundsAreSorted(final List<Double> bucketBoundaries) {
                if (bucketBoundaries.size() >= 1) {
                    double previous = Utils.checkNotNull(bucketBoundaries.get(0), "bucketBoundary");
                    Utils.checkArgument(previous > 0.0, (Object)"bucket boundary should be > 0");
                    for (int i = 1; i < bucketBoundaries.size(); ++i) {
                        final double next = Utils.checkNotNull(bucketBoundaries.get(i), "bucketBoundary");
                        Utils.checkArgument(previous < next, (Object)"bucket boundaries not sorted.");
                        previous = next;
                    }
                }
            }
            
            public abstract List<Double> getBucketBoundaries();
        }
    }
    
    @Immutable
    public abstract static class Bucket
    {
        Bucket() {
        }
        
        public static Bucket create(final long count) {
            Utils.checkArgument(count >= 0L, (Object)"bucket count should be non-negative.");
            return new AutoValue_Distribution_Bucket(count, null);
        }
        
        public static Bucket create(final long count, final Exemplar exemplar) {
            Utils.checkArgument(count >= 0L, (Object)"bucket count should be non-negative.");
            Utils.checkNotNull(exemplar, "exemplar");
            return new AutoValue_Distribution_Bucket(count, exemplar);
        }
        
        public abstract long getCount();
        
        @Nullable
        public abstract Exemplar getExemplar();
    }
}
