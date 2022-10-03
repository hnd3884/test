package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class SingularMatrixException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -4206514844735401070L;
    
    public SingularMatrixException() {
        super(LocalizedFormats.SINGULAR_MATRIX, new Object[0]);
    }
}
