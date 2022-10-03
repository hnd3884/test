package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class InvalidRepresentationException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 1L;
    
    public InvalidRepresentationException(final Localizable pattern, final Object... args) {
        super(pattern, args);
    }
}
