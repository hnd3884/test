package org.apache.commons.math3.ode.events;

public interface EventHandler
{
    void init(final double p0, final double[] p1, final double p2);
    
    double g(final double p0, final double[] p1);
    
    Action eventOccurred(final double p0, final double[] p1, final boolean p2);
    
    void resetState(final double p0, final double[] p1);
    
    public enum Action
    {
        STOP, 
        RESET_STATE, 
        RESET_DERIVATIVES, 
        CONTINUE;
    }
}
