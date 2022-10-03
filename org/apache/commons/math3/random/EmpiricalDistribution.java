package org.apache.commons.math3.random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.distribution.ConstantRealDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.util.FastMath;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.commons.math3.util.MathUtils;
import java.net.URL;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.IOException;
import org.apache.commons.math3.exception.MathInternalError;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import java.util.List;
import org.apache.commons.math3.distribution.AbstractRealDistribution;

public class EmpiricalDistribution extends AbstractRealDistribution
{
    public static final int DEFAULT_BIN_COUNT = 1000;
    private static final String FILE_CHARSET = "US-ASCII";
    private static final long serialVersionUID = 5729073523949762654L;
    protected final RandomDataGenerator randomData;
    private final List<SummaryStatistics> binStats;
    private SummaryStatistics sampleStats;
    private double max;
    private double min;
    private double delta;
    private final int binCount;
    private boolean loaded;
    private double[] upperBounds;
    
    public EmpiricalDistribution() {
        this(1000);
    }
    
    public EmpiricalDistribution(final int binCount) {
        this(binCount, new RandomDataGenerator());
    }
    
    public EmpiricalDistribution(final int binCount, final RandomGenerator generator) {
        this(binCount, new RandomDataGenerator(generator));
    }
    
    public EmpiricalDistribution(final RandomGenerator generator) {
        this(1000, generator);
    }
    
    @Deprecated
    public EmpiricalDistribution(final int binCount, final RandomDataImpl randomData) {
        this(binCount, randomData.getDelegate());
    }
    
    @Deprecated
    public EmpiricalDistribution(final RandomDataImpl randomData) {
        this(1000, randomData);
    }
    
    private EmpiricalDistribution(final int binCount, final RandomDataGenerator randomData) {
        super(randomData.getRandomGenerator());
        this.sampleStats = null;
        this.max = Double.NEGATIVE_INFINITY;
        this.min = Double.POSITIVE_INFINITY;
        this.delta = 0.0;
        this.loaded = false;
        this.upperBounds = null;
        if (binCount <= 0) {
            throw new NotStrictlyPositiveException(binCount);
        }
        this.binCount = binCount;
        this.randomData = randomData;
        this.binStats = new ArrayList<SummaryStatistics>();
    }
    
    public void load(final double[] in) throws NullArgumentException {
        final DataAdapter da = new ArrayDataAdapter(in);
        try {
            da.computeStats();
            this.fillBinStats(new ArrayDataAdapter(in));
        }
        catch (final IOException ex) {
            throw new MathInternalError();
        }
        this.loaded = true;
    }
    
