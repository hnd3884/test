package org.apache.commons.collections.list;

import java.util.NoSuchElementException;
import java.util.ConcurrentModificationException;
import org.apache.commons.collections.OrderedIterator;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Collection;
import java.util.AbstractList;

public class TreeList extends AbstractList
{
    private AVLNode root;
    private int size;
    
    public TreeList() {
    }
    
    public TreeList(final Collection coll) {
        this.addAll(coll);
    }
    
    public Object get(final int index) {
        this.checkInterval(index, 0, this.size() - 1);
        return this.root.get(index).getValue();
    }
    
    public int size() {
        return this.size;
    }
    
    public Iterator iterator() {
        return this.listIterator(0);
    }
    
    public ListIterator listIterator() {
        return this.listIterator(0);
    }
    
    public ListIterator listIterator(final int fromIndex) {
        this.checkInterval(fromIndex, 0, this.size());
        return new TreeListIterator(this, fromIndex);
    }
    
    public int indexOf(final Object object) {
        if (this.root == null) {
            return -1;
        }
        return this.root.indexOf(object, this.root.relativePosition);
    }
    
    public boolean contains(final Object object) {
        return this.indexOf(object) >= 0;
    }
    
    public Object[] toArray() {
        final Object[] array = new Object[this.size()];
        if (this.root != null) {
            this.root.toArray(array, this.root.relativePosition);
        }
        return array;
    }
    
