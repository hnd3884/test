package org.apache.commons.math3.genetics;

import org.apache.commons.math3.random.RandomGenerator;
import java.util.Set;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import org.apache.commons.math3.util.FastMath;
import java.util.HashSet;
import java.util.ArrayList;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class OrderedCrossover<T> implements CrossoverPolicy
{
    public ChromosomePair crossover(final Chromosome first, final Chromosome second) throws DimensionMismatchException, MathIllegalArgumentException {
        if (!(first instanceof AbstractListChromosome) || !(second instanceof AbstractListChromosome)) {
            throw new MathIllegalArgumentException(LocalizedFormats.INVALID_FIXED_LENGTH_CHROMOSOME, new Object[0]);
        }
        return this.mate((AbstractListChromosome<T>)first, (AbstractListChromosome<T>)second);
    }
    
    protected ChromosomePair mate(final AbstractListChromosome<T> first, final AbstractListChromosome<T> second) throws DimensionMismatchException {
        final int length = first.getLength();
        if (length != second.getLength()) {
            throw new DimensionMismatchException(second.getLength(), length);
        }
        final List<T> parent1Rep = first.getRepresentation();
        final List<T> parent2Rep = second.getRepresentation();
        final List<T> child1 = new ArrayList<T>(length);
        final List<T> child2 = new ArrayList<T>(length);
        final Set<T> child1Set = new HashSet<T>(length);
        final Set<T> child2Set = new HashSet<T>(length);
        final RandomGenerator random = GeneticAlgorithm.getRandomGenerator();
        final int a = random.nextInt(length);
        int b;
        do {
            b = random.nextInt(length);
        } while (a == b);
        final int lb = FastMath.min(a, b);
        final int ub = FastMath.max(a, b);
        child1.addAll((Collection<? extends T>)parent1Rep.subList(lb, ub + 1));
        child1Set.addAll((Collection<? extends T>)child1);
        child2.addAll((Collection<? extends T>)parent2Rep.subList(lb, ub + 1));
        child2Set.addAll((Collection<? extends T>)child2);
        for (int i = 1; i <= length; ++i) {
            final int idx = (ub + i) % length;
            final T item1 = parent1Rep.get(idx);
            final T item2 = parent2Rep.get(idx);
            if (!child1Set.contains(item2)) {
                child1.add(item2);
                child1Set.add(item2);
            }
            if (!child2Set.contains(item1)) {
                child2.add(item1);
                child2Set.add(item1);
            }
        }
        Collections.rotate(child1, lb);
        Collections.rotate(child2, lb);
        return new ChromosomePair(first.newFixedLengthChromosome(child1), second.newFixedLengthChromosome(child2));
    }
}
