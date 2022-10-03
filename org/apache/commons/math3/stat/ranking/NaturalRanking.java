package org.apache.commons.math3.stat.ranking;

import java.util.Iterator;
import org.apache.commons.math3.util.FastMath;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NotANumberException;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.RandomDataGenerator;

public class NaturalRanking implements RankingAlgorithm
{
    public static final NaNStrategy DEFAULT_NAN_STRATEGY;
    public static final TiesStrategy DEFAULT_TIES_STRATEGY;
    private final NaNStrategy nanStrategy;
    private final TiesStrategy tiesStrategy;
    private final RandomDataGenerator randomData;
    
    public NaturalRanking() {
        this.tiesStrategy = NaturalRanking.DEFAULT_TIES_STRATEGY;
        this.nanStrategy = NaturalRanking.DEFAULT_NAN_STRATEGY;
        this.randomData = null;
    }
    
    public NaturalRanking(final TiesStrategy tiesStrategy) {
        this.tiesStrategy = tiesStrategy;
        this.nanStrategy = NaturalRanking.DEFAULT_NAN_STRATEGY;
        this.randomData = new RandomDataGenerator();
    }
    
    public NaturalRanking(final NaNStrategy nanStrategy) {
        this.nanStrategy = nanStrategy;
        this.tiesStrategy = NaturalRanking.DEFAULT_TIES_STRATEGY;
        this.randomData = null;
    }
    
    public NaturalRanking(final NaNStrategy nanStrategy, final TiesStrategy tiesStrategy) {
        this.nanStrategy = nanStrategy;
        this.tiesStrategy = tiesStrategy;
        this.randomData = new RandomDataGenerator();
    }
    
    public NaturalRanking(final RandomGenerator randomGenerator) {
        this.tiesStrategy = TiesStrategy.RANDOM;
        this.nanStrategy = NaturalRanking.DEFAULT_NAN_STRATEGY;
        this.randomData = new RandomDataGenerator(randomGenerator);
    }
    
    public NaturalRanking(final NaNStrategy nanStrategy, final RandomGenerator randomGenerator) {
        this.nanStrategy = nanStrategy;
        this.tiesStrategy = TiesStrategy.RANDOM;
        this.randomData = new RandomDataGenerator(randomGenerator);
    }
    
    public NaNStrategy getNanStrategy() {
        return this.nanStrategy;
    }
    
    public TiesStrategy getTiesStrategy() {
        return this.tiesStrategy;
    }
    
    public double[] rank(final double[] data) {
        IntDoublePair[] ranks = new IntDoublePair[data.length];
        for (int i = 0; i < data.length; ++i) {
            ranks[i] = new IntDoublePair(data[i], i);
        }
        List<Integer> nanPositions = null;
        switch (this.nanStrategy) {
            case MAXIMAL: {
                this.recodeNaNs(ranks, Double.POSITIVE_INFINITY);
                break;
            }
            case MINIMAL: {
                this.recodeNaNs(ranks, Double.NEGATIVE_INFINITY);
                break;
            }
            case REMOVED: {
                ranks = this.removeNaNs(ranks);
                break;
            }
            case FIXED: {
                nanPositions = this.getNanPositions(ranks);
                break;
            }
            case FAILED: {
                nanPositions = this.getNanPositions(ranks);
                if (nanPositions.size() > 0) {
                    throw new NotANumberException();
                }
                break;
            }
            default: {
                throw new MathInternalError();
            }
        }
        Arrays.sort(ranks);
        final double[] out = new double[ranks.length];
        int pos = 1;
        out[ranks[0].getPosition()] = pos;
        List<Integer> tiesTrace = new ArrayList<Integer>();
        tiesTrace.add(ranks[0].getPosition());
        for (int j = 1; j < ranks.length; ++j) {
            if (Double.compare(ranks[j].getValue(), ranks[j - 1].getValue()) > 0) {
                pos = j + 1;
                if (tiesTrace.size() > 1) {
                    this.resolveTie(out, tiesTrace);
                }
                tiesTrace = new ArrayList<Integer>();
                tiesTrace.add(ranks[j].getPosition());
            }
            else {
                tiesTrace.add(ranks[j].getPosition());
            }
            out[ranks[j].getPosition()] = pos;
        }
        if (tiesTrace.size() > 1) {
            this.resolveTie(out, tiesTrace);
        }
        if (this.nanStrategy == NaNStrategy.FIXED) {
            this.restoreNaNs(out, nanPositions);
        }
        return out;
    }
    