    public void add(final int index, final Object obj) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size());
        if (this.root == null) {
            this.root = new AVLNode(index, obj, (AVLNode)null, (AVLNode)null);
        }
        else {
            this.root = this.root.insert(index, obj);
        }
        ++this.size;
    }
    
    public Object set(final int index, final Object obj) {
        this.checkInterval(index, 0, this.size() - 1);
        final AVLNode node = this.root.get(index);
        final Object result = node.value;
        node.setValue(obj);
        return result;
    }
    
    public Object remove(final int index) {
        ++this.modCount;
        this.checkInterval(index, 0, this.size() - 1);
        final Object result = this.get(index);
        this.root = this.root.remove(index);
        --this.size;
        return result;
    }
    
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
    
    static class AVLNode
    {
        private AVLNode left;
        private boolean leftIsPrevious;
        private AVLNode right;
        private boolean rightIsNext;
        private int height;
        private int relativePosition;
        private Object value;
        
        private AVLNode(final int relativePosition, final Object obj, final AVLNode rightFollower, final AVLNode leftFollower) {
            this.relativePosition = relativePosition;
            this.value = obj;
            this.rightIsNext = true;
            this.leftIsPrevious = true;
            this.right = rightFollower;
            this.left = leftFollower;
        }
        
        Object getValue() {
            return this.value;
        }
        
        void setValue(final Object obj) {
            this.value = obj;
        }
        
        AVLNode get(final int index) {
            final int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe == 0) {
                return this;
            }
            final AVLNode nextNode = (indexRelativeToMe < 0) ? this.getLeftSubTree() : this.getRightSubTree();
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
        
        AVLNode next() {
            if (this.rightIsNext || this.right == null) {
                return this.right;
            }
            return this.right.min();
        }
        
        AVLNode previous() {
            if (this.leftIsPrevious || this.left == null) {
                return this.left;
            }
            return this.left.max();
        }
        
        AVLNode insert(final int index, final Object obj) {
            final int indexRelativeToMe = index - this.relativePosition;
            if (indexRelativeToMe <= 0) {
                return this.insertOnLeft(indexRelativeToMe, obj);
            }
            return this.insertOnRight(indexRelativeToMe, obj);
        }
        
        private AVLNode insertOnLeft(final int indexRelativeToMe, final Object obj) {
            AVLNode ret = this;
            if (this.getLeftSubTree() == null) {
                this.setLeft(new AVLNode(-1, obj, this, this.left), null);
            }
            else {
                this.setLeft(this.left.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition >= 0) {
                ++this.relativePosition;
            }
            ret = this.balance();
            this.recalcHeight();
            return ret;
        }
        
        private AVLNode insertOnRight(final int indexRelativeToMe, final Object obj) {
            AVLNode ret = this;
            if (this.getRightSubTree() == null) {
                this.setRight(new AVLNode(1, obj, this.right, this), null);
            }
            else {
                this.setRight(this.right.insert(indexRelativeToMe, obj), null);
            }
            if (this.relativePosition < 0) {
                --this.relativePosition;
            }
            ret = this.balance();
            this.recalcHeight();
            return ret;
        }
        
        private AVLNode getLeftSubTree() {
            return this.leftIsPrevious ? null : this.left;
        }
        
        private AVLNode getRightSubTree() {
            return this.rightIsNext ? null : this.right;
        }
        
        private AVLNode max() {
            return (this.getRightSubTree() == null) ? this : this.right.max();
        }
        
        private AVLNode min() {
            return (this.getLeftSubTree() == null) ? this : this.left.min();
        }
        
        AVLNode remove(final int index) {
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
        
        private AVLNode removeMax() {
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
        
        private AVLNode removeMin() {
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
        
        private AVLNode removeSelf() {
            if (this.getRightSubTree() == null && this.getLeftSubTree() == null) {
                return null;
            }
            if (this.getRightSubTree() == null) {
                if (this.relativePosition > 0) {
                    final AVLNode left = this.left;
                    left.relativePosition += this.relativePosition + ((this.relativePosition <= 0) ? 1 : 0);
                }
                this.left.max().setRight(null, this.right);
                return this.left;
            }
            if (this.getLeftSubTree() == null) {
                final AVLNode right = this.right;
                right.relativePosition += this.relativePosition - ((this.relativePosition >= 0) ? 1 : 0);
                this.right.min().setLeft(null, this.left);
                return this.right;
            }
            if (this.heightRightMinusLeft() > 0) {
                final AVLNode rightMin = this.right.min();
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
                final AVLNode leftMax = this.left.max();
                this.value = leftMax.value;
                if (this.rightIsNext) {
                    this.right = leftMax.right;
                }
                final AVLNode leftPrevious = this.left.left;
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
        
        private AVLNode balance() {
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
        
        private int getOffset(final AVLNode node) {
            if (node == null) {
                return 0;
            }
            return node.relativePosition;
        }
        
        private int setOffset(final AVLNode node, final int newOffest) {
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
        
        private int getHeight(final AVLNode node) {
            return (node == null) ? -1 : node.height;
        }
        
        private int heightRightMinusLeft() {
            return this.getHeight(this.getRightSubTree()) - this.getHeight(this.getLeftSubTree());
        }
        
        private AVLNode rotateLeft() {
            final AVLNode newTop = this.right;
            final AVLNode movedNode = this.getRightSubTree().getLeftSubTree();
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
        
        private AVLNode rotateRight() {
            final AVLNode newTop = this.left;
            final AVLNode movedNode = this.getLeftSubTree().getRightSubTree();
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
        
        private void setLeft(final AVLNode node, final AVLNode previous) {
            this.leftIsPrevious = (node == null);
            this.left = (this.leftIsPrevious ? previous : node);
            this.recalcHeight();
        }
        
        private void setRight(final AVLNode node, final AVLNode next) {
            this.rightIsNext = (node == null);
            this.right = (this.rightIsNext ? next : node);
            this.recalcHeight();
        }
        
        public String toString() {
            return "AVLNode(" + this.relativePosition + "," + (this.left != null) + "," + this.value + "," + (this.getRightSubTree() != null) + ", faedelung " + this.rightIsNext + " )";
        }
    }
    
    static class TreeListIterator implements ListIterator, OrderedIterator
    {
        protected final TreeList parent;
        protected AVLNode next;
        protected int nextIndex;
        protected AVLNode current;
        protected int currentIndex;
        protected int expectedModCount;
        
        protected TreeListIterator(final TreeList parent, final int fromIndex) throws IndexOutOfBoundsException {
            this.parent = parent;
            this.expectedModCount = parent.modCount;
            this.next = ((parent.root == null) ? null : parent.root.get(fromIndex));
            this.nextIndex = fromIndex;
            this.currentIndex = -1;
        }
        
        protected void checkModCount() {
            if (this.parent.modCount != this.expectedModCount) {
                throw new ConcurrentModificationException();
            }
        }
        
        public boolean hasNext() {
            return this.nextIndex < this.parent.size();
        }
        
        public Object next() {
            this.checkModCount();
            if (!this.hasNext()) {
                throw new NoSuchElementException("No element at index " + this.nextIndex + ".");
            }
            if (this.next == null) {
                this.next = this.parent.root.get(this.nextIndex);
            }
            final Object value = this.next.getValue();
            this.current = this.next;
            this.currentIndex = this.nextIndex++;
            this.next = this.next.next();
            return value;
        }
        
        public boolean hasPrevious() {
            return this.nextIndex > 0;
        }
        
        public Object previous() {
            this.checkModCount();
            if (!this.hasPrevious()) {
                throw new NoSuchElementException("Already at start of list.");
            }
            if (this.next == null) {
                this.next = this.parent.root.get(this.nextIndex - 1);
            }
            else {
                this.next = this.next.previous();
            }
            final Object value = this.next.getValue();
            this.current = this.next;
            final int n = this.nextIndex - 1;
            this.nextIndex = n;
            this.currentIndex = n;
            return value;
        }
        
        public int nextIndex() {
            return this.nextIndex;
        }
        
        public int previousIndex() {
            return this.nextIndex() - 1;
        }
        
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
        
        public void set(final Object obj) {
            this.checkModCount();
            if (this.current == null) {
                throw new IllegalStateException();
            }
            this.current.setValue(obj);
        }
        
        public void add(final Object obj) {
            this.checkModCount();
            this.parent.add(this.nextIndex, obj);
            this.current = null;
            this.currentIndex = -1;
            ++this.nextIndex;
            ++this.expectedModCount;
        }
    }
}
