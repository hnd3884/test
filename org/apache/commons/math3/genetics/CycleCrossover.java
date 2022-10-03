package org.apache.commons.math3.genetics;

import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.util.LocalizedFormats;

public class CycleCrossover<T> implements CrossoverPolicy
{
    private final boolean randomStart;
    
    public CycleCrossover() {
        this(false);
    }
    
    public CycleCrossover(final boolean randomStart) {
        this.randomStart = randomStart;
    }
    
    public boolean isRandomStart() {
        return this.randomStart;
    }
    
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
        final List<T> child1Rep = new ArrayList<T>((Collection<? extends T>)second.getRepresentation());
        final List<T> child2Rep = new ArrayList<T>((Collection<? extends T>)first.getRepresentation());
        final Set<Integer> visitedIndices = new HashSet<Integer>(length);
        final List<Integer> indices = new ArrayList<Integer>(length);
        int idx = this.randomStart ? GeneticAlgorithm.getRandomGenerator().nextInt(length) : 0;
        int cycle = 1;
        while (visitedIndices.size() < length) {
            indices.add(idx);
            T item;
            for (item = parent2Rep.get(idx), idx = parent1Rep.indexOf(item); idx != indices.get(0); idx = parent1Rep.indexOf(item)) {
                indices.add(idx);
                item = parent2Rep.get(idx);
            }
            if (cycle++ % 2 != 0) {
                for (final int i : indices) {
                    final T tmp = child1Rep.get(i);
                    child1Rep.set(i, child2Rep.get(i));
                    child2Rep.set(i, tmp);
                }
            }
            visitedIndices.addAll(indices);
            for (idx = (indices.get(0) + 1) % length; visitedIndices.contains(idx) && visitedIndices.size() < length; idx = 0) {
                if (++idx >= length) {}
            }
            indices.clear();
        }
        return new ChromosomePair(first.newFixedLengthChromosome(child1Rep), second.newFixedLengthChromosome(child2Rep));
    }
}
