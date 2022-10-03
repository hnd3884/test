package org.apache.commons.math3.ml.clustering;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class Cluster<T extends Clusterable> implements Serializable
{
    private static final long serialVersionUID = -3442297081515880464L;
    private final List<T> points;
    
    public Cluster() {
        this.points = new ArrayList<T>();
    }
    
    public void addPoint(final T point) {
        this.points.add(point);
    }
    
    public List<T> getPoints() {
        return this.points;
    }
}
