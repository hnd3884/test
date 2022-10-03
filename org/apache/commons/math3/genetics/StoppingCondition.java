package org.apache.commons.math3.genetics;

public interface StoppingCondition
{
    boolean isSatisfied(final Population p0);
}
