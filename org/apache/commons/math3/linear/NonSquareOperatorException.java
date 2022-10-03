package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class NonSquareOperatorException extends DimensionMismatchException
{
    private static final long serialVersionUID = -4145007524150846242L;
    
    public NonSquareOperatorException(final int wrong, final int expected) {
        super(LocalizedFormats.NON_SQUARE_OPERATOR, wrong, expected);
    }
}
