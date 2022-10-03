package org.apache.commons.collections4.bidimap;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.UnmodifiableMapEntry;
import org.apache.commons.collections4.OrderedIterator;
import java.util.ConcurrentModificationException;
import java.util.AbstractSet;
import java.util.Collection;
import org.apache.commons.collections4.BidiMap;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections4.OrderedMapIterator;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.Serializable;
import org.apache.commons.collections4.OrderedBidiMap;

public class TreeBidiMap<K extends Comparable<K>, V extends Comparable<V>> implements OrderedBidiMap<K, V>, Serializable
{
    private static final long serialVersionUID = 721969328361807L;
    private transient Node<K, V>[] rootNode;
    private transient int nodeCount;
    private transient int modifications;
    private transient Set<K> keySet;
    private transient Set<V> valuesSet;
    private transient Set<Map.Entry<K, V>> entrySet;
    private transient Inverse inverse;
    
    public TreeBidiMap() {
        this.nodeCount = 0;
        this.modifications = 0;
        this.inverse = null;
        this.rootNode = new Node[2];
    }
    
    public TreeBidiMap(final Map<? extends K, ? extends V> map) {
        this();
        this.putAll(map);
    }
    
    @Override
    public int size() {
        return this.nodeCount;
    }
    
    @Override
    public boolean isEmpty() {
        return this.nodeCount == 0;
    }
    
    @Override
    public boolean containsKey(final Object key) {
        checkKey(key);
        return this.lookupKey(key) != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        checkValue(value);
        return this.lookupValue(value) != null;
    }
    
    @Override
    public V get(final Object key) {
        checkKey(key);
        final Node<K, V> node = this.lookupKey(key);
        return (V)((node == null) ? null : node.getValue());
    }
    
    @Override
    public V put(final K key, final V value) {
        final V result = this.get((Object)key);
        this.doPut(key, value);
        return result;
    }
    
    @Override
    public void putAll(final Map<? extends K, ? extends V> map) {
        for (final Map.Entry<? extends K, ? extends V> e : map.entrySet()) {
            this.put((K)e.getKey(), (V)e.getValue());
        }
    }
    
    @Override
    public V remove(final Object key) {
        return this.doRemoveKey(key);
    }
    
    @Override
    public void clear() {
        this.modify();
        this.nodeCount = 0;
        this.rootNode[DataElement.KEY.ordinal()] = null;
        this.rootNode[DataElement.VALUE.ordinal()] = null;
    }
    
    @Override
    public K getKey(final Object value) {
        checkValue(value);
        final Node<K, V> node = this.lookupValue(value);
        return (K)((node == null) ? null : node.getKey());
    }
    
    @Override
    public K removeValue(final Object value) {
        return this.doRemoveValue(value);
    }
    
