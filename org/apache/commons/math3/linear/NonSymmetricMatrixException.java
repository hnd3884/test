package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class NonSymmetricMatrixException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -7518495577824189882L;
    private final int row;
    private final int column;
    private final double threshold;
    
    public NonSymmetricMatrixException(final int row, final int column, final double threshold) {
        super(LocalizedFormats.NON_SYMMETRIC_MATRIX, new Object[] { row, column, threshold });
        this.row = row;
        this.column = column;
        this.threshold = threshold;
    }
    
    public int getRow() {
        return this.row;
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public double getThreshold() {
        return this.threshold;
    }
}
