package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.Serializable;

public class DefaultTransformer implements NumberTransformer, Serializable
{
    private static final long serialVersionUID = 4019938025047800455L;
    
    public double transform(final Object o) throws NullArgumentException, MathIllegalArgumentException {
        if (o == null) {
            throw new NullArgumentException(LocalizedFormats.OBJECT_TRANSFORMATION, new Object[0]);
        }
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        try {
            return Double.parseDouble(o.toString());
        }
        catch (final NumberFormatException e) {
            throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE, new Object[] { o.toString() });
        }
    }
    
    @Override
    public boolean equals(final Object other) {
        return this == other || other instanceof DefaultTransformer;
    }
    
    @Override
    public int hashCode() {
        return 401993047;
    }
}
