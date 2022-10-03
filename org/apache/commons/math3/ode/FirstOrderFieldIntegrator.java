package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import java.util.Collection;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import org.apache.commons.math3.RealFieldElement;

public interface FirstOrderFieldIntegrator<T extends RealFieldElement<T>>
{
    String getName();
    
    void addStepHandler(final FieldStepHandler<T> p0);
    
    Collection<FieldStepHandler<T>> getStepHandlers();
    
    void clearStepHandlers();
    
    void addEventHandler(final FieldEventHandler<T> p0, final double p1, final double p2, final int p3);
    
    void addEventHandler(final FieldEventHandler<T> p0, final double p1, final double p2, final int p3, final BracketedRealFieldUnivariateSolver<T> p4);
    
    Collection<FieldEventHandler<T>> getEventHandlers();
    
    void clearEventHandlers();
    
    FieldODEStateAndDerivative<T> getCurrentStepStart();
    
    T getCurrentSignedStepsize();
    
    void setMaxEvaluations(final int p0);
    
    int getMaxEvaluations();
    
    int getEvaluations();
    
    FieldODEStateAndDerivative<T> integrate(final FieldExpandableODE<T> p0, final FieldODEState<T> p1, final T p2) throws NumberIsTooSmallException, MaxCountExceededException, NoBracketingException;
}
