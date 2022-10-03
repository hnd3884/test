package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.stat.descriptive.moment.SecondMoment;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.linear.QRDecomposition;

public class OLSMultipleLinearRegression extends AbstractMultipleLinearRegression
{
    private QRDecomposition qr;
    private final double threshold;
    
    public OLSMultipleLinearRegression() {
        this(0.0);
    }
    
    public OLSMultipleLinearRegression(final double threshold) {
        this.qr = null;
        this.threshold = threshold;
    }
    
    public void newSampleData(final double[] y, final double[][] x) throws MathIllegalArgumentException {
        this.validateSampleData(x, y);
        this.newYSampleData(y);
        this.newXSampleData(x);
    }
    
    @Override
    public void newSampleData(final double[] data, final int nobs, final int nvars) {
        super.newSampleData(data, nobs, nvars);
        this.qr = new QRDecomposition(this.getX(), this.threshold);
    }
    
    public RealMatrix calculateHat() {
        final RealMatrix Q = this.qr.getQ();
        final int p = this.qr.getR().getColumnDimension();
        final int n = Q.getColumnDimension();
        final Array2DRowRealMatrix augI = new Array2DRowRealMatrix(n, n);
        final double[][] augIData = augI.getDataRef();
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if (i == j && i < p) {
                    augIData[i][j] = 1.0;
                }
                else {
                    augIData[i][j] = 0.0;
                }
            }
        }
        return Q.multiply(augI).multiply(Q.transpose());
    }
    
    public double calculateTotalSumOfSquares() {
        if (this.isNoIntercept()) {
            return StatUtils.sumSq(this.getY().toArray());
        }
        return new SecondMoment().evaluate(this.getY().toArray());
    }
    
    public double calculateResidualSumOfSquares() {
        final RealVector residuals = this.calculateResiduals();
        return residuals.dotProduct(residuals);
    }
    
    public double calculateRSquared() {
        return 1.0 - this.calculateResidualSumOfSquares() / this.calculateTotalSumOfSquares();
    }
    
    public double calculateAdjustedRSquared() {
        final double n = this.getX().getRowDimension();
        if (this.isNoIntercept()) {
            return 1.0 - (1.0 - this.calculateRSquared()) * (n / (n - this.getX().getColumnDimension()));
        }
        return 1.0 - this.calculateResidualSumOfSquares() * (n - 1.0) / (this.calculateTotalSumOfSquares() * (n - this.getX().getColumnDimension()));
    }
    
    @Override
    protected void newXSampleData(final double[][] x) {
        super.newXSampleData(x);
        this.qr = new QRDecomposition(this.getX(), this.threshold);
    }
    
    @Override
    protected RealVector calculateBeta() {
        return this.qr.getSolver().solve(this.getY());
    }
    
    @Override
    protected RealMatrix calculateBetaVariance() {
        final int p = this.getX().getColumnDimension();
        final RealMatrix Raug = this.qr.getR().getSubMatrix(0, p - 1, 0, p - 1);
        final RealMatrix Rinv = new LUDecomposition(Raug).getSolver().getInverse();
        return Rinv.multiply(Rinv.transpose());
    }
}
