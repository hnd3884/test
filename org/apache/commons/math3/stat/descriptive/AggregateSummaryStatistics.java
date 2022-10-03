package org.apache.commons.math3.stat.descriptive;

import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.math3.exception.NullArgumentException;
import java.io.Serializable;

public class AggregateSummaryStatistics implements StatisticalSummary, Serializable
{
    private static final long serialVersionUID = -8207112444016386906L;
    private final SummaryStatistics statisticsPrototype;
    private final SummaryStatistics statistics;
    
    public AggregateSummaryStatistics() {
        this(new SummaryStatistics());
    }
    
    public AggregateSummaryStatistics(final SummaryStatistics prototypeStatistics) throws NullArgumentException {
        this(prototypeStatistics, (prototypeStatistics == null) ? null : new SummaryStatistics(prototypeStatistics));
    }
    
    public AggregateSummaryStatistics(final SummaryStatistics prototypeStatistics, final SummaryStatistics initialStatistics) {
        this.statisticsPrototype = ((prototypeStatistics == null) ? new SummaryStatistics() : prototypeStatistics);
        this.statistics = ((initialStatistics == null) ? new SummaryStatistics() : initialStatistics);
    }
    
    public double getMax() {
        synchronized (this.statistics) {
            return this.statistics.getMax();
        }
    }
    
    public double getMean() {
        synchronized (this.statistics) {
            return this.statistics.getMean();
        }
    }
    
    public double getMin() {
        synchronized (this.statistics) {
            return this.statistics.getMin();
        }
    }
    
    public long getN() {
        synchronized (this.statistics) {
            return this.statistics.getN();
        }
    }
    
    public double getStandardDeviation() {
        synchronized (this.statistics) {
            return this.statistics.getStandardDeviation();
        }
    }
    
    public double getSum() {
        synchronized (this.statistics) {
            return this.statistics.getSum();
        }
    }
    
    public double getVariance() {
        synchronized (this.statistics) {
            return this.statistics.getVariance();
        }
    }
    
    public double getSumOfLogs() {
        synchronized (this.statistics) {
            return this.statistics.getSumOfLogs();
        }
    }
    
    public double getGeometricMean() {
        synchronized (this.statistics) {
            return this.statistics.getGeometricMean();
        }
    }
    
    public double getSumsq() {
        synchronized (this.statistics) {
            return this.statistics.getSumsq();
        }
    }
    
    public double getSecondMoment() {
        synchronized (this.statistics) {
            return this.statistics.getSecondMoment();
        }
    }
    
    public StatisticalSummary getSummary() {
        synchronized (this.statistics) {
            return new StatisticalSummaryValues(this.getMean(), this.getVariance(), this.getN(), this.getMax(), this.getMin(), this.getSum());
        }
    }
    
    public SummaryStatistics createContributingStatistics() {
        final SummaryStatistics contributingStatistics = new AggregatingSummaryStatistics(this.statistics);
        SummaryStatistics.copy(this.statisticsPrototype, contributingStatistics);
        return contributingStatistics;
    }
    
    public static StatisticalSummaryValues aggregate(final Collection<? extends StatisticalSummary> statistics) {
        if (statistics == null) {
            return null;
        }
        final Iterator<? extends StatisticalSummary> iterator = statistics.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        StatisticalSummary current = (StatisticalSummary)iterator.next();
        long n = current.getN();
        double min = current.getMin();
        double sum = current.getSum();
        double max = current.getMax();
        final double var = current.getVariance();
        double m2 = var * (n - 1.0);
        double mean = current.getMean();
        while (iterator.hasNext()) {
            current = (StatisticalSummary)iterator.next();
            if (current.getMin() < min || Double.isNaN(min)) {
                min = current.getMin();
            }
            if (current.getMax() > max || Double.isNaN(max)) {
                max = current.getMax();
            }
            sum += current.getSum();
            final double oldN = (double)n;
            final double curN = (double)current.getN();
            n += (long)curN;
            final double meanDiff = current.getMean() - mean;
            mean = sum / n;
            final double curM2 = current.getVariance() * (curN - 1.0);
            m2 = m2 + curM2 + meanDiff * meanDiff * oldN * curN / n;
        }
        double variance;
        if (n == 0L) {
            variance = Double.NaN;
        }
        else if (n == 1L) {
            variance = 0.0;
        }
        else {
            variance = m2 / (n - 1L);
        }
        return new StatisticalSummaryValues(mean, variance, n, max, min, sum);
    }
    
    private static class AggregatingSummaryStatistics extends SummaryStatistics
    {
        private static final long serialVersionUID = 1L;
        private final SummaryStatistics aggregateStatistics;
        
        AggregatingSummaryStatistics(final SummaryStatistics aggregateStatistics) {
            this.aggregateStatistics = aggregateStatistics;
        }
        
        @Override
        public void addValue(final double value) {
            super.addValue(value);
            synchronized (this.aggregateStatistics) {
                this.aggregateStatistics.addValue(value);
            }
        }
        
        @Override
        public boolean equals(final Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof AggregatingSummaryStatistics)) {
                return false;
            }
            final AggregatingSummaryStatistics stat = (AggregatingSummaryStatistics)object;
            return super.equals(stat) && this.aggregateStatistics.equals(stat.aggregateStatistics);
        }
        
        @Override
        public int hashCode() {
            return 123 + super.hashCode() + this.aggregateStatistics.hashCode();
        }
    }
}
