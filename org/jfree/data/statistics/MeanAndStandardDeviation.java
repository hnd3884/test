package org.jfree.data.statistics;

import org.jfree.util.ObjectUtilities;
import java.io.Serializable;

public class MeanAndStandardDeviation implements Serializable
{
    private static final long serialVersionUID = 7413468697315721515L;
    private Number mean;
    private Number standardDeviation;
    
    public MeanAndStandardDeviation(final double mean, final double standardDeviation) {
        this(new Double(mean), new Double(standardDeviation));
    }
    
    public MeanAndStandardDeviation(final Number mean, final Number standardDeviation) {
        this.mean = mean;
        this.standardDeviation = standardDeviation;
    }
    
    public Number getMean() {
        return this.mean;
    }
    
    public Number getStandardDeviation() {
        return this.standardDeviation;
    }
    
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MeanAndStandardDeviation)) {
            return false;
        }
        final MeanAndStandardDeviation that = (MeanAndStandardDeviation)obj;
        return ObjectUtilities.equal((Object)this.mean, (Object)that.mean) && ObjectUtilities.equal((Object)this.standardDeviation, (Object)that.standardDeviation);
    }
}
