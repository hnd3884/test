package org.apache.commons.collections4.list;

import java.util.AbstractList;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections4.OrderedIterator;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.NoSuchElementException;
import java.lang.reflect.Array;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;

public abstract class AbstractLinkedList<E> implements List<E>
{
    transient Node<E> header;
    transient int size;
    transient int modCount;
    
    protected AbstractLinkedList() {
    }
    
    protected AbstractLinkedList(final Collection<? extends E> coll) {
        this.init();
        this.addAll(coll);
    }
    
    protected void init() {
        this.header = this.createHeaderNode();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }
    
    @Override
    public E get(final int index) {
        final Node<E> node = this.getNode(index, false);
        return node.getValue();
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.listIterator();
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return new LinkedListIterator<E>(this, 0);
    }
    
    @Override
    public ListIterator<E> listIterator(final int fromIndex) {
        return new LinkedListIterator<E>(this, fromIndex);
    }
    
    @Override
    public int indexOf(final Object value) {
        int i = 0;
        for (Node<E> node = this.header.next; node != this.header; node = node.next) {
            if (this.isEqualValue(node.getValue(), value)) {
                return i;
            }
            ++i;
        }
        return -1;
    }
    
    @Override
    public int lastIndexOf(final Object value) {
        int i = this.size - 1;
        for (Node<E> node = this.header.previous; node != this.header; node = node.previous) {
            if (this.isEqualValue(node.getValue(), value)) {
                return i;
            }
            --i;
        }
        return -1;
    }
    
    @Override
    public boolean contains(final Object value) {
        return this.indexOf(value) != -1;
    }
    
