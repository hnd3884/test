package org.apache.commons.math3.fitting;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class WeightedObservedPoints implements Serializable
{
    private static final long serialVersionUID = 20130813L;
    private final List<WeightedObservedPoint> observations;
    
    public WeightedObservedPoints() {
        this.observations = new ArrayList<WeightedObservedPoint>();
    }
    
    public void add(final double x, final double y) {
        this.add(1.0, x, y);
    }
    
    public void add(final double weight, final double x, final double y) {
        this.observations.add(new WeightedObservedPoint(weight, x, y));
    }
    
    public void add(final WeightedObservedPoint observed) {
        this.observations.add(observed);
    }
    
    public List<WeightedObservedPoint> toList() {
        return new ArrayList<WeightedObservedPoint>(this.observations);
    }
    
    public void clear() {
        this.observations.clear();
    }
}
