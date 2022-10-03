package org.apache.commons.math3.ml.neuralnet.twod.util;

import java.util.Iterator;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class SmoothedDataHistogram implements MapDataVisualization
{
    private final int smoothingBins;
    private final DistanceMeasure distance;
    private final double membershipNormalization;
    
    public SmoothedDataHistogram(final int smoothingBins, final DistanceMeasure distance) {
        this.smoothingBins = smoothingBins;
        this.distance = distance;
        double sum = 0.0;
        for (int i = 0; i < smoothingBins; ++i) {
            sum += smoothingBins - i;
        }
        this.membershipNormalization = 1.0 / sum;
    }
    
    public double[][] computeImage(final NeuronSquareMesh2D map, final Iterable<double[]> data) {
        final int nR = map.getNumberOfRows();
        final int nC = map.getNumberOfColumns();
        final int mapSize = nR * nC;
        if (mapSize < this.smoothingBins) {
            throw new NumberIsTooSmallException(mapSize, this.smoothingBins, true);
        }
        final LocationFinder finder = new LocationFinder(map);
        final double[][] histo = new double[nR][nC];
        for (final double[] sample : data) {
            final Neuron[] sorted = MapUtils.sort(sample, map.getNetwork(), this.distance);
            for (int i = 0; i < this.smoothingBins; ++i) {
                final LocationFinder.Location loc = finder.getLocation(sorted[i]);
                final int row = loc.getRow();
                final int col = loc.getColumn();
                final double[] array = histo[row];
                final int n = col;
                array[n] += (this.smoothingBins - i) * this.membershipNormalization;
            }
        }
        return histo;
    }
}
