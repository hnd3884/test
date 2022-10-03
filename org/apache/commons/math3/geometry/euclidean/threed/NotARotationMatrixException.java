package org.apache.commons.math3.geometry.euclidean.threed;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class NotARotationMatrixException extends MathIllegalArgumentException
{
    private static final long serialVersionUID = 5647178478658937642L;
    
    public NotARotationMatrixException(final Localizable specifier, final Object... parts) {
        super(specifier, parts);
    }
}
