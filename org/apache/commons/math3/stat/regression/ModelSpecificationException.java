package org.apache.commons.math3.stat.regression;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class ModelSpecificationException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 4206514456095401070L;
    
    public ModelSpecificationException(final Localizable pattern, final Object... args) {
        super(pattern, args);
    }
}
