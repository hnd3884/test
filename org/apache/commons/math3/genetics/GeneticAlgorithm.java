package org.apache.commons.math3.genetics;

import org.apache.commons.math3.random.JDKRandomGenerator;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.random.RandomGenerator;

public class GeneticAlgorithm
{
    private static RandomGenerator randomGenerator;
    private final CrossoverPolicy crossoverPolicy;
    private final double crossoverRate;
    private final MutationPolicy mutationPolicy;
    private final double mutationRate;
    private final SelectionPolicy selectionPolicy;
    private int generationsEvolved;
    
    public GeneticAlgorithm(final CrossoverPolicy crossoverPolicy, final double crossoverRate, final MutationPolicy mutationPolicy, final double mutationRate, final SelectionPolicy selectionPolicy) throws OutOfRangeException {
        this.generationsEvolved = 0;
        if (crossoverRate < 0.0 || crossoverRate > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.CROSSOVER_RATE, crossoverRate, 0, 1);
        }
        if (mutationRate < 0.0 || mutationRate > 1.0) {
            throw new OutOfRangeException(LocalizedFormats.MUTATION_RATE, mutationRate, 0, 1);
        }
        this.crossoverPolicy = crossoverPolicy;
        this.crossoverRate = crossoverRate;
        this.mutationPolicy = mutationPolicy;
        this.mutationRate = mutationRate;
        this.selectionPolicy = selectionPolicy;
    }
    
    public static synchronized void setRandomGenerator(final RandomGenerator random) {
        GeneticAlgorithm.randomGenerator = random;
    }
    
    public static synchronized RandomGenerator getRandomGenerator() {
        return GeneticAlgorithm.randomGenerator;
    }
    
    public Population evolve(final Population initial, final StoppingCondition condition) {
        Population current = initial;
        this.generationsEvolved = 0;
        while (!condition.isSatisfied(current)) {
            current = this.nextGeneration(current);
            ++this.generationsEvolved;
        }
        return current;
    }
    
    public Population nextGeneration(final Population current) {
        final Population nextGeneration = current.nextGeneration();
        final RandomGenerator randGen = getRandomGenerator();
        while (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
            ChromosomePair pair = this.getSelectionPolicy().select(current);
            if (randGen.nextDouble() < this.getCrossoverRate()) {
                pair = this.getCrossoverPolicy().crossover(pair.getFirst(), pair.getSecond());
            }
            if (randGen.nextDouble() < this.getMutationRate()) {
                pair = new ChromosomePair(this.getMutationPolicy().mutate(pair.getFirst()), this.getMutationPolicy().mutate(pair.getSecond()));
            }
            nextGeneration.addChromosome(pair.getFirst());
            if (nextGeneration.getPopulationSize() < nextGeneration.getPopulationLimit()) {
                nextGeneration.addChromosome(pair.getSecond());
            }
        }
        return nextGeneration;
    }
    
    public CrossoverPolicy getCrossoverPolicy() {
        return this.crossoverPolicy;
    }
    
    public double getCrossoverRate() {
        return this.crossoverRate;
    }
    
    public MutationPolicy getMutationPolicy() {
        return this.mutationPolicy;
    }
    
    public double getMutationRate() {
        return this.mutationRate;
    }
    
    public SelectionPolicy getSelectionPolicy() {
        return this.selectionPolicy;
    }
    
    public int getGenerationsEvolved() {
        return this.generationsEvolved;
    }
    
    static {
        GeneticAlgorithm.randomGenerator = new JDKRandomGenerator();
    }
}
