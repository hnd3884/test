package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.exception.MathIllegalStateException;
import java.util.HashMap;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import java.util.Map;

public class LocationFinder
{
    private final Map<Long, Location> locations;
    
    public LocationFinder(final NeuronSquareMesh2D map) {
        this.locations = new HashMap<Long, Location>();
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();
        for (int r = 0; r < nR; ++r) {
            for (int c = 0; c < nC; ++c) {
                final Long id = map.getNeuron(r, c).getIdentifier();
                if (this.locations.get(id) != null) {
                    throw new MathIllegalStateException();
                }
                this.locations.put(id, new Location(r, c));
            }
        }
    }
    
    public Location getLocation(final Neuron n) {
        return this.locations.get(n.getIdentifier());
    }
    
    public static class Location
    {
        private final int row;
        private final int column;
        
        public Location(final int row, final int column) {
            this.row = row;
            this.column = column;
        }
        
        public int getRow() {
            return this.row;
        }
        
        public int getColumn() {
            return this.column;
        }
    }
}
