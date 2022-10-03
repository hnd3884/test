package org.apache.commons.math3.geometry.hull;

import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import java.util.Collection;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface ConvexHullGenerator<S extends Space, P extends Point<S>>
{
    ConvexHull<S, P> generate(final Collection<P> p0) throws NullArgumentException, ConvergenceException;
}
