package org.apache.commons.math3.ode.sampling;

public class DummyStepHandler implements StepHandler
{
    private DummyStepHandler() {
    }
    
    public static DummyStepHandler getInstance() {
        return LazyHolder.INSTANCE;
    }
    
    public void init(final double t0, final double[] y0, final double t) {
    }
    
    public void handleStep(final StepInterpolator interpolator, final boolean isLast) {
    }
    
    private Object readResolve() {
        return LazyHolder.INSTANCE;
    }
    
    private static class LazyHolder
    {
        private static final DummyStepHandler INSTANCE;
        
        static {
            INSTANCE = new DummyStepHandler(null);
        }
    }
}
