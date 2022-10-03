package org.apache.commons.math3.analysis.solvers;

public class PegasusSolver extends BaseSecantSolver
{
    public PegasusSolver() {
        super(1.0E-6, Method.PEGASUS);
    }
    
    public PegasusSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy, Method.PEGASUS);
    }
    
    public PegasusSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, Method.PEGASUS);
    }
    
    public PegasusSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, Method.PEGASUS);
    }
}
