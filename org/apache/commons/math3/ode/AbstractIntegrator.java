package org.apache.commons.math3.ode;

import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.util.FastMath;
import java.util.SortedSet;
import org.apache.commons.math3.util.Precision;
import java.util.TreeSet;
import java.util.Comparator;
import org.apache.commons.math3.ode.sampling.StepInterpolator;
import org.apache.commons.math3.ode.sampling.AbstractStepInterpolator;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.util.Incrementor;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.math3.analysis.solvers.UnivariateSolver;
import org.apache.commons.math3.analysis.solvers.BracketingNthOrderBrentSolver;
import org.apache.commons.math3.ode.events.EventHandler;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.commons.math3.util.IntegerSequence;
import org.apache.commons.math3.ode.events.EventState;
import org.apache.commons.math3.ode.sampling.StepHandler;
import java.util.Collection;

public abstract class AbstractIntegrator implements FirstOrderIntegrator
{
    protected Collection<StepHandler> stepHandlers;
    protected double stepStart;
    protected double stepSize;
    protected boolean isLastStep;
    protected boolean resetOccurred;
    private Collection<EventState> eventsStates;
    private boolean statesInitialized;
    private final String name;
    private IntegerSequence.Incrementor evaluations;
    private transient ExpandableStatefulODE expandable;
    
    public AbstractIntegrator(final String name) {
        this.name = name;
        this.stepHandlers = new ArrayList<StepHandler>();
        this.stepStart = Double.NaN;
        this.stepSize = Double.NaN;
        this.eventsStates = new ArrayList<EventState>();
        this.statesInitialized = false;
        this.evaluations = IntegerSequence.Incrementor.create().withMaximalCount(Integer.MAX_VALUE);
    }
    
