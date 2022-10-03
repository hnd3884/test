package org.apache.commons.math3.ml.neuralnet.twod.util;

import java.util.Iterator;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class QuantizationError implements MapDataVisualization
{
    private final DistanceMeasure distance;
    
    public QuantizationError(final DistanceMeasure distance) {
        this.distance = distance;
    }
    
    public double[][] computeImage(final NeuronSquareMesh2D map, final Iterable<double[]> data) {
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();
        final LocationFinder finder = new LocationFinder(map);
        final int[][] hit = new int[nR][nC];
        final double[][] error = new double[nR][nC];
        for (final double[] sample : data) {
            final Neuron best = MapUtils.findBest(sample, map, this.distance);
            final LocationFinder.Location loc = finder.getLocation(best);
            final int row = loc.getRow();
            final int col = loc.getColumn();
            final int[] array = hit[row];
            final int n = col;
            ++array[n];
            final double[] array2 = error[row];
            final int n2 = col;
            array2[n2] += this.distance.compute(sample, best.getFeatures());
        }
        for (int r = 0; r < nR; ++r) {
            for (int c = 0; c < nC; ++c) {
                final int count = hit[r][c];
                if (count != 0) {
                    final double[] array3 = error[r];
                    final int n3 = c;
                    array3[n3] /= count;
                }
            }
        }
        return error;
    }
}
