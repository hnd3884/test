package org.apache.commons.math3.ode;

public interface ParameterizedODE extends Parameterizable
{
    double getParameter(final String p0) throws UnknownParameterException;
    
    void setParameter(final String p0, final double p1) throws UnknownParameterException;
}
