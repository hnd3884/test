package org.apache.commons.math3.stat.descriptive;

import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.NullArgumentException;

public class SynchronizedSummaryStatistics extends SummaryStatistics
{
    private static final long serialVersionUID = 1909861009042253704L;
    
    public SynchronizedSummaryStatistics() {
    }
    
    public SynchronizedSummaryStatistics(final SynchronizedSummaryStatistics original) throws NullArgumentException {
        copy(original, this);
    }
    
    @Override
    public synchronized StatisticalSummary getSummary() {
        return super.getSummary();
    }
    
    @Override
    public synchronized void addValue(final double value) {
        super.addValue(value);
    }
    
    @Override
    public synchronized long getN() {
        return super.getN();
    }
    
    @Override
    public synchronized double getSum() {
        return super.getSum();
    }
    
    @Override
    public synchronized double getSumsq() {
        return super.getSumsq();
    }
    
    @Override
    public synchronized double getMean() {
        return super.getMean();
    }
    
    @Override
    public synchronized double getStandardDeviation() {
        return super.getStandardDeviation();
    }
    
    @Override
    public synchronized double getQuadraticMean() {
        return super.getQuadraticMean();
    }
    
    @Override
    public synchronized double getVariance() {
        return super.getVariance();
    }
    
    @Override
    public synchronized double getPopulationVariance() {
        return super.getPopulationVariance();
    }
    
    @Override
    public synchronized double getMax() {
        return super.getMax();
    }
    
    @Override
    public synchronized double getMin() {
        return super.getMin();
    }
    
    @Override
    public synchronized double getGeometricMean() {
        return super.getGeometricMean();
    }
    
    @Override
    public synchronized String toString() {
        return super.toString();
    }
    
    @Override
    public synchronized void clear() {
        super.clear();
    }
    
    @Override
    public synchronized boolean equals(final Object object) {
        return super.equals(object);
    }
    
    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getSumImpl() {
        return super.getSumImpl();
    }
    
    @Override
    public synchronized void setSumImpl(final StorelessUnivariateStatistic sumImpl) throws MathIllegalStateException {
        super.setSumImpl(sumImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getSumsqImpl() {
        return super.getSumsqImpl();
    }
    
    @Override
    public synchronized void setSumsqImpl(final StorelessUnivariateStatistic sumsqImpl) throws MathIllegalStateException {
        super.setSumsqImpl(sumsqImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getMinImpl() {
        return super.getMinImpl();
    }
    
    @Override
    public synchronized void setMinImpl(final StorelessUnivariateStatistic minImpl) throws MathIllegalStateException {
        super.setMinImpl(minImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getMaxImpl() {
        return super.getMaxImpl();
    }
    
    @Override
    public synchronized void setMaxImpl(final StorelessUnivariateStatistic maxImpl) throws MathIllegalStateException {
        super.setMaxImpl(maxImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getSumLogImpl() {
        return super.getSumLogImpl();
    }
    
    @Override
    public synchronized void setSumLogImpl(final StorelessUnivariateStatistic sumLogImpl) throws MathIllegalStateException {
        super.setSumLogImpl(sumLogImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getGeoMeanImpl() {
        return super.getGeoMeanImpl();
    }
    
    @Override
    public synchronized void setGeoMeanImpl(final StorelessUnivariateStatistic geoMeanImpl) throws MathIllegalStateException {
        super.setGeoMeanImpl(geoMeanImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getMeanImpl() {
        return super.getMeanImpl();
    }
    
    @Override
    public synchronized void setMeanImpl(final StorelessUnivariateStatistic meanImpl) throws MathIllegalStateException {
        super.setMeanImpl(meanImpl);
    }
    
    @Override
    public synchronized StorelessUnivariateStatistic getVarianceImpl() {
        return super.getVarianceImpl();
    }
    
    @Override
    public synchronized void setVarianceImpl(final StorelessUnivariateStatistic varianceImpl) throws MathIllegalStateException {
        super.setVarianceImpl(varianceImpl);
    }
    
    @Override
    public synchronized SynchronizedSummaryStatistics copy() {
        final SynchronizedSummaryStatistics result = new SynchronizedSummaryStatistics();
        copy(this, result);
        return result;
    }
    
    public static void copy(final SynchronizedSummaryStatistics source, final SynchronizedSummaryStatistics dest) throws NullArgumentException {
        MathUtils.checkNotNull(source);
        MathUtils.checkNotNull(dest);
        synchronized (source) {
            synchronized (dest) {
                SummaryStatistics.copy(source, dest);
            }
        }
    }
}
