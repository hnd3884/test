package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class SingularOperatorException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = -476049978595245033L;
    
    public SingularOperatorException() {
        super(LocalizedFormats.SINGULAR_OPERATOR, new Object[0]);
    }
}
