package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NoBracketingException;
import java.util.SortedSet;
import org.apache.commons.math3.util.FastMath;
import java.util.TreeSet;
import java.util.Comparator;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.ode.sampling.AbstractFieldStepInterpolator;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.analysis.solvers.FieldBracketingNthOrderBrentSolver;
import org.apache.commons.math3.ode.events.FieldEventHandler;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.ode.events.FieldEventState;
import org.apache.commons.math3.Field;
import org.apache.commons.math3.ode.sampling.FieldStepHandler;
import java.util.Collection;
import org.apache.commons.math3.RealFieldElement;

public abstract class AbstractFieldIntegrator<T extends RealFieldElement<T>> implements FirstOrderFieldIntegrator<T>
{
    private static final double DEFAULT_RELATIVE_ACCURACY = 1.0E-14;
    private static final double DEFAULT_FUNCTION_VALUE_ACCURACY = 1.0E-15;
    private Collection<FieldStepHandler<T>> stepHandlers;
    private FieldODEStateAndDerivative<T> stepStart;
    private T stepSize;
    private boolean isLastStep;
    private boolean resetOccurred;
    private final Field<T> field;
    private Collection<FieldEventState<T>> eventsStates;
    private boolean statesInitialized;
    private final String name;
    private IntegerSequence.Incrementor evaluations;
    private transient FieldExpandableODE<T> equations;
    
    protected AbstractFieldIntegrator(final Field<T> field, final String name) {
        this.field = field;
        this.name = name;
        this.stepHandlers = new ArrayList<FieldStepHandler<T>>();
        this.stepStart = null;
        this.stepSize = null;
        this.eventsStates = new ArrayList<FieldEventState<T>>();
        this.statesInitialized = false;
        this.evaluations = IntegerSequence.Incrementor.create().withMaximalCount(Integer.MAX_VALUE);
    }
    
