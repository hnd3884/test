package org.apache.commons.math3.ode;

import java.io.Serializable;

class ParameterConfiguration implements Serializable
{
    private static final long serialVersionUID = 2247518849090889379L;
    private String parameterName;
    private double hP;
    
    ParameterConfiguration(final String parameterName, final double hP) {
        this.parameterName = parameterName;
        this.hP = hP;
    }
    
    public String getParameterName() {
        return this.parameterName;
    }
    
    public double getHP() {
        return this.hP;
    }
    
    public void setHP(final double hParam) {
        this.hP = hParam;
    }
}
