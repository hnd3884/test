package org.apache.commons.math3.ml.neuralnet.twod.util;

import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.math3.ml.neuralnet.Network;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

public class UnifiedDistanceMatrix implements MapVisualization
{
    private final boolean individualDistances;
    private final DistanceMeasure distance;
    
    public UnifiedDistanceMatrix(final boolean individualDistances, final DistanceMeasure distance) {
        this.individualDistances = individualDistances;
        this.distance = distance;
    }
    
    public double[][] computeImage(final NeuronSquareMesh2D map) {
        if (this.individualDistances) {
            return this.individualDistances(map);
        }
        return this.averageDistances(map);
    }
    
    private double[][] individualDistances(final NeuronSquareMesh2D map) {
        final int numRows = map.getNumberOfRows();
        final int numCols = map.getNumberOfColumns();
        final double[][] uMatrix = new double[numRows * 2 + 1][numCols * 2 + 1];
        for (int i = 0; i < numRows; ++i) {
            final int iR = 2 * i + 1;
            for (int j = 0; j < numCols; ++j) {
                final int jR = 2 * j + 1;
                final double[] current = map.getNeuron(i, j).getFeatures();
                Neuron neighbour = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
                if (neighbour != null) {
                    uMatrix[iR][jR + 1] = this.distance.compute(current, neighbour.getFeatures());
                }
                neighbour = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
                if (neighbour != null) {
                    uMatrix[iR + 1][jR] = this.distance.compute(current, neighbour.getFeatures());
                }
            }
        }
        for (int i = 0; i < numRows; ++i) {
            final int iR = 2 * i + 1;
            for (int j = 0; j < numCols; ++j) {
                final int jR = 2 * j + 1;
                final Neuron current2 = map.getNeuron(i, j);
                final Neuron right = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.CENTER);
                final Neuron bottom = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.CENTER, NeuronSquareMesh2D.VerticalDirection.DOWN);
                final Neuron bottomRight = map.getNeuron(i, j, NeuronSquareMesh2D.HorizontalDirection.RIGHT, NeuronSquareMesh2D.VerticalDirection.DOWN);
                final double current2BottomRight = (bottomRight == null) ? 0.0 : this.distance.compute(current2.getFeatures(), bottomRight.getFeatures());
                final double right2Bottom = (right == null || bottom == null) ? 0.0 : this.distance.compute(right.getFeatures(), bottom.getFeatures());
                uMatrix[iR + 1][jR + 1] = 0.5 * (current2BottomRight + right2Bottom);
            }
        }
        final int lastRow = uMatrix.length - 1;
        uMatrix[0] = uMatrix[lastRow];
        final int lastCol = uMatrix[0].length - 1;
        for (int r = 0; r < lastRow; ++r) {
            uMatrix[r][0] = uMatrix[r][lastCol];
        }
        return uMatrix;
    }
    
    private double[][] averageDistances(final NeuronSquareMesh2D map) {
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
                    d += this.distance.compute(features, n.getFeatures());
                }
                uMatrix[i][j] = d / count;
            }
        }
        return uMatrix;
    }
}
