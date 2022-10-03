package org.apache.commons.collections.bidimap;

import org.apache.commons.collections.KeyValue;
import org.apache.commons.collections.keyvalue.UnmodifiableMapEntry;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections.OrderedIterator;
import java.util.AbstractSet;
import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.OrderedMapIterator;
import org.apache.commons.collections.iterators.EmptyOrderedMapIterator;
import org.apache.commons.collections.MapIterator;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.collections.OrderedBidiMap;

public class TreeBidiMap implements OrderedBidiMap
{
    private static final int KEY = 0;
    private static final int VALUE = 1;
    private static final int MAPENTRY = 2;
    private static final int INVERSEMAPENTRY = 3;
    private static final int SUM_OF_INDICES = 1;
    private static final int FIRST_INDEX = 0;
    private static final int NUMBER_OF_INDICES = 2;
    private static final String[] dataName;
    private Node[] rootNode;
    private int nodeCount;
    private int modifications;
    private Set keySet;
    private Set valuesSet;
    private Set entrySet;
    private Inverse inverse;
    
    public TreeBidiMap() {
        this.rootNode = new Node[2];
        this.nodeCount = 0;
        this.modifications = 0;
        this.inverse = null;
    }
    
    public TreeBidiMap(final Map map) {
        this.rootNode = new Node[2];
        this.nodeCount = 0;
        this.modifications = 0;
        this.inverse = null;
        this.putAll(map);
    }
    
    public int size() {
        return this.nodeCount;
    }
    
    public boolean isEmpty() {
        return this.nodeCount == 0;
    }
    
    public boolean containsKey(final Object key) {
        checkKey(key);
        return this.lookup((Comparable)key, 0) != null;
    }
    
    public boolean containsValue(final Object value) {
        checkValue(value);
        return this.lookup((Comparable)value, 1) != null;
    }
    
    public Object get(final Object key) {
        return this.doGet((Comparable)key, 0);
    }
    
    public Object put(final Object key, final Object value) {
        return this.doPut((Comparable)key, (Comparable)value, 0);
    }
    
