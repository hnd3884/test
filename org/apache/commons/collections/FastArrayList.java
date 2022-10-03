package org.apache.commons.collections;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public class FastArrayList extends ArrayList
{
    protected ArrayList list;
    protected boolean fast;
    
    public FastArrayList() {
        this.list = null;
        this.fast = false;
        this.list = new ArrayList();
    }
    
    public FastArrayList(final int capacity) {
        this.list = null;
        this.fast = false;
        this.list = new ArrayList(capacity);
    }
    
    public FastArrayList(final Collection collection) {
        this.list = null;
        this.fast = false;
        this.list = new ArrayList(collection);
    }
    
    public boolean getFast() {
        return this.fast;
    }
    
    public void setFast(final boolean fast) {
        this.fast = fast;
    }
    
    public boolean add(final Object element) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.add(element);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.add(element);
        }
    }
    
    public void add(final int index, final Object element) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                temp.add(index, element);
                this.list = temp;
            }
        }
        else {
            synchronized (this.list) {
                this.list.add(index, element);
            }
        }
    }
    
    public boolean addAll(final Collection collection) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.addAll(collection);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.addAll(collection);
        }
    }
    
    public boolean addAll(final int index, final Collection collection) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.addAll(index, collection);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.addAll(index, collection);
        }
    }
    
    public void clear() {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                temp.clear();
                this.list = temp;
            }
        }
        else {
            synchronized (this.list) {
                this.list.clear();
            }
        }
    }
    
    public Object clone() {
        FastArrayList results = null;
        if (this.fast) {
            results = new FastArrayList(this.list);
        }
        else {
            synchronized (this.list) {
                results = new FastArrayList(this.list);
            }
        }
        results.setFast(this.getFast());
        return results;
    }
    
    public boolean contains(final Object element) {
        if (this.fast) {
            return this.list.contains(element);
        }
        synchronized (this.list) {
            return this.list.contains(element);
        }
    }
    
    public boolean containsAll(final Collection collection) {
        if (this.fast) {
            return this.list.containsAll(collection);
        }
        synchronized (this.list) {
            return this.list.containsAll(collection);
        }
    }
    
    public void ensureCapacity(final int capacity) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                temp.ensureCapacity(capacity);
                this.list = temp;
            }
        }
        else {
            synchronized (this.list) {
                this.list.ensureCapacity(capacity);
            }
        }
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof List)) {
            return false;
        }
        final List lo = (List)o;
        if (this.fast) {
            final ListIterator li1 = this.list.listIterator();
            final ListIterator li2 = lo.listIterator();
            while (li1.hasNext() && li2.hasNext()) {
                final Object o2 = li1.next();
                final Object o3 = li2.next();
                if (o2 == null) {
                    if (o3 == null) {
                        continue;
                    }
                    return false;
                }
                else {
                    if (!o2.equals(o3)) {
                        return false;
                    }
                    continue;
                }
            }
            return !li1.hasNext() && !li2.hasNext();
        }
        synchronized (this.list) {
            final ListIterator li3 = this.list.listIterator();
            final ListIterator li4 = lo.listIterator();
            while (li3.hasNext() && li4.hasNext()) {
                final Object o4 = li3.next();
                final Object o5 = li4.next();
                if (o4 == null) {
                    if (o5 == null) {
                        continue;
                    }
                    return false;
                }
                else {
                    if (!o4.equals(o5)) {
                        return false;
                    }
                    continue;
                }
            }
            return !li3.hasNext() && !li4.hasNext();
        }
    }
    
    public Object get(final int index) {
        if (this.fast) {
            return this.list.get(index);
        }
        synchronized (this.list) {
            return this.list.get(index);
        }
    }
    
    public int hashCode() {
        if (this.fast) {
            int hashCode = 1;
            final Iterator i = this.list.iterator();
            while (i.hasNext()) {
                final Object o = i.next();
                hashCode = 31 * hashCode + ((o == null) ? 0 : o.hashCode());
            }
            return hashCode;
        }
        synchronized (this.list) {
            int hashCode2 = 1;
            final Iterator j = this.list.iterator();
            while (j.hasNext()) {
                final Object o2 = j.next();
                hashCode2 = 31 * hashCode2 + ((o2 == null) ? 0 : o2.hashCode());
            }
            return hashCode2;
        }
    }
    
    public int indexOf(final Object element) {
        if (this.fast) {
            return this.list.indexOf(element);
        }
        synchronized (this.list) {
            return this.list.indexOf(element);
        }
    }
    
    public boolean isEmpty() {
        if (this.fast) {
            return this.list.isEmpty();
        }
        synchronized (this.list) {
            return this.list.isEmpty();
        }
    }
    
    public Iterator iterator() {
        if (this.fast) {
            return new ListIter(0);
        }
        return this.list.iterator();
    }
    
    public int lastIndexOf(final Object element) {
        if (this.fast) {
            return this.list.lastIndexOf(element);
        }
        synchronized (this.list) {
            return this.list.lastIndexOf(element);
        }
    }
    
    public ListIterator listIterator() {
        if (this.fast) {
            return new ListIter(0);
        }
        return this.list.listIterator();
    }
    
    public ListIterator listIterator(final int index) {
        if (this.fast) {
            return new ListIter(index);
        }
        return this.list.listIterator(index);
    }
    
    public Object remove(final int index) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final Object result = temp.remove(index);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.remove(index);
        }
    }
    
    public boolean remove(final Object element) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.remove(element);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.remove(element);
        }
    }
    
    public boolean removeAll(final Collection collection) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.removeAll(collection);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.removeAll(collection);
        }
    }
    
    public boolean retainAll(final Collection collection) {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                final boolean result = temp.retainAll(collection);
                this.list = temp;
                return result;
            }
        }
        synchronized (this.list) {
            return this.list.retainAll(collection);
        }
    }
    
    public Object set(final int index, final Object element) {
        if (this.fast) {
            return this.list.set(index, element);
        }
        synchronized (this.list) {
            return this.list.set(index, element);
        }
    }
    
    public int size() {
        if (this.fast) {
            return this.list.size();
        }
        synchronized (this.list) {
            return this.list.size();
        }
    }
    
    public List subList(final int fromIndex, final int toIndex) {
        if (this.fast) {
            return new SubList(fromIndex, toIndex);
        }
        return this.list.subList(fromIndex, toIndex);
    }
    
    public Object[] toArray() {
        if (this.fast) {
            return this.list.toArray();
        }
        synchronized (this.list) {
            return this.list.toArray();
        }
    }
    
    public Object[] toArray(final Object[] array) {
        if (this.fast) {
            return this.list.toArray(array);
        }
        synchronized (this.list) {
            return this.list.toArray(array);
        }
    }
    
    public String toString() {
        final StringBuffer sb = new StringBuffer("FastArrayList[");
        sb.append(this.list.toString());
        sb.append("]");
        return sb.toString();
    }
    
    public void trimToSize() {
        if (this.fast) {
            synchronized (this) {
                final ArrayList temp = (ArrayList)this.list.clone();
                temp.trimToSize();
                this.list = temp;
            }
        }
        else {
            synchronized (this.list) {
                this.list.trimToSize();
            }
        }
    }
    
    private class SubList implements List
    {
        private int first;
        private int last;
        private List expected;
        private final /* synthetic */ FastArrayList this$0;
        
        public SubList(final int first, final int last) {
            this.first = first;
            this.last = last;
            this.expected = FastArrayList.this.list;
        }
        
        private List get(final List l) {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
            return l.subList(this.first, this.last);
        }
        
        public void clear() {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    this.get(temp).clear();
                    this.last = this.first;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                }
            }
            else {
                synchronized (FastArrayList.this.list) {
                    this.get(this.expected).clear();
                }
            }
        }
        
        public boolean remove(final Object o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final boolean r = this.get(temp).remove(o);
                    if (r) {
                        --this.last;
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).remove(o);
            }
        }
        
        public boolean removeAll(final Collection o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final List sub = this.get(temp);
                    final boolean r = sub.removeAll(o);
                    if (r) {
                        this.last = this.first + sub.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).removeAll(o);
            }
        }
        
        public boolean retainAll(final Collection o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final List sub = this.get(temp);
                    final boolean r = sub.retainAll(o);
                    if (r) {
                        this.last = this.first + sub.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).retainAll(o);
            }
        }
        
        public int size() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).size();
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).size();
            }
        }
        
        public boolean isEmpty() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).isEmpty();
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).isEmpty();
            }
        }
        
        public boolean contains(final Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).contains(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).contains(o);
            }
        }
        
        public boolean containsAll(final Collection o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).containsAll(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).containsAll(o);
            }
        }
        
        public Object[] toArray(final Object[] o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).toArray(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).toArray(o);
            }
        }
        
        public Object[] toArray() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).toArray();
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).toArray();
            }
        }
        
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (FastArrayList.this.fast) {
                return this.get(this.expected).equals(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).equals(o);
            }
        }
        
        public int hashCode() {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).hashCode();
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).hashCode();
            }
        }
        
        public boolean add(final Object o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final boolean r = this.get(temp).add(o);
                    if (r) {
                        ++this.last;
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).add(o);
            }
        }
        
        public boolean addAll(final Collection o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final boolean r = this.get(temp).addAll(o);
                    if (r) {
                        this.last += o.size();
                    }
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).addAll(o);
            }
        }
        
        public void add(final int i, final Object o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    this.get(temp).add(i, o);
                    ++this.last;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                }
            }
            else {
                synchronized (FastArrayList.this.list) {
                    this.get(this.expected).add(i, o);
                }
            }
        }
        
        public boolean addAll(final int i, final Collection o) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final boolean r = this.get(temp).addAll(i, o);
                    FastArrayList.this.list = temp;
                    if (r) {
                        this.last += o.size();
                    }
                    this.expected = temp;
                    return r;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).addAll(i, o);
            }
        }
        
        public Object remove(final int i) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final Object o = this.get(temp).remove(i);
                    --this.last;
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return o;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).remove(i);
            }
        }
        
        public Object set(final int i, final Object a) {
            if (FastArrayList.this.fast) {
                synchronized (FastArrayList.this) {
                    final ArrayList temp = (ArrayList)FastArrayList.this.list.clone();
                    final Object o = this.get(temp).set(i, a);
                    FastArrayList.this.list = temp;
                    this.expected = temp;
                    return o;
                }
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).set(i, a);
            }
        }
        
        public Iterator iterator() {
            return new SubListIter(0);
        }
        
        public ListIterator listIterator() {
            return new SubListIter(0);
        }
        
        public ListIterator listIterator(final int i) {
            return new SubListIter(i);
        }
        
        public Object get(final int i) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).get(i);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).get(i);
            }
        }
        
        public int indexOf(final Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).indexOf(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).indexOf(o);
            }
        }
        
        public int lastIndexOf(final Object o) {
            if (FastArrayList.this.fast) {
                return this.get(this.expected).lastIndexOf(o);
            }
            synchronized (FastArrayList.this.list) {
                return this.get(this.expected).lastIndexOf(o);
            }
        }
        
        public List subList(final int f, final int l) {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
            return new SubList(this.first + f, f + l);
        }
        
        private class SubListIter implements ListIterator
        {
            private List expected;
            private ListIterator iter;
            private int lastReturnedIndex;
            
            public SubListIter(final int i) {
                this.lastReturnedIndex = -1;
                this.expected = SubList.this.this$0.list;
                this.iter = SubList.this.get(this.expected).listIterator(i);
            }
            
            private void checkMod() {
                if (FastArrayList.this.list != this.expected) {
                    throw new ConcurrentModificationException();
                }
            }
            
            List get() {
                return SubList.this.get(this.expected);
            }
            
            public boolean hasNext() {
                this.checkMod();
                return this.iter.hasNext();
            }
            
            public Object next() {
                this.checkMod();
                this.lastReturnedIndex = this.iter.nextIndex();
                return this.iter.next();
            }
            
            public boolean hasPrevious() {
                this.checkMod();
                return this.iter.hasPrevious();
            }
            
            public Object previous() {
                this.checkMod();
                this.lastReturnedIndex = this.iter.previousIndex();
                return this.iter.previous();
            }
            
            public int previousIndex() {
                this.checkMod();
                return this.iter.previousIndex();
            }
            
            public int nextIndex() {
                this.checkMod();
                return this.iter.nextIndex();
            }
            
            public void remove() {
                this.checkMod();
                if (this.lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                this.get().remove(this.lastReturnedIndex);
                SubList.this.last--;
                this.expected = FastArrayList.this.list;
                this.iter = this.get().listIterator(this.lastReturnedIndex);
                this.lastReturnedIndex = -1;
            }
            
            public void set(final Object o) {
                this.checkMod();
                if (this.lastReturnedIndex < 0) {
                    throw new IllegalStateException();
                }
                this.get().set(this.lastReturnedIndex, o);
                this.expected = FastArrayList.this.list;
                this.iter = this.get().listIterator(this.previousIndex() + 1);
            }
            
            public void add(final Object o) {
                this.checkMod();
                final int i = this.nextIndex();
                this.get().add(i, o);
                SubList.this.last++;
                this.expected = FastArrayList.this.list;
                this.iter = this.get().listIterator(i + 1);
                this.lastReturnedIndex = -1;
            }
        }
    }
    
    private class ListIter implements ListIterator
    {
        private List expected;
        private ListIterator iter;
        private int lastReturnedIndex;
        
        public ListIter(final int i) {
            this.lastReturnedIndex = -1;
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(i);
        }
        
        private void checkMod() {
            if (FastArrayList.this.list != this.expected) {
                throw new ConcurrentModificationException();
            }
        }
        
        List get() {
            return this.expected;
        }
        
        public boolean hasNext() {
            return this.iter.hasNext();
        }
        
        public Object next() {
            this.lastReturnedIndex = this.iter.nextIndex();
            return this.iter.next();
        }
        
        public boolean hasPrevious() {
            return this.iter.hasPrevious();
        }
        
        public Object previous() {
            this.lastReturnedIndex = this.iter.previousIndex();
            return this.iter.previous();
        }
        
        public int previousIndex() {
            return this.iter.previousIndex();
        }
        
        public int nextIndex() {
            return this.iter.nextIndex();
        }
        
        public void remove() {
            this.checkMod();
            if (this.lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            this.get().remove(this.lastReturnedIndex);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(this.lastReturnedIndex);
            this.lastReturnedIndex = -1;
        }
        
        public void set(final Object o) {
            this.checkMod();
            if (this.lastReturnedIndex < 0) {
                throw new IllegalStateException();
            }
            this.get().set(this.lastReturnedIndex, o);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(this.previousIndex() + 1);
        }
        
        public void add(final Object o) {
            this.checkMod();
            final int i = this.nextIndex();
            this.get().add(i, o);
            this.expected = FastArrayList.this.list;
            this.iter = this.get().listIterator(i + 1);
            this.lastReturnedIndex = -1;
        }
    }
}
