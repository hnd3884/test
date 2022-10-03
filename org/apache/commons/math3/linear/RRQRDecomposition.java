package org.apache.commons.math3.linear;

import org.apache.commons.math3.util.FastMath;

public class RRQRDecomposition extends QRDecomposition
{
    private int[] p;
    private RealMatrix cachedP;
    
    public RRQRDecomposition(final RealMatrix matrix) {
        this(matrix, 0.0);
    }
    
    public RRQRDecomposition(final RealMatrix matrix, final double threshold) {
        super(matrix, threshold);
    }
    
    @Override
    protected void decompose(final double[][] qrt) {
        this.p = new int[qrt.length];
        for (int i = 0; i < this.p.length; ++i) {
            this.p[i] = i;
        }
        super.decompose(qrt);
    }
    
    @Override
    protected void performHouseholderReflection(final int minor, final double[][] qrt) {
        double l2NormSquaredMax = 0.0;
        int l2NormSquaredMaxIndex = minor;
        for (int i = minor; i < qrt.length; ++i) {
            double l2NormSquared = 0.0;
            for (int j = 0; j < qrt[i].length; ++j) {
                l2NormSquared += qrt[i][j] * qrt[i][j];
            }
            if (l2NormSquared > l2NormSquaredMax) {
                l2NormSquaredMax = l2NormSquared;
                l2NormSquaredMaxIndex = i;
            }
        }
        if (l2NormSquaredMaxIndex != minor) {
            final double[] tmp1 = qrt[minor];
            qrt[minor] = qrt[l2NormSquaredMaxIndex];
            qrt[l2NormSquaredMaxIndex] = tmp1;
            final int tmp2 = this.p[minor];
            this.p[minor] = this.p[l2NormSquaredMaxIndex];
            this.p[l2NormSquaredMaxIndex] = tmp2;
        }
        super.performHouseholderReflection(minor, qrt);
    }
    
    public RealMatrix getP() {
        if (this.cachedP == null) {
            final int n = this.p.length;
            this.cachedP = MatrixUtils.createRealMatrix(n, n);
            for (int i = 0; i < n; ++i) {
                this.cachedP.setEntry(this.p[i], i, 1.0);
            }
        }
        return this.cachedP;
    }
    
    public int getRank(final double dropThreshold) {
        final RealMatrix r = this.getR();
        final int rows = r.getRowDimension();
        final int columns = r.getColumnDimension();
        int rank = 1;
        final double rNorm;
        double lastNorm = rNorm = r.getFrobeniusNorm();
        while (rank < FastMath.min(rows, columns)) {
            final double thisNorm = r.getSubMatrix(rank, rows - 1, rank, columns - 1).getFrobeniusNorm();
            if (thisNorm == 0.0) {
                break;
            }
            if (thisNorm / lastNorm * rNorm < dropThreshold) {
                break;
            }
            lastNorm = thisNorm;
            ++rank;
        }
        return rank;
    }
    
    @Override
    public DecompositionSolver getSolver() {
        return new Solver(super.getSolver(), this.getP());
    }
    
    private static class Solver implements DecompositionSolver
    {
        private final DecompositionSolver upper;
        private RealMatrix p;
        
        private Solver(final DecompositionSolver upper, final RealMatrix p) {
            this.upper = upper;
            this.p = p;
        }
        
        public boolean isNonSingular() {
            return this.upper.isNonSingular();
        }
        
        public RealVector solve(final RealVector b) {
            return this.p.operate(this.upper.solve(b));
        }
        
        public RealMatrix solve(final RealMatrix b) {
            return this.p.multiply(this.upper.solve(b));
        }
        
        public RealMatrix getInverse() {
            return this.solve(MatrixUtils.createRealIdentityMatrix(this.p.getRowDimension()));
        }
    }
}
