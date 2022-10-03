package org.apache.commons.math3.analysis.solvers;

public class IllinoisSolver extends BaseSecantSolver
{
    public IllinoisSolver() {
        super(1.0E-6, Method.ILLINOIS);
    }
    
    public IllinoisSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy, Method.ILLINOIS);
    }
    
    public IllinoisSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, Method.ILLINOIS);
    }
    
    public IllinoisSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, Method.PEGASUS);
    }
}
