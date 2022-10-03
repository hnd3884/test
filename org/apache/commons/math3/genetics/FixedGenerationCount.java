package org.apache.commons.math3.genetics;

import org.apache.commons.math3.exception.NumberIsTooSmallException;

public class FixedGenerationCount implements StoppingCondition
{
    private int numGenerations;
    private final int maxGenerations;
    
    public FixedGenerationCount(final int maxGenerations) throws NumberIsTooSmallException {
        this.numGenerations = 0;
        if (maxGenerations <= 0) {
            throw new NumberIsTooSmallException(maxGenerations, 1, true);
        }
        this.maxGenerations = maxGenerations;
    }
    
    public boolean isSatisfied(final Population population) {
        if (this.numGenerations < this.maxGenerations) {
            ++this.numGenerations;
            return false;
        }
        return true;
    }
    
    public int getNumGenerations() {
        return this.numGenerations;
    }
}