    public Field<T> getField() {
        return this.field;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addStepHandler(final FieldStepHandler<T> handler) {
        this.stepHandlers.add(handler);
    }
    
    public Collection<FieldStepHandler<T>> getStepHandlers() {
        return Collections.unmodifiableCollection((Collection<? extends FieldStepHandler<T>>)this.stepHandlers);
    }
    
    public void clearStepHandlers() {
        this.stepHandlers.clear();
    }
    
    public void addEventHandler(final FieldEventHandler<T> handler, final double maxCheckInterval, final double convergence, final int maxIterationCount) {
        this.addEventHandler(handler, maxCheckInterval, convergence, maxIterationCount, new FieldBracketingNthOrderBrentSolver<T>(this.field.getZero().add(1.0E-14), this.field.getZero().add(convergence), this.field.getZero().add(1.0E-15), 5));
    }
    
    public void addEventHandler(final FieldEventHandler<T> handler, final double maxCheckInterval, final double convergence, final int maxIterationCount, final BracketedRealFieldUnivariateSolver<T> solver) {
        this.eventsStates.add(new FieldEventState<T>(handler, maxCheckInterval, this.field.getZero().add(convergence), maxIterationCount, solver));
    }
    
    public Collection<FieldEventHandler<T>> getEventHandlers() {
        final List<FieldEventHandler<T>> list = new ArrayList<FieldEventHandler<T>>(this.eventsStates.size());
        for (final FieldEventState<T> state : this.eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection((Collection<? extends FieldEventHandler<T>>)list);
    }
    
    public void clearEventHandlers() {
        this.eventsStates.clear();
    }
    
    public FieldODEStateAndDerivative<T> getCurrentStepStart() {
        return this.stepStart;
    }
    
    public T getCurrentSignedStepsize() {
        return this.stepSize;
    }
    
    public void setMaxEvaluations(final int maxEvaluations) {
        this.evaluations = this.evaluations.withMaximalCount((maxEvaluations < 0) ? Integer.MAX_VALUE : maxEvaluations);
    }
    
    public int getMaxEvaluations() {
        return this.evaluations.getMaximalCount();
    }
    
    public int getEvaluations() {
        return this.evaluations.getCount();
    }
    
    protected FieldODEStateAndDerivative<T> initIntegration(final FieldExpandableODE<T> eqn, final T t0, final T[] y0, final T t) {
        this.equations = eqn;
        this.evaluations = this.evaluations.withStart(0);
        eqn.init(t0, y0, t);
        final T[] y0Dot = this.computeDerivatives(t0, y0);
        final FieldODEStateAndDerivative<T> state0 = new FieldODEStateAndDerivative<T>(t0, y0, y0Dot);
        for (final FieldEventState<T> state2 : this.eventsStates) {
            state2.getEventHandler().init(state0, t);
        }
        for (final FieldStepHandler<T> handler : this.stepHandlers) {
            handler.init(state0, t);
        }
        this.setStateInitialized(false);
        return state0;
    }
    
    protected FieldExpandableODE<T> getEquations() {
        return this.equations;
    }
    
    protected IntegerSequence.Incrementor getEvaluationsCounter() {
        return this.evaluations;
    }
    
    public T[] computeDerivatives(final T t, final T[] y) throws DimensionMismatchException, MaxCountExceededException, NullPointerException {
        this.evaluations.increment();
        return this.equations.computeDerivatives(t, y);
    }
    
    protected void setStateInitialized(final boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }
    
    protected FieldODEStateAndDerivative<T> acceptStep(final AbstractFieldStepInterpolator<T> interpolator, final T tEnd) throws MaxCountExceededException, DimensionMismatchException, NoBracketingException {
        FieldODEStateAndDerivative<T> previousState = interpolator.getGlobalPreviousState();
        final FieldODEStateAndDerivative<T> currentState = interpolator.getGlobalCurrentState();
        if (!this.statesInitialized) {
            for (final FieldEventState<T> state : this.eventsStates) {
                state.reinitializeBegin(interpolator);
            }
            this.statesInitialized = true;
        }
        final int orderingSign = interpolator.isForward() ? 1 : -1;
        final SortedSet<FieldEventState<T>> occurringEvents = new TreeSet<FieldEventState<T>>(new Comparator<FieldEventState<T>>() {
            public int compare(final FieldEventState<T> es0, final FieldEventState<T> es1) {
                return orderingSign * Double.compare(es0.getEventTime().getReal(), es1.getEventTime().getReal());
            }
        });
        for (final FieldEventState<T> state2 : this.eventsStates) {
            if (state2.evaluateStep(interpolator)) {
                occurringEvents.add(state2);
            }
        }
        AbstractFieldStepInterpolator<T> restricted = interpolator;
        while (!occurringEvents.isEmpty()) {
            final Iterator<FieldEventState<T>> iterator = occurringEvents.iterator();
            final FieldEventState<T> currentEvent = iterator.next();
            iterator.remove();
            final FieldODEStateAndDerivative<T> eventState = restricted.getInterpolatedState(currentEvent.getEventTime());
            restricted = restricted.restrictStep(previousState, eventState);
            for (final FieldEventState<T> state3 : this.eventsStates) {
                state3.stepAccepted(eventState);
                this.isLastStep = (this.isLastStep || state3.stop());
            }
            for (final FieldStepHandler<T> handler : this.stepHandlers) {
                handler.handleStep(restricted, this.isLastStep);
            }
            if (this.isLastStep) {
                return eventState;
            }
            FieldODEState<T> newState = null;
            this.resetOccurred = false;
            for (final FieldEventState<T> state4 : this.eventsStates) {
                newState = state4.reset(eventState);
                if (newState != null) {
                    final T[] y = this.equations.getMapper().mapState(newState);
                    final T[] yDot = this.computeDerivatives(newState.getTime(), y);
                    this.resetOccurred = true;
                    return this.equations.getMapper().mapStateAndDerivative(newState.getTime(), y, yDot);
                }
            }
            previousState = eventState;
            restricted = restricted.restrictStep(eventState, currentState);
            if (!currentEvent.evaluateStep(restricted)) {
                continue;
            }
            occurringEvents.add(currentEvent);
        }
        for (final FieldEventState<T> state5 : this.eventsStates) {
            state5.stepAccepted(currentState);
            this.isLastStep = (this.isLastStep || state5.stop());
        }
        this.isLastStep = (this.isLastStep || ((RealFieldElement<RealFieldElement>)currentState.getTime().subtract(tEnd)).abs().getReal() <= FastMath.ulp(tEnd.getReal()));
        for (final FieldStepHandler<T> handler2 : this.stepHandlers) {
            handler2.handleStep(restricted, this.isLastStep);
        }
        return currentState;
    }
    
    protected void sanityChecks(final FieldODEState<T> eqn, final T t) throws NumberIsTooSmallException, DimensionMismatchException {
        final double threshold = 1000.0 * FastMath.ulp(FastMath.max(FastMath.abs(eqn.getTime().getReal()), FastMath.abs(t.getReal())));
        final double dt = ((RealFieldElement<RealFieldElement>)eqn.getTime().subtract(t)).abs().getReal();
        if (dt <= threshold) {
            throw new NumberIsTooSmallException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, dt, threshold, false);
        }
    }
    
    protected boolean resetOccurred() {
        return this.resetOccurred;
    }
    
    protected void setStepSize(final T stepSize) {
        this.stepSize = stepSize;
    }
    
    protected T getStepSize() {
        return this.stepSize;
    }
    
    protected void setStepStart(final FieldODEStateAndDerivative<T> stepStart) {
        this.stepStart = stepStart;
    }
    
    protected FieldODEStateAndDerivative<T> getStepStart() {
        return this.stepStart;
    }
    
    protected void setIsLastStep(final boolean isLastStep) {
        this.isLastStep = isLastStep;
    }
    
    protected boolean isLastStep() {
        return this.isLastStep;
    }
}
