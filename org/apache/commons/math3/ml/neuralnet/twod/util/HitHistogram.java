package org.apache.commons.math3.ml.neuralnet.twod.util;

import java.util.Iterator;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class HitHistogram implements MapDataVisualization
{
    private final DistanceMeasure distance;
    private final boolean normalizeCount;
    
    public HitHistogram(final boolean normalizeCount, final DistanceMeasure distance) {
        this.normalizeCount = normalizeCount;
        this.distance = distance;
    }
    
    public double[][] computeImage(final NeuronSquareMesh2D map, final Iterable<double[]> data) {
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();
        final LocationFinder finder = new LocationFinder(map);
        int numSamples = 0;
        final double[][] hit = new double[nR][nC];
        for (final double[] sample : data) {
            final Neuron best = MapUtils.findBest(sample, map, this.distance);
            final LocationFinder.Location loc = finder.getLocation(best);
            final int row = loc.getRow();
            final int col = loc.getColumn();
            final double[] array = hit[row];
            final int n = col;
            ++array[n];
            ++numSamples;
        }
        if (this.normalizeCount) {
            for (int r = 0; r < nR; ++r) {
                for (int c = 0; c < nC; ++c) {
                    final double[] array2 = hit[r];
                    final int n2 = c;
                    array2[n2] /= numSamples;
                }
            }
        }
        return hit;
    }
}
