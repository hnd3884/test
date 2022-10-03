package org.apache.commons.math3.stat.descriptive.moment;

import java.util.Arrays;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.io.Serializable;

public class VectorialMean implements Serializable
{
    private static final long serialVersionUID = 8223009086481006892L;
    private final Mean[] means;
    
    public VectorialMean(final int dimension) {
        this.means = new Mean[dimension];
        for (int i = 0; i < dimension; ++i) {
            this.means[i] = new Mean();
        }
    }
    
    public void increment(final double[] v) throws DimensionMismatchException {
        if (v.length != this.means.length) {
            throw new DimensionMismatchException(v.length, this.means.length);
        }
        for (int i = 0; i < v.length; ++i) {
            this.means[i].increment(v[i]);
        }
    }
    
    public double[] getResult() {
        final double[] result = new double[this.means.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this.means[i].getResult();
        }
        return result;
    }
    
    public long getN() {
        return (this.means.length == 0) ? 0L : this.means[0].getN();
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = 31 * result + Arrays.hashCode(this.means);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VectorialMean)) {
            return false;
        }
        final VectorialMean other = (VectorialMean)obj;
        return Arrays.equals(this.means, other.means);
    }
}
