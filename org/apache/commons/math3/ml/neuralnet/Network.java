package org.apache.commons.math3.ml.neuralnet;

import java.io.ObjectInputStream;
import java.util.NoSuchElementException;
import org.apache.commons.math3.exception.DimensionMismatchException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import org.apache.commons.math3.exception.MathIllegalStateException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;

public class Network implements Iterable<Neuron>, Serializable
{
    private static final long serialVersionUID = 20130207L;
    private final ConcurrentHashMap<Long, Neuron> neuronMap;
    private final AtomicLong nextId;
    private final int featureSize;
    private final ConcurrentHashMap<Long, Set<Long>> linkMap;
    
    Network(final long nextId, final int featureSize, final Neuron[] neuronList, final long[][] neighbourIdList) {
        this.neuronMap = new ConcurrentHashMap<Long, Neuron>();
        this.linkMap = new ConcurrentHashMap<Long, Set<Long>>();
        final int numNeurons = neuronList.length;
        if (numNeurons != neighbourIdList.length) {
            throw new MathIllegalStateException();
        }
        for (final Neuron n : neuronList) {
            final long id = n.getIdentifier();
            if (id >= nextId) {
                throw new MathIllegalStateException();
            }
            this.neuronMap.put(id, n);
            this.linkMap.put(id, new HashSet<Long>());
        }
        for (int i = 0; i < numNeurons; ++i) {
            final long aId = neuronList[i].getIdentifier();
            final Set<Long> aLinks = this.linkMap.get(aId);
            for (final Long bId : neighbourIdList[i]) {
                if (this.neuronMap.get(bId) == null) {
                    throw new MathIllegalStateException();
                }
                this.addLinkToLinkSet(aLinks, bId);
            }
        }
        this.nextId = new AtomicLong(nextId);
        this.featureSize = featureSize;
    }
    
    public Network(final long initialIdentifier, final int featureSize) {
        this.neuronMap = new ConcurrentHashMap<Long, Neuron>();
        this.linkMap = new ConcurrentHashMap<Long, Set<Long>>();
        this.nextId = new AtomicLong(initialIdentifier);
        this.featureSize = featureSize;
    }
    
    public synchronized Network copy() {
        final Network copy = new Network(this.nextId.get(), this.featureSize);
        for (final Map.Entry<Long, Neuron> e : this.neuronMap.entrySet()) {
            copy.neuronMap.put(e.getKey(), e.getValue().copy());
        }
        for (final Map.Entry<Long, Set<Long>> e2 : this.linkMap.entrySet()) {
            copy.linkMap.put(e2.getKey(), new HashSet<Long>(e2.getValue()));
        }
        return copy;
    }
    
    public Iterator<Neuron> iterator() {
        return this.neuronMap.values().iterator();
    }
    
    public Collection<Neuron> getNeurons(final Comparator<Neuron> comparator) {
        final List<Neuron> neurons = new ArrayList<Neuron>();
        neurons.addAll(this.neuronMap.values());
        Collections.sort(neurons, comparator);
        return neurons;
    }
    
    public long createNeuron(final double[] features) {
        if (features.length != this.featureSize) {
            throw new DimensionMismatchException(features.length, this.featureSize);
        }
        final long id = this.createNextId();
        this.neuronMap.put(id, new Neuron(id, features));
        this.linkMap.put(id, new HashSet<Long>());
        return id;
    }
    
    public void deleteNeuron(final Neuron neuron) {
        final Collection<Neuron> neighbours = this.getNeighbours(neuron);
        for (final Neuron n : neighbours) {
            this.deleteLink(n, neuron);
        }
        this.neuronMap.remove(neuron.getIdentifier());
    }
    
    public int getFeaturesSize() {
        return this.featureSize;
    }
    
    public void addLink(final Neuron a, final Neuron b) {
        final long aId = a.getIdentifier();
        final long bId = b.getIdentifier();
        if (a != this.getNeuron(aId)) {
            throw new NoSuchElementException(Long.toString(aId));
        }
        if (b != this.getNeuron(bId)) {
            throw new NoSuchElementException(Long.toString(bId));
        }
        this.addLinkToLinkSet(this.linkMap.get(aId), bId);
    }
    