    protected AbstractIntegrator() {
        this(null);
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addStepHandler(final StepHandler handler) {
        this.stepHandlers.add(handler);
    }
    
    public Collection<StepHandler> getStepHandlers() {
        return Collections.unmodifiableCollection((Collection<? extends StepHandler>)this.stepHandlers);
    }
    
    public void clearStepHandlers() {
        this.stepHandlers.clear();
    }
    
    public void addEventHandler(final EventHandler handler, final double maxCheckInterval, final double convergence, final int maxIterationCount) {
        this.addEventHandler(handler, maxCheckInterval, convergence, maxIterationCount, new BracketingNthOrderBrentSolver(convergence, 5));
    }
    
    public void addEventHandler(final EventHandler handler, final double maxCheckInterval, final double convergence, final int maxIterationCount, final UnivariateSolver solver) {
        this.eventsStates.add(new EventState(handler, maxCheckInterval, convergence, maxIterationCount, solver));
    }
    
    public Collection<EventHandler> getEventHandlers() {
        final List<EventHandler> list = new ArrayList<EventHandler>(this.eventsStates.size());
        for (final EventState state : this.eventsStates) {
            list.add(state.getEventHandler());
        }
        return Collections.unmodifiableCollection((Collection<? extends EventHandler>)list);
    }
    
    public void clearEventHandlers() {
        this.eventsStates.clear();
    }
    
    public double getCurrentStepStart() {
        return this.stepStart;
    }
    
    public double getCurrentSignedStepsize() {
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
    
    protected void initIntegration(final double t0, final double[] y0, final double t) {
        this.evaluations = this.evaluations.withStart(0);
        for (final EventState state : this.eventsStates) {
            state.setExpandable(this.expandable);
            state.getEventHandler().init(t0, y0, t);
        }
        for (final StepHandler handler : this.stepHandlers) {
            handler.init(t0, y0, t);
        }
        this.setStateInitialized(false);
    }
    
    protected void setEquations(final ExpandableStatefulODE equations) {
        this.expandable = equations;
    }
    
    protected ExpandableStatefulODE getExpandable() {
        return this.expandable;
    }
    
    @Deprecated
    protected Incrementor getEvaluationsCounter() {
        return Incrementor.wrap(this.evaluations);
    }
    
    protected IntegerSequence.Incrementor getCounter() {
        return this.evaluations;
    }
    
    public double integrate(final FirstOrderDifferentialEquations equations, final double t0, final double[] y0, final double t, final double[] y) throws DimensionMismatchException, NumberIsTooSmallException, MaxCountExceededException, NoBracketingException {
        if (y0.length != equations.getDimension()) {
            throw new DimensionMismatchException(y0.length, equations.getDimension());
        }
        if (y.length != equations.getDimension()) {
            throw new DimensionMismatchException(y.length, equations.getDimension());
        }
        final ExpandableStatefulODE expandableODE = new ExpandableStatefulODE(equations);
        expandableODE.setTime(t0);
        expandableODE.setPrimaryState(y0);
        this.integrate(expandableODE, t);
        System.arraycopy(expandableODE.getPrimaryState(), 0, y, 0, y.length);
        return expandableODE.getTime();
    }
    
    public abstract void integrate(final ExpandableStatefulODE p0, final double p1) throws NumberIsTooSmallException, DimensionMismatchException, MaxCountExceededException, NoBracketingException;
    
    public void computeDerivatives(final double t, final double[] y, final double[] yDot) throws MaxCountExceededException, DimensionMismatchException, NullPointerException {
        this.evaluations.increment();
        this.expandable.computeDerivatives(t, y, yDot);
    }
    
    protected void setStateInitialized(final boolean stateInitialized) {
        this.statesInitialized = stateInitialized;
    }
    
    protected double acceptStep(final AbstractStepInterpolator interpolator, final double[] y, final double[] yDot, final double tEnd) throws MaxCountExceededException, DimensionMismatchException, NoBracketingException {
        double previousT = interpolator.getGlobalPreviousTime();
        final double currentT = interpolator.getGlobalCurrentTime();
        if (!this.statesInitialized) {
            for (final EventState state : this.eventsStates) {
                state.reinitializeBegin(interpolator);
            }
            this.statesInitialized = true;
        }
        final int orderingSign = interpolator.isForward() ? 1 : -1;
        final SortedSet<EventState> occurringEvents = new TreeSet<EventState>(new Comparator<EventState>() {
            public int compare(final EventState es0, final EventState es1) {
                return orderingSign * Double.compare(es0.getEventTime(), es1.getEventTime());
            }
        });
        for (final EventState state2 : this.eventsStates) {
            if (state2.evaluateStep(interpolator)) {
                occurringEvents.add(state2);
            }
        }
        while (!occurringEvents.isEmpty()) {
            final Iterator<EventState> iterator = occurringEvents.iterator();
            final EventState currentEvent = iterator.next();
            iterator.remove();
            final double eventT = currentEvent.getEventTime();
            interpolator.setSoftPreviousTime(previousT);
            interpolator.setSoftCurrentTime(eventT);
            interpolator.setInterpolatedTime(eventT);
            final double[] eventYComplete = new double[y.length];
            this.expandable.getPrimaryMapper().insertEquationData(interpolator.getInterpolatedState(), eventYComplete);
            int index = 0;
            for (final EquationsMapper secondary : this.expandable.getSecondaryMappers()) {
                secondary.insertEquationData(interpolator.getInterpolatedSecondaryState(index++), eventYComplete);
            }
            for (final EventState state3 : this.eventsStates) {
                state3.stepAccepted(eventT, eventYComplete);
                this.isLastStep = (this.isLastStep || state3.stop());
            }
            for (final StepHandler handler : this.stepHandlers) {
                handler.handleStep(interpolator, this.isLastStep);
            }
            if (this.isLastStep) {
                System.arraycopy(eventYComplete, 0, y, 0, y.length);
                return eventT;
            }
            boolean needReset = false;
            this.resetOccurred = false;
            needReset = currentEvent.reset(eventT, eventYComplete);
            if (needReset) {
                interpolator.setInterpolatedTime(eventT);
                System.arraycopy(eventYComplete, 0, y, 0, y.length);
                this.computeDerivatives(eventT, y, yDot);
                this.resetOccurred = true;
                return eventT;
            }
            previousT = eventT;
            interpolator.setSoftPreviousTime(eventT);
            interpolator.setSoftCurrentTime(currentT);
            if (!currentEvent.evaluateStep(interpolator)) {
                continue;
            }
            occurringEvents.add(currentEvent);
        }
        interpolator.setInterpolatedTime(currentT);
        final double[] currentY = new double[y.length];
        this.expandable.getPrimaryMapper().insertEquationData(interpolator.getInterpolatedState(), currentY);
        int index2 = 0;
        for (final EquationsMapper secondary2 : this.expandable.getSecondaryMappers()) {
            secondary2.insertEquationData(interpolator.getInterpolatedSecondaryState(index2++), currentY);
        }
        for (final EventState state4 : this.eventsStates) {
            state4.stepAccepted(currentT, currentY);
            this.isLastStep = (this.isLastStep || state4.stop());
        }
        this.isLastStep = (this.isLastStep || Precision.equals(currentT, tEnd, 1));
        for (final StepHandler handler2 : this.stepHandlers) {
            handler2.handleStep(interpolator, this.isLastStep);
        }
        return currentT;
    }
    
    protected void sanityChecks(final ExpandableStatefulODE equations, final double t) throws NumberIsTooSmallException, DimensionMismatchException {
        final double threshold = 1000.0 * FastMath.ulp(FastMath.max(FastMath.abs(equations.getTime()), FastMath.abs(t)));
        final double dt = FastMath.abs(equations.getTime() - t);
        if (dt <= threshold) {
            throw new NumberIsTooSmallException(LocalizedFormats.TOO_SMALL_INTEGRATION_INTERVAL, dt, threshold, false);
        }
    }
}