    public void putAll(final Map map) {
        final Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            final Map.Entry entry = it.next();
            this.put(entry.getKey(), entry.getValue());
        }
    }
    
    public Object remove(final Object key) {
        return this.doRemove((Comparable)key, 0);
    }
    
    public void clear() {
        this.modify();
        this.nodeCount = 0;
        this.rootNode[0] = null;
        this.rootNode[1] = null;
    }
    
    public Object getKey(final Object value) {
        return this.doGet((Comparable)value, 1);
    }
    
    public Object removeValue(final Object value) {
        return this.doRemove((Comparable)value, 1);
    }
    
    public Object firstKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return leastNode(this.rootNode[0], 0).getKey();
    }
    
    public Object lastKey() {
        if (this.nodeCount == 0) {
            throw new NoSuchElementException("Map is empty");
        }
        return greatestNode(this.rootNode[0], 0).getKey();
    }
    
    public Object nextKey(final Object key) {
        checkKey(key);
        final Node node = this.nextGreater(this.lookup((Comparable)key, 0), 0);
        return (node == null) ? null : node.getKey();
    }
    
    public Object previousKey(final Object key) {
        checkKey(key);
        final Node node = this.nextSmaller(this.lookup((Comparable)key, 0), 0);
        return (node == null) ? null : node.getKey();
    }
    
    public Set keySet() {
        if (this.keySet == null) {
            this.keySet = new View(this, 0, 0);
        }
        return this.keySet;
    }
    
    public Collection values() {
        if (this.valuesSet == null) {
            this.valuesSet = new View(this, 0, 1);
        }
        return this.valuesSet;
    }
    
    public Set entrySet() {
        if (this.entrySet == null) {
            this.entrySet = new EntryView(this, 0, 2);
        }
        return this.entrySet;
    }
    
    public MapIterator mapIterator() {
        if (this.isEmpty()) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new ViewMapIterator(this, 0);
    }
    
    public OrderedMapIterator orderedMapIterator() {
        if (this.isEmpty()) {
            return EmptyOrderedMapIterator.INSTANCE;
        }
        return new ViewMapIterator(this, 0);
    }
    
    public BidiMap inverseBidiMap() {
        return this.inverseOrderedBidiMap();
    }
    
    public OrderedBidiMap inverseOrderedBidiMap() {
        if (this.inverse == null) {
            this.inverse = new Inverse(this);
        }
        return this.inverse;
    }
    
    public boolean equals(final Object obj) {
        return this.doEquals(obj, 0);
    }
    
    public int hashCode() {
        return this.doHashCode(0);
    }
    
    public String toString() {
        return this.doToString(0);
    }
    
    private Object doGet(final Comparable obj, final int index) {
        checkNonNullComparable(obj, index);
        final Node node = this.lookup(obj, index);
        return (node == null) ? null : node.getData(oppositeIndex(index));
    }
    
    private Object doPut(final Comparable key, final Comparable value, final int index) {
        checkKeyAndValue(key, value);
        final Object prev = (index == 0) ? this.doGet(key, 0) : this.doGet(value, 1);
        this.doRemove(key, 0);
        this.doRemove(value, 1);
        Node node = this.rootNode[0];
        if (node == null) {
            final Node root = new Node(key, value);
            this.rootNode[0] = root;
            this.rootNode[1] = root;
            this.grow();
        }
        else {
            while (true) {
                final int cmp = compare(key, node.getData(0));
                if (cmp == 0) {
                    throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
                }
                if (cmp < 0) {
                    if (node.getLeft(0) == null) {
                        final Node newNode = new Node(key, value);
                        this.insertValue(newNode);
                        node.setLeft(newNode, 0);
                        newNode.setParent(node, 0);
                        this.doRedBlackInsert(newNode, 0);
                        this.grow();
                        break;
                    }
                    node = node.getLeft(0);
                }
                else {
                    if (node.getRight(0) == null) {
                        final Node newNode = new Node(key, value);
                        this.insertValue(newNode);
                        node.setRight(newNode, 0);
                        newNode.setParent(node, 0);
                        this.doRedBlackInsert(newNode, 0);
                        this.grow();
                        break;
                    }
                    node = node.getRight(0);
                }
            }
        }
        return prev;
    }
    
    private Object doRemove(final Comparable o, final int index) {
        final Node node = this.lookup(o, index);
        Object rval = null;
        if (node != null) {
            rval = node.getData(oppositeIndex(index));
            this.doRedBlackDelete(node);
        }
        return rval;
    }
    
    private Node lookup(final Comparable data, final int index) {
        Node rval = null;
        int cmp;
        for (Node node = this.rootNode[index]; node != null; node = ((cmp < 0) ? node.getLeft(index) : node.getRight(index))) {
            cmp = compare(data, node.getData(index));
            if (cmp == 0) {
                rval = node;
                break;
            }
        }
        return rval;
    }
    
    private Node nextGreater(final Node node, final int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        }
        else if (node.getRight(index) != null) {
            rval = leastNode(node.getRight(index), index);
        }
        else {
            Node parent = node.getParent(index);
            for (Node child = node; parent != null && child == parent.getRight(index); child = parent, parent = parent.getParent(index)) {}
            rval = parent;
        }
        return rval;
    }
    
    private Node nextSmaller(final Node node, final int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        }
        else if (node.getLeft(index) != null) {
            rval = greatestNode(node.getLeft(index), index);
        }
        else {
            Node parent = node.getParent(index);
            for (Node child = node; parent != null && child == parent.getLeft(index); child = parent, parent = parent.getParent(index)) {}
            rval = parent;
        }
        return rval;
    }
    
    private static int oppositeIndex(final int index) {
        return 1 - index;
    }
    
    private static int compare(final Comparable o1, final Comparable o2) {
        return o1.compareTo(o2);
    }
    
    private static Node leastNode(final Node node, final int index) {
        Node rval = node;
        if (rval != null) {
            while (rval.getLeft(index) != null) {
                rval = rval.getLeft(index);
            }
        }
        return rval;
    }
    
    private static Node greatestNode(final Node node, final int index) {
        Node rval = node;
        if (rval != null) {
            while (rval.getRight(index) != null) {
                rval = rval.getRight(index);
            }
        }
        return rval;
    }
    
    private static void copyColor(final Node from, final Node to, final int index) {
        if (to != null) {
            if (from == null) {
                to.setBlack(index);
            }
            else {
                to.copyColor(from, index);
            }
        }
    }
    
    private static boolean isRed(final Node node, final int index) {
        return node != null && node.isRed(index);
    }
    
    private static boolean isBlack(final Node node, final int index) {
        return node == null || node.isBlack(index);
    }
    
    private static void makeRed(final Node node, final int index) {
        if (node != null) {
            node.setRed(index);
        }
    }
    
    private static void makeBlack(final Node node, final int index) {
        if (node != null) {
            node.setBlack(index);
        }
    }
    
    private static Node getGrandParent(final Node node, final int index) {
        return getParent(getParent(node, index), index);
    }
    
    private static Node getParent(final Node node, final int index) {
        return (node == null) ? null : node.getParent(index);
    }
    
    private static Node getRightChild(final Node node, final int index) {
        return (node == null) ? null : node.getRight(index);
    }
    
    private static Node getLeftChild(final Node node, final int index) {
        return (node == null) ? null : node.getLeft(index);
    }
    
    private static boolean isLeftChild(final Node node, final int index) {
        return node == null || (node.getParent(index) != null && node == node.getParent(index).getLeft(index));
    }
    
    private static boolean isRightChild(final Node node, final int index) {
        return node == null || (node.getParent(index) != null && node == node.getParent(index).getRight(index));
    }
    
    private void rotateLeft(final Node node, final int index) {
        final Node rightChild = node.getRight(index);
        node.setRight(rightChild.getLeft(index), index);
        if (rightChild.getLeft(index) != null) {
            rightChild.getLeft(index).setParent(node, index);
        }
        rightChild.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this.rootNode[index] = rightChild;
        }
        else if (node.getParent(index).getLeft(index) == node) {
            node.getParent(index).setLeft(rightChild, index);
        }
        else {
            node.getParent(index).setRight(rightChild, index);
        }
        rightChild.setLeft(node, index);
        node.setParent(rightChild, index);
    }
    
    private void rotateRight(final Node node, final int index) {
        final Node leftChild = node.getLeft(index);
        node.setLeft(leftChild.getRight(index), index);
        if (leftChild.getRight(index) != null) {
            leftChild.getRight(index).setParent(node, index);
        }
        leftChild.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this.rootNode[index] = leftChild;
        }
        else if (node.getParent(index).getRight(index) == node) {
            node.getParent(index).setRight(leftChild, index);
        }
        else {
            node.getParent(index).setLeft(leftChild, index);
        }
        leftChild.setRight(node, index);
        node.setParent(leftChild, index);
    }
    
    private void doRedBlackInsert(final Node insertedNode, final int index) {
        Node currentNode = insertedNode;
        makeRed(currentNode, index);
        while (currentNode != null && currentNode != this.rootNode[index] && isRed(currentNode.getParent(index), index)) {
            if (isLeftChild(getParent(currentNode, index), index)) {
                final Node y = getRightChild(getGrandParent(currentNode, index), index);
                if (isRed(y, index)) {
                    makeBlack(getParent(currentNode, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(currentNode, index), index);
                    currentNode = getGrandParent(currentNode, index);
                }
                else {
                    if (isRightChild(currentNode, index)) {
                        currentNode = getParent(currentNode, index);
                        this.rotateLeft(currentNode, index);
                    }
                    makeBlack(getParent(currentNode, index), index);
                    makeRed(getGrandParent(currentNode, index), index);
                    if (getGrandParent(currentNode, index) == null) {
                        continue;
                    }
                    this.rotateRight(getGrandParent(currentNode, index), index);
                }
            }
            else {
                final Node y = getLeftChild(getGrandParent(currentNode, index), index);
                if (isRed(y, index)) {
                    makeBlack(getParent(currentNode, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(currentNode, index), index);
                    currentNode = getGrandParent(currentNode, index);
                }
                else {
                    if (isLeftChild(currentNode, index)) {
                        currentNode = getParent(currentNode, index);
                        this.rotateRight(currentNode, index);
                    }
                    makeBlack(getParent(currentNode, index), index);
                    makeRed(getGrandParent(currentNode, index), index);
                    if (getGrandParent(currentNode, index) == null) {
                        continue;
                    }
                    this.rotateLeft(getGrandParent(currentNode, index), index);
                }
            }
        }
        makeBlack(this.rootNode[index], index);
    }
    
    private void doRedBlackDelete(final Node deletedNode) {
        for (int index = 0; index < 2; ++index) {
            if (deletedNode.getLeft(index) != null && deletedNode.getRight(index) != null) {
                this.swapPosition(this.nextGreater(deletedNode, index), deletedNode, index);
            }
            final Node replacement = (deletedNode.getLeft(index) != null) ? deletedNode.getLeft(index) : deletedNode.getRight(index);
            if (replacement != null) {
                replacement.setParent(deletedNode.getParent(index), index);
                if (deletedNode.getParent(index) == null) {
                    this.rootNode[index] = replacement;
                }
                else if (deletedNode == deletedNode.getParent(index).getLeft(index)) {
                    deletedNode.getParent(index).setLeft(replacement, index);
                }
                else {
                    deletedNode.getParent(index).setRight(replacement, index);
                }
                deletedNode.setLeft(null, index);
                deletedNode.setRight(null, index);
                deletedNode.setParent(null, index);
                if (isBlack(deletedNode, index)) {
                    this.doRedBlackDeleteFixup(replacement, index);
                }
            }
            else if (deletedNode.getParent(index) == null) {
                this.rootNode[index] = null;
            }
            else {
                if (isBlack(deletedNode, index)) {
                    this.doRedBlackDeleteFixup(deletedNode, index);
                }
                if (deletedNode.getParent(index) != null) {
                    if (deletedNode == deletedNode.getParent(index).getLeft(index)) {
                        deletedNode.getParent(index).setLeft(null, index);
                    }
                    else {
                        deletedNode.getParent(index).setRight(null, index);
                    }
                    deletedNode.setParent(null, index);
                }
            }
        }
        this.shrink();
    }
    
    private void doRedBlackDeleteFixup(final Node replacementNode, final int index) {
        Node currentNode = replacementNode;
        while (currentNode != this.rootNode[index] && isBlack(currentNode, index)) {
            if (isLeftChild(currentNode, index)) {
                Node siblingNode = getRightChild(getParent(currentNode, index), index);
                if (isRed(siblingNode, index)) {
                    makeBlack(siblingNode, index);
                    makeRed(getParent(currentNode, index), index);
                    this.rotateLeft(getParent(currentNode, index), index);
                    siblingNode = getRightChild(getParent(currentNode, index), index);
                }
                if (isBlack(getLeftChild(siblingNode, index), index) && isBlack(getRightChild(siblingNode, index), index)) {
                    makeRed(siblingNode, index);
                    currentNode = getParent(currentNode, index);
                }
                else {
                    if (isBlack(getRightChild(siblingNode, index), index)) {
                        makeBlack(getLeftChild(siblingNode, index), index);
                        makeRed(siblingNode, index);
                        this.rotateRight(siblingNode, index);
                        siblingNode = getRightChild(getParent(currentNode, index), index);
                    }
                    copyColor(getParent(currentNode, index), siblingNode, index);
                    makeBlack(getParent(currentNode, index), index);
                    makeBlack(getRightChild(siblingNode, index), index);
                    this.rotateLeft(getParent(currentNode, index), index);
                    currentNode = this.rootNode[index];
                }
            }
            else {
                Node siblingNode = getLeftChild(getParent(currentNode, index), index);
                if (isRed(siblingNode, index)) {
                    makeBlack(siblingNode, index);
                    makeRed(getParent(currentNode, index), index);
                    this.rotateRight(getParent(currentNode, index), index);
                    siblingNode = getLeftChild(getParent(currentNode, index), index);
                }
                if (isBlack(getRightChild(siblingNode, index), index) && isBlack(getLeftChild(siblingNode, index), index)) {
                    makeRed(siblingNode, index);
                    currentNode = getParent(currentNode, index);
                }
                else {
                    if (isBlack(getLeftChild(siblingNode, index), index)) {
                        makeBlack(getRightChild(siblingNode, index), index);
                        makeRed(siblingNode, index);
                        this.rotateLeft(siblingNode, index);
                        siblingNode = getLeftChild(getParent(currentNode, index), index);
                    }
                    copyColor(getParent(currentNode, index), siblingNode, index);
                    makeBlack(getParent(currentNode, index), index);
                    makeBlack(getLeftChild(siblingNode, index), index);
                    this.rotateRight(getParent(currentNode, index), index);
                    currentNode = this.rootNode[index];
                }
            }
        }
        makeBlack(currentNode, index);
    }
    
    private void swapPosition(final Node x, final Node y, final int index) {
        final Node xFormerParent = x.getParent(index);
        final Node xFormerLeftChild = x.getLeft(index);
        final Node xFormerRightChild = x.getRight(index);
        final Node yFormerParent = y.getParent(index);
        final Node yFormerLeftChild = y.getLeft(index);
        final Node yFormerRightChild = y.getRight(index);
        final boolean xWasLeftChild = x.getParent(index) != null && x == x.getParent(index).getLeft(index);
        final boolean yWasLeftChild = y.getParent(index) != null && y == y.getParent(index).getLeft(index);
        if (x == yFormerParent) {
            x.setParent(y, index);
            if (yWasLeftChild) {
                y.setLeft(x, index);
                y.setRight(xFormerRightChild, index);
            }
            else {
                y.setRight(x, index);
                y.setLeft(xFormerLeftChild, index);
            }
        }
        else {
            x.setParent(yFormerParent, index);
            if (yFormerParent != null) {
                if (yWasLeftChild) {
                    yFormerParent.setLeft(x, index);
                }
                else {
                    yFormerParent.setRight(x, index);
                }
            }
            y.setLeft(xFormerLeftChild, index);
            y.setRight(xFormerRightChild, index);
        }
        if (y == xFormerParent) {
            y.setParent(x, index);
            if (xWasLeftChild) {
                x.setLeft(y, index);
                x.setRight(yFormerRightChild, index);
            }
            else {
                x.setRight(y, index);
                x.setLeft(yFormerLeftChild, index);
            }
        }
        else {
            y.setParent(xFormerParent, index);
            if (xFormerParent != null) {
                if (xWasLeftChild) {
                    xFormerParent.setLeft(y, index);
                }
                else {
                    xFormerParent.setRight(y, index);
                }
            }
            x.setLeft(yFormerLeftChild, index);
            x.setRight(yFormerRightChild, index);
        }
        if (x.getLeft(index) != null) {
            x.getLeft(index).setParent(x, index);
        }
        if (x.getRight(index) != null) {
            x.getRight(index).setParent(x, index);
        }
        if (y.getLeft(index) != null) {
            y.getLeft(index).setParent(y, index);
        }
        if (y.getRight(index) != null) {
            y.getRight(index).setParent(y, index);
        }
        x.swapColors(y, index);
        if (this.rootNode[index] == x) {
            this.rootNode[index] = y;
        }
        else if (this.rootNode[index] == y) {
            this.rootNode[index] = x;
        }
    }
    
    private static void checkNonNullComparable(final Object o, final int index) {
        if (o == null) {
            throw new NullPointerException(TreeBidiMap.dataName[index] + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(TreeBidiMap.dataName[index] + " must be Comparable");
        }
    }
    
    private static void checkKey(final Object key) {
        checkNonNullComparable(key, 0);
    }
    
    private static void checkValue(final Object value) {
        checkNonNullComparable(value, 1);
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
    
    private void insertValue(final Node newNode) throws IllegalArgumentException {
        Node node = this.rootNode[1];
        while (true) {
            final int cmp = compare(newNode.getData(1), node.getData(1));
            if (cmp == 0) {
                throw new IllegalArgumentException("Cannot store a duplicate value (\"" + newNode.getData(1) + "\") in this Map");
            }
            if (cmp < 0) {
                if (node.getLeft(1) == null) {
                    node.setLeft(newNode, 1);
                    newNode.setParent(node, 1);
                    this.doRedBlackInsert(newNode, 1);
                    break;
                }
                node = node.getLeft(1);
            }
            else {
                if (node.getRight(1) == null) {
                    node.setRight(newNode, 1);
                    newNode.setParent(node, 1);
                    this.doRedBlackInsert(newNode, 1);
                    break;
                }
                node = node.getRight(1);
            }
        }
    }
    
    private boolean doEquals(final Object obj, final int type) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map)) {
            return false;
        }
        final Map other = (Map)obj;
        if (other.size() != this.size()) {
            return false;
        }
        if (this.nodeCount > 0) {
            try {
                final MapIterator it = new ViewMapIterator(this, type);
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
    
    private int doHashCode(final int type) {
        int total = 0;
        if (this.nodeCount > 0) {
            final MapIterator it = new ViewMapIterator(this, type);
            while (it.hasNext()) {
                final Object key = it.next();
                final Object value = it.getValue();
                total += (key.hashCode() ^ value.hashCode());
            }
        }
        return total;
    }
    
    private String doToString(final int type) {
        if (this.nodeCount == 0) {
            return "{}";
        }
        final StringBuffer buf = new StringBuffer(this.nodeCount * 32);
        buf.append('{');
        final MapIterator it = new ViewMapIterator(this, type);
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
    
    static {
        dataName = new String[] { "key", "value" };
    }
    
    static class View extends AbstractSet
    {
        protected final TreeBidiMap main;
        protected final int orderType;
        protected final int dataType;
        
        View(final TreeBidiMap main, final int orderType, final int dataType) {
            this.main = main;
            this.orderType = orderType;
            this.dataType = dataType;
        }
        
        public Iterator iterator() {
            return new ViewIterator(this.main, this.orderType, this.dataType);
        }
        
        public int size() {
            return this.main.size();
        }
        
        public boolean contains(final Object obj) {
            checkNonNullComparable(obj, this.dataType);
            return this.main.lookup((Comparable)obj, this.dataType) != null;
        }
        
        public boolean remove(final Object obj) {
            return this.main.doRemove((Comparable)obj, this.dataType) != null;
        }
        
        public void clear() {
            this.main.clear();
        }
    }
    
    static class ViewIterator implements OrderedIterator
    {
        protected final TreeBidiMap main;
        protected final int orderType;
        protected final int dataType;
        protected Node lastReturnedNode;
        protected Node nextNode;
        protected Node previousNode;
        private int expectedModifications;
        
        ViewIterator(final TreeBidiMap main, final int orderType, final int dataType) {
            this.main = main;
            this.orderType = orderType;
            this.dataType = dataType;
            this.expectedModifications = main.modifications;
            this.nextNode = leastNode(main.rootNode[orderType], orderType);
            this.lastReturnedNode = null;
            this.previousNode = null;
        }
        
        public final boolean hasNext() {
            return this.nextNode != null;
        }
        
        public final Object next() {
            if (this.nextNode == null) {
                throw new NoSuchElementException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.lastReturnedNode = this.nextNode;
            this.previousNode = this.nextNode;
            this.nextNode = this.main.nextGreater(this.nextNode, this.orderType);
            return this.doGetData();
        }
        
        public boolean hasPrevious() {
            return this.previousNode != null;
        }
        
        public Object previous() {
            if (this.previousNode == null) {
                throw new NoSuchElementException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.nextNode = this.lastReturnedNode;
            if (this.nextNode == null) {
                this.nextNode = this.main.nextGreater(this.previousNode, this.orderType);
            }
            this.lastReturnedNode = this.previousNode;
            this.previousNode = this.main.nextSmaller(this.previousNode, this.orderType);
            return this.doGetData();
        }
        
        protected Object doGetData() {
            switch (this.dataType) {
                case 0: {
                    return this.lastReturnedNode.getKey();
                }
                case 1: {
                    return this.lastReturnedNode.getValue();
                }
                case 2: {
                    return this.lastReturnedNode;
                }
                case 3: {
                    return new UnmodifiableMapEntry(this.lastReturnedNode.getValue(), this.lastReturnedNode.getKey());
                }
                default: {
                    return null;
                }
            }
        }
        
        public final void remove() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException();
            }
            if (this.main.modifications != this.expectedModifications) {
                throw new ConcurrentModificationException();
            }
            this.main.doRedBlackDelete(this.lastReturnedNode);
            ++this.expectedModifications;
            this.lastReturnedNode = null;
            if (this.nextNode == null) {
                this.previousNode = greatestNode(this.main.rootNode[this.orderType], this.orderType);
            }
            else {
                this.previousNode = this.main.nextSmaller(this.nextNode, this.orderType);
            }
        }
    }
    
    static class ViewMapIterator extends ViewIterator implements OrderedMapIterator
    {
        private final int oppositeType;
        
        ViewMapIterator(final TreeBidiMap main, final int orderType) {
            super(main, orderType, orderType);
            this.oppositeType = oppositeIndex(this.dataType);
        }
        
        public Object getKey() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getKey() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getData(this.dataType);
        }
        
        public Object getValue() {
            if (this.lastReturnedNode == null) {
                throw new IllegalStateException("Iterator getValue() can only be called after next() and before remove()");
            }
            return this.lastReturnedNode.getData(this.oppositeType);
        }
        
        public Object setValue(final Object obj) {
            throw new UnsupportedOperationException();
        }
    }
    
    static class EntryView extends View
    {
        private final int oppositeType;
        
        EntryView(final TreeBidiMap main, final int orderType, final int dataType) {
            super(main, orderType, dataType);
            this.oppositeType = oppositeIndex(orderType);
        }
        
        public boolean contains(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            final Object value = entry.getValue();
            final Node node = this.main.lookup(entry.getKey(), this.orderType);
            return node != null && node.getData(this.oppositeType).equals(value);
        }
        
        public boolean remove(final Object obj) {
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry entry = (Map.Entry)obj;
            final Object value = entry.getValue();
            final Node node = this.main.lookup(entry.getKey(), this.orderType);
            if (node != null && node.getData(this.oppositeType).equals(value)) {
                this.main.doRedBlackDelete(node);
                return true;
            }
            return false;
        }
    }
    
    static class Node implements Map.Entry, KeyValue
    {
        private Comparable[] data;
        private Node[] leftNode;
        private Node[] rightNode;
        private Node[] parentNode;
        private boolean[] blackColor;
        private int hashcodeValue;
        private boolean calculatedHashCode;
        
        Node(final Comparable key, final Comparable value) {
            this.data = new Comparable[] { key, value };
            this.leftNode = new Node[2];
            this.rightNode = new Node[2];
            this.parentNode = new Node[2];
            this.blackColor = new boolean[] { true, true };
            this.calculatedHashCode = false;
        }
        
        private Comparable getData(final int index) {
            return this.data[index];
        }
        
        private void setLeft(final Node node, final int index) {
            this.leftNode[index] = node;
        }
        
        private Node getLeft(final int index) {
            return this.leftNode[index];
        }
        
        private void setRight(final Node node, final int index) {
            this.rightNode[index] = node;
        }
        
        private Node getRight(final int index) {
            return this.rightNode[index];
        }
        
        private void setParent(final Node node, final int index) {
            this.parentNode[index] = node;
        }
        
        private Node getParent(final int index) {
            return this.parentNode[index];
        }
        
        private void swapColors(final Node node, final int index) {
            final boolean[] blackColor = this.blackColor;
            blackColor[index] ^= node.blackColor[index];
            final boolean[] blackColor2 = node.blackColor;
            blackColor2[index] ^= this.blackColor[index];
            final boolean[] blackColor3 = this.blackColor;
            blackColor3[index] ^= node.blackColor[index];
        }
        
        private boolean isBlack(final int index) {
            return this.blackColor[index];
        }
        
        private boolean isRed(final int index) {
            return !this.blackColor[index];
        }
        
        private void setBlack(final int index) {
            this.blackColor[index] = true;
        }
        
        private void setRed(final int index) {
            this.blackColor[index] = false;
        }
        
        private void copyColor(final Node node, final int index) {
            this.blackColor[index] = node.blackColor[index];
        }
        
        public Object getKey() {
            return this.data[0];
        }
        
        public Object getValue() {
            return this.data[1];
        }
        
        public Object setValue(final Object ignored) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }
        
        public boolean equals(final Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)obj;
            return this.data[0].equals(e.getKey()) && this.data[1].equals(e.getValue());
        }
        
        public int hashCode() {
            if (!this.calculatedHashCode) {
                this.hashcodeValue = (this.data[0].hashCode() ^ this.data[1].hashCode());
                this.calculatedHashCode = true;
            }
            return this.hashcodeValue;
        }
    }
    
    static class Inverse implements OrderedBidiMap
    {
        private final TreeBidiMap main;
        private Set keySet;
        private Set valuesSet;
        private Set entrySet;
        
        Inverse(final TreeBidiMap main) {
            this.main = main;
        }
        
        public int size() {
            return this.main.size();
        }
        
        public boolean isEmpty() {
            return this.main.isEmpty();
        }
        
        public Object get(final Object key) {
            return this.main.getKey(key);
        }
        
        public Object getKey(final Object value) {
            return this.main.get(value);
        }
        
        public boolean containsKey(final Object key) {
            return this.main.containsValue(key);
        }
        
        public boolean containsValue(final Object value) {
            return this.main.containsKey(value);
        }
        
        public Object firstKey() {
            if (this.main.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return leastNode(this.main.rootNode[1], 1).getValue();
        }
        
        public Object lastKey() {
            if (this.main.nodeCount == 0) {
                throw new NoSuchElementException("Map is empty");
            }
            return greatestNode(this.main.rootNode[1], 1).getValue();
        }
        
        public Object nextKey(final Object key) {
            checkKey(key);
            final Node node = this.main.nextGreater(this.main.lookup((Comparable)key, 1), 1);
            return (node == null) ? null : node.getValue();
        }
        
        public Object previousKey(final Object key) {
            checkKey(key);
            final Node node = this.main.nextSmaller(this.main.lookup((Comparable)key, 1), 1);
            return (node == null) ? null : node.getValue();
        }
        
        public Object put(final Object key, final Object value) {
            return this.main.doPut((Comparable)value, (Comparable)key, 1);
        }
        
        public void putAll(final Map map) {
            final Iterator it = map.entrySet().iterator();
            while (it.hasNext()) {
                final Map.Entry entry = it.next();
                this.put(entry.getKey(), entry.getValue());
            }
        }
        
        public Object remove(final Object key) {
            return this.main.removeValue(key);
        }
        
        public Object removeValue(final Object value) {
            return this.main.remove(value);
        }
        
        public void clear() {
            this.main.clear();
        }
        
        public Set keySet() {
            if (this.keySet == null) {
                this.keySet = new View(this.main, 1, 1);
            }
            return this.keySet;
        }
        
        public Collection values() {
            if (this.valuesSet == null) {
                this.valuesSet = new View(this.main, 1, 0);
            }
            return this.valuesSet;
        }
        
        public Set entrySet() {
            if (this.entrySet == null) {
                return new EntryView(this.main, 1, 3);
            }
            return this.entrySet;
        }
        
        public MapIterator mapIterator() {
            if (this.isEmpty()) {
                return EmptyOrderedMapIterator.INSTANCE;
            }
            return new ViewMapIterator(this.main, 1);
        }
        
        public OrderedMapIterator orderedMapIterator() {
            if (this.isEmpty()) {
                return EmptyOrderedMapIterator.INSTANCE;
            }
            return new ViewMapIterator(this.main, 1);
        }
        
        public BidiMap inverseBidiMap() {
            return this.main;
        }
        
        public OrderedBidiMap inverseOrderedBidiMap() {
            return this.main;
        }
        
        public boolean equals(final Object obj) {
            return this.main.doEquals(obj, 1);
        }
        
        public int hashCode() {
            return this.main.doHashCode(1);
        }
        
        public String toString() {
            return this.main.doToString(1);
        }
    }
}
