package org.apache.commons.math3.optimization.linear;

import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.optimization.PointValuePair;
import org.apache.commons.math3.optimization.GoalType;
import java.util.Collection;

@Deprecated
public interface LinearOptimizer
{
    void setMaxIterations(final int p0);
    
    int getMaxIterations();
    
    int getIterations();
    
    PointValuePair optimize(final LinearObjectiveFunction p0, final Collection<LinearConstraint> p1, final GoalType p2, final boolean p3) throws MathIllegalStateException;
}
