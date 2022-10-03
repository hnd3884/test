package org.apache.commons.math3.stat.correlation;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.math3.stat.ranking.NaNStrategy;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.commons.math3.stat.ranking.RankingAlgorithm;
import org.apache.commons.math3.linear.RealMatrix;

public class SpearmansCorrelation
{
    private final RealMatrix data;
    private final RankingAlgorithm rankingAlgorithm;
    private final PearsonsCorrelation rankCorrelation;
    
    public SpearmansCorrelation() {
        this(new NaturalRanking());
    }
    
    public SpearmansCorrelation(final RankingAlgorithm rankingAlgorithm) {
        this.data = null;
        this.rankingAlgorithm = rankingAlgorithm;
        this.rankCorrelation = null;
    }
    
    public SpearmansCorrelation(final RealMatrix dataMatrix) {
        this(dataMatrix, new NaturalRanking());
    }
    
    public SpearmansCorrelation(final RealMatrix dataMatrix, final RankingAlgorithm rankingAlgorithm) {
        this.rankingAlgorithm = rankingAlgorithm;
        this.data = this.rankTransform(dataMatrix);
        this.rankCorrelation = new PearsonsCorrelation(this.data);
    }
    
    public RealMatrix getCorrelationMatrix() {
        return this.rankCorrelation.getCorrelationMatrix();
    }
    
    public PearsonsCorrelation getRankCorrelation() {
        return this.rankCorrelation;
    }
    
    public RealMatrix computeCorrelationMatrix(final RealMatrix matrix) {
        final RealMatrix matrixCopy = this.rankTransform(matrix);
        return new PearsonsCorrelation().computeCorrelationMatrix(matrixCopy);
    }
    
    public RealMatrix computeCorrelationMatrix(final double[][] matrix) {
        return this.computeCorrelationMatrix(new BlockRealMatrix(matrix));
    }
    
    public double correlation(final double[] xArray, final double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new DimensionMismatchException(xArray.length, yArray.length);
        }
        if (xArray.length < 2) {
            throw new MathIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { xArray.length, 2 });
        }
        double[] x = xArray;
        double[] y = yArray;
        if (this.rankingAlgorithm instanceof NaturalRanking && NaNStrategy.REMOVED == ((NaturalRanking)this.rankingAlgorithm).getNanStrategy()) {
            final Set<Integer> nanPositions = new HashSet<Integer>();
            nanPositions.addAll(this.getNaNPositions(xArray));
            nanPositions.addAll(this.getNaNPositions(yArray));
            x = this.removeValues(xArray, nanPositions);
            y = this.removeValues(yArray, nanPositions);
        }
        return new PearsonsCorrelation().correlation(this.rankingAlgorithm.rank(x), this.rankingAlgorithm.rank(y));
    }
    
    private RealMatrix rankTransform(final RealMatrix matrix) {
        RealMatrix transformed = null;
        if (this.rankingAlgorithm instanceof NaturalRanking && ((NaturalRanking)this.rankingAlgorithm).getNanStrategy() == NaNStrategy.REMOVED) {
            final Set<Integer> nanPositions = new HashSet<Integer>();
            for (int i = 0; i < matrix.getColumnDimension(); ++i) {
                nanPositions.addAll(this.getNaNPositions(matrix.getColumn(i)));
            }
            if (!nanPositions.isEmpty()) {
                transformed = new BlockRealMatrix(matrix.getRowDimension() - nanPositions.size(), matrix.getColumnDimension());
                for (int i = 0; i < transformed.getColumnDimension(); ++i) {
                    transformed.setColumn(i, this.removeValues(matrix.getColumn(i), nanPositions));
                }
            }
        }
        if (transformed == null) {
            transformed = matrix.copy();
        }
        for (int j = 0; j < transformed.getColumnDimension(); ++j) {
            transformed.setColumn(j, this.rankingAlgorithm.rank(transformed.getColumn(j)));
        }
        return transformed;
    }
    
    private List<Integer> getNaNPositions(final double[] input) {
        final List<Integer> positions = new ArrayList<Integer>();
        for (int i = 0; i < input.length; ++i) {
            if (Double.isNaN(input[i])) {
                positions.add(i);
            }
        }
        return positions;
    }
    
    private double[] removeValues(final double[] input, final Set<Integer> indices) {
        if (indices.isEmpty()) {
            return input;
        }
        final double[] result = new double[input.length - indices.size()];
        int i = 0;
        int j = 0;
        while (i < input.length) {
            if (!indices.contains(i)) {
                result[j++] = input[i];
            }
            ++i;
        }
        return result;
    }
}
