package org.apache.commons.math3.genetics;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class RandomKeyMutation implements MutationPolicy
{
    public Chromosome mutate(final Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof RandomKey)) {
            throw new MathIllegalArgumentException(LocalizedFormats.RANDOMKEY_MUTATION_WRONG_CLASS, new Object[] { original.getClass().getSimpleName() });
        }
        final RandomKey<?> originalRk = (RandomKey<?>)original;
        final List<Double> repr = originalRk.getRepresentation();
        final int rInd = GeneticAlgorithm.getRandomGenerator().nextInt(repr.size());
        final List<Double> newRepr = new ArrayList<Double>(repr);
        newRepr.set(rInd, GeneticAlgorithm.getRandomGenerator().nextDouble());
        return originalRk.newFixedLengthChromosome(newRepr);
    }
}
