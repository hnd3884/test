package org.apache.commons.math3.ml.neuralnet;

import org.apache.commons.math3.exception.NoDataException;
import java.util.HashMap;
import java.util.Collection;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.util.ArrayList;
import org.apache.commons.math3.util.Pair;
import java.util.Iterator;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class MapUtils
{
    private MapUtils() {
    }
    
    public static Neuron findBest(final double[] features, final Iterable<Neuron> neurons, final DistanceMeasure distance) {
        Neuron best = null;
        double min = Double.POSITIVE_INFINITY;
        for (final Neuron n : neurons) {
            final double d = distance.compute(n.getFeatures(), features);
            if (d < min) {
                min = d;
                best = n;
            }
        }
        return best;
    }
    
    public static Pair<Neuron, Neuron> findBestAndSecondBest(final double[] features, final Iterable<Neuron> neurons, final DistanceMeasure distance) {
        final Neuron[] best = { null, null };
        final double[] min = { Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY };
        for (final Neuron n : neurons) {
            final double d = distance.compute(n.getFeatures(), features);
            if (d < min[0]) {
                min[1] = min[0];
                best[1] = best[0];
                min[0] = d;
                best[0] = n;
            }
            else {
                if (d >= min[1]) {
                    continue;
                }
                min[1] = d;
                best[1] = n;
            }
        }
        return new Pair<Neuron, Neuron>(best[0], best[1]);
    }
    
    public static Neuron[] sort(final double[] features, final Iterable<Neuron> neurons, final DistanceMeasure distance) {
        final List<PairNeuronDouble> list = new ArrayList<PairNeuronDouble>();
        for (final Neuron n : neurons) {
            final double d = distance.compute(n.getFeatures(), features);
            list.add(new PairNeuronDouble(n, d));
        }
        Collections.sort(list, PairNeuronDouble.COMPARATOR);
        final int len = list.size();
        final Neuron[] sorted = new Neuron[len];
        for (int i = 0; i < len; ++i) {
            sorted[i] = list.get(i).getNeuron();
        }
        return sorted;
    }
    
    public static double[][] computeU(final NeuronSquareMesh2D map, final DistanceMeasure distance) {
        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();
        final double[][] uMatrix = new double[numRows][numCols];
        final Network net = map.getNetwork();
        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numCols; ++j) {
                final Neuron neuron = map.getNeuron(i, j);
                final Collection<Neuron> neighbours = net.getNeighbours(neuron);
                final double[] features = neuron.getFeatures();
                double d = 0.0;
                int count = 0;
                for (final Neuron n : neighbours) {
                    ++count;
                    d += distance.compute(features, n.getFeatures());
                }
                uMatrix[i][j] = d / count;
            }
        }
        return uMatrix;
    }
    
    public static int[][] computeHitHistogram(final Iterable<double[]> data, final NeuronSquareMesh2D map, final DistanceMeasure distance) {
        final HashMap<Neuron, Integer> hit = new HashMap<Neuron, Integer>();
        final Network net = map.getNetwork();
        for (final double[] f : data) {
            final Neuron best = findBest(f, net, distance);
            final Integer count = hit.get(best);
            if (count == null) {
                hit.put(best, 1);
            }
            else {
                hit.put(best, count + 1);
            }
        }
        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();
        final int[][] histo = new int[numRows][numCols];
        for (int i = 0; i < numRows; ++i) {
            for (int j = 0; j < numCols; ++j) {
                final Neuron neuron = map.getNeuron(i, j);
                final Integer count2 = hit.get(neuron);
                if (count2 == null) {
                    histo[i][j] = 0;
                }
                else {
                    histo[i][j] = count2;
                }
            }
        }
        return histo;
    }
    
    public static double computeQuantizationError(final Iterable<double[]> data, final Iterable<Neuron> neurons, final DistanceMeasure distance) {
        double d = 0.0;
        int count = 0;
        for (final double[] f : data) {
            ++count;
            d += distance.compute(f, findBest(f, neurons, distance).getFeatures());
        }
        if (count == 0) {
            throw new NoDataException();
        }
        return d / count;
    }
    
    public static double computeTopographicError(final Iterable<double[]> data, final Network net, final DistanceMeasure distance) {
        int notAdjacentCount = 0;
        int count = 0;
        for (final double[] f : data) {
            ++count;
            final Pair<Neuron, Neuron> p = findBestAndSecondBest(f, net, distance);
            if (!net.getNeighbours(p.getFirst()).contains(p.getSecond())) {
                ++notAdjacentCount;
            }
        }
        if (count == 0) {
            throw new NoDataException();
        }
        return notAdjacentCount / (double)count;
    }
    
    private static class PairNeuronDouble
    {
        static final Comparator<PairNeuronDouble> COMPARATOR;
        private final Neuron neuron;
        private final double value;
        
        PairNeuronDouble(final Neuron neuron, final double value) {
            this.neuron = neuron;
            this.value = value;
        }
        
        public Neuron getNeuron() {
            return this.neuron;
        }
        
        static {
            COMPARATOR = new Comparator<PairNeuronDouble>() {
                public int compare(final PairNeuronDouble o1, final PairNeuronDouble o2) {
                    return Double.compare(o1.value, o2.value);
                }
            };
        }
    }
}
