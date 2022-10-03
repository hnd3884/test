package org.apache.commons.math3.linear;

public interface DecompositionSolver
{
    RealVector solve(final RealVector p0) throws SingularMatrixException;
    
    RealMatrix solve(final RealMatrix p0) throws SingularMatrixException;
    
    boolean isNonSingular();
    
    RealMatrix getInverse() throws SingularMatrixException;
}
