package org.apache.commons.math3.geometry;

import java.io.Serializable;

public interface Point<S extends Space> extends Serializable
{
    Space getSpace();
    
    boolean isNaN();
    
    double distance(final Point<S> p0);
}