    @Override
    public K firstKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.leastNode(this.rootNode[DataElement.KEY.ordinal()], DataElement.KEY).getKey();
    }
    
    @Override
    public K lastKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return this.greatestNode(this.rootNode[DataElement.KEY.ordinal()], DataElement.KEY).getKey();
    }
    
    @Override
    public K nextKey(final K key) {
        checkKey(key);
        final Node<K, V> node = this.nextGreater(this.lookupKey(key), DataElement.KEY);
        return (K)((node == null) ? null : node.getKey());
    }
    
    @Override
    public K previousKey(final K key) {
        checkKey(key);
        final Node<K, V> node = this.nextSmaller(this.lookupKey(key), DataElement.KEY);
        return (K)((node == null) ? null : node.getKey());
    }
    
    @Override
    public Set<K> keySet() {
        if (this.keySet == null) {
            this.keySet = new KeyView(DataElement.KEY);
        }
        return this.keySet;
    }
    
    @Override
    public Set<V> values() {
        if (this.valuesSet == null) {
            this.valuesSet = new ValueView(DataElement.KEY);
        }
        return this.valuesSet;
    }
    
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntryView();
        }
        return this.entrySet;
    }
    
    @Override
    public OrderedMapIterator<K, V> mapIterator() {
        if (this.isEmpty()) {
            return EmptyOrderedMapIterator.emptyOrderedMapIterator();
        }
        return new ViewMapIterator(DataElement.KEY);
    }
    
    @Override
    public OrderedBidiMap<V, K> inverseBidiMap() {
        if (this.inverse == null) {
            this.inverse = new Inverse();
        }
        return this.inverse;
    }
    
    @Override
    public boolean equals(final Object obj) {
        return this.doEquals(obj, DataElement.KEY);
    }
    
    @Override
    public int hashCode() {
        return this.doHashCode(DataElement.KEY);
    }
    
    @Override
    public String toString() {
        return this.doToString(DataElement.KEY);
    }
    
    private void doPut(final K key, final V value) {
        checkKeyAndValue(key, value);
        this.doRemoveKey(key);
        this.doRemoveValue(value);
        Node<K, V> node = this.rootNode[DataElement.KEY.ordinal()];
        if (node == null) {
            final Node<K, V> root = new Node<K, V>(key, value);
            this.rootNode[DataElement.KEY.ordinal()] = root;
            this.rootNode[DataElement.VALUE.ordinal()] = root;
            this.grow();
        }
        else {
            while (true) {
                final int cmp = compare(key, node.getKey());
                if (cmp == 0) {
                    throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
                }
                if (cmp < 0) {
                    if (((Node<Comparable, Comparable>)node).getLeft(DataElement.KEY) == null) {
                        final Node<K, V> newNode = new Node<K, V>(key, value);
                        this.insertValue(newNode);
                        ((Node<Comparable, Comparable>)node).setLeft((Node<Comparable, Comparable>)newNode, DataElement.KEY);
                        ((Node<Comparable, Comparable>)newNode).setParent((Node<Comparable, Comparable>)node, DataElement.KEY);
                        this.doRedBlackInsert(newNode, DataElement.KEY);
                        this.grow();
                        break;
                    }
                    node = ((Node<Comparable, Comparable>)node).getLeft(DataElement.KEY);
                }
                else {
                    if (((Node<Comparable, Comparable>)node).getRight(DataElement.KEY) == null) {
                        final Node<K, V> newNode = new Node<K, V>(key, value);
                        this.insertValue(newNode);
                        ((Node<Comparable, Comparable>)node).setRight((Node<Comparable, Comparable>)newNode, DataElement.KEY);
                        ((Node<Comparable, Comparable>)newNode).setParent((Node<Comparable, Comparable>)node, DataElement.KEY);
                        this.doRedBlackInsert(newNode, DataElement.KEY);
                        this.grow();
                        break;
                    }
                    node = ((Node<Comparable, Comparable>)node).getRight(DataElement.KEY);
                }
            }
        }
    }
    
    private V doRemoveKey(final Object key) {
        final Node<K, V> node = this.lookupKey(key);
        if (node == null) {
            return null;
        }
        this.doRedBlackDelete(node);
        return node.getValue();
    }
    
    private K doRemoveValue(final Object value) {
        final Node<K, V> node = this.lookupValue(value);
        if (node == null) {
            return null;
        }
        this.doRedBlackDelete(node);
        return node.getKey();
    }
    
    private <T extends Comparable<T>> Node<K, V> lookup(final Object data, final DataElement dataElement) {
        Node<K, V> rval = null;
        int cmp;
        for (Node<K, V> node = this.rootNode[dataElement.ordinal()]; node != null; node = ((cmp < 0) ? ((Node<Comparable, Comparable>)node).getLeft(dataElement) : ((Node<Comparable, Comparable>)node).getRight(dataElement))) {
            cmp = compare(data, ((Node<Comparable, Comparable>)node).getData(dataElement));
            if (cmp == 0) {
                rval = node;
                break;
            }
        }
        return rval;
    }
    
    private Node<K, V> lookupKey(final Object key) {
        return this.lookup(key, DataElement.KEY);
    }
    
    private Node<K, V> lookupValue(final Object value) {
        return this.lookup(value, DataElement.VALUE);
    }
    
    private Node<K, V> nextGreater(final Node<K, V> node, final DataElement dataElement) {
        Node<K, V> rval;
        if (node == null) {
            rval = null;
        }
        else if (((Node<Comparable, Comparable>)node).getRight(dataElement) != null) {
            rval = (Node<K, V>)this.leastNode(((Node<Comparable, Comparable>)node).getRight(dataElement), dataElement);
        }
        else {
            Node<K, V> parent = ((Node<Comparable, Comparable>)node).getParent(dataElement);
            for (Node<K, V> child = node; parent != null && child == ((Node<Comparable, Comparable>)parent).getRight(dataElement); child = parent, parent = ((Node<Comparable, Comparable>)parent).getParent(dataElement)) {}
            rval = parent;
        }
        return rval;
    }
    
    private Node<K, V> nextSmaller(final Node<K, V> node, final DataElement dataElement) {
        Node<K, V> rval;
        if (node == null) {
            rval = null;
        }
        else if (((Node<Comparable, Comparable>)node).getLeft(dataElement) != null) {
            rval = (Node<K, V>)this.greatestNode(((Node<Comparable, Comparable>)node).getLeft(dataElement), dataElement);
        }
        else {
            Node<K, V> parent = ((Node<Comparable, Comparable>)node).getParent(dataElement);
            for (Node<K, V> child = node; parent != null && child == ((Node<Comparable, Comparable>)parent).getLeft(dataElement); child = parent, parent = ((Node<Comparable, Comparable>)parent).getParent(dataElement)) {}
            rval = parent;
        }
        return rval;
    }
    
    private static <T extends Comparable<T>> int compare(final T o1, final T o2) {
        return o1.compareTo(o2);
    }
    
    private Node<K, V> leastNode(final Node<K, V> node, final DataElement dataElement) {
        Node<K, V> rval = node;
        if (rval != null) {
            while (((Node<Comparable, Comparable>)rval).getLeft(dataElement) != null) {
                rval = ((Node<Comparable, Comparable>)rval).getLeft(dataElement);
            }
        }
        return rval;
    }
    
    private Node<K, V> greatestNode(final Node<K, V> node, final DataElement dataElement) {
        Node<K, V> rval = node;
        if (rval != null) {
            while (((Node<Comparable, Comparable>)rval).getRight(dataElement) != null) {
                rval = ((Node<Comparable, Comparable>)rval).getRight(dataElement);
            }
        }
        return rval;
    }
    
    private void copyColor(final Node<K, V> from, final Node<K, V> to, final DataElement dataElement) {
        if (to != null) {
            if (from == null) {
                ((Node<Comparable, Comparable>)to).setBlack(dataElement);
            }
            else {
                ((Node<Comparable, Comparable>)to).copyColor((Node<Comparable, Comparable>)from, dataElement);
            }
        }
    }
    
    private static boolean isRed(final Node<?, ?> node, final DataElement dataElement) {
        return node != null && ((Node<Comparable, Comparable>)node).isRed(dataElement);
    }
    
    private static boolean isBlack(final Node<?, ?> node, final DataElement dataElement) {
        return node == null || ((Node<Comparable, Comparable>)node).isBlack(dataElement);
    }
    
    private static void makeRed(final Node<?, ?> node, final DataElement dataElement) {
        if (node != null) {
            ((Node<Comparable, Comparable>)node).setRed(dataElement);
        }
    }
    
    private static void makeBlack(final Node<?, ?> node, final DataElement dataElement) {
        if (node != null) {
            ((Node<Comparable, Comparable>)node).setBlack(dataElement);
        }
    }
    
    private Node<K, V> getGrandParent(final Node<K, V> node, final DataElement dataElement) {
        return this.getParent(this.getParent(node, dataElement), dataElement);
    }
    
    private Node<K, V> getParent(final Node<K, V> node, final DataElement dataElement) {
        return (node == null) ? null : ((Node<Comparable, Comparable>)node).getParent(dataElement);
    }
    
    private Node<K, V> getRightChild(final Node<K, V> node, final DataElement dataElement) {
        return (node == null) ? null : ((Node<Comparable, Comparable>)node).getRight(dataElement);
    }
    
    private Node<K, V> getLeftChild(final Node<K, V> node, final DataElement dataElement) {
        return (node == null) ? null : ((Node<Comparable, Comparable>)node).getLeft(dataElement);
    }
    
    private void rotateLeft(final Node<K, V> node, final DataElement dataElement) {
        final Node<K, V> rightChild = ((Node<Comparable, Comparable>)node).getRight(dataElement);
        ((Node<Comparable, Comparable>)node).setRight(((Node<Comparable, Comparable>)rightChild).getLeft(dataElement), dataElement);
        if (((Node<Comparable, Comparable>)rightChild).getLeft(dataElement) != null) {
            ((Node<Comparable, Comparable>)rightChild).getLeft(dataElement).setParent(node, dataElement);
        }
        ((Node<Comparable, Comparable>)rightChild).setParent(((Node<Comparable, Comparable>)node).getParent(dataElement), dataElement);
        if (((Node<Comparable, Comparable>)node).getParent(dataElement) == null) {
            this.rootNode[dataElement.ordinal()] = rightChild;
        }
        else if (((Node<Comparable, Comparable>)node).getParent(dataElement).getLeft(dataElement) == node) {
            ((Node<Comparable, Comparable>)node).getParent(dataElement).setLeft(rightChild, dataElement);
        }
        else {
            ((Node<Comparable, Comparable>)node).getParent(dataElement).setRight(rightChild, dataElement);
        }
        ((Node<Comparable, Comparable>)rightChild).setLeft((Node<Comparable, Comparable>)node, dataElement);
        ((Node<Comparable, Comparable>)node).setParent((Node<Comparable, Comparable>)rightChild, dataElement);
    }
    
    private void rotateRight(final Node<K, V> node, final DataElement dataElement) {
        final Node<K, V> leftChild = ((Node<Comparable, Comparable>)node).getLeft(dataElement);
        ((Node<Comparable, Comparable>)node).setLeft(((Node<Comparable, Comparable>)leftChild).getRight(dataElement), dataElement);
        if (((Node<Comparable, Comparable>)leftChild).getRight(dataElement) != null) {
            ((Node<Comparable, Comparable>)leftChild).getRight(dataElement).setParent(node, dataElement);
        }
        ((Node<Comparable, Comparable>)leftChild).setParent(((Node<Comparable, Comparable>)node).getParent(dataElement), dataElement);
        if (((Node<Comparable, Comparable>)node).getParent(dataElement) == null) {
            this.rootNode[dataElement.ordinal()] = leftChild;
        }
        else if (((Node<Comparable, Comparable>)node).getParent(dataElement).getRight(dataElement) == node) {
            ((Node<Comparable, Comparable>)node).getParent(dataElement).setRight(leftChild, dataElement);
        }
        else {
            ((Node<Comparable, Comparable>)node).getParent(dataElement).setLeft(leftChild, dataElement);
        }
        ((Node<Comparable, Comparable>)leftChild).setRight((Node<Comparable, Comparable>)node, dataElement);
        ((Node<Comparable, Comparable>)node).setParent((Node<Comparable, Comparable>)leftChild, dataElement);
    }
    
    private void doRedBlackInsert(final Node<K, V> insertedNode, final DataElement dataElement) {
        Node<K, V> currentNode = insertedNode;
        makeRed(currentNode, dataElement);
        while (currentNode != null && currentNode != this.rootNode[dataElement.ordinal()] && isRed(((Node<Comparable, Comparable>)currentNode).getParent(dataElement), dataElement)) {
            if (((Node<Comparable, Comparable>)currentNode).isLeftChild(dataElement)) {
                final Node<K, V> y = this.getRightChild(this.getGrandParent(currentNode, dataElement), dataElement);
                if (isRed(y, dataElement)) {
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeBlack(y, dataElement);
                    makeRed(this.getGrandParent(currentNode, dataElement), dataElement);
                    currentNode = this.getGrandParent(currentNode, dataElement);
                }
                else {
                    if (((Node<Comparable, Comparable>)currentNode).isRightChild(dataElement)) {
                        currentNode = this.getParent(currentNode, dataElement);
                        this.rotateLeft(currentNode, dataElement);
                    }
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeRed(this.getGrandParent(currentNode, dataElement), dataElement);
                    if (this.getGrandParent(currentNode, dataElement) == null) {
                        continue;
                    }
                    this.rotateRight(this.getGrandParent(currentNode, dataElement), dataElement);
                }
            }
            else {
                final Node<K, V> y = this.getLeftChild(this.getGrandParent(currentNode, dataElement), dataElement);
                if (isRed(y, dataElement)) {
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeBlack(y, dataElement);
                    makeRed(this.getGrandParent(currentNode, dataElement), dataElement);
                    currentNode = this.getGrandParent(currentNode, dataElement);
                }
                else {
                    if (((Node<Comparable, Comparable>)currentNode).isLeftChild(dataElement)) {
                        currentNode = this.getParent(currentNode, dataElement);
                        this.rotateRight(currentNode, dataElement);
                    }
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeRed(this.getGrandParent(currentNode, dataElement), dataElement);
                    if (this.getGrandParent(currentNode, dataElement) == null) {
                        continue;
                    }
                    this.rotateLeft(this.getGrandParent(currentNode, dataElement), dataElement);
                }
            }
        }
        makeBlack(this.rootNode[dataElement.ordinal()], dataElement);
    }
    
    private void doRedBlackDelete(final Node<K, V> deletedNode) {
        for (final DataElement dataElement : DataElement.values()) {
            if (((Node<Comparable, Comparable>)deletedNode).getLeft(dataElement) != null && ((Node<Comparable, Comparable>)deletedNode).getRight(dataElement) != null) {
                this.swapPosition(this.nextGreater(deletedNode, dataElement), deletedNode, dataElement);
            }
            final Node<K, V> replacement = (((Node<Comparable, Comparable>)deletedNode).getLeft(dataElement) != null) ? ((Node<Comparable, Comparable>)deletedNode).getLeft(dataElement) : ((Node<Comparable, Comparable>)deletedNode).getRight(dataElement);
            if (replacement != null) {
                ((Node<Comparable, Comparable>)replacement).setParent(((Node<Comparable, Comparable>)deletedNode).getParent(dataElement), dataElement);
                if (((Node<Comparable, Comparable>)deletedNode).getParent(dataElement) == null) {
                    this.rootNode[dataElement.ordinal()] = replacement;
                }
                else if (deletedNode == ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).getLeft(dataElement)) {
                    ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).setLeft(replacement, dataElement);
                }
                else {
                    ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).setRight(replacement, dataElement);
                }
                ((Node<Comparable, Comparable>)deletedNode).setLeft(null, dataElement);
                ((Node<Comparable, Comparable>)deletedNode).setRight(null, dataElement);
                ((Node<Comparable, Comparable>)deletedNode).setParent(null, dataElement);
                if (isBlack(deletedNode, dataElement)) {
                    this.doRedBlackDeleteFixup(replacement, dataElement);
                }
            }
            else if (((Node<Comparable, Comparable>)deletedNode).getParent(dataElement) == null) {
                this.rootNode[dataElement.ordinal()] = null;
            }
            else {
                if (isBlack(deletedNode, dataElement)) {
                    this.doRedBlackDeleteFixup(deletedNode, dataElement);
                }
                if (((Node<Comparable, Comparable>)deletedNode).getParent(dataElement) != null) {
                    if (deletedNode == ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).getLeft(dataElement)) {
                        ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).setLeft(null, dataElement);
                    }
                    else {
                        ((Node<Comparable, Comparable>)deletedNode).getParent(dataElement).setRight(null, dataElement);
                    }
                    ((Node<Comparable, Comparable>)deletedNode).setParent(null, dataElement);
                }
            }
        }
        this.shrink();
    }
    
    private void doRedBlackDeleteFixup(final Node<K, V> replacementNode, final DataElement dataElement) {
        Node<K, V> currentNode = replacementNode;
        while (currentNode != this.rootNode[dataElement.ordinal()] && isBlack(currentNode, dataElement)) {
            if (((Node<Comparable, Comparable>)currentNode).isLeftChild(dataElement)) {
                Node<K, V> siblingNode = this.getRightChild(this.getParent(currentNode, dataElement), dataElement);
                if (isRed(siblingNode, dataElement)) {
                    makeBlack(siblingNode, dataElement);
                    makeRed(this.getParent(currentNode, dataElement), dataElement);
                    this.rotateLeft(this.getParent(currentNode, dataElement), dataElement);
                    siblingNode = this.getRightChild(this.getParent(currentNode, dataElement), dataElement);
                }
                if (isBlack(this.getLeftChild(siblingNode, dataElement), dataElement) && isBlack(this.getRightChild(siblingNode, dataElement), dataElement)) {
                    makeRed(siblingNode, dataElement);
                    currentNode = this.getParent(currentNode, dataElement);
                }
                else {
                    if (isBlack(this.getRightChild(siblingNode, dataElement), dataElement)) {
                        makeBlack(this.getLeftChild(siblingNode, dataElement), dataElement);
                        makeRed(siblingNode, dataElement);
                        this.rotateRight(siblingNode, dataElement);
                        siblingNode = this.getRightChild(this.getParent(currentNode, dataElement), dataElement);
                    }
                    this.copyColor(this.getParent(currentNode, dataElement), siblingNode, dataElement);
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeBlack(this.getRightChild(siblingNode, dataElement), dataElement);
                    this.rotateLeft(this.getParent(currentNode, dataElement), dataElement);
                    currentNode = this.rootNode[dataElement.ordinal()];
                }
            }
            else {
                Node<K, V> siblingNode = this.getLeftChild(this.getParent(currentNode, dataElement), dataElement);
                if (isRed(siblingNode, dataElement)) {
                    makeBlack(siblingNode, dataElement);
                    makeRed(this.getParent(currentNode, dataElement), dataElement);
                    this.rotateRight(this.getParent(currentNode, dataElement), dataElement);
                    siblingNode = this.getLeftChild(this.getParent(currentNode, dataElement), dataElement);
                }
                if (isBlack(this.getRightChild(siblingNode, dataElement), dataElement) && isBlack(this.getLeftChild(siblingNode, dataElement), dataElement)) {
                    makeRed(siblingNode, dataElement);
                    currentNode = this.getParent(currentNode, dataElement);
                }
                else {
                    if (isBlack(this.getLeftChild(siblingNode, dataElement), dataElement)) {
                        makeBlack(this.getRightChild(siblingNode, dataElement), dataElement);
                        makeRed(siblingNode, dataElement);
                        this.rotateLeft(siblingNode, dataElement);
                        siblingNode = this.getLeftChild(this.getParent(currentNode, dataElement), dataElement);
                    }
                    this.copyColor(this.getParent(currentNode, dataElement), siblingNode, dataElement);
                    makeBlack(this.getParent(currentNode, dataElement), dataElement);
                    makeBlack(this.getLeftChild(siblingNode, dataElement), dataElement);
                    this.rotateRight(this.getParent(currentNode, dataElement), dataElement);
                    currentNode = this.rootNode[dataElement.ordinal()];
                }
            }
        }
        makeBlack(currentNode, dataElement);
    }
    
    private void swapPosition(final Node<K, V> x, final Node<K, V> y, final DataElement dataElement) {
        final Node<K, V> xFormerParent = ((Node<Comparable, Comparable>)x).getParent(dataElement);
        final Node<K, V> xFormerLeftChild = ((Node<Comparable, Comparable>)x).getLeft(dataElement);
        final Node<K, V> xFormerRightChild = ((Node<Comparable, Comparable>)x).getRight(dataElement);
        final Node<K, V> yFormerParent = ((Node<Comparable, Comparable>)y).getParent(dataElement);
        final Node<K, V> yFormerLeftChild = ((Node<Comparable, Comparable>)y).getLeft(dataElement);
        final Node<K, V> yFormerRightChild = ((Node<Comparable, Comparable>)y).getRight(dataElement);
        final boolean xWasLeftChild = ((Node<Comparable, Comparable>)x).getParent(dataElement) != null && x == ((Node<Comparable, Comparable>)x).getParent(dataElement).getLeft(dataElement);
        final boolean yWasLeftChild = ((Node<Comparable, Comparable>)y).getParent(dataElement) != null && y == ((Node<Comparable, Comparable>)y).getParent(dataElement).getLeft(dataElement);
        if (x == yFormerParent) {
            ((Node<Comparable, Comparable>)x).setParent((Node<Comparable, Comparable>)y, dataElement);
            if (yWasLeftChild) {
                ((Node<Comparable, Comparable>)y).setLeft((Node<Comparable, Comparable>)x, dataElement);
                ((Node<Comparable, Comparable>)y).setRight((Node<Comparable, Comparable>)xFormerRightChild, dataElement);
            }
            else {
                ((Node<Comparable, Comparable>)y).setRight((Node<Comparable, Comparable>)x, dataElement);
                ((Node<Comparable, Comparable>)y).setLeft((Node<Comparable, Comparable>)xFormerLeftChild, dataElement);
            }
        }
        else {
            ((Node<Comparable, Comparable>)x).setParent((Node<Comparable, Comparable>)yFormerParent, dataElement);
            if (yFormerParent != null) {
                if (yWasLeftChild) {
                    ((Node<Comparable, Comparable>)yFormerParent).setLeft((Node<Comparable, Comparable>)x, dataElement);
                }
                else {
                    ((Node<Comparable, Comparable>)yFormerParent).setRight((Node<Comparable, Comparable>)x, dataElement);
                }
            }
            ((Node<Comparable, Comparable>)y).setLeft((Node<Comparable, Comparable>)xFormerLeftChild, dataElement);
            ((Node<Comparable, Comparable>)y).setRight((Node<Comparable, Comparable>)xFormerRightChild, dataElement);
        }
        if (y == xFormerParent) {
            ((Node<Comparable, Comparable>)y).setParent((Node<Comparable, Comparable>)x, dataElement);
            if (xWasLeftChild) {
                ((Node<Comparable, Comparable>)x).setLeft((Node<Comparable, Comparable>)y, dataElement);
                ((Node<Comparable, Comparable>)x).setRight((Node<Comparable, Comparable>)yFormerRightChild, dataElement);
            }
            else {
                ((Node<Comparable, Comparable>)x).setRight((Node<Comparable, Comparable>)y, dataElement);
                ((Node<Comparable, Comparable>)x).setLeft((Node<Comparable, Comparable>)yFormerLeftChild, dataElement);
            }
        }
        else {
            ((Node<Comparable, Comparable>)y).setParent((Node<Comparable, Comparable>)xFormerParent, dataElement);
            if (xFormerParent != null) {
                if (xWasLeftChild) {
                    ((Node<Comparable, Comparable>)xFormerParent).setLeft((Node<Comparable, Comparable>)y, dataElement);
                }
                else {
                    ((Node<Comparable, Comparable>)xFormerParent).setRight((Node<Comparable, Comparable>)y, dataElement);
                }
            }
            ((Node<Comparable, Comparable>)x).setLeft((Node<Comparable, Comparable>)yFormerLeftChild, dataElement);
            ((Node<Comparable, Comparable>)x).setRight((Node<Comparable, Comparable>)yFormerRightChild, dataElement);
        }
        if (((Node<Comparable, Comparable>)x).getLeft(dataElement) != null) {
            ((Node<Comparable, Comparable>)x).getLeft(dataElement).setParent(x, dataElement);
        }
        if (((Node<Comparable, Comparable>)x).getRight(dataElement) != null) {
            ((Node<Comparable, Comparable>)x).getRight(dataElement).setParent(x, dataElement);
        }
        if (((Node<Comparable, Comparable>)y).getLeft(dataElement) != null) {
            ((Node<Comparable, Comparable>)y).getLeft(dataElement).setParent(y, dataElement);
        }
        if (((Node<Comparable, Comparable>)y).getRight(dataElement) != null) {
            ((Node<Comparable, Comparable>)y).getRight(dataElement).setParent(y, dataElement);
        }
        ((Node<Comparable, Comparable>)x).swapColors((Node<Comparable, Comparable>)y, dataElement);
        if (this.rootNode[dataElement.ordinal()] == x) {
            this.rootNode[dataElement.ordinal()] = y;
        }
        else if (this.rootNode[dataElement.ordinal()] == y) {
            this.rootNode[dataElement.ordinal()] = x;
        }
    }
    
    private static void checkNonNullComparable(final Object o, final DataElement dataElement) {
        if (o == null) {
            throw new NullPointerException(dataElement + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(dataElement + " must be Comparable");
        }
    }
    
    private static void checkKey(final Object key) {
        checkNonNullComparable(key, DataElement.KEY);
    }
    
    private static void checkValue(final Object value) {
        checkNonNullComparable(value, DataElement.VALUE);
    }
    
    private static void checkKeyAndValue(final Object key, final Object value) {
        checkKey(key);
        checkValue(value);
    }
    
    private void modify() {
        ++this.modifications;
    }
    
    private void grow() {
        this.modify();
        ++this.nodeCount;
    }
    
    private void shrink() {
        this.modify();
        --this.nodeCount;
    }
    
    private void insertValue(final Node<K, V> newNode) throws IllegalArgumentException {
        Node<K, V> node = this.rootNode[DataElement.VALUE.ordinal()];
        while (true) {
            final int cmp = compare(newNode.getValue(), node.getValue());
            if (cmp == 0) {
                throw new IllegalArgumentException("Cannot store a duplicate value (\"" + ((Node<Comparable, Comparable>)newNode).getData(DataElement.VALUE) + "\") in this Map");
            }
            if (cmp < 0) {
                if (((Node<Comparable, Comparable>)node).getLeft(DataElement.VALUE) == null) {
                    ((Node<Comparable, Comparable>)node).setLeft((Node<Comparable, Comparable>)newNode, DataElement.VALUE);
                    ((Node<Comparable, Comparable>)newNode).setParent((Node<Comparable, Comparable>)node, DataElement.VALUE);
                    this.doRedBlackInsert(newNode, DataElement.VALUE);
                    break;
                }
                node = ((Node<Comparable, Comparable>)node).getLeft(DataElement.VALUE);
            }
            else {
                if (((Node<Comparable, Comparable>)node).getRight(DataElement.VALUE) == null) {
                    ((Node<Comparable, Comparable>)node).setRight((Node<Comparable, Comparable>)newNode, DataElement.VALUE);
                    ((Node<Comparable, Comparable>)newNode).setParent((Node<Comparable, Comparable>)node, DataElement.VALUE);
                    this.doRedBlackInsert(newNode, DataElement.VALUE);
                    break;
                }
                node = ((Node<Comparable, Comparable>)node).getRight(DataElement.VALUE);
            }
        }
    }
    
    private boolean doEquals(final Object obj, final DataElement dataElement) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map<?, ?> other = (Map<?, ?>)obj;
        if (other.size() != this.size()) {
            return false;
        }
        if (this.nodeCount > 0) {
            try {
                final MapIterator<?, ?> it = this.getMapIterator(dataElement);
                while (it.hasNext()) {
                    final Object key = it.next();
                    final Object value = it.getValue();
                    if (!value.equals(other.get(key))) {
                        return false;
                    }
                }
            }
            catch (final ClassCastException ex) {
                return false;
            }
            catch (final NullPointerException ex2) {
                return false;
            }
        }
        return true;
    }
    
    private int doHashCode(final DataElement dataElement) {
        int total = 0;
        if (this.nodeCount > 0) {
            final MapIterator<?, ?> it = this.getMapIterator(dataElement);
            while (it.hasNext()) {
                final Object key = it.next();
                final Object value = it.getValue();
                total += (key.hashCode() ^ value.hashCode());
            }
        }
        return total;
    }
    
    private String doToString(final DataElement dataElement) {
        if (this.nodeCount == 0) {
            return "{}";
        }
        final StringBuilder buf = new StringBuilder(this.nodeCount * 32);
        buf.append('{');
        final MapIterator<?, ?> it = this.getMapIterator(dataElement);
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final Object key = it.next();
            final Object value = it.getValue();
            buf.append((key == this) ? "(this Map)" : key).append('=').append((value == this) ? "(this Map)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }
    
    private MapIterator<?, ?> getMapIterator(final DataElement dataElement) {
        switch (dataElement) {
            case KEY: {
                return new ViewMapIterator(DataElement.KEY);
            }
            case VALUE: {
                return new InverseViewMapIterator(DataElement.VALUE);
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }
    
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.rootNode = new Node[2];
        for (int size = stream.readInt(), i = 0; i < size; ++i) {
            final K k = (K)stream.readObject();
            final V v = (V)stream.readObject();
            this.put(k, v);
        }
    }
    
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.size());
        for (final Map.Entry<K, V> entry : this.entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }
    
    enum DataElement
    {
        KEY("key"), 
        VALUE("value");
        
        private final String description;
        
        private DataElement(final String description) {
            this.description = description;
        }
        
        @Override
        public String toString() {
            return this.description;
        }
    }
    
    abstract class View<E> extends AbstractSet<E>
    {
        final DataElement orderType;
        
        View(final DataElement orderType) {
            this.orderType = orderType;
        }
        
        @Override
        public int size() {
            return TreeBidiMap.this.size();
        }
        
        @Override
        public void clear() {
            TreeBidiMap.this.clear();
        }
    }
    
    class KeyView extends View<K>
    {
        public KeyView(final DataElement orderType) {
            super(orderType);
        }
        
        @Override
        public Iterator<K> iterator() {
            return new ViewMapIterator(this.orderType);
        }
        
        @Override
        public boolean contains(final Object obj) {
            checkNonNullComparable(obj, DataElement.KEY);
            return TreeBidiMap.this.lookupKey(obj) != null;
        }
        
        @Override
        public boolean remove(final Object o) {
            return TreeBidiMap.this.doRemoveKey(o) != null;
        }
    }
    
    class ValueView extends View<V>
    {
        public ValueView(final DataElement orderType) {
            super(orderType);
        }
        
        @Override
        public Iterator<V> iterator() {
            return new InverseViewMapIterator(this.orderType);
        }
        
        @Override
        public boolean contains(final Object obj) {
            checkNonNullComparable(obj, DataElement.VALUE);
            return TreeBidiMap.this.lookupValue(obj) != null;
        }
        
        @Override
        public boolean remove(final Object o) {
            return TreeBidiMap.this.doRemoveValue(o) != null;
        }
    }
    
    class EntryView extends View<Map.Entry<K, V>>
    {
        EntryView() {
            super(DataElement.KEY);
        }
        
        @Override
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object value = entry.getValue();
            final Node<K, V> node = TreeBidiMap.this.lookupKey(entry.getKey());
            return node != null && node.getValue().equals(value);
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object value = entry.getValue();
            final Node<K, V> node = TreeBidiMap.this.lookupKey(entry.getKey());
            if (node != null && node.getValue().equals(value)) {
                TreeBidiMap.this.doRedBlackDelete(node);
                return true;
            }
            return false;
        }
        
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new ViewMapEntryIterator();
        }
    }
    
    class InverseEntryView extends View<Map.Entry<V, K>>
    {
        InverseEntryView() {
            super(DataElement.VALUE);
        }
        
        @Override
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object value = entry.getValue();
            final Node<K, V> node = TreeBidiMap.this.lookupValue(entry.getKey());
            return node != null && node.getKey().equals(value);
        }
        
        @Override
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
            final Object value = entry.getValue();
            final Node<K, V> node = TreeBidiMap.this.lookupValue(entry.getKey());
            if (node != null && node.getKey().equals(value)) {
                TreeBidiMap.this.doRedBlackDelete(node);
                return true;
            }
            return false;
        }
        
        @Override
        public Iterator<Map.Entry<V, K>> iterator() {
            return new InverseViewMapEntryIterator();
        }
    }
    
    abstract class ViewIterator
    {
        private final DataElement orderType;
        Node<K, V> lastReturnedNode;
        private Node<K, V> nextNode;
        private Node<K, V> previousNode;
        private int expectedModifications;
        
        ViewIterator(final DataElement orderType) {
            this.orderType = orderType;
            this.expectedModifications = TreeBidiMap.this.modifications;
            this.nextNode = TreeBidiMap.this.leastNode(TreeBidiMap.this.rootNode[orderType.ordinal()], orderType);
            this.lastReturnedNode = null;
            this.previousNode = null;
        }
        
        public final boolean hasNext() {
            return this.nextNode != null;
        }
        
        protected Node<K, V> navigateNext() {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            if (TreeBidiMap.this.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.lastReturnedNode = this.nextNode;
            this.previousNode = this.nextNode;
            this.nextNode = TreeBidiMap.this.nextGreater(this.nextNode, this.orderType);
            return this.lastReturnedNode;
        }
        
        public boolean hasPrevious() {
            return this.previousNode != null;
        }
        
        protected Node<K, V> navigatePrevious() {
            if (this.previousNode == null) {
                throw new NoSuchElementException();
            }
            if (TreeBidiMap.this.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.nextNode = this.lastReturnedNode;
            if (this.nextNode == null) {
                this.nextNode = TreeBidiMap.this.nextGreater(this.previousNode, this.orderType);
            }
            this.lastReturnedNode = this.previousNode;
            this.previousNode = TreeBidiMap.this.nextSmaller(this.previousNode, this.orderType);
            return this.lastReturnedNode;
        }
        
        public final void remove() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            if (TreeBidiMap.this.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            TreeBidiMap.this.doRedBlackDelete(this.lastReturnedNode);
            ++this.expectedModifications;
            this.lastReturnedNode = null;
            if (this.nextNode == null) {
                this.previousNode = TreeBidiMap.this.greatestNode(TreeBidiMap.this.rootNode[this.orderType.ordinal()], this.orderType);
            }
            else {
                this.previousNode = TreeBidiMap.this.nextSmaller(this.nextNode, this.orderType);
            }
        }
    }
    
    class ViewMapIterator extends ViewIterator implements OrderedMapIterator<K, V>
    {
        ViewMapIterator(final DataElement orderType) {
            super(orderType);
        }
        
        @Override
        public K getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getKey();
        }
        
        @Override
        public V getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getValue();
        }
        
        @Override
        public V setValue(final V obj) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public K next() {
            return this.navigateNext().getKey();
        }
        
        @Override
        public K previous() {
            return this.navigatePrevious().getKey();
        }
    }
    
    class InverseViewMapIterator extends ViewIterator implements OrderedMapIterator<V, K>
    {
        public InverseViewMapIterator(final DataElement orderType) {
            super(orderType);
        }
        
        @Override
        public V getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getValue();
        }
        
        @Override
        public K getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getKey();
        }
        
        @Override
        public K setValue(final K obj) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public V next() {
            return this.navigateNext().getValue();
        }
        
        @Override
        public V previous() {
            return this.navigatePrevious().getValue();
        }
    }
    
    class ViewMapEntryIterator extends ViewIterator implements OrderedIterator<Map.Entry<K, V>>
    {
        ViewMapEntryIterator() {
            super(DataElement.KEY);
        }
        
        @Override
        public Map.Entry<K, V> next() {
            return this.navigateNext();
        }
        
        @Override
        public Map.Entry<K, V> previous() {
            return this.navigatePrevious();
        }
    }
    
    class InverseViewMapEntryIterator extends ViewIterator implements OrderedIterator<Map.Entry<V, K>>
    {
        InverseViewMapEntryIterator() {
            super(DataElement.VALUE);
        }
        
        @Override
        public Map.Entry<V, K> next() {
            return this.createEntry(this.navigateNext());
        }
        
        @Override
        public Map.Entry<V, K> previous() {
            return this.createEntry(this.navigatePrevious());
        }
        
        private Map.Entry<V, K> createEntry(final Node<K, V> node) {
            return new UnmodifiableMapEntry<V, K>(node.getValue(), node.getKey());
        }
    }
    
    static class Node<K extends Comparable<K>, V extends Comparable<V>> implements Map.Entry<K, V>, KeyValue<K, V>
    {
        private final K key;
        private final V value;
        private final Node<K, V>[] leftNode;
        private final Node<K, V>[] rightNode;
        private final Node<K, V>[] parentNode;
        private final boolean[] blackColor;
        private int hashcodeValue;
        private boolean calculatedHashCode;
        
        Node(final K key, final V value) {
            this.key = key;
            this.value = value;
            this.leftNode = new Node[2];
            this.rightNode = new Node[2];
            this.parentNode = new Node[2];
            this.blackColor = new boolean[] { true, true };
            this.calculatedHashCode = false;
        }
        
        private Object getData(final DataElement dataElement) {
            switch (dataElement) {
                case KEY: {
                    return this.getKey();
                }
                case VALUE: {
                    return this.getValue();
                }
                default: {
                    throw new IllegalArgumentException();
                }
            }
        }
        
        private void setLeft(final Node<K, V> node, final DataElement dataElement) {
            this.leftNode[dataElement.ordinal()] = node;
        }
        
        private Node<K, V> getLeft(final DataElement dataElement) {
            return this.leftNode[dataElement.ordinal()];
        }
        
        private void setRight(final Node<K, V> node, final DataElement dataElement) {
            this.rightNode[dataElement.ordinal()] = node;
        }
        
        private Node<K, V> getRight(final DataElement dataElement) {
            return this.rightNode[dataElement.ordinal()];
        }
        
        private void setParent(final Node<K, V> node, final DataElement dataElement) {
            this.parentNode[dataElement.ordinal()] = node;
        }
        
        private Node<K, V> getParent(final DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()];
        }
        
        private void swapColors(final Node<K, V> node, final DataElement dataElement) {
            final boolean[] blackColor = this.blackColor;
            final int ordinal = dataElement.ordinal();
            blackColor[ordinal] ^= node.blackColor[dataElement.ordinal()];
            final boolean[] blackColor2 = node.blackColor;
            final int ordinal2 = dataElement.ordinal();
            blackColor2[ordinal2] ^= this.blackColor[dataElement.ordinal()];
            final boolean[] blackColor3 = this.blackColor;
            final int ordinal3 = dataElement.ordinal();
            blackColor3[ordinal3] ^= node.blackColor[dataElement.ordinal()];
        }
        
        private boolean isBlack(final DataElement dataElement) {
            return this.blackColor[dataElement.ordinal()];
        }
        
        private boolean isRed(final DataElement dataElement) {
            return !this.blackColor[dataElement.ordinal()];
        }
        
        private void setBlack(final DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = true;
        }
        
        private void setRed(final DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = false;
        }
        
        private void copyColor(final Node<K, V> node, final DataElement dataElement) {
            this.blackColor[dataElement.ordinal()] = node.blackColor[dataElement.ordinal()];
        }
        
        private boolean isLeftChild(final DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()] != null && this.parentNode[dataElement.ordinal()].leftNode[dataElement.ordinal()] == this;
        }
        
        private boolean isRightChild(final DataElement dataElement) {
            return this.parentNode[dataElement.ordinal()] != null && this.parentNode[dataElement.ordinal()].rightNode[dataElement.ordinal()] == this;
        }
        
        @Override
        public K getKey() {
            return this.key;
        }
        
        @Override
        public V getValue() {
            return this.value;
        }
        
        @Override
        public V setValue(final V ignored) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry<?, ?> e = (Map.Entry<?, ?>)obj;
            return this.getKey().equals(e.getKey()) && this.getValue().equals(e.getValue());
        }
        
        @Override
        public int hashCode() {
            if (!this.calculatedHashCode) {
                this.hashcodeValue = (this.getKey().hashCode() ^ this.getValue().hashCode());
                this.calculatedHashCode = true;
            }
            return this.hashcodeValue;
        }
    }
    
    class Inverse implements OrderedBidiMap<V, K>
    {
        private Set<V> inverseKeySet;
        private Set<K> inverseValuesSet;
        private Set<Map.Entry<V, K>> inverseEntrySet;
        
        @Override
        public int size() {
            return TreeBidiMap.this.size();
        }
        
        @Override
        public boolean isEmpty() {
            return TreeBidiMap.this.isEmpty();
        }
        
        @Override
        public K get(final Object key) {
            return TreeBidiMap.this.getKey(key);
        }
        
        @Override
        public V getKey(final Object value) {
            return TreeBidiMap.this.get(value);
        }
        
        @Override
        public boolean containsKey(final Object key) {
            return TreeBidiMap.this.containsValue(key);
        }
        
        @Override
        public boolean containsValue(final Object value) {
            return TreeBidiMap.this.containsKey(value);
        }
        
        @Override
        public V firstKey() {
            if (TreeBidiMap.this.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return TreeBidiMap.this.leastNode(TreeBidiMap.this.rootNode[DataElement.VALUE.ordinal()], DataElement.VALUE).getValue();
        }
        
        @Override
        public V lastKey() {
            if (TreeBidiMap.this.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return TreeBidiMap.this.greatestNode(TreeBidiMap.this.rootNode[DataElement.VALUE.ordinal()], DataElement.VALUE).getValue();
        }
        
        @Override
        public V nextKey(final V key) {
            checkKey(key);
            final Node<K, V> node = TreeBidiMap.this.nextGreater(TreeBidiMap.this.lookup(key, DataElement.VALUE), DataElement.VALUE);
            return (V)((node == null) ? null : node.getValue());
        }
        
        @Override
        public V previousKey(final V key) {
            checkKey(key);
            final Node<K, V> node = TreeBidiMap.this.nextSmaller(TreeBidiMap.this.lookup(key, DataElement.VALUE), DataElement.VALUE);
            return (V)((node == null) ? null : node.getValue());
        }
        
        @Override
        public K put(final V key, final K value) {
            final K result = this.get((Object)key);
            TreeBidiMap.this.doPut(value, key);
            return result;
        }
        
        @Override
        public void putAll(final Map<? extends V, ? extends K> map) {
            for (final Map.Entry<? extends V, ? extends K> e : map.entrySet()) {
                this.put((V)e.getKey(), (K)e.getValue());
            }
        }
        
        @Override
        public K remove(final Object key) {
            return TreeBidiMap.this.removeValue(key);
        }
        
        @Override
        public V removeValue(final Object value) {
            return TreeBidiMap.this.remove(value);
        }
        
        @Override
        public void clear() {
            TreeBidiMap.this.clear();
        }
        
        @Override
        public Set<V> keySet() {
            if (this.inverseKeySet == null) {
                this.inverseKeySet = new ValueView(DataElement.VALUE);
            }
            return this.inverseKeySet;
        }
        
        @Override
        public Set<K> values() {
            if (this.inverseValuesSet == null) {
                this.inverseValuesSet = new KeyView(DataElement.VALUE);
            }
            return this.inverseValuesSet;
        }
        
        @Override
        public Set<Map.Entry<V, K>> entrySet() {
            if (this.inverseEntrySet == null) {
                this.inverseEntrySet = new InverseEntryView();
            }
            return this.inverseEntrySet;
        }
        
        @Override
        public OrderedMapIterator<V, K> mapIterator() {
            if (this.isEmpty()) {
                return EmptyOrderedMapIterator.emptyOrderedMapIterator();
            }
            return new InverseViewMapIterator(DataElement.VALUE);
        }
        
        @Override
        public OrderedBidiMap<K, V> inverseBidiMap() {
            return TreeBidiMap.this;
        }
        
        @Override
        public boolean equals(final Object obj) {
            return TreeBidiMap.this.doEquals(obj, DataElement.VALUE);
        }
        
        @Override
        public int hashCode() {
            return TreeBidiMap.this.doHashCode(DataElement.VALUE);
        }
        
        @Override
        public String toString() {
            return TreeBidiMap.this.doToString(DataElement.VALUE);
        }
    }
}
