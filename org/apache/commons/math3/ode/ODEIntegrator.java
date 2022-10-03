package org.apache.commons.math3.ode;

import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.ode.events.EventHandler;
import java.util.Collection;
import org.apache.commons.math3.ode.sampling.StepHandler;

public interface ODEIntegrator
{
    String getName();
    
    void addStepHandler(final StepHandler p0);
    
    Collection<StepHandler> getStepHandlers();
    
    void clearStepHandlers();
    
    void addEventHandler(final EventHandler p0, final double p1, final double p2, final int p3);
    
    void addEventHandler(final EventHandler p0, final double p1, final double p2, final int p3, final UnivariateSolver p4);
    
    Collection<EventHandler> getEventHandlers();
    
    void clearEventHandlers();
    
    double getCurrentStepStart();
    
    double getCurrentSignedStepsize();
    
    void setMaxEvaluations(final int p0);
    
    int getMaxEvaluations();
    
    int getEvaluations();
}
