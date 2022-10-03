package org.apache.commons.math3.genetics;

import org.apache.commons.math3.random.RandomGenerator;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;

public class NPointCrossover<T> implements CrossoverPolicy
{
    private final int crossoverPoints;
    
    public NPointCrossover(final int crossoverPoints) throws NotStrictlyPositiveException {
        if (crossoverPoints <= 0) {
            throw new NotStrictlyPositiveException(crossoverPoints);
        }
        this.crossoverPoints = crossoverPoints;
    }
    
    public int getCrossoverPoints() {
        return this.crossoverPoints;
    }
    
    public ChromosomePair crossover(final Chromosome first, final Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
        if (!(first instanceof AbstractListChromosome) || !(second instanceof AbstractListChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME, new Object[0]);
        }
        return this.mate((AbstractListChromosome<T>)first, (AbstractListChromosome<T>)second);
    }
    
    private ChromosomePair mate(final AbstractListChromosome<T> first, final AbstractListChromosome<T> second) throws DimensionMismatchException, NumberIsTooLargeException {
        final int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        }
        if (this.crossoverPoints >= length) {
            throw new NumberIsTooLargeException(this.crossoverPoints, length, false);
        }
        final List<T> parent1Rep = first.getRepresentation();
        final List<T> parent2Rep = second.getRepresentation();
        final List<T> child1Rep = new ArrayList<T>(length);
        final List<T> child2Rep = new ArrayList<T>(length);
        final RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
        List<T> c1 = child1Rep;
        List<T> c2 = child2Rep;
        int remainingPoints = this.crossoverPoints;
        int lastIndex = 0;
        for (int i = 0; i < this.crossoverPoints; ++i, --remainingPoints) {
            final int crossoverIndex = 1 + lastIndex + random.nextInt(length - lastIndex - remainingPoints);
            for (int j = lastIndex; j < crossoverIndex; ++j) {
                c1.add(parent1Rep.get(j));
                c2.add(parent2Rep.get(j));
            }
            final List<T> tmp = c1;
            c1 = c2;
            c2 = tmp;
            lastIndex = crossoverIndex;
        }
        for (int k = lastIndex; k < length; ++k) {
            c1.add(parent1Rep.get(k));
            c2.add(parent2Rep.get(k));
        }
        return new ChromosomePair(first.newFixedLengthChromosome(child1Rep), second.newFixedLengthChromosome(child2Rep));
    }
}
