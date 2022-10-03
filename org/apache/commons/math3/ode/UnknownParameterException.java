package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class UnknownParameterException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 20120902L;
    private final String name;
    
    public UnknownParameterException(final String name) {
        super(LocalizedFormats.UNKNOWN_PARAMETER, new Object[] { name });
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
