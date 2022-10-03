package org.apache.commons.math3.geometry.partitioning;

import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface Embedding<S extends Space, T extends Space>
{
    Point<T> toSubSpace(final Point<S> p0);
    
    Point<S> toSpace(final Point<T> p0);
}