    @Override
    public boolean containsAll(final Collection<?> coll) {
        for (final Object o : coll) {
            if (!this.contains(o)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public Object[] toArray() {
        return this.toArray(new Object[this.size]);
    }
    
    @Override
    public <T> T[] toArray(T[] array) {
        if (array.length < this.size) {
            final Class<?> componentType = array.getClass().getComponentType();
            array = (T[])Array.newInstance(componentType, this.size);
        }
        int i = 0;
        for (Node<E> node = this.header.next; node != this.header; node = node.next, ++i) {
            array[i] = (T)node.getValue();
        }
        if (array.length > this.size) {
            array[this.size] = null;
        }
        return array;
    }
    
    @Override
    public List<E> subList(final int fromIndexInclusive, final int toIndexExclusive) {
        return new LinkedSubList<E>(this, fromIndexInclusive, toIndexExclusive);
    }
    
    @Override
    public boolean add(final E value) {
        this.addLast(value);
        return true;
    }
    
    @Override
    public void add(final int index, final E value) {
        final Node<E> node = this.getNode(index, true);
        this.addNodeBefore(node, value);
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        return this.addAll(this.size, coll);
    }
    
    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        final Node<E> node = this.getNode(index, true);
        for (final E e : coll) {
            this.addNodeBefore(node, e);
        }
        return true;
    }
    
    @Override
    public E remove(final int index) {
        final Node<E> node = this.getNode(index, false);
        final E oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }
    
    @Override
    public boolean remove(final Object value) {
        for (Node<E> node = this.header.next; node != this.header; node = node.next) {
            if (this.isEqualValue(node.getValue(), value)) {
                this.removeNode(node);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean removeAll(final Collection<?> coll) {
        boolean modified = false;
        final Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            if (coll.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    @Override
    public boolean retainAll(final Collection<?> coll) {
        boolean modified = false;
        final Iterator<E> it = this.iterator();
        while (it.hasNext()) {
            if (!coll.contains(it.next())) {
                it.remove();
                modified = true;
            }
        }
        return modified;
    }
    
    @Override
    public E set(final int index, final E value) {
        final Node<E> node = this.getNode(index, false);
        final E oldValue = node.getValue();
        this.updateNode(node, value);
        return oldValue;
    }
    
    @Override
    public void clear() {
        this.removeAllNodes();
    }
    
    public E getFirst() {
        final Node<E> node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }
    
    public E getLast() {
        final Node<E> node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        return node.getValue();
    }
    
    public boolean addFirst(final E o) {
        this.addNodeAfter(this.header, o);
        return true;
    }
    
    public boolean addLast(final E o) {
        this.addNodeBefore(this.header, o);
        return true;
    }
    
    public E removeFirst() {
        final Node<E> node = this.header.next;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        final E oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }
    
    public E removeLast() {
        final Node<E> node = this.header.previous;
        if (node == this.header) {
            throw new NoSuchElementException();
        }
        final E oldValue = node.getValue();
        this.removeNode(node);
        return oldValue;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof List)) {
            return false;
        }
        final List<?> other = (List<?>)obj;
        if (other.size() != this.size()) {
            return false;
        }
        final ListIterator<?> it1 = this.listIterator();
        final ListIterator<?> it2 = other.listIterator();
        while (it1.hasNext() && it2.hasNext()) {
            final Object o1 = it1.next();
            final Object o2 = it2.next();
            if (o1 == null) {
                if (o2 == null) {
                    continue;
                }
                return false;
            }
            else {
                if (!o1.equals(o2)) {
                    return false;
                }
                continue;
            }
        }
        return !it1.hasNext() && !it2.hasNext();
    }
    
    @Override
    public int hashCode() {
        int hashCode = 1;
        for (final E e : this) {
            hashCode = 31 * hashCode + ((e == null) ? 0 : e.hashCode());
        }
        return hashCode;
    }
    
    @Override
    public String toString() {
        if (this.size() == 0) {
            return "[]";
        }
        final StringBuilder buf = new StringBuilder(16 * this.size());
        buf.append('[');
        final Iterator<E> it = this.iterator();
        boolean hasNext = it.hasNext();
        while (hasNext) {
            final Object value = it.next();
            buf.append((value == this) ? "(this Collection)" : value);
            hasNext = it.hasNext();
            if (hasNext) {
                buf.append(", ");
            }
        }
        buf.append(']');
        return buf.toString();
    }
    
    protected boolean isEqualValue(final Object value1, final Object value2) {
        if (value1 != value2) {
            if (value1 != null) {
                if (value1.equals(value2)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    protected void updateNode(final Node<E> node, final E value) {
        node.setValue(value);
    }
    
    protected Node<E> createHeaderNode() {
        return new Node<E>();
    }
    
    protected Node<E> createNode(final E value) {
        return new Node<E>(value);
    }
    
    protected void addNodeBefore(final Node<E> node, final E value) {
        final Node<E> newNode = this.createNode(value);
        this.addNode(newNode, node);
    }
    
    protected void addNodeAfter(final Node<E> node, final E value) {
        final Node<E> newNode = this.createNode(value);
        this.addNode(newNode, node.next);
    }
    
    protected void addNode(final Node<E> nodeToInsert, final Node<E> insertBeforeNode) {
        nodeToInsert.next = insertBeforeNode;
        nodeToInsert.previous = insertBeforeNode.previous;
        insertBeforeNode.previous.next = nodeToInsert;
        insertBeforeNode.previous = nodeToInsert;
        ++this.size;
        ++this.modCount;
    }
    
    protected void removeNode(final Node<E> node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
        --this.size;
        ++this.modCount;
    }
    
    protected void removeAllNodes() {
        this.header.next = this.header;
        this.header.previous = this.header;
        this.size = 0;
        ++this.modCount;
    }
    
    protected Node<E> getNode(final int index, final boolean endMarkerAllowed) throws IndexOutOfBoundsException {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") less than zero.");
        }
        if (!endMarkerAllowed && index == this.size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") is the size of the list.");
        }
        if (index > this.size) {
            throw new IndexOutOfBoundsException("Couldn't get the node: index (" + index + ") greater than the size of the " + "list (" + this.size + ").");
        }
        Node<E> node;
        if (index < this.size / 2) {
            node = this.header.next;
            for (int currentIndex = 0; currentIndex < index; ++currentIndex) {
                node = node.next;
            }
        }
        else {
            node = this.header;
            for (int currentIndex = this.size; currentIndex > index; --currentIndex) {
                node = node.previous;
            }
        }
        return node;
    }
    
    protected Iterator<E> createSubListIterator(final LinkedSubList<E> subList) {
        return this.createSubListListIterator(subList, 0);
    }
    
    protected ListIterator<E> createSubListListIterator(final LinkedSubList<E> subList, final int fromIndex) {
        return new LinkedSubListIterator<E>(subList, fromIndex);
    }
    
    protected void doWriteObject(final ObjectOutputStream outputStream) throws IOException {
        outputStream.writeInt(this.size());
        for (final E e : this) {
            outputStream.writeObject(e);
        }
    }
    
    protected void doReadObject(final ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
        this.init();
        for (int size = inputStream.readInt(), i = 0; i < size; ++i) {
            this.add(inputStream.readObject());
        }
    }
    
    protected static class Node<E>
    {
        protected Node<E> previous;
        protected Node<E> next;
        protected E value;
        
        protected Node() {
            this.previous = this;
            this.next = this;
        }
        
        protected Node(final E value) {
            this.value = value;
        }
        
        protected Node(final Node<E> previous, final Node<E> next, final E value) {
            this.previous = previous;
            this.next = next;
            this.value = value;
        }
        
        protected E getValue() {
            return this.value;
        }
        
        protected void setValue(final E value) {
            this.value = value;
        }
        
        protected Node<E> getPreviousNode() {
            return this.previous;
        }
        
        protected void setPreviousNode(final Node<E> previous) {
            this.previous = previous;
        }
        
        protected Node<E> getNextNode() {
            return this.next;
        }
        
        protected void setNextNode(final Node<E> next) {
            this.next = next;
        }
    }
    
    protected static class LinkedListIterator<E> implements ListIterator<E>, OrderedIterator<E>
    {
        protected final AbstractLinkedList<E> parent;
        protected Node<E> next;
        protected int nextIndex;
        protected Node<E> current;
        protected int expectedModCount;
        
        protected LinkedListIterator(final AbstractLinkedList<E> parent, final int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = parent.getNode(fromIndex, true);
            this.nextIndex = fromIndex;
        }
        
        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        protected Node<E> getLastNodeReturned() throws IllegalStateException {
            if (this.current == null) {
                throw new IllegalStateException();
            }
            return this.current;
        }
        
        @Override
        public boolean hasNext() {
            return this.next != this.parent.header;
        }
        
        @Override
        public E next() {
            this.checkModCount();
            if (!this.hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            final E value = this.next.getValue();
            this.current = this.next;
            this.next = this.next.next;
            ++this.nextIndex;
            return value;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.next.previous != this.parent.header;
        }
        
        @Override
        public E previous() {
            this.checkModCount();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            this.next = this.next.previous;
            final E value = this.next.getValue();
            this.current = this.next;
            --this.nextIndex;
            return value;
        }
        
        @Override
        public int nextIndex() {
            return this.nextIndex;
        }
        
        @Override
        public int previousIndex() {
            return this.nextIndex() - 1;
        }
        
        @Override
        public void remove() {
            this.checkModCount();
            if (this.current == this.next) {
                this.next = this.next.next;
                this.parent.removeNode(this.getLastNodeReturned());
            }
            else {
                this.parent.removeNode(this.getLastNodeReturned());
                --this.nextIndex;
            }
            this.current = null;
            ++this.expectedModCount;
        }
        
        @Override
        public void set(final E obj) {
            this.checkModCount();
            this.getLastNodeReturned().setValue(obj);
        }
        
        @Override
        public void add(final E obj) {
            this.checkModCount();
            this.parent.addNodeBefore(this.next, obj);
            this.current = null;
            ++this.nextIndex;
            ++this.expectedModCount;
        }
    }
    
    protected static class LinkedSubListIterator<E> extends LinkedListIterator<E>
    {
        protected final LinkedSubList<E> sub;
        
        protected LinkedSubListIterator(final LinkedSubList<E> sub, final int startIndex) {
            super(sub.parent, startIndex + sub.offset);
            this.sub = sub;
        }
        
        @Override
        public boolean hasNext() {
            return this.nextIndex() < this.sub.size;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.previousIndex() >= 0;
        }
        
        @Override
        public int nextIndex() {
            return super.nextIndex() - this.sub.offset;
        }
        
        @Override
        public void add(final E obj) {
            super.add(obj);
            this.sub.expectedModCount = this.parent.modCount;
            final LinkedSubList<E> sub = this.sub;
            ++sub.size;
        }
        
        @Override
        public void remove() {
            super.remove();
            this.sub.expectedModCount = this.parent.modCount;
            final LinkedSubList<E> sub = this.sub;
            --sub.size;
        }
    }
    
    protected static class LinkedSubList<E> extends AbstractList<E>
    {
        AbstractLinkedList<E> parent;
        int offset;
        int size;
        int expectedModCount;
        
        protected LinkedSubList(final AbstractLinkedList<E> parent, final int fromIndex, final int toIndex) {
            if (fromIndex < 0) {
                throw new IndexOutOfBoundsException("fromIndex = " + fromIndex);
            }
            if (toIndex > parent.size()) {
                throw new IndexOutOfBoundsException("toIndex = " + toIndex);
            }
            if (fromIndex > toIndex) {
                throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
            }
            this.parent = parent;
            this.offset = fromIndex;
            this.size = toIndex - fromIndex;
            this.expectedModCount = parent.modCount;
        }
        
        @Override
        public int size() {
            this.checkModCount();
            return this.size;
        }
        
        @Override
        public E get(final int index) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            return this.parent.get(index + this.offset);
        }
        
        @Override
        public void add(final int index, final E obj) {
            this.rangeCheck(index, this.size + 1);
            this.checkModCount();
            this.parent.add(index + this.offset, obj);
            this.expectedModCount = this.parent.modCount;
            ++this.size;
            ++this.modCount;
        }
        
        @Override
        public E remove(final int index) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            final E result = this.parent.remove(index + this.offset);
            this.expectedModCount = this.parent.modCount;
            --this.size;
            ++this.modCount;
            return result;
        }
        
        @Override
        public boolean addAll(final Collection<? extends E> coll) {
            return this.addAll(this.size, coll);
        }
        
        @Override
        public boolean addAll(final int index, final Collection<? extends E> coll) {
            this.rangeCheck(index, this.size + 1);
            final int cSize = coll.size();
            if (cSize == 0) {
                return false;
            }
            this.checkModCount();
            this.parent.addAll(this.offset + index, coll);
            this.expectedModCount = this.parent.modCount;
            this.size += cSize;
            ++this.modCount;
            return true;
        }
        
        @Override
        public E set(final int index, final E obj) {
            this.rangeCheck(index, this.size);
            this.checkModCount();
            return this.parent.set(index + this.offset, obj);
        }
        
        @Override
        public void clear() {
            this.checkModCount();
            final Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
        
        @Override
        public Iterator<E> iterator() {
            this.checkModCount();
            return this.parent.createSubListIterator(this);
        }
        
        @Override
        public ListIterator<E> listIterator(final int index) {
            this.rangeCheck(index, this.size + 1);
            this.checkModCount();
            return this.parent.createSubListListIterator(this, index);
        }
        
        @Override
        public List<E> subList(final int fromIndexInclusive, final int toIndexExclusive) {
            return new LinkedSubList((AbstractLinkedList<Object>)this.parent, fromIndexInclusive + this.offset, toIndexExclusive + this.offset);
        }
        
        protected void rangeCheck(final int index, final int beyond) {
            if (index < 0 || index >= beyond) {
                throw new IndexOutOfBoundsException("Index '" + index + "' out of bounds for size '" + this.size + "'");
            }
        }
        
        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
    }
}
