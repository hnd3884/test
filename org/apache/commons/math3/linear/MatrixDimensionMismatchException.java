package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MultiDimensionMismatchException;

public class MatrixDimensionMismatchException extends MultiDimensionMismatchException
{
    private static final long serialVersionUID = -8415396756375798143L;
    
    public MatrixDimensionMismatchException(final int wrongRowDim, final int wrongColDim, final int expectedRowDim, final int expectedColDim) {
        super(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Integer[] { wrongRowDim, wrongColDim }, new Integer[] { expectedRowDim, expectedColDim });
    }
    
    public int getWrongRowDimension() {
        return this.getWrongDimension(0);
    }
    
    public int getExpectedRowDimension() {
        return this.getExpectedDimension(0);
    }
    
    public int getWrongColumnDimension() {
        return this.getWrongDimension(1);
    }
    
    public int getExpectedColumnDimension() {
        return this.getExpectedDimension(1);
    }
}
