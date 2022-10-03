package org.apache.commons.math3.ml.neuralnet.twod.util;

import org.apache.commons.math3.ml.neuralnet.twod.NeuronSquareMesh2D;

public interface MapDataVisualization
{
    double[][] computeImage(final NeuronSquareMesh2D p0, final Iterable<double[]> p1);
}
