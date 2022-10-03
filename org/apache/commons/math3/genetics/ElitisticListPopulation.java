package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import java.util.Collections;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.List;

public class ElitisticListPopulation extends ListPopulation
{
    private double elitismRate;
    
    public ElitisticListPopulation(final List<Chromosome> chromosomes, final int populationLimit, final double elitismRate) throws NullArgumentException, NotPositiveException, NumberIsTooLargeException, OutOfRangeException {
        super(chromosomes, populationLimit);
        this.elitismRate = 0.9;
        this.setElitismRate(elitismRate);
    }
    
    public ElitisticListPopulation(final int populationLimit, final double elitismRate) throws NotPositiveException, OutOfRangeException {
        super(populationLimit);
        this.elitismRate = 0.9;
        this.setElitismRate(elitismRate);
    }
    
    public Population nextGeneration() {
        final ElitisticListPopulation nextGeneration = new ElitisticListPopulation(this.getPopulationLimit(), this.getElitismRate());
        final List<Chromosome> oldChromosomes = this.getChromosomeList();
        Collections.sort(oldChromosomes);
        int i;
        for (int boundIndex = i = (int)FastMath.ceil((1.0 - this.getElitismRate()) * oldChromosomes.size()); i < oldChromosomes.size(); ++i) {
            nextGeneration.addChromosome(oldChromosomes.get(i));
        }
        return nextGeneration;
    }
    
    public void setElitismRate(final double elitismRate) throws OutOfRangeException {
        if (elitismRate < 0.0 || elitismRate > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.ELITISM_RATE, elitismRate, 0, 1);
        }
        this.elitismRate = elitismRate;
    }
    
    public double getElitismRate() {
        return this.elitismRate;
    }
}
