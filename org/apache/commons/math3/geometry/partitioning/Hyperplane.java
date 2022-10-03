package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Hyperplane<S extends Space>
{
    Hyperplane<S> copySelf();
    
    double getOffset(final Point<S> p0);
    
    Point<S> project(final Point<S> p0);
    
    double getTolerance();
    
    boolean sameOrientationAs(final Hyperplane<S> p0);
    
    SubHyperplane<S> wholeHyperplane();
    
    Region<S> wholeSpace();
}
