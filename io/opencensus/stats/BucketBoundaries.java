package io.opencensus.stats;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import io.opencensus.internal.Utils;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class BucketBoundaries
{
    private static final Logger logger;
    
    public static final BucketBoundaries create(final List<Double> bucketBoundaries) {
        Utils.checkNotNull(bucketBoundaries, "bucketBoundaries");
        final List<Double> bucketBoundariesCopy = new ArrayList<Double>(bucketBoundaries);
        if (bucketBoundariesCopy.size() > 1) {
            double previous = bucketBoundariesCopy.get(0);
            for (int i = 1; i < bucketBoundariesCopy.size(); ++i) {
                final double next = bucketBoundariesCopy.get(i);
                Utils.checkArgument(previous < next, (Object)"Bucket boundaries not sorted.");
                previous = next;
            }
        }
        return new AutoValue_BucketBoundaries(Collections.unmodifiableList((List<? extends Double>)dropNegativeBucketBounds(bucketBoundariesCopy)));
    }
    
    private static List<Double> dropNegativeBucketBounds(final List<Double> bucketBoundaries) {
        int negativeBucketBounds = 0;
        int zeroBucketBounds = 0;
        for (final Double value : bucketBoundaries) {
            if (value > 0.0) {
                break;
            }
            if (value == 0.0) {
                ++zeroBucketBounds;
            }
            else {
                ++negativeBucketBounds;
            }
        }
        if (negativeBucketBounds > 0) {
            BucketBoundaries.logger.log(Level.WARNING, "Dropping " + negativeBucketBounds + " negative bucket boundaries, the values must be strictly > 0.");
        }
        return bucketBoundaries.subList(negativeBucketBounds + zeroBucketBounds, bucketBoundaries.size());
    }
    
    public abstract List<Double> getBoundaries();
    
    static {
        logger = Logger.getLogger(BucketBoundaries.class.getName());
    }
}
