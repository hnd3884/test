package org.apache.commons.math3.stat.descriptive.rank;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import java.util.BitSet;
import org.apache.commons.math3.util.Precision;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.util.Arrays;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.util.PivotingStrategyInterface;
import org.apache.commons.math3.util.MedianOf3PivotingStrategy;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.util.KthSelector;
import java.io.Serializable;
import org.apache.commons.math3.stat.descriptive.AbstractUnivariateStatistic;

public class Percentile extends AbstractUnivariateStatistic implements Serializable
{
    private static final long serialVersionUID = -8091216485095130416L;
    private static final int MAX_CACHED_LEVELS = 10;
    private static final int PIVOTS_HEAP_LENGTH = 512;
    private final KthSelector kthSelector;
    private final EstimationType estimationType;
    private final NaNStrategy nanStrategy;
    private double quantile;
    private int[] cachedPivots;
    
    public Percentile() {
        this(50.0);
    }
    
    public Percentile(final double quantile) throws MathIllegalArgumentException {
        this(quantile, EstimationType.LEGACY, NaNStrategy.REMOVED, new KthSelector(new MedianOf3PivotingStrategy()));
    }
    
    public Percentile(final Percentile original) throws NullArgumentException {
        MathUtils.checkNotNull(original);
        this.estimationType = original.getEstimationType();
        this.nanStrategy = original.getNaNStrategy();
        this.kthSelector = original.getKthSelector();
        this.setData(original.getDataRef());
        if (original.cachedPivots != null) {
            System.arraycopy(original.cachedPivots, 0, this.cachedPivots, 0, original.cachedPivots.length);
        }
        this.setQuantile(original.quantile);
    }
    
    protected Percentile(final double quantile, final EstimationType estimationType, final NaNStrategy nanStrategy, final KthSelector kthSelector) throws MathIllegalArgumentException {
        this.setQuantile(quantile);
        this.cachedPivots = null;
        MathUtils.checkNotNull(estimationType);
        MathUtils.checkNotNull(nanStrategy);
        MathUtils.checkNotNull(kthSelector);
        this.estimationType = estimationType;
        this.nanStrategy = nanStrategy;
        this.kthSelector = kthSelector;
    }
    
    @Override
    public void setData(final double[] values) {
        if (values == null) {
            this.cachedPivots = null;
        }
        else {
            Arrays.fill(this.cachedPivots = new int[512], -1);
        }
        super.setData(values);
    }
    
    @Override
    public void setData(final double[] values, final int begin, final int length) throws MathIllegalArgumentException {
        if (values == null) {
            this.cachedPivots = null;
        }
        else {
            Arrays.fill(this.cachedPivots = new int[512], -1);
        }
        super.setData(values, begin, length);
    }
    
    public double evaluate(final double p) throws MathIllegalArgumentException {
        return this.evaluate(this.getDataRef(), p);
    }
    
    public double evaluate(final double[] values, final double p) throws MathIllegalArgumentException {
        this.test(values, 0, 0);
        return this.evaluate(values, 0, values.length, p);
    }
    
    @Override
    public double evaluate(final double[] values, final int start, final int length) throws MathIllegalArgumentException {
        return this.evaluate(values, start, length, this.quantile);
    }
    
