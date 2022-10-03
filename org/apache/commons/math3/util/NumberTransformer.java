package org.apache.commons.math3.util;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface NumberTransformer
{
    double transform(final Object p0) throws MathIllegalArgumentException;
}
