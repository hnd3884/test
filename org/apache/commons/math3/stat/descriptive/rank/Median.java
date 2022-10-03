package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.KthSelector;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

public class Median extends Percentile implements Serializable
{
    private static final long serialVersionUID = -3961477041290915687L;
    private static final double FIXED_QUANTILE_50 = 50.0;
    
    public Median() {
        super(50.0);
    }
    
    public Median(final Median original) throws NullArgumentException {
        super(original);
    }
    
    private Median(final EstimationType estimationType, final NaNStrategy nanStrategy, final KthSelector kthSelector) throws MathIllegalArgumentException {
        super(50.0, estimationType, nanStrategy, kthSelector);
    }
    
    @Override
    public Median withEstimationType(final EstimationType newEstimationType) {
        return new Median(newEstimationType, this.getNaNStrategy(), this.getKthSelector());
    }
    
    @Override
    public Median withNaNStrategy(final NaNStrategy newNaNStrategy) {
        return new Median(this.getEstimationType(), newNaNStrategy, this.getKthSelector());
    }
    
    @Override
    public Median withKthSelector(final KthSelector newKthSelector) {
        return new Median(this.getEstimationType(), this.getNaNStrategy(), newKthSelector);
    }
}
