package org.apache.commons.collections4.list;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections4.OrderedIterator;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractList;

public class TreeList<E> extends AbstractList<E>
{
    private AVLNode<E> root;
    private int size;
    
    public TreeList() {
    }
    
    public TreeList(final Collection<? extends E> coll) {
        if (!coll.isEmpty()) {
            this.root = new AVLNode<E>((Collection)coll);
            this.size = coll.size();
        }
    }
    
    @Override
    public E get(final int index) {
        this.checkInterval(index, 0, this.size() - 1);
        return this.root.get(index).getValue();
    }
    
    @Override
    public int size() {
        return this.size;
    }
    
    @Override
    public Iterator<E> iterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.listIterator(0);
    }
    
    @Override
    public ListIterator<E> listIterator(final int fromIndex) {
        this.checkInterval(fromIndex, 0, this.size());
        return new TreeListIterator<E>(this, fromIndex);
    }
    
    @Override
    public int indexOf(final Object object) {
        if (this.root == null) {
            return -1;
        }
        return this.root.indexOf(object, ((AVLNode<Object>)this.root).relativePosition);
    }
    
    @Override
    public boolean contains(final Object object) {
        return this.indexOf(object) >= 0;
    }
    
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[this.size()];
        if (this.root != null) {
            this.root.toArray(array, ((AVLNode<Object>)this.root).relativePosition);
        }
        return array;
    }
    
    @Override
    public void add(final int index, final E obj) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size());
        if (this.root == null) {
            this.root = new AVLNode<E>(index, (Object)obj, (AVLNode<E>)null, (AVLNode<E>)null);
        }
        else {
            this.root = this.root.insert(index, obj);
        }
        ++this.size;
    }
    
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        if (c.isEmpty()) {
            return false;
        }
        this.modCount += c.size();
        final AVLNode<E> cTree = new AVLNode<E>((Collection)c);
        this.root = ((this.root == null) ? cTree : ((AVLNode<Object>)this.root).addAll((AVLNode<Object>)cTree, this.size));
        this.size += c.size();
        return true;
    }
    
    @Override
    public E set(final int index, final E obj) {
        this.checkInterval(index, 0, this.size() - 1);
        final AVLNode<E> node = this.root.get(index);
        final E result = (E)((AVLNode<Object>)node).value;
        node.setValue(obj);
        return result;
    }
    
    @Override
    public E remove(final int index) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size() - 1);
        final E result = this.get(index);
        this.root = this.root.remove(index);
        --this.size;
        return result;
    }
    
    @Override
    public void clear() {
        ++this.modCount;
        this.root = null;
        this.size = 0;
    }
    
    private void checkInterval(final int index, final int startIndex, final int endIndex) {
        if (index < startIndex || index > endIndex) {
            throw new IndexOutOfBoundsException("Invalid index:" + index + ", size=" + this.size());
        }
    }
    
    static class AVLNode<E>
    {
        private AVLNode<E> left;
        private boolean leftIsPrevious;
        private AVLNode<E> right;
        private boolean rightIsNext;
        private int height;
        private int relativePosition;
        private E value;
        
        private AVLNode(final int relativePosition, final E obj, final AVLNode<E> rightFollower, final AVLNode<E> leftFollower) {
            this.relativePosition = relativePosition;
            this.value = obj;
            this.rightIsNext = true;
            this.leftIsPrevious = true;
            this.right = rightFollower;
            this.left = leftFollower;
        }
        
        private AVLNode(final Collection<? extends E> coll) {
            this(coll.iterator(), 0, coll.size() - 1, 0, null, null);
        }
        
        private AVLNode(final Iterator<? extends E> iterator, final int start, final int end, final int absolutePositionOfParent, final AVLNode<E> prev, final AVLNode<E> next) {
            final int mid = start + (end - start) / 2;
            if (start < mid) {
                this.left = new AVLNode<E>(iterator, start, mid - 1, mid, prev, this);
            }
            else {
                this.leftIsPrevious = true;
                this.left = prev;
            }
            this.value = (E)iterator.next();
            this.relativePosition = mid - absolutePositionOfParent;
            if (mid < end) {
                this.right = new AVLNode<E>(iterator, mid + 1, end, mid, this, next);
            }
            else {
                this.rightIsNext = true;
                this.right = next;
            }
            this.recalcHeight();
        }
        
        E getValue() {
            return this.value;
        }
        
        void setValue(final E obj) {
            this.value = obj;
        }
        
        AVLNode<E> get(final int index) {
            final int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this;
            }
            final AVLNode<E> nextNode = (indexRelativeToMe < 0) ? this.getLeftSubTree() : this.getRightSubTree();
            if (nextNode == null) {
                return null;
            }
            return nextNode.get(indexRelativeToMe);
        }
        
        int indexOf(final Object object, final int index) {
            if (this.getLeftSubTree() != null) {
                final int result = this.left.indexOf(object, index + this.left.relativePosition);
                if (result != -1) {
                    return result;
                }
            }
            Label_0063: {
                if (this.value == null) {
                    if (this.value != object) {
                        break Label_0063;
                    }
                }
                else if (!this.value.equals(object)) {
                    break Label_0063;
                }
                return index;
            }
            if (this.getRightSubTree() != null) {
                return this.right.indexOf(object, index + this.right.relativePosition);
            }
            return -1;
        }
        
        void toArray(final Object[] array, final int index) {
            array[index] = this.value;
            if (this.getLeftSubTree() != null) {
                this.left.toArray(array, index + this.left.relativePosition);
            }
            if (this.getRightSubTree() != null) {
                this.right.toArray(array, index + this.right.relativePosition);
            }
        }
        
        AVLNode<E> next() {
            if (this.rightIsNext || this.right == null) {
                return this.right;
            }
            return this.right.min();
        }
        
        AVLNode<E> previous() {
            if (this.leftIsPrevious || this.left == null) {
                return this.left;
            }
            return this.left.max();
        }
        
        AVLNode<E> insert(final int index, final E obj) {
            final int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe <= 0) {
                return this.insertOnLeft(indexRelativeToMe, obj);
            }
            return this.insertOnRight(indexRelativeToMe, obj);
        }
        
        private AVLNode<E> insertOnLeft(final int indexRelativeToMe, final E obj) {
            if (this.getLeftSubTree() == null) {
                this.setLeft(new AVLNode<E>(-1, obj, this, this.left), null);
            }
            else {
                this.setLeft(this.left.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition >= 0) {
                ++this.relativePosition;
            }
            final AVLNode<E> ret = this.balance();
            this.recalcHeight();
            return ret;
        }
        
        private AVLNode<E> insertOnRight(final int indexRelativeToMe, final E obj) {
            if (this.getRightSubTree() == null) {
                this.setRight(new AVLNode<E>(1, obj, this.right, this), null);
            }
            else {
                this.setRight(this.right.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition < 0) {
                --this.relativePosition;
            }
            final AVLNode<E> ret = this.balance();
            this.recalcHeight();
            return ret;
        }
        
        private AVLNode<E> getLeftSubTree() {
            return this.leftIsPrevious ? null : this.left;
        }
        
        private AVLNode<E> getRightSubTree() {
            return this.rightIsNext ? null : this.right;
        }
        
        private AVLNode<E> max() {
            return (this.getRightSubTree() == null) ? this : this.right.max();
        }
        
        private AVLNode<E> min() {
            return (this.getLeftSubTree() == null) ? this : this.left.min();
        }
        
        AVLNode<E> remove(final int index) {
            final int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this.removeSelf();
            }
            if (indexRelativeToMe > 0) {
                this.setRight(this.right.remove(indexRelativeToMe), this.right.right);
                if (this.relativePosition < 0) {
                    ++this.relativePosition;
                }
            }
            else {
                this.setLeft(this.left.remove(indexRelativeToMe), this.left.left);
                if (this.relativePosition > 0) {
                    --this.relativePosition;
                }
            }
            this.recalcHeight();
            return this.balance();
        }
        
        private AVLNode<E> removeMax() {
            if (this.getRightSubTree() == null) {
                return this.removeSelf();
            }
            this.setRight(this.right.removeMax(), this.right.right);
            if (this.relativePosition < 0) {
                ++this.relativePosition;
            }
            this.recalcHeight();
            return this.balance();
        }
        
        private AVLNode<E> removeMin() {
            if (this.getLeftSubTree() == null) {
                return this.removeSelf();
            }
            this.setLeft(this.left.removeMin(), this.left.left);
            if (this.relativePosition > 0) {
                --this.relativePosition;
            }
            this.recalcHeight();
            return this.balance();
        }
        
        private AVLNode<E> removeSelf() {
            if (this.getRightSubTree() == null && this.getLeftSubTree() == null) {
                return null;
            }
            if (this.getRightSubTree() == null) {
                if (this.relativePosition > 0) {
                    final AVLNode<E> left = this.left;
                    left.relativePosition += this.relativePosition + ((this.relativePosition <= 0) ? 1 : 0);
                }
                this.left.max().setRight(null, this.right);
                return this.left;
            }
            if (this.getLeftSubTree() == null) {
                final AVLNode<E> right = this.right;
                right.relativePosition += this.relativePosition - ((this.relativePosition >= 0) ? 1 : 0);
                this.right.min().setLeft(null, this.left);
                return this.right;
            }
            if (this.heightRightMinusLeft() > 0) {
                final AVLNode<E> rightMin = this.right.min();
                this.value = rightMin.value;
                if (this.leftIsPrevious) {
                    this.left = rightMin.left;
                }
                this.right = this.right.removeMin();
                if (this.relativePosition < 0) {
                    ++this.relativePosition;
                }
            }
            else {
                final AVLNode<E> leftMax = this.left.max();
                this.value = leftMax.value;
                if (this.rightIsNext) {
                    this.right = leftMax.right;
                }
                final AVLNode<E> leftPrevious = this.left.left;
                this.left = this.left.removeMax();
                if (this.left == null) {
                    this.left = leftPrevious;
                    this.leftIsPrevious = true;
                }
                if (this.relativePosition > 0) {
                    --this.relativePosition;
                }
            }
            this.recalcHeight();
            return this;
        }
        
        private AVLNode<E> balance() {
            switch (this.heightRightMinusLeft()) {
                case -1:
                case 0:
                case 1: {
                    return this;
                }
                case -2: {
                    if (this.left.heightRightMinusLeft() > 0) {
                        this.setLeft(this.left.rotateLeft(), null);
                    }
                    return this.rotateRight();
                }
                case 2: {
                    if (this.right.heightRightMinusLeft() < 0) {
                        this.setRight(this.right.rotateRight(), null);
                    }
                    return this.rotateLeft();
                }
                default: {
                    throw new RuntimeException("tree inconsistent!");
                }
            }
        }
        
        private int getOffset(final AVLNode<E> node) {
            if (node == null) {
                return 0;
            }
            return node.relativePosition;
        }
        
        private int setOffset(final AVLNode<E> node, final int newOffest) {
            if (node == null) {
                return 0;
            }
            final int oldOffset = this.getOffset(node);
            node.relativePosition = newOffest;
            return oldOffset;
        }
        
        private void recalcHeight() {
            this.height = Math.max((this.getLeftSubTree() == null) ? -1 : this.getLeftSubTree().height, (this.getRightSubTree() == null) ? -1 : this.getRightSubTree().height) + 1;
        }
        
        private int getHeight(final AVLNode<E> node) {
            return (node == null) ? -1 : node.height;
        }
        
        private int heightRightMinusLeft() {
            return this.getHeight(this.getRightSubTree()) - this.getHeight(this.getLeftSubTree());
        }
        
        private AVLNode<E> rotateLeft() {
            final AVLNode<E> newTop = this.right;
            final AVLNode<E> movedNode = this.getRightSubTree().getLeftSubTree();
            final int newTopPosition = this.relativePosition + this.getOffset(newTop);
            final int myNewPosition = -newTop.relativePosition;
            final int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
            this.setRight(movedNode, newTop);
            newTop.setLeft(this, null);
            this.setOffset(newTop, newTopPosition);
            this.setOffset(this, myNewPosition);
            this.setOffset(movedNode, movedPosition);
            return newTop;
        }
        
        private AVLNode<E> rotateRight() {
            final AVLNode<E> newTop = this.left;
            final AVLNode<E> movedNode = this.getLeftSubTree().getRightSubTree();
            final int newTopPosition = this.relativePosition + this.getOffset(newTop);
            final int myNewPosition = -newTop.relativePosition;
            final int movedPosition = this.getOffset(newTop) + this.getOffset(movedNode);
            this.setLeft(movedNode, newTop);
            newTop.setRight(this, null);
            this.setOffset(newTop, newTopPosition);
            this.setOffset(this, myNewPosition);
            this.setOffset(movedNode, movedPosition);
            return newTop;
        }
        
        private void setLeft(final AVLNode<E> node, final AVLNode<E> previous) {
            this.leftIsPrevious = (node == null);
            this.left = (this.leftIsPrevious ? previous : node);
            this.recalcHeight();
        }
        
        private void setRight(final AVLNode<E> node, final AVLNode<E> next) {
            this.rightIsNext = (node == null);
            this.right = (this.rightIsNext ? next : node);
            this.recalcHeight();
        }
        
        private AVLNode<E> addAll(AVLNode<E> otherTree, final int currentSize) {
            final AVLNode<E> maxNode = this.max();
            final AVLNode<E> otherTreeMin = otherTree.min();
            if (otherTree.height > this.height) {
                final AVLNode<E> leftSubTree = this.removeMax();
                final Deque<AVLNode<E>> sAncestors = new ArrayDeque<AVLNode<E>>();
                AVLNode<E> s = otherTree;
                int sAbsolutePosition = s.relativePosition + currentSize;
                int sParentAbsolutePosition = 0;
                while (s != null && s.height > this.getHeight(leftSubTree)) {
                    sParentAbsolutePosition = sAbsolutePosition;
                    sAncestors.push(s);
                    s = s.left;
                    if (s != null) {
                        sAbsolutePosition += s.relativePosition;
                    }
                }
                maxNode.setLeft(leftSubTree, null);
                maxNode.setRight(s, otherTreeMin);
                if (leftSubTree != null) {
                    leftSubTree.max().setRight(null, maxNode);
                    final AVLNode<E> avlNode = leftSubTree;
                    avlNode.relativePosition -= currentSize - 1;
                }
                if (s != null) {
                    s.min().setLeft(null, maxNode);
                    s.relativePosition = sAbsolutePosition - currentSize + 1;
                }
                maxNode.relativePosition = currentSize - 1 - sParentAbsolutePosition;
                final AVLNode<E> avlNode2 = otherTree;
                avlNode2.relativePosition += currentSize;
                s = maxNode;
                while (!sAncestors.isEmpty()) {
                    final AVLNode<E> sAncestor = sAncestors.pop();
                    sAncestor.setLeft(s, null);
                    s = sAncestor.balance();
                }
                return s;
            }
            otherTree = otherTree.removeMin();
            final Deque<AVLNode<E>> sAncestors2 = new ArrayDeque<AVLNode<E>>();
            AVLNode<E> s2 = this;
            int sAbsolutePosition2 = s2.relativePosition;
            int sParentAbsolutePosition2 = 0;
            while (s2 != null && s2.height > this.getHeight(otherTree)) {
                sParentAbsolutePosition2 = sAbsolutePosition2;
                sAncestors2.push(s2);
                s2 = s2.right;
                if (s2 != null) {
                    sAbsolutePosition2 += s2.relativePosition;
                }
            }
            otherTreeMin.setRight(otherTree, null);
            otherTreeMin.setLeft(s2, maxNode);
            if (otherTree != null) {
                otherTree.min().setLeft(null, otherTreeMin);
                final AVLNode<E> avlNode3 = otherTree;
                ++avlNode3.relativePosition;
            }
            if (s2 != null) {
                s2.max().setRight(null, otherTreeMin);
                s2.relativePosition = sAbsolutePosition2 - currentSize;
            }
            otherTreeMin.relativePosition = currentSize - sParentAbsolutePosition2;
            s2 = otherTreeMin;
            while (!sAncestors2.isEmpty()) {
                final AVLNode<E> sAncestor2 = sAncestors2.pop();
                sAncestor2.setRight(s2, null);
                s2 = sAncestor2.balance();
            }
            return s2;
        }
        
        @Override
        public String toString() {
            return "AVLNode(" + this.relativePosition + ',' + (this.left != null) + ',' + this.value + ',' + (this.getRightSubTree() != null) + ", faedelung " + this.rightIsNext + " )";
        }
    }
    
    static class TreeListIterator<E> implements ListIterator<E>, OrderedIterator<E>
    {
        private final TreeList<E> parent;
        private AVLNode<E> next;
        private int nextIndex;
        private AVLNode<E> current;
        private int currentIndex;
        private int expectedModCount;
        
        protected TreeListIterator(final TreeList<E> parent, final int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = ((TreeList<Object>)parent).modCount;
            this.next = ((((TreeList<Object>)parent).root == null) ? null : ((TreeList<Object>)parent).root.get(fromIndex));
            this.nextIndex = fromIndex;
            this.currentIndex = -1;
        }
        
        protected void checkModCount() {
            if (((TreeList<Object>)this.parent).modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.nextIndex < this.parent.size();
        }
        
        @Override
        public E next() {
            this.checkModCount();
            if (!this.hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            if (this.next == null) {
                this.next = ((TreeList<Object>)this.parent).root.get(this.nextIndex);
            }
            final E value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = this.nextIndex++;
            this.next = this.next.next();
            return value;
        }
        
        @Override
        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }
        
        @Override
        public E previous() {
            this.checkModCount();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            if (this.next == null) {
                this.next = ((TreeList<Object>)this.parent).root.get(this.nextIndex - 1);
            }
            else {
                this.next = this.next.previous();
            }
            final E value = this.next.getValue();
            this.current = this.next;
            final int n = this.nextIndex - 1;
            this.nextIndex = n;
            this.currentIndex = n;
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
            if (this.currentIndex == -1) {
                throw new IllegalStateException();
            }
            this.parent.remove(this.currentIndex);
            if (this.nextIndex != this.currentIndex) {
                --this.nextIndex;
            }
            this.next = null;
            this.current = null;
            this.currentIndex = -1;
            ++this.expectedModCount;
        }
        
        @Override
        public void set(final E obj) {
            this.checkModCount();
            if (this.current == null) {
                throw new IllegalStateException();
            }
            this.current.setValue(obj);
        }
        
        @Override
        public void add(final E obj) {
            this.checkModCount();
            this.parent.add(this.nextIndex, obj);
            this.current = null;
            this.currentIndex = -1;
            ++this.nextIndex;
            ++this.expectedModCount;
        }
    }
}