    private IntDoublePair[] removeNaNs(final IntDoublePair[] ranks) {
        if (!this.containsNaNs(ranks)) {
            return ranks;
        }
        final IntDoublePair[] outRanks = new IntDoublePair[ranks.length];
        int j = 0;
        for (int i = 0; i < ranks.length; ++i) {
            if (Double.isNaN(ranks[i].getValue())) {
                for (int k = i + 1; k < ranks.length; ++k) {
                    ranks[k] = new IntDoublePair(ranks[k].getValue(), ranks[k].getPosition() - 1);
                }
            }
            else {
                outRanks[j] = new IntDoublePair(ranks[i].getValue(), ranks[i].getPosition());
                ++j;
            }
        }
        final IntDoublePair[] returnRanks = new IntDoublePair[j];
        System.arraycopy(outRanks, 0, returnRanks, 0, j);
        return returnRanks;
    }
    
    private void recodeNaNs(final IntDoublePair[] ranks, final double value) {
        for (int i = 0; i < ranks.length; ++i) {
            if (Double.isNaN(ranks[i].getValue())) {
                ranks[i] = new IntDoublePair(value, ranks[i].getPosition());
            }
        }
    }
    
    private boolean containsNaNs(final IntDoublePair[] ranks) {
        for (int i = 0; i < ranks.length; ++i) {
            if (Double.isNaN(ranks[i].getValue())) {
                return true;
            }
        }
        return false;
    }
    
    private void resolveTie(final double[] ranks, final List<Integer> tiesTrace) {
        final double c = ranks[tiesTrace.get(0)];
        final int length = tiesTrace.size();
        switch (this.tiesStrategy) {
            case AVERAGE: {
                this.fill(ranks, tiesTrace, (2.0 * c + length - 1.0) / 2.0);
                break;
            }
            case MAXIMUM: {
                this.fill(ranks, tiesTrace, c + length - 1.0);
                break;
            }
            case MINIMUM: {
                this.fill(ranks, tiesTrace, c);
                break;
            }
            case RANDOM: {
                final Iterator<Integer> iterator = tiesTrace.iterator();
                final long f = FastMath.round(c);
                while (iterator.hasNext()) {
                    ranks[iterator.next()] = (double)this.randomData.nextLong(f, f + length - 1L);
                }
                break;
            }
            case SEQUENTIAL: {
                final Iterator<Integer> iterator = tiesTrace.iterator();
                final long f = FastMath.round(c);
                int i = 0;
                while (iterator.hasNext()) {
                    ranks[iterator.next()] = (double)(f + i++);
                }
                break;
            }
            default: {
                throw new MathInternalError();
            }
        }
    }
    
    private void fill(final double[] data, final List<Integer> tiesTrace, final double value) {
        final Iterator<Integer> iterator = tiesTrace.iterator();
        while (iterator.hasNext()) {
            data[iterator.next()] = value;
        }
    }
    
    private void restoreNaNs(final double[] ranks, final List<Integer> nanPositions) {
        if (nanPositions.size() == 0) {
            return;
        }
        final Iterator<Integer> iterator = nanPositions.iterator();
        while (iterator.hasNext()) {
            ranks[iterator.next()] = Double.NaN;
        }
    }
    
    private List<Integer> getNanPositions(final IntDoublePair[] ranks) {
        final ArrayList<Integer> out = new ArrayList<Integer>();
        for (int i = 0; i < ranks.length; ++i) {
            if (Double.isNaN(ranks[i].getValue())) {
                out.add(i);
            }
        }
        return out;
    }
    
    static {
        DEFAULT_NAN_STRATEGY = NaNStrategy.FAILED;
        DEFAULT_TIES_STRATEGY = TiesStrategy.AVERAGE;
    }
    
    private static class IntDoublePair implements Comparable<IntDoublePair>
    {
        private final double value;
        private final int position;
        
        IntDoublePair(final double value, final int position) {
            this.value = value;
            this.position = position;
        }
        
        public int compareTo(final IntDoublePair other) {
            return Double.compare(this.value, other.value);
        }
        
        public double getValue() {
            return this.value;
        }
        
        public int getPosition() {
            return this.position;
        }
    }
}
