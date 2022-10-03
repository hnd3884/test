package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class IllConditionedOperatorException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -7883263944530490135L;
    
    public IllConditionedOperatorException(final double cond) {
        super(LocalizedFormats.ILL_CONDITIONED_OPERATOR, new Object[] { cond });
    }
}
