package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;

public class MathIllegalNumberException extends MathIllegalArgumentException
{
    protected static final Integer INTEGER_ZERO;
    private static final long serialVersionUID = -7447085893598031110L;
    private final Number argument;
    
    protected MathIllegalNumberException(final Localizable pattern, final Number wrong, final Object... arguments) {
        super(pattern, new Object[] { wrong, arguments });
        this.argument = wrong;
    }
    
    public Number getArgument() {
        return this.argument;
    }
    
    static {
        INTEGER_ZERO = 0;
    }
}
