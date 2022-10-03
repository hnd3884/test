package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.MathIllegalArgumentException;

public interface MutationPolicy
{
    Chromosome mutate(final Chromosome p0) throws MathIllegalArgumentException;
}
