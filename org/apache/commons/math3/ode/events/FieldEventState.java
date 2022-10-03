package org.apache.commons.math3.ode.events;

import org.apache.commons.math3.ode.FieldODEState;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.analysis.RealFieldUnivariateFunction;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FieldODEStateAndDerivative;
import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.ode.sampling.FieldStepInterpolator;
import org.apache.commons.math3.analysis.solvers.BracketedRealFieldUnivariateSolver;
import org.apache.commons.math3.RealFieldElement;

public class FieldEventState<T extends RealFieldElement<T>>
{
    private final FieldEventHandler<T> handler;
    private final double maxCheckInterval;
    private final T convergence;
    private final int maxIterationCount;
    private T t0;
    private T g0;
    private boolean g0Positive;
    private boolean pendingEvent;
    private T pendingEventTime;
    private T previousEventTime;
    private boolean forward;
    private boolean increasing;
    private Action nextAction;
    private final BracketedRealFieldUnivariateSolver<T> solver;
    
    public FieldEventState(final FieldEventHandler<T> handler, final double maxCheckInterval, final T convergence, final int maxIterationCount, final BracketedRealFieldUnivariateSolver<T> solver) {
        this.handler = handler;
        this.maxCheckInterval = maxCheckInterval;
        this.convergence = convergence.abs();
        this.maxIterationCount = maxIterationCount;
        this.solver = solver;
        this.t0 = null;
        this.g0 = null;
        this.g0Positive = true;
        this.pendingEvent = false;
        this.pendingEventTime = null;
        this.previousEventTime = null;
        this.increasing = true;
        this.nextAction = Action.CONTINUE;
    }
    
    public FieldEventHandler<T> getEventHandler() {
        return this.handler;
    }
    
    public double getMaxCheckInterval() {
        return this.maxCheckInterval;
    }
    
    public T getConvergence() {
        return this.convergence;
    }
    
    public int getMaxIterationCount() {
        return this.maxIterationCount;
    }
    
    public void reinitializeBegin(final FieldStepInterpolator<T> interpolator) throws MaxCountExceededException {
        final FieldODEStateAndDerivative<T> s0 = interpolator.getPreviousState();
        this.t0 = s0.getTime();
        this.g0 = this.handler.g(s0);
        if (this.g0.getReal() == 0.0) {
            final double epsilon = FastMath.max(this.solver.getAbsoluteAccuracy().getReal(), FastMath.abs(this.solver.getRelativeAccuracy().multiply(this.t0).getReal()));
            final T tStart = this.t0.add(0.5 * epsilon);
            this.g0 = this.handler.g(interpolator.getInterpolatedState(tStart));
        }
        this.g0Positive = (this.g0.getReal() >= 0.0);
    }
    
    public boolean evaluateStep(final FieldStepInterpolator<T> interpolator) throws MaxCountExceededException, NoBracketingException {
        this.forward = interpolator.isForward();
        final FieldODEStateAndDerivative<T> s1 = interpolator.getCurrentState();
        final T t1 = s1.getTime();
        final T dt = t1.subtract(this.t0);
        if (((RealFieldElement<RealFieldElement<T>>)dt).abs().subtract(this.convergence).getReal() < 0.0) {
            return false;
        }
        final int n = FastMath.max(1, (int)FastMath.ceil(FastMath.abs(dt.getReal()) / this.maxCheckInterval));
        final T h = dt.divide(n);
        final RealFieldUnivariateFunction<T> f = new RealFieldUnivariateFunction<T>() {
            public T value(final T t) {
                return FieldEventState.this.handler.g(interpolator.getInterpolatedState(t));
            }
        };
        T ta = this.t0;
        T ga = this.g0;
        for (int i = 0; i < n; ++i) {
            final T tb = (i == n - 1) ? t1 : this.t0.add(h.multiply(i + 1));
            final T gb = this.handler.g(interpolator.getInterpolatedState(tb));
            if (this.g0Positive ^ gb.getReal() >= 0.0) {
                this.increasing = (gb.subtract(ga).getReal() >= 0.0);
                final T root = this.forward ? this.solver.solve(this.maxIterationCount, f, ta, tb, AllowedSolution.RIGHT_SIDE) : this.solver.solve(this.maxIterationCount, f, tb, ta, AllowedSolution.LEFT_SIDE);
                if (this.previousEventTime != null && ((RealFieldElement<RealFieldElement<T>>)root.subtract(ta)).abs().subtract(this.convergence).getReal() <= 0.0 && ((RealFieldElement<RealFieldElement<T>>)root.subtract(this.previousEventTime)).abs().subtract(this.convergence).getReal() <= 0.0) {
                    do {
                        ta = (this.forward ? ta.add(this.convergence) : ta.subtract(this.convergence));
                        ga = f.value(ta);
                    } while ((this.g0Positive ^ ga.getReal() >= 0.0) && (this.forward ^ ta.subtract(tb).getReal() >= 0.0));
                    if (!(this.forward ^ ta.subtract(tb).getReal() >= 0.0)) {
                        this.pendingEventTime = root;
                        return this.pendingEvent = true;
                    }
                    --i;
                }
                else {
                    if (this.previousEventTime == null || ((RealFieldElement<RealFieldElement<T>>)this.previousEventTime.subtract(root)).abs().subtract(this.convergence).getReal() > 0.0) {
                        this.pendingEventTime = root;
                        return this.pendingEvent = true;
                    }
                    ta = tb;
                    ga = gb;
                }
            }
            else {
                ta = tb;
                ga = gb;
            }
        }
        this.pendingEvent = false;
        this.pendingEventTime = null;
        return false;
    }
    
    public T getEventTime() {
        return this.pendingEvent ? this.pendingEventTime : this.t0.getField().getZero().add(this.forward ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY);
    }
    
    public void stepAccepted(final FieldODEStateAndDerivative<T> state) {
        this.t0 = state.getTime();
        this.g0 = this.handler.g(state);
        if (this.pendingEvent && ((RealFieldElement<RealFieldElement<T>>)this.pendingEventTime.subtract(state.getTime())).abs().subtract(this.convergence).getReal() <= 0.0) {
            this.previousEventTime = state.getTime();
            this.g0Positive = this.increasing;
            this.nextAction = this.handler.eventOccurred(state, !(this.increasing ^ this.forward));
        }
        else {
            this.g0Positive = (this.g0.getReal() >= 0.0);
            this.nextAction = Action.CONTINUE;
        }
    }
    
    public boolean stop() {
        return this.nextAction == Action.STOP;
    }
    
    public FieldODEState<T> reset(final FieldODEStateAndDerivative<T> state) {
        if (!this.pendingEvent || ((RealFieldElement<RealFieldElement<T>>)this.pendingEventTime.subtract(state.getTime())).abs().subtract(this.convergence).getReal() > 0.0) {
            return null;
        }
        FieldODEState<T> newState;
        if (this.nextAction == Action.RESET_STATE) {
            newState = this.handler.resetState(state);
        }
        else if (this.nextAction == Action.RESET_DERIVATIVES) {
            newState = state;
        }
        else {
            newState = null;
        }
        this.pendingEvent = false;
        this.pendingEventTime = null;
        return newState;
    }
}
