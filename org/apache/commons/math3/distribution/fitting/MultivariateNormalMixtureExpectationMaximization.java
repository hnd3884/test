package org.apache.commons.math3.distribution.fitting;

import org.apache.commons.math3.stat.correlation.Covariance;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.linear.SingularMatrixException;
import java.util.List;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.Pair;
import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.util.MathArrays;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.apache.commons.math3.distribution.MixtureMultivariateNormalDistribution;

public class MultivariateNormalMixtureExpectationMaximization
{
    private static final int DEFAULT_MAX_ITERATIONS = 1000;
    private static final double DEFAULT_THRESHOLD = 1.0E-5;
    private final double[][] data;
    private MixtureMultivariateNormalDistribution fittedModel;
    private double logLikelihood;
    
    public MultivariateNormalMixtureExpectationMaximization(final double[][] data) throws NotStrictlyPositiveException, DimensionMismatchException, NumberIsTooSmallException {
        this.logLikelihood = 0.0;
        if (data.length < 1) {
            throw new NotStrictlyPositiveException(data.length);
        }
        this.data = new double[data.length][data[0].length];
        for (int i = 0; i < data.length; ++i) {
            if (data[i].length != data[0].length) {
                throw new DimensionMismatchException(data[i].length, data[0].length);
            }
            if (data[i].length < 2) {
                throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_TOO_SMALL, data[i].length, 2, true);
            }
            this.data[i] = MathArrays.copyOf(data[i], data[i].length);
        }
    }
    
    public void fit(final MixtureMultivariateNormalDistribution initialMixture, final int maxIterations, final double threshold) throws SingularMatrixException, NotStrictlyPositiveException, DimensionMismatchException {
        if (maxIterations < 1) {
            throw new NotStrictlyPositiveException(maxIterations);
        }
        if (threshold < Double.MIN_VALUE) {
            throw new NotStrictlyPositiveException(threshold);
        }
        final int n = this.data.length;
        final int numCols = this.data[0].length;
        final int k = initialMixture.getComponents().size();
        final int numMeanColumns = ((MultivariateNormalDistribution)initialMixture.getComponents().get(0).getSecond()).getMeans().length;
        if (numMeanColumns != numCols) {
            throw new DimensionMismatchException(numMeanColumns, numCols);
        }
        int numIterations = 0;
        double previousLogLikelihood = 0.0;
        this.logLikelihood = Double.NEGATIVE_INFINITY;
        this.fittedModel = new MixtureMultivariateNormalDistribution(initialMixture.getComponents());
        while (numIterations++ <= maxIterations && FastMath.abs(previousLogLikelihood - this.logLikelihood) > threshold) {
            previousLogLikelihood = this.logLikelihood;
            double sumLogLikelihood = 0.0;
            final List<Pair<Double, MultivariateNormalDistribution>> components = this.fittedModel.getComponents();
            final double[] weights = new double[k];
            final MultivariateNormalDistribution[] mvns = new MultivariateNormalDistribution[k];
            for (int j = 0; j < k; ++j) {
                weights[j] = (double)components.get(j).getFirst();
                mvns[j] = (MultivariateNormalDistribution)components.get(j).getSecond();
            }
            final double[][] gamma = new double[n][k];
            final double[] gammaSums = new double[k];
            final double[][] gammaDataProdSums = new double[k][numCols];
            for (int i = 0; i < n; ++i) {
                final double rowDensity = this.fittedModel.density(this.data[i]);
                sumLogLikelihood += FastMath.log(rowDensity);
                for (int l = 0; l < k; ++l) {
                    gamma[i][l] = weights[l] * mvns[l].density(this.data[i]) / rowDensity;
                    final double[] array = gammaSums;
                    final int n2 = l;
                    array[n2] += gamma[i][l];
                    for (int col = 0; col < numCols; ++col) {
                        final double[] array2 = gammaDataProdSums[l];
                        final int n3 = col;
                        array2[n3] += gamma[i][l] * this.data[i][col];
                    }
                }
            }
            this.logLikelihood = sumLogLikelihood / n;
            final double[] newWeights = new double[k];
            final double[][] newMeans = new double[k][numCols];
            for (int m = 0; m < k; ++m) {
                newWeights[m] = gammaSums[m] / n;
                for (int col2 = 0; col2 < numCols; ++col2) {
                    newMeans[m][col2] = gammaDataProdSums[m][col2] / gammaSums[m];
                }
            }
            final RealMatrix[] newCovMats = new RealMatrix[k];
            for (int l = 0; l < k; ++l) {
                newCovMats[l] = new Array2DRowRealMatrix(numCols, numCols);
            }
            for (int i2 = 0; i2 < n; ++i2) {
                for (int j2 = 0; j2 < k; ++j2) {
                    final RealMatrix vec = new Array2DRowRealMatrix(MathArrays.ebeSubtract(this.data[i2], newMeans[j2]));
                    final RealMatrix dataCov = vec.multiply(vec.transpose()).scalarMultiply(gamma[i2][j2]);
                    newCovMats[j2] = newCovMats[j2].add(dataCov);
                }
            }
            final double[][][] newCovMatArrays = new double[k][numCols][numCols];
            for (int j2 = 0; j2 < k; ++j2) {
                newCovMats[j2] = newCovMats[j2].scalarMultiply(1.0 / gammaSums[j2]);
                newCovMatArrays[j2] = newCovMats[j2].getData();
            }
            this.fittedModel = new MixtureMultivariateNormalDistribution(newWeights, newMeans, newCovMatArrays);
        }
        if (FastMath.abs(previousLogLikelihood - this.logLikelihood) > threshold) {
            throw new ConvergenceException();
        }
    }
    
    public void fit(final MixtureMultivariateNormalDistribution initialMixture) throws SingularMatrixException, NotStrictlyPositiveException {
        this.fit(initialMixture, 1000, 1.0E-5);
    }
    
    public static MixtureMultivariateNormalDistribution estimate(final double[][] data, final int numComponents) throws NotStrictlyPositiveException, DimensionMismatchException {
        if (data.length < 2) {
            throw new NotStrictlyPositiveException(data.length);
        }
        if (numComponents < 2) {
            throw new NumberIsTooSmallException(numComponents, 2, true);
        }
        if (numComponents > data.length) {
            throw new NumberIsTooLargeException(numComponents, data.length, true);
        }
        final int numRows = data.length;
        final int numCols = data[0].length;
        final DataRow[] sortedData = new DataRow[numRows];
        for (int i = 0; i < numRows; ++i) {
            sortedData[i] = new DataRow(data[i]);
        }
        Arrays.sort(sortedData);
        final double weight = 1.0 / numComponents;
        final List<Pair<Double, MultivariateNormalDistribution>> components = new ArrayList<Pair<Double, MultivariateNormalDistribution>>(numComponents);
        for (int binIndex = 0; binIndex < numComponents; ++binIndex) {
            final int minIndex = binIndex * numRows / numComponents;
            final int maxIndex = (binIndex + 1) * numRows / numComponents;
            final int numBinRows = maxIndex - minIndex;
            final double[][] binData = new double[numBinRows][numCols];
            final double[] columnMeans = new double[numCols];
            for (int j = minIndex, iBin = 0; j < maxIndex; ++j, ++iBin) {
                for (int k = 0; k < numCols; ++k) {
                    final double val = sortedData[j].getRow()[k];
                    final double[] array = columnMeans;
                    final int n = k;
                    array[n] += val;
                    binData[iBin][k] = val;
                }
            }
            MathArrays.scaleInPlace(1.0 / numBinRows, columnMeans);
            final double[][] covMat = new Covariance(binData).getCovarianceMatrix().getData();
            final MultivariateNormalDistribution mvn = new MultivariateNormalDistribution(columnMeans, covMat);
            components.add(new Pair<Double, MultivariateNormalDistribution>(weight, mvn));
        }
        return new MixtureMultivariateNormalDistribution(components);
    }
    
    public double getLogLikelihood() {
        return this.logLikelihood;
    }
    
    public MixtureMultivariateNormalDistribution getFittedModel() {
        return new MixtureMultivariateNormalDistribution(this.fittedModel.getComponents());
    }
    
    private static class DataRow implements Comparable<DataRow>
    {
        private final double[] row;
        private Double mean;
        
        DataRow(final double[] data) {
            this.row = data;
            this.mean = 0.0;
            for (int i = 0; i < data.length; ++i) {
                this.mean += data[i];
            }
            this.mean /= (Double)data.length;
        }
        
        public int compareTo(final DataRow other) {
            return this.mean.compareTo(other.mean);
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof DataRow && MathArrays.equals(this.row, ((DataRow)other).row));
        }
        
        @Override
        public int hashCode() {
            return Arrays.hashCode(this.row);
        }
        
        public double[] getRow() {
            return this.row;
        }
    }
}
