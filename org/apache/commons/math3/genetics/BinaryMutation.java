package org.apache.commons.math3.genetics;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class BinaryMutation implements MutationPolicy
{
    public Chromosome mutate(final Chromosome original) throws MathIllegalArgumentException {
        if (!(original instanceof BinaryChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_BINARY_CHROMOSOME, new Object[0]);
        }
        final BinaryChromosome origChrom = (BinaryChromosome)original;
        final List<Integer> newRepr = new ArrayList<Integer>(origChrom.getRepresentation());
        final int geneIndex = GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
        newRepr.set(geneIndex, (origChrom.getRepresentation().get(geneIndex) == 0) ? 1 : 0);
        final Chromosome newChrom = origChrom.newFixedLengthChromosome(newRepr);
        return newChrom;
    }
}
