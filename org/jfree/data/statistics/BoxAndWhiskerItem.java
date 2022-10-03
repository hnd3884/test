package org.jfree.data.statistics;

import org.jfree.util.ObjectUtilities;
import java.util.Collections;
import java.util.List;
import java.io.Serializable;

public class BoxAndWhiskerItem implements Serializable
{
    private static final long serialVersionUID = 7329649623148167423L;
    private Number mean;
    private Number median;
    private Number q1;
    private Number q3;
    private Number minRegularValue;
    private Number maxRegularValue;
    private Number minOutlier;
    private Number maxOutlier;
    private List outliers;
    
    public BoxAndWhiskerItem(final Number mean, final Number median, final Number q1, final Number q3, final Number minRegularValue, final Number maxRegularValue, final Number minOutlier, final Number maxOutlier, final List outliers) {
        this.mean = mean;
        this.median = median;
        this.q1 = q1;
        this.q3 = q3;
        this.minRegularValue = minRegularValue;
        this.maxRegularValue = maxRegularValue;
        this.minOutlier = minOutlier;
        this.maxOutlier = maxOutlier;
        this.outliers = outliers;
    }
    
    public Number getMean() {
        return this.mean;
    }
    
    public Number getMedian() {
        return this.median;
    }
    
    public Number getQ1() {
        return this.q1;
    }
    
    public Number getQ3() {
        return this.q3;
    }
    
    public Number getMinRegularValue() {
        return this.minRegularValue;
    }
    
    public Number getMaxRegularValue() {
        return this.maxRegularValue;
    }
    
    public Number getMinOutlier() {
        return this.minOutlier;
    }
    
    public Number getMaxOutlier() {
        return this.maxOutlier;
    }
    
    public List getOutliers() {
        if (this.outliers == null) {
            return null;
        }
        return Collections.unmodifiableList((List<?>)this.outliers);
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BoxAndWhiskerItem)) {
            return false;
        }
        final BoxAndWhiskerItem that = (BoxAndWhiskerItem)obj;
        return ObjectUtilities.equal((Object)this.mean, (Object)that.mean) && ObjectUtilities.equal((Object)this.median, (Object)that.median) && ObjectUtilities.equal((Object)this.q1, (Object)that.q1) && ObjectUtilities.equal((Object)this.q3, (Object)that.q3) && ObjectUtilities.equal((Object)this.minRegularValue, (Object)that.minRegularValue) && ObjectUtilities.equal((Object)this.maxRegularValue, (Object)that.maxRegularValue) && ObjectUtilities.equal((Object)this.minOutlier, (Object)that.minOutlier) && ObjectUtilities.equal((Object)this.maxOutlier, (Object)that.maxOutlier) && ObjectUtilities.equal((Object)this.outliers, (Object)that.outliers);
    }
}
