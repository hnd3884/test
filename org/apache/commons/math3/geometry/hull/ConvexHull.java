package org.apache.commons.math3.geometry.hull;

import org.apache.commons.math3.exception.InsufficientDataException;
import org.apache.commons.math3.geometry.partitioning.Region;
import java.io.Serializable;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public interface ConvexHull<S extends Space, P extends Point<S>> extends Serializable
{
    P[] getVertices();
    
    Region<S> createRegion() throws InsufficientDataException;
}
