package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Region<S extends Space>
{
    Region<S> buildNew(final BSPTree<S> p0);
    
    Region<S> copySelf();
    
    boolean isEmpty();
    
    boolean isEmpty(final BSPTree<S> p0);
    
    boolean isFull();
    
    boolean isFull(final BSPTree<S> p0);
    
    boolean contains(final Region<S> p0);
    
    Location checkPoint(final Point<S> p0);
    
    BoundaryProjection<S> projectToBoundary(final Point<S> p0);
    
    BSPTree<S> getTree(final boolean p0);
    
    double getBoundarySize();
    
    double getSize();
    
    Point<S> getBarycenter();
    
    @Deprecated
    Side side(final Hyperplane<S> p0);
    
    SubHyperplane<S> intersection(final SubHyperplane<S> p0);
    
    public enum Location
    {
        INSIDE, 
        OUTSIDE, 
        BOUNDARY;
    }
}