    public double evaluate(final double[] values, final int begin, final int length, final double p) throws MathIllegalArgumentException {
        this.test(values, begin, length);
        if (p > 100.0 || p <= 0.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p, 0, 100);
        }
        if (length == 0) {
            return Double.NaN;
        }
        if (length == 1) {
            return values[begin];
        }
        final double[] work = this.getWorkArray(values, begin, length);
        final int[] pivotsHeap = this.getPivots(values);
        return (work.length == 0) ? Double.NaN : this.estimationType.evaluate(work, pivotsHeap, p, this.kthSelector);
    }
    
    @Deprecated
    int medianOf3(final double[] work, final int begin, final int end) {
        return new MedianOf3PivotingStrategy().pivotIndex(work, begin, end);
    }
    
    public double getQuantile() {
        return this.quantile;
    }
    
    public void setQuantile(final double p) throws MathIllegalArgumentException {
        if (p <= 0.0 || p > 100.0) {
            throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p, 0, 100);
        }
        this.quantile = p;
    }
    
    @Override
    public Percentile copy() {
        return new Percentile(this);
    }
    
    @Deprecated
    public static void copy(final Percentile source, final Percentile dest) throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
    
    protected double[] getWorkArray(final double[] values, final int begin, final int length) {
        double[] work = null;
        if (values == this.getDataRef()) {
            work = this.getDataRef();
        }
        else {
            switch (this.nanStrategy) {
                case MAXIMAL: {
                    work = replaceAndSlice(values, begin, length, Double.NaN, Double.POSITIVE_INFINITY);
                    break;
                }
                case MINIMAL: {
                    work = replaceAndSlice(values, begin, length, Double.NaN, Double.NEGATIVE_INFINITY);
                    break;
                }
                case REMOVED: {
                    work = removeAndSlice(values, begin, length, Double.NaN);
                    break;
                }
                case FAILED: {
                    work = copyOf(values, begin, length);
                    MathArrays.checkNotNaN(work);
                    break;
                }
                default: {
                    work = copyOf(values, begin, length);
                    break;
                }
            }
        }
        return work;
    }
    
    private static double[] copyOf(final double[] values, final int begin, final int length) {
        MathArrays.verifyValues(values, begin, length);
        return MathArrays.copyOfRange(values, begin, begin + length);
    }
    
    private static double[] replaceAndSlice(final double[] values, final int begin, final int length, final double original, final double replacement) {
        final double[] temp = copyOf(values, begin, length);
        for (int i = 0; i < length; ++i) {
            temp[i] = (Precision.equalsIncludingNaN(original, temp[i]) ? replacement : temp[i]);
        }
        return temp;
    }
    
    private static double[] removeAndSlice(final double[] values, final int begin, final int length, final double removedValue) {
        MathArrays.verifyValues(values, begin, length);
        final BitSet bits = new BitSet(length);
        for (int i = begin; i < begin + length; ++i) {
            if (Precision.equalsIncludingNaN(removedValue, values[i])) {
                bits.set(i - begin);
            }
        }
        double[] temp;
        if (bits.isEmpty()) {
            temp = copyOf(values, begin, length);
        }
        else if (bits.cardinality() == length) {
            temp = new double[0];
        }
        else {
            temp = new double[length - bits.cardinality()];
            int start = begin;
            int dest = 0;
            for (int nextOne = -1, bitSetPtr = 0; (nextOne = bits.nextSetBit(bitSetPtr)) != -1; start = begin + (bitSetPtr = bits.nextClearBit(nextOne))) {
                final int lengthToCopy = nextOne - bitSetPtr;
                System.arraycopy(values, start, temp, dest, lengthToCopy);
                dest += lengthToCopy;
            }
            if (start < begin + length) {
                System.arraycopy(values, start, temp, dest, begin + length - start);
            }
        }
        return temp;
    }
    
    private int[] getPivots(final double[] values) {
        int[] pivotsHeap;
        if (values == this.getDataRef()) {
            pivotsHeap = this.cachedPivots;
        }
        else {
            pivotsHeap = new int[512];
            Arrays.fill(pivotsHeap, -1);
        }
        return pivotsHeap;
    }
    
    public EstimationType getEstimationType() {
        return this.estimationType;
    }
    
    public Percentile withEstimationType(final EstimationType newEstimationType) {
        return new Percentile(this.quantile, newEstimationType, this.nanStrategy, this.kthSelector);
    }
    
    public NaNStrategy getNaNStrategy() {
        return this.nanStrategy;
    }
    
    public Percentile withNaNStrategy(final NaNStrategy newNaNStrategy) {
        return new Percentile(this.quantile, this.estimationType, newNaNStrategy, this.kthSelector);
    }
    
    public KthSelector getKthSelector() {
        return this.kthSelector;
    }
    
    public PivotingStrategyInterface getPivotingStrategy() {
        return this.kthSelector.getPivotingStrategy();
    }
    
    public Percentile withKthSelector(final KthSelector newKthSelector) {
        return new Percentile(this.quantile, this.estimationType, this.nanStrategy, newKthSelector);
    }
    
    public enum EstimationType
    {
        LEGACY("Legacy Apache Commons Math") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.0;
                final double maxLimit = 1.0;
                return (Double.compare(p, 0.0) == 0) ? 0.0 : ((Double.compare(p, 1.0) == 0) ? length : (p * (length + 1)));
            }
        }, 
        R_1("R-1") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.0;
                return (Double.compare(p, 0.0) == 0) ? 0.0 : (length * p + 0.5);
            }
            
            @Override
            protected double estimate(final double[] values, final int[] pivotsHeap, final double pos, final int length, final KthSelector selector) {
                return super.estimate(values, pivotsHeap, FastMath.ceil(pos - 0.5), length, selector);
            }
        }, 
        R_2("R-2") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.0;
                final double maxLimit = 1.0;
                return (Double.compare(p, 1.0) == 0) ? length : ((Double.compare(p, 0.0) == 0) ? 0.0 : (length * p + 0.5));
            }
            
            @Override
            protected double estimate(final double[] values, final int[] pivotsHeap, final double pos, final int length, final KthSelector selector) {
                final double low = super.estimate(values, pivotsHeap, FastMath.ceil(pos - 0.5), length, selector);
                final double high = super.estimate(values, pivotsHeap, FastMath.floor(pos + 0.5), length, selector);
                return (low + high) / 2.0;
            }
        }, 
        R_3("R-3") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.5 / length;
                return (Double.compare(p, minLimit) <= 0) ? 0.0 : FastMath.rint(length * p);
            }
        }, 
        R_4("R-4") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1.0 / length;
                final double maxLimit = 1.0;
                return (Double.compare(p, minLimit) < 0) ? 0.0 : ((Double.compare(p, 1.0) == 0) ? length : (length * p));
            }
        }, 
        R_5("R-5") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.5 / length;
                final double maxLimit = (length - 0.5) / length;
                return (Double.compare(p, minLimit) < 0) ? 0.0 : ((Double.compare(p, maxLimit) >= 0) ? length : (length * p + 0.5));
            }
        }, 
        R_6("R-6") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 1.0 / (length + 1);
                final double maxLimit = 1.0 * length / (length + 1);
                return (Double.compare(p, minLimit) < 0) ? 0.0 : ((Double.compare(p, maxLimit) >= 0) ? length : ((length + 1) * p));
            }
        }, 
        R_7("R-7") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.0;
                final double maxLimit = 1.0;
                return (Double.compare(p, 0.0) == 0) ? 0.0 : ((Double.compare(p, 1.0) == 0) ? length : (1.0 + (length - 1) * p));
            }
        }, 
        R_8("R-8") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.6666666666666666 / (length + 0.3333333333333333);
                final double maxLimit = (length - 0.3333333333333333) / (length + 0.3333333333333333);
                return (Double.compare(p, minLimit) < 0) ? 0.0 : ((Double.compare(p, maxLimit) >= 0) ? length : ((length + 0.3333333333333333) * p + 0.3333333333333333));
            }
        }, 
        R_9("R-9") {
            @Override
            protected double index(final double p, final int length) {
                final double minLimit = 0.625 / (length + 0.25);
                final double maxLimit = (length - 0.375) / (length + 0.25);
                return (Double.compare(p, minLimit) < 0) ? 0.0 : ((Double.compare(p, maxLimit) >= 0) ? length : ((length + 0.25) * p + 0.375));
            }
        };
        
        private final String name;
        
        private EstimationType(final String type) {
            this.name = type;
        }
        
        protected abstract double index(final double p0, final int p1);
        
        protected double estimate(final double[] work, final int[] pivotsHeap, final double pos, final int length, final KthSelector selector) {
            final double fpos = FastMath.floor(pos);
            final int intPos = (int)fpos;
            final double dif = pos - fpos;
            if (pos < 1.0) {
                return selector.select(work, pivotsHeap, 0);
            }
            if (pos >= length) {
                return selector.select(work, pivotsHeap, length - 1);
            }
            final double lower = selector.select(work, pivotsHeap, intPos - 1);
            final double upper = selector.select(work, pivotsHeap, intPos);
            return lower + dif * (upper - lower);
        }
        
        protected double evaluate(final double[] work, final int[] pivotsHeap, final double p, final KthSelector selector) {
            MathUtils.checkNotNull(work);
            if (p > 100.0 || p <= 0.0) {
                throw new OutOfRangeException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, p, 0, 100);
            }
            return this.estimate(work, pivotsHeap, this.index(p / 100.0, work.length), work.length, selector);
        }
        
        public double evaluate(final double[] work, final double p, final KthSelector selector) {
            return this.evaluate(work, null, p, selector);
        }
        
        String getName() {
            return this.name;
        }
    }
}
