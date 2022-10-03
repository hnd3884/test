package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class ZeroException extends MathIllegalNumberException
{
    private static final long serialVersionUID = -1960874856936000015L;
    
    public ZeroException() {
        this(LocalizedFormats.ZERO_NOT_ALLOWED, new Object[0]);
    }
    
    public ZeroException(final Localizable specific, final Object... arguments) {
        super(specific, ZeroException.INTEGER_ZERO, arguments);
    }
}
