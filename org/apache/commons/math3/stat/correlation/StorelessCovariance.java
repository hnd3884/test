package org.apache.commons.math3.stat.correlation;

import org.apache.commons.math3.exception.MathUnsupportedOperationException;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class StorelessCovariance extends Covariance
{
    private StorelessBivariateCovariance[] covMatrix;
    private int dimension;
    
    public StorelessCovariance(final int dim) {
        this(dim, true);
    }
    
    public StorelessCovariance(final int dim, final boolean biasCorrected) {
        this.dimension = dim;
        this.covMatrix = new StorelessBivariateCovariance[this.dimension * (this.dimension + 1) / 2];
        this.initializeMatrix(biasCorrected);
    }
    
    private void initializeMatrix(final boolean biasCorrected) {
        for (int i = 0; i < this.dimension; ++i) {
            for (int j = 0; j < this.dimension; ++j) {
                this.setElement(i, j, new StorelessBivariateCovariance(biasCorrected));
            }
        }
    }
    
    private int indexOf(final int i, final int j) {
        return (j < i) ? (i * (i + 1) / 2 + j) : (j * (j + 1) / 2 + i);
    }
    
    private StorelessBivariateCovariance getElement(final int i, final int j) {
        return this.covMatrix[this.indexOf(i, j)];
    }
    
    private void setElement(final int i, final int j, final StorelessBivariateCovariance cov) {
        this.covMatrix[this.indexOf(i, j)] = cov;
    }
    
    public double getCovariance(final int xIndex, final int yIndex) throws NumberIsTooSmallException {
        return this.getElement(xIndex, yIndex).getResult();
    }
    
    public void increment(final double[] data) throws DimensionMismatchException {
        final int length = data.length;
        if (length != this.dimension) {
            throw new DimensionMismatchException(length, this.dimension);
        }
        for (int i = 0; i < length; ++i) {
            for (int j = i; j < length; ++j) {
                this.getElement(i, j).increment(data[i], data[j]);
            }
        }
    }
    
    public void append(final StorelessCovariance sc) throws DimensionMismatchException {
        if (sc.dimension != this.dimension) {
            throw new DimensionMismatchException(sc.dimension, this.dimension);
        }
        for (int i = 0; i < this.dimension; ++i) {
            for (int j = i; j < this.dimension; ++j) {
                this.getElement(i, j).append(sc.getElement(i, j));
            }
        }
    }
    
    @Override
    public RealMatrix getCovarianceMatrix() throws NumberIsTooSmallException {
        return MatrixUtils.createRealMatrix(this.getData());
    }
    
    public double[][] getData() throws NumberIsTooSmallException {
        final double[][] data = new double[this.dimension][this.dimension];
        for (int i = 0; i < this.dimension; ++i) {
            for (int j = 0; j < this.dimension; ++j) {
                data[i][j] = this.getElement(i, j).getResult();
            }
        }
        return data;
    }
    
    @Override
    public int getN() throws MathUnsupportedOperationException {
        throw new MathUnsupportedOperationException();
    }
}
