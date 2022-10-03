package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class NonPositiveDefiniteOperatorException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 917034489420549847L;
    
    public NonPositiveDefiniteOperatorException() {
        super(LocalizedFormats.NON_POSITIVE_DEFINITE_OPERATOR, new Object[0]);
    }
}
