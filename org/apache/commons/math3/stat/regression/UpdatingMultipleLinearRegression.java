package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NoDataException;

public interface UpdatingMultipleLinearRegression
{
    boolean hasIntercept();
    
    long getN();
    
    void addObservation(final double[] p0, final double p1) throws ModelSpecificationException;
    
    void addObservations(final double[][] p0, final double[] p1) throws ModelSpecificationException;
    
    void clear();
    
    RegressionResults regress() throws ModelSpecificationException, NoDataException;
    
    RegressionResults regress(final int[] p0) throws ModelSpecificationException, MathIllegalArgumentException;
}
