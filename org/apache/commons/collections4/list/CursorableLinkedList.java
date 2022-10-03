package org.apache.commons.collections4.list;

import java.util.ConcurrentModificationException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.lang.ref.WeakReference;
import java.util.List;
import java.io.Serializable;

public class CursorableLinkedList<E> extends AbstractLinkedList<E> implements Serializable
{
    private static final long serialVersionUID = 8836393098519411393L;
    private transient List<WeakReference<Cursor<E>>> cursors;
    
    public CursorableLinkedList() {
        this.init();
    }
    
    public CursorableLinkedList(final Collection<? extends E> coll) {
        super(coll);
    }
    
    @Override
    protected void init() {
        super.init();
        this.cursors = new ArrayList<WeakReference<Cursor<E>>>();
    }
    
    @Override
    public Iterator<E> iterator() {
        return super.listIterator(0);
    }
    
    @Override
    public ListIterator<E> listIterator() {
        return this.cursor(0);
    }
    
    @Override
    public ListIterator<E> listIterator(final int fromIndex) {
        return this.cursor(fromIndex);
    }
    
    public Cursor<E> cursor() {
        return this.cursor(0);
    }
    
    public Cursor<E> cursor(final int fromIndex) {
        final Cursor<E> cursor = new Cursor<E>(this, fromIndex);
        this.registerCursor(cursor);
        return cursor;
    }
    
    @Override
    protected void updateNode(final Node<E> node, final E value) {
        super.updateNode(node, value);
        this.broadcastNodeChanged(node);
    }
    
    @Override
    protected void addNode(final Node<E> nodeToInsert, final Node<E> insertBeforeNode) {
        super.addNode(nodeToInsert, insertBeforeNode);
        this.broadcastNodeInserted(nodeToInsert);
    }
    
    @Override
    protected void removeNode(final Node<E> node) {
        super.removeNode(node);
        this.broadcastNodeRemoved(node);
    }
    
    @Override
    protected void removeAllNodes() {
        if (this.size() > 0) {
            final Iterator<E> it = this.iterator();
            while (it.hasNext()) {
                it.next();
                it.remove();
            }
        }
    }
    
    protected void registerCursor(final Cursor<E> cursor) {
        final Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();
        while (it.hasNext()) {
            final WeakReference<Cursor<E>> ref = it.next();
            if (ref.get() == null) {
                it.remove();
            }
        }
        this.cursors.add(new WeakReference<Cursor<E>>(cursor));
    }
    
    protected void unregisterCursor(final Cursor<E> cursor) {
        final Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();
        while (it.hasNext()) {
            final WeakReference<Cursor<E>> ref = it.next();
            final Cursor<E> cur = ref.get();
            if (cur == null) {
                it.remove();
            }
            else {
                if (cur == cursor) {
                    ref.clear();
                    it.remove();
                    break;
                }
                continue;
            }
        }
    }
    
    protected void broadcastNodeChanged(final Node<E> node) {
        final Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();
        while (it.hasNext()) {
            final WeakReference<Cursor<E>> ref = it.next();
            final Cursor<E> cursor = ref.get();
            if (cursor == null) {
                it.remove();
            }
            else {
                cursor.nodeChanged(node);
            }
        }
    }
    
    protected void broadcastNodeRemoved(final Node<E> node) {
        final Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();
        while (it.hasNext()) {
            final WeakReference<Cursor<E>> ref = it.next();
            final Cursor<E> cursor = ref.get();
            if (cursor == null) {
                it.remove();
            }
            else {
                cursor.nodeRemoved(node);
            }
        }
    }
    
    protected void broadcastNodeInserted(final Node<E> node) {
        final Iterator<WeakReference<Cursor<E>>> it = this.cursors.iterator();
        while (it.hasNext()) {
            final WeakReference<Cursor<E>> ref = it.next();
            final Cursor<E> cursor = ref.get();
            if (cursor == null) {
                it.remove();
            }
            else {
                cursor.nodeInserted(node);
            }
        }
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        this.doWriteObject(out);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.doReadObject(in);
    }
    
    @Override
    protected ListIterator<E> createSubListListIterator(final LinkedSubList<E> subList, final int fromIndex) {
        final SubCursor<E> cursor = new SubCursor<E>(subList, fromIndex);
        this.registerCursor(cursor);
        return cursor;
    }
    
    public static class Cursor<E> extends LinkedListIterator<E>
    {
        boolean valid;
        boolean nextIndexValid;
        boolean currentRemovedByAnother;
        
        protected Cursor(final CursorableLinkedList<E> parent, final int index) {
            super(parent, index);
            this.valid = true;
            this.nextIndexValid = true;
            this.currentRemovedByAnother = false;
            this.valid = true;
        }
        
        @Override
        public void remove() {
            if (this.current != null || !this.currentRemovedByAnother) {
                this.checkModCount();
                this.parent.removeNode(this.getLastNodeReturned());
            }
            this.currentRemovedByAnother = false;
        }
        
        @Override
        public void add(final E obj) {
            super.add(obj);
            this.next = this.next.next;
        }
        
        @Override
        public int nextIndex() {
            if (!this.nextIndexValid) {
                if (this.next == this.parent.header) {
                    this.nextIndex = this.parent.size();
                }
                else {
                    int pos = 0;
                    for (Node<E> temp = this.parent.header.next; temp != this.next; temp = temp.next) {
                        ++pos;
                    }
                    this.nextIndex = pos;
                }
                this.nextIndexValid = true;
            }
            return this.nextIndex;
        }
        
        protected void nodeChanged(final Node<E> node) {
        }
        
        protected void nodeRemoved(final Node<E> node) {
            if (node == this.next && node == this.current) {
                this.next = node.next;
                this.current = null;
                this.currentRemovedByAnother = true;
            }
            else if (node == this.next) {
                this.next = node.next;
                this.currentRemovedByAnother = false;
            }
            else if (node == this.current) {
                this.current = null;
                this.currentRemovedByAnother = true;
                --this.nextIndex;
            }
            else {
                this.nextIndexValid = false;
                this.currentRemovedByAnother = false;
            }
        }
        
        protected void nodeInserted(final Node<E> node) {
            if (node.previous == this.current) {
                this.next = node;
            }
            else if (this.next.previous == node) {
                this.next = node;
            }
            else {
                this.nextIndexValid = false;
            }
        }
        
        @Override
        protected void checkModCount() {
            if (!this.valid) {
                throw new ConcurrentModificationException("Cursor closed");
            }
        }
        
        public void close() {
            if (this.valid) {
                ((CursorableLinkedList)this.parent).unregisterCursor(this);
                this.valid = false;
            }
        }
    }
    
    protected static class SubCursor<E> extends Cursor<E>
    {
        protected final LinkedSubList<E> sub;
        
        protected SubCursor(final LinkedSubList<E> sub, final int index) {
            super((CursorableLinkedList)sub.parent, index + sub.offset);
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
}
