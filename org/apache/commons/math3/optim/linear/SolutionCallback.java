package org.apache.commons.math3.optim.linear;

import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.OptimizationData;

public class SolutionCallback implements OptimizationData
{
    private SimplexTableau tableau;
    
    void setTableau(final SimplexTableau tableau) {
        this.tableau = tableau;
    }
    
    public PointValuePair getSolution() {
        return (this.tableau != null) ? this.tableau.getSolution() : null;
    }
    
    public boolean isSolutionOptimal() {
        return this.tableau != null && this.tableau.isOptimal();
    }
}
