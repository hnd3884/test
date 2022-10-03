package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooSmallException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NotPositiveException;
import java.util.Collections;
import java.util.List;

public abstract class ListPopulation implements Population
{
    private List<Chromosome> chromosomes;
    private int populationLimit;
    
    public ListPopulation(final int populationLimit) throws NotPositiveException {
        this(Collections.emptyList(), populationLimit);
    }
    
    public ListPopulation(final List<Chromosome> chromosomes, final int populationLimit) throws NullArgumentException, NotPositiveException, NumberIsTooLargeException {
        if (chromosomes == null) {
            throw new NullArgumentException();
        }
        if (populationLimit <= 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit);
        }
        if (chromosomes.size() > populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, chromosomes.size(), populationLimit, false);
        }
        this.populationLimit = populationLimit;
        (this.chromosomes = new ArrayList<Chromosome>(populationLimit)).addAll(chromosomes);
    }
    
    @Deprecated
    public void setChromosomes(final List<Chromosome> chromosomes) throws NullArgumentException, NumberIsTooLargeException {
        if (chromosomes == null) {
            throw new NullArgumentException();
        }
        if (chromosomes.size() > this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, chromosomes.size(), this.populationLimit, false);
        }
        this.chromosomes.clear();
        this.chromosomes.addAll(chromosomes);
    }
    
    public void addChromosomes(final Collection<Chromosome> chromosomeColl) throws NumberIsTooLargeException {
        if (this.chromosomes.size() + chromosomeColl.size() > this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.chromosomes.size(), this.populationLimit, false);
        }
        this.chromosomes.addAll(chromosomeColl);
    }
    
    public List<Chromosome> getChromosomes() {
        return Collections.unmodifiableList((List<? extends Chromosome>)this.chromosomes);
    }
    
    protected List<Chromosome> getChromosomeList() {
        return this.chromosomes;
    }
    
    public void addChromosome(final Chromosome chromosome) throws NumberIsTooLargeException {
        if (this.chromosomes.size() >= this.populationLimit) {
            throw new NumberIsTooLargeException(LocalizedFormats.LIST_OF_CHROMOSOMES_BIGGER_THAN_POPULATION_SIZE, this.chromosomes.size(), this.populationLimit, false);
        }
        this.chromosomes.add(chromosome);
    }
    
    public Chromosome getFittestChromosome() {
        Chromosome bestChromosome = this.chromosomes.get(0);
        for (final Chromosome chromosome : this.chromosomes) {
            if (chromosome.compareTo(bestChromosome) > 0) {
                bestChromosome = chromosome;
            }
        }
        return bestChromosome;
    }
    
    public int getPopulationLimit() {
        return this.populationLimit;
    }
    
    public void setPopulationLimit(final int populationLimit) throws NotPositiveException, NumberIsTooSmallException {
        if (populationLimit <= 0) {
            throw new NotPositiveException(LocalizedFormats.POPULATION_LIMIT_NOT_POSITIVE, populationLimit);
        }
        if (populationLimit < this.chromosomes.size()) {
            throw new NumberIsTooSmallException(populationLimit, this.chromosomes.size(), true);
        }
        this.populationLimit = populationLimit;
    }
    
    public int getPopulationSize() {
        return this.chromosomes.size();
    }
    
    @Override
    public String toString() {
        return this.chromosomes.toString();
    }
    
    public Iterator<Chromosome> iterator() {
        return this.getChromosomes().iterator();
    }
}
