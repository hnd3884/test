package org.apache.commons.math3.geometry.enclosing;

import java.io.Serializable;
import org.apache.commons.math3.geometry.Point;
import org.apache.commons.math3.geometry.Space;

public class EnclosingBall<S extends Space, P extends Point<S>> implements Serializable
{
    private static final long serialVersionUID = 20140126L;
    private final P center;
    private final double radius;
    private final P[] support;
    
    public EnclosingBall(final P center, final double radius, final P... support) {
        this.center = center;
        this.radius = radius;
        this.support = support.clone();
    }
    
    public P getCenter() {
        return this.center;
    }
    
    public double getRadius() {
        return this.radius;
    }
    
    public P[] getSupport() {
        return this.support.clone();
    }
    
    public int getSupportSize() {
        return this.support.length;
    }
    
    public boolean contains(final P point) {
        return point.distance(this.center) <= this.radius;
    }
    
    public boolean contains(final P point, final double margin) {
        return point.distance(this.center) <= this.radius + margin;
    }
}
