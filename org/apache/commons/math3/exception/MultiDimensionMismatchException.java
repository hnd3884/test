package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class MultiDimensionMismatchException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -8415396756375798143L;
    private final Integer[] wrong;
    private final Integer[] expected;
    
    public MultiDimensionMismatchException(final Integer[] wrong, final Integer[] expected) {
        this(LocalizedFormats.DIMENSIONS_MISMATCH, wrong, expected);
    }
    
    public MultiDimensionMismatchException(final Localizable specific, final Integer[] wrong, final Integer[] expected) {
        super(specific, new Object[] { wrong, expected });
        this.wrong = wrong.clone();
        this.expected = expected.clone();
    }
    
    public Integer[] getWrongDimensions() {
        return this.wrong.clone();
    }
    
    public Integer[] getExpectedDimensions() {
        return this.expected.clone();
    }
    
    public int getWrongDimension(final int index) {
        return this.wrong[index];
    }
    
    public int getExpectedDimension(final int index) {
        return this.expected[index];
    }
}
