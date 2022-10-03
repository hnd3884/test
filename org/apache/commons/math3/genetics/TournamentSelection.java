package org.apache.commons.math3.genetics;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

public class TournamentSelection implements SelectionPolicy
{
    private int arity;
    
    public TournamentSelection(final int arity) {
        this.arity = arity;
    }
    
    public ChromosomePair select(final Population population) throws MathIllegalArgumentException {
        return new ChromosomePair(this.tournament((ListPopulation)population), this.tournament((ListPopulation)population));
    }
    
    private Chromosome tournament(final ListPopulation population) throws MathIllegalArgumentException {
        if (population.getPopulationSize() < this.arity) {
            throw new MathIllegalArgumentException(LocalizedFormats.TOO_LARGE_TOURNAMENT_ARITY, new Object[] { this.arity, population.getPopulationSize() });
        }
        final ListPopulation tournamentPopulation = new ListPopulation(this.arity) {
            public Population nextGeneration() {
                return null;
            }
        };
        final List<Chromosome> chromosomes = new ArrayList<Chromosome>(population.getChromosomes());
        for (int i = 0; i < this.arity; ++i) {
            final int rind = GeneticAlgorithm.getRandomGenerator().nextInt(chromosomes.size());
            tournamentPopulation.addChromosome(chromosomes.get(rind));
            chromosomes.remove(rind);
        }
        return tournamentPopulation.getFittestChromosome();
    }
    
    public int getArity() {
        return this.arity;
    }
    
    public void setArity(final int arity) {
        this.arity = arity;
    }
}
