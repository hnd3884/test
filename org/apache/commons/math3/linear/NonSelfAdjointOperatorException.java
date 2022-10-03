package org.apache.commons.math3.linear;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class NonSelfAdjointOperatorException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 1784999305030258247L;
    
    public NonSelfAdjointOperatorException() {
        super(LocalizedFormats.NON_SELF_ADJOINT_OPERATOR, new Object[0]);
    }
}