    private void addLinkToLinkSet(final Set<Long> linkSet, final long id) {
        linkSet.add(id);
    }
    
    public void deleteLink(final Neuron a, final Neuron b) {
        final long aId = a.getIdentifier();
        final long bId = b.getIdentifier();
        if (a != this.getNeuron(aId)) {
            throw new NoSuchElementException(Long.toString(aId));
        }
        if (b != this.getNeuron(bId)) {
            throw new NoSuchElementException(Long.toString(bId));
        }
        this.deleteLinkFromLinkSet(this.linkMap.get(aId), bId);
    }
    
    private void deleteLinkFromLinkSet(final Set<Long> linkSet, final long id) {
        linkSet.remove(id);
    }
    
    public Neuron getNeuron(final long id) {
        final Neuron n = this.neuronMap.get(id);
        if (n == null) {
            throw new NoSuchElementException(Long.toString(id));
        }
        return n;
    }
    
    public Collection<Neuron> getNeighbours(final Iterable<Neuron> neurons) {
        return this.getNeighbours(neurons, null);
    }
    
    public Collection<Neuron> getNeighbours(final Iterable<Neuron> neurons, final Iterable<Neuron> exclude) {
        final Set<Long> idList = new HashSet<Long>();
        for (final Neuron n : neurons) {
            idList.addAll(this.linkMap.get(n.getIdentifier()));
        }
        if (exclude != null) {
            for (final Neuron n : exclude) {
                idList.remove(n.getIdentifier());
            }
        }
        final List<Neuron> neuronList = new ArrayList<Neuron>();
        for (final Long id : idList) {
            neuronList.add(this.getNeuron(id));
        }
        return neuronList;
    }
    
    public Collection<Neuron> getNeighbours(final Neuron neuron) {
        return this.getNeighbours(neuron, null);
    }
    
    public Collection<Neuron> getNeighbours(final Neuron neuron, final Iterable<Neuron> exclude) {
        final Set<Long> idList = this.linkMap.get(neuron.getIdentifier());
        if (exclude != null) {
            for (final Neuron n : exclude) {
                idList.remove(n.getIdentifier());
            }
        }
        final List<Neuron> neuronList = new ArrayList<Neuron>();
        for (final Long id : idList) {
            neuronList.add(this.getNeuron(id));
        }
        return neuronList;
    }
    
    private Long createNextId() {
        return this.nextId.getAndIncrement();
    }
    
    private void readObject(final ObjectInputStream in) {
        throw new IllegalStateException();
    }
    
    private Object writeReplace() {
        final Neuron[] neuronList = this.neuronMap.values().toArray(new Neuron[0]);
        final long[][] neighbourIdList = new long[neuronList.length][];
        for (int i = 0; i < neuronList.length; ++i) {
            final Collection<Neuron> neighbours = this.getNeighbours(neuronList[i]);
            final long[] neighboursId = new long[neighbours.size()];
            int count = 0;
            for (final Neuron n : neighbours) {
                neighboursId[count] = n.getIdentifier();
                ++count;
            }
            neighbourIdList[i] = neighboursId;
        }
        return new SerializationProxy(this.nextId.get(), this.featureSize, neuronList, neighbourIdList);
    }
    
    public static class NeuronIdentifierComparator implements Comparator<Neuron>, Serializable
    {
        private static final long serialVersionUID = 20130207L;
        
        public int compare(final Neuron a, final Neuron b) {
            final long aId = a.getIdentifier();
            final long bId = b.getIdentifier();
            return (aId < bId) ? -1 : ((aId > bId) ? 1 : 0);
        }
    }
    
    private static class SerializationProxy implements Serializable
    {
        private static final long serialVersionUID = 20130207L;
        private final long nextId;
        private final int featureSize;
        private final Neuron[] neuronList;
        private final long[][] neighbourIdList;
        
        SerializationProxy(final long nextId, final int featureSize, final Neuron[] neuronList, final long[][] neighbourIdList) {
            this.nextId = nextId;
            this.featureSize = featureSize;
            this.neuronList = neuronList;
            this.neighbourIdList = neighbourIdList;
        }
        
        private Object readResolve() {
            return new Network(this.nextId, this.featureSize, this.neuronList, this.neighbourIdList);
        }
    }
}
