package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class BoundaryProjection<S extends Space>
{
    private final Point<S> original;
    private final Point<S> projected;
    private final double offset;
    
    public BoundaryProjection(final Point<S> original, final Point<S> projected, final double offset) {
        this.original = original;
        this.projected = projected;
        this.offset = offset;
    }
    
    public Point<S> getOriginal() {
        return this.original;
    }
    
    public Point<S> getProjected() {
        return this.projected;
    }
    
    public double getOffset() {
        return this.offset;
    }
}
