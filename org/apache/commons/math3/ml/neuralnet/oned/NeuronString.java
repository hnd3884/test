package org.apache.commons.math3.ml.neuralnet.oned;

import java.io.ObjectInputStream;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.ml.neuralnet.FeatureInitializer;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.ml.neuralnet.Network;
import java.io.Serializable;

public class NeuronString implements Serializable
{
    private static final long serialVersionUID = 1L;
    private final Network network;
    private final int size;
    private final boolean wrap;
    private final long[] identifiers;
    
    NeuronString(final boolean wrap, final double[][] featuresList) {
        this.size = featuresList.length;
        if (this.size < 2) {
            throw new NumberIsTooSmallException(this.size, 2, true);
        }
        this.wrap = wrap;
        final int fLen = featuresList[0].length;
        this.network = new Network(0L, fLen);
        this.identifiers = new long[this.size];
        for (int i = 0; i < this.size; ++i) {
            this.identifiers[i] = this.network.createNeuron(featuresList[i]);
        }
        this.createLinks();
    }
    
    public NeuronString(final int num, final boolean wrap, final FeatureInitializer[] featureInit) {
        if (num < 2) {
            throw new NumberIsTooSmallException(num, 2, true);
        }
        this.size = num;
        this.wrap = wrap;
        this.identifiers = new long[num];
        final int fLen = featureInit.length;
        this.network = new Network(0L, fLen);
        for (int i = 0; i < num; ++i) {
            final double[] features = new double[fLen];
            for (int fIndex = 0; fIndex < fLen; ++fIndex) {
                features[fIndex] = featureInit[fIndex].value();
            }
            this.identifiers[i] = this.network.createNeuron(features);
        }
        this.createLinks();
    }
    
    public Network getNetwork() {
        return this.network;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public double[] getFeatures(final int i) {
        if (i < 0 || i >= this.size) {
            throw new OutOfRangeException(i, 0, this.size - 1);
        }
        return this.network.getNeuron(this.identifiers[i]).getFeatures();
    }
    
    private void createLinks() {
        for (int i = 0; i < this.size - 1; ++i) {
            this.network.addLink(this.network.getNeuron(i), this.network.getNeuron(i + 1));
        }
        for (int i = this.size - 1; i > 0; --i) {
            this.network.addLink(this.network.getNeuron(i), this.network.getNeuron(i - 1));
        }
        if (this.wrap) {
            this.network.addLink(this.network.getNeuron(0L), this.network.getNeuron(this.size - 1));
            this.network.addLink(this.network.getNeuron(this.size - 1), this.network.getNeuron(0L));
        }
    }
    
    private void readObject(final ObjectInputStream in) {
        throw new IllegalStateException();
    }
    
    private Object writeReplace() {
        final double[][] featuresList = new double[this.size][];
        for (int i = 0; i < this.size; ++i) {
            featuresList[i] = this.getFeatures(i);
        }
        return new SerializationProxy(this.wrap, featuresList);
    }
    
    private static class SerializationProxy implements Serializable
    {
        private static final long serialVersionUID = 20130226L;
        private final boolean wrap;
        private final double[][] featuresList;
        
        SerializationProxy(final boolean wrap, final double[][] featuresList) {
            this.wrap = wrap;
            this.featuresList = featuresList;
        }
        
        private Object readResolve() {
            return new NeuronString(this.wrap, this.featuresList);
        }
    }
}
