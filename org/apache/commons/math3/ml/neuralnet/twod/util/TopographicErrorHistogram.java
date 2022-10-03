package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.util.Pair;
import java.util.Iterator;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class TopographicErrorHistogram implements MapDataVisualization
{
    private final DistanceMeasure distance;
    private final boolean relativeCount;
    
    public TopographicErrorHistogram(final boolean relativeCount, final DistanceMeasure distance) {
        this.relativeCount = relativeCount;
        this.distance = distance;
    }
    
    public double[][] computeImage(final NeuronSquareMesh2D map, final Iterable<double[]> data) {
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();
        final Network net = map.getNetwork();
        final LocationFinder finder = new LocationFinder(map);
        final int[][] hit = new int[nR][nC];
        final double[][] error = new double[nR][nC];
        for (final double[] sample : data) {
            final Pair<Neuron, Neuron> p = MapUtils.findBestAndSecondBest(sample, map, this.distance);
            final Neuron best = p.getFirst();
            final LocationFinder.Location loc = finder.getLocation(best);
            final int row = loc.getRow();
            final int col = loc.getColumn();
            final int[] array = hit[row];
            final int n = col;
            ++array[n];
            if (!net.getNeighbours(best).contains(p.getSecond())) {
                final double[] array2 = error[row];
                final int n2 = col;
                ++array2[n2];
            }
        }
        if (this.relativeCount) {
            for (int r = 0; r < nR; ++r) {
                for (int c = 0; c < nC; ++c) {
                    final double[] array3 = error[r];
                    final int n3 = c;
                    array3[n3] /= hit[r][c];
                }
            }
        }
        return error;
    }
}