    public void load(final URL url) throws IOException, NullArgumentException, ZeroException {
        MathUtils.checkNotNull(url);
        final Charset charset = Charset.forName("US-ASCII");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
        try {
            final DataAdapter da = new StreamDataAdapter(in);
            da.computeStats();
            if (this.sampleStats.getN() == 0L) {
                throw new ZeroException(LocalizedFormats.URL_CONTAINS_NO_DATA, new Object[] { url });
            }
            in = new BufferedReader(new InputStreamReader(url.openStream(), charset));
            this.fillBinStats(new StreamDataAdapter(in));
            this.loaded = true;
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    public void load(final File file) throws IOException, NullArgumentException {
        MathUtils.checkNotNull(file);
        final Charset charset = Charset.forName("US-ASCII");
        InputStream is = new FileInputStream(file);
        BufferedReader in = new BufferedReader(new InputStreamReader(is, charset));
        try {
            final DataAdapter da = new StreamDataAdapter(in);
            da.computeStats();
            is = new FileInputStream(file);
            in = new BufferedReader(new InputStreamReader(is, charset));
            this.fillBinStats(new StreamDataAdapter(in));
            this.loaded = true;
        }
        finally {
            try {
                in.close();
            }
            catch (final IOException ex) {}
        }
    }
    
    private void fillBinStats(final DataAdapter da) throws IOException {
        this.min = this.sampleStats.getMin();
        this.max = this.sampleStats.getMax();
        this.delta = (this.max - this.min) / this.binCount;
        if (!this.binStats.isEmpty()) {
            this.binStats.clear();
        }
        for (int i = 0; i < this.binCount; ++i) {
            final SummaryStatistics stats = new SummaryStatistics();
            this.binStats.add(i, stats);
        }
        da.computeBinStats();
        (this.upperBounds = new double[this.binCount])[0] = this.binStats.get(0).getN() / (double)this.sampleStats.getN();
        for (int i = 1; i < this.binCount - 1; ++i) {
            this.upperBounds[i] = this.upperBounds[i - 1] + this.binStats.get(i).getN() / (double)this.sampleStats.getN();
        }
        this.upperBounds[this.binCount - 1] = 1.0;
    }
    
    private int findBin(final double value) {
        return FastMath.min(FastMath.max((int)FastMath.ceil((value - this.min) / this.delta) - 1, 0), this.binCount - 1);
    }
    
    public double getNextValue() throws MathIllegalStateException {
        if (!this.loaded) {
            throw new MathIllegalStateException(LocalizedFormats.DISTRIBUTION_NOT_LOADED, new Object[0]);
        }
        return this.sample();
    }
    
    public StatisticalSummary getSampleStats() {
        return this.sampleStats;
    }
    
    public int getBinCount() {
        return this.binCount;
    }
    
    public List<SummaryStatistics> getBinStats() {
        return this.binStats;
    }
    
    public double[] getUpperBounds() {
        final double[] binUpperBounds = new double[this.binCount];
        for (int i = 0; i < this.binCount - 1; ++i) {
            binUpperBounds[i] = this.min + this.delta * (i + 1);
        }
        binUpperBounds[this.binCount - 1] = this.max;
        return binUpperBounds;
    }
    
    public double[] getGeneratorUpperBounds() {
        final int len = this.upperBounds.length;
        final double[] out = new double[len];
        System.arraycopy(this.upperBounds, 0, out, 0, len);
        return out;
    }
    
    public boolean isLoaded() {
        return this.loaded;
    }
    
    public void reSeed(final long seed) {
        this.randomData.reSeed(seed);
    }
    
    @Override
    public double probability(final double x) {
        return 0.0;
    }
    
    public double density(final double x) {
        if (x < this.min || x > this.max) {
            return 0.0;
        }
        final int binIndex = this.findBin(x);
        final RealDistribution kernel = this.getKernel(this.binStats.get(binIndex));
        return kernel.density(x) * this.pB(binIndex) / this.kB(binIndex);
    }
    
    public double cumulativeProbability(final double x) {
        if (x < this.min) {
            return 0.0;
        }
        if (x >= this.max) {
            return 1.0;
        }
        final int binIndex = this.findBin(x);
        final double pBminus = this.pBminus(binIndex);
        final double pB = this.pB(binIndex);
        final RealDistribution kernel = this.k(x);
        if (!(kernel instanceof ConstantRealDistribution)) {
            final double[] binBounds = this.getUpperBounds();
            final double kB = this.kB(binIndex);
            final double lower = (binIndex == 0) ? this.min : binBounds[binIndex - 1];
            final double withinBinCum = (kernel.cumulativeProbability(x) - kernel.cumulativeProbability(lower)) / kB;
            return pBminus + pB * withinBinCum;
        }
        if (x < kernel.getNumericalMean()) {
            return pBminus;
        }
        return pBminus + pB;
    }
    
    @Override
    public double inverseCumulativeProbability(final double p) throws OutOfRangeException {
        if (p < 0.0 || p > 1.0) {
            throw new OutOfRangeException(p, 0, 1);
        }
        if (p == 0.0) {
            return this.getSupportLowerBound();
        }
        if (p == 1.0) {
            return this.getSupportUpperBound();
        }
        int i;
        for (i = 0; this.cumBinP(i) < p; ++i) {}
        final RealDistribution kernel = this.getKernel(this.binStats.get(i));
        final double kB = this.kB(i);
        final double[] binBounds = this.getUpperBounds();
        final double lower = (i == 0) ? this.min : binBounds[i - 1];
        final double kBminus = kernel.cumulativeProbability(lower);
        final double pB = this.pB(i);
        final double pBminus = this.pBminus(i);
        final double pCrit = p - pBminus;
        if (pCrit <= 0.0) {
            return lower;
        }
        return kernel.inverseCumulativeProbability(kBminus + pCrit * kB / pB);
    }
    
    public double getNumericalMean() {
        return this.sampleStats.getMean();
    }
    
    public double getNumericalVariance() {
        return this.sampleStats.getVariance();
    }
    
    public double getSupportLowerBound() {
        return this.min;
    }
    
    public double getSupportUpperBound() {
        return this.max;
    }
    
    public boolean isSupportLowerBoundInclusive() {
        return true;
    }
    
    public boolean isSupportUpperBoundInclusive() {
        return true;
    }
    
    public boolean isSupportConnected() {
        return true;
    }
    
    @Override
    public void reseedRandomGenerator(final long seed) {
        this.randomData.reSeed(seed);
    }
    
    private double pB(final int i) {
        return (i == 0) ? this.upperBounds[0] : (this.upperBounds[i] - this.upperBounds[i - 1]);
    }
    
    private double pBminus(final int i) {
        return (i == 0) ? 0.0 : this.upperBounds[i - 1];
    }
    
    private double kB(final int i) {
        final double[] binBounds = this.getUpperBounds();
        final RealDistribution kernel = this.getKernel(this.binStats.get(i));
        return (i == 0) ? kernel.cumulativeProbability(this.min, binBounds[0]) : kernel.cumulativeProbability(binBounds[i - 1], binBounds[i]);
    }
    
    private RealDistribution k(final double x) {
        final int binIndex = this.findBin(x);
        return this.getKernel(this.binStats.get(binIndex));
    }
    
    private double cumBinP(final int binIndex) {
        return this.upperBounds[binIndex];
    }
    
    protected RealDistribution getKernel(final SummaryStatistics bStats) {
        if (bStats.getN() == 1L || bStats.getVariance() == 0.0) {
            return new ConstantRealDistribution(bStats.getMean());
        }
        return new NormalDistribution(this.randomData.getRandomGenerator(), bStats.getMean(), bStats.getStandardDeviation(), 1.0E-9);
    }
    
    private abstract class DataAdapter
    {
        public abstract void computeBinStats() throws IOException;
        
        public abstract void computeStats() throws IOException;
    }
    
    private class StreamDataAdapter extends DataAdapter
    {
        private BufferedReader inputStream;
        
        StreamDataAdapter(final BufferedReader in) {
            this.inputStream = in;
        }
        
        @Override
        public void computeBinStats() throws IOException {
            String str = null;
            double val = 0.0;
            while ((str = this.inputStream.readLine()) != null) {
                val = Double.parseDouble(str);
                final SummaryStatistics stats = EmpiricalDistribution.this.binStats.get(EmpiricalDistribution.this.findBin(val));
                stats.addValue(val);
            }
            this.inputStream.close();
            this.inputStream = null;
        }
        
        @Override
        public void computeStats() throws IOException {
            String str = null;
            double val = 0.0;
            EmpiricalDistribution.this.sampleStats = new SummaryStatistics();
            while ((str = this.inputStream.readLine()) != null) {
                val = Double.parseDouble(str);
                EmpiricalDistribution.this.sampleStats.addValue(val);
            }
            this.inputStream.close();
            this.inputStream = null;
        }
    }
    
    private class ArrayDataAdapter extends DataAdapter
    {
        private double[] inputArray;
        
        ArrayDataAdapter(final double[] in) throws NullArgumentException {
            MathUtils.checkNotNull(in);
            this.inputArray = in;
        }
        
        @Override
        public void computeStats() throws IOException {
            EmpiricalDistribution.this.sampleStats = new SummaryStatistics();
            for (int i = 0; i < this.inputArray.length; ++i) {
                EmpiricalDistribution.this.sampleStats.addValue(this.inputArray[i]);
            }
        }
        
        @Override
        public void computeBinStats() throws IOException {
            for (int i = 0; i < this.inputArray.length; ++i) {
                final SummaryStatistics stats = EmpiricalDistribution.this.binStats.get(EmpiricalDistribution.this.findBin(this.inputArray[i]));
                stats.addValue(this.inputArray[i]);
            }
        }
    }
}
