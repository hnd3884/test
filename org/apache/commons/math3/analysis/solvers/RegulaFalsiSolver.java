package org.apache.commons.math3.analysis.solvers;

public class RegulaFalsiSolver extends BaseSecantSolver
{
    public RegulaFalsiSolver() {
        super(1.0E-6, Method.REGULA_FALSI);
    }
    
    public RegulaFalsiSolver(final double absoluteAccuracy) {
        super(absoluteAccuracy, Method.REGULA_FALSI);
    }
    
    public RegulaFalsiSolver(final double relativeAccuracy, final double absoluteAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, Method.REGULA_FALSI);
    }
    
    public RegulaFalsiSolver(final double relativeAccuracy, final double absoluteAccuracy, final double functionValueAccuracy) {
        super(relativeAccuracy, absoluteAccuracy, functionValueAccuracy, Method.REGULA_FALSI);
    }
}
