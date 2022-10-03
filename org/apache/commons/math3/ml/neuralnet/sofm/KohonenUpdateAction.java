package org.apache.commons.math3.ml.neuralnet.sofm;

import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.ml.neuralnet.MapUtils;
import java.util.Iterator;
import java.util.Collection;
import org.apache.commons.math3.ml.neuralnet.Neuron;
import java.util.HashSet;
import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.ml.neuralnet.Network;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.neuralnet.UpdateAction;

public class KohonenUpdateAction implements UpdateAction
{
    private final DistanceMeasure distance;
    private final LearningFactorFunction learningFactor;
    private final NeighbourhoodSizeFunction neighbourhoodSize;
    private final AtomicLong numberOfCalls;
    
    public KohonenUpdateAction(final DistanceMeasure distance, final LearningFactorFunction learningFactor, final NeighbourhoodSizeFunction neighbourhoodSize) {
        this.numberOfCalls = new AtomicLong(0L);
        this.distance = distance;
        this.learningFactor = learningFactor;
        this.neighbourhoodSize = neighbourhoodSize;
    }
    
    public void update(final Network net, final double[] features) {
        final long numCalls = this.numberOfCalls.incrementAndGet() - 1L;
        final double currentLearning = this.learningFactor.value(numCalls);
        final Neuron best = this.findAndUpdateBestNeuron(net, features, currentLearning);
        final int currentNeighbourhood = this.neighbourhoodSize.value(numCalls);
        final Gaussian neighbourhoodDecay = new Gaussian(currentLearning, 0.0, currentNeighbourhood);
        if (currentNeighbourhood > 0) {
            Collection<Neuron> neighbours = new HashSet<Neuron>();
            neighbours.add(best);
            final HashSet<Neuron> exclude = new HashSet<Neuron>();
            exclude.add(best);
            int radius = 1;
            do {
                neighbours = net.getNeighbours(neighbours, exclude);
                for (final Neuron n : neighbours) {
                    this.updateNeighbouringNeuron(n, features, neighbourhoodDecay.value(radius));
                }
                exclude.addAll((Collection<?>)neighbours);
            } while (++radius <= currentNeighbourhood);
        }
    }
    
    public long getNumberOfCalls() {
        return this.numberOfCalls.get();
    }
    
    private boolean attemptNeuronUpdate(final Neuron n, final double[] features, final double learningRate) {
        final double[] expect = n.getFeatures();
        final double[] update = this.computeFeatures(expect, features, learningRate);
        return n.compareAndSetFeatures(expect, update);
    }
    
    private void updateNeighbouringNeuron(final Neuron n, final double[] features, final double learningRate) {
        while (!this.attemptNeuronUpdate(n, features, learningRate)) {}
    }
    
    private Neuron findAndUpdateBestNeuron(final Network net, final double[] features, final double learningRate) {
        Neuron best;
        do {
            best = MapUtils.findBest(features, net, this.distance);
        } while (!this.attemptNeuronUpdate(best, features, learningRate));
        return best;
    }
    
    private double[] computeFeatures(final double[] current, final double[] sample, final double learningRate) {
        final ArrayRealVector c = new ArrayRealVector(current, false);
        final ArrayRealVector s = new ArrayRealVector(sample, false);
        return s.subtract(c).mapMultiplyToSelf(learningRate).add(c).toArray();
    }
}
