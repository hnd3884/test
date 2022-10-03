package org.apache.commons.math3.exception;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class NotFiniteNumberException extends MathIllegalNumberException
{
    private static final long serialVersionUID = -6100997100383932834L;
    
    public NotFiniteNumberException(final Number wrong, final Object... args) {
        this(LocalizedFormats.NOT_FINITE_NUMBER, wrong, args);
    }
    
    public NotFiniteNumberException(final Localizable specific, final Number wrong, final Object... args) {
        super(specific, wrong, args);
    }
}
