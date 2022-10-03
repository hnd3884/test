package org.apache.lucene.search;

import java.util.Arrays;
import java.util.Iterator;

public final class DisiPriorityQueue implements Iterable<DisiWrapper>
{
    private final DisiWrapper[] heap;
    private int size;
    
    static int leftNode(final int node) {
        return (node + 1 << 1) - 1;
    }
    
    static int rightNode(final int leftNode) {
        return leftNode + 1;
    }
    
    static int parentNode(final int node) {
        return (node + 1 >>> 1) - 1;
    }
    
    public DisiPriorityQueue(final int maxSize) {
        this.heap = new DisiWrapper[maxSize];
        this.size = 0;
    }
    
    public int size() {
        return this.size;
    }
    
    public DisiWrapper top() {
        return this.heap[0];
    }
    
    public DisiWrapper topList() {
        final DisiWrapper[] heap = this.heap;
        final int size = this.size;
        DisiWrapper list = heap[0];
        list.next = null;
        if (size >= 3) {
            list = this.topList(list, heap, size, 1);
            list = this.topList(list, heap, size, 2);
        }
        else if (size == 2 && heap[1].doc == list.doc) {
            list = this.prepend(heap[1], list);
        }
        return list;
    }
    
    private DisiWrapper prepend(final DisiWrapper w1, final DisiWrapper w2) {
        w1.next = w2;
        return w1;
    }
    
    private DisiWrapper topList(DisiWrapper list, final DisiWrapper[] heap, final int size, final int i) {
        final DisiWrapper w = heap[i];
        if (w.doc == list.doc) {
            list = this.prepend(w, list);
            final int left = leftNode(i);
            final int right = left + 1;
            if (right < size) {
                list = this.topList(list, heap, size, left);
                list = this.topList(list, heap, size, right);
            }
            else if (left < size && heap[left].doc == list.doc) {
                list = this.prepend(heap[left], list);
            }
        }
        return list;
    }
    
    public DisiWrapper add(final DisiWrapper entry) {
        final DisiWrapper[] heap = this.heap;
        final int size = this.size;
        heap[size] = entry;
        this.upHeap(size);
        this.size = size + 1;
        return heap[0];
    }
    
    public DisiWrapper pop() {
        final DisiWrapper[] heap = this.heap;
        final DisiWrapper result = heap[0];
        final int size = this.size - 1;
        this.size = size;
        final int i = size;
        heap[0] = heap[i];
        heap[i] = null;
        this.downHeap(i);
        return result;
    }
    
    public DisiWrapper updateTop() {
        this.downHeap(this.size);
        return this.heap[0];
    }
    
    DisiWrapper updateTop(final DisiWrapper topReplacement) {
        this.heap[0] = topReplacement;
        return this.updateTop();
    }
    
    void upHeap(int i) {
        final DisiWrapper node = this.heap[i];
        for (int nodeDoc = node.doc, j = parentNode(i); j >= 0 && nodeDoc < this.heap[j].doc; j = parentNode(j)) {
            this.heap[i] = this.heap[j];
            i = j;
        }
        this.heap[i] = node;
    }
    
    void downHeap(final int size) {
        int i = 0;
        final DisiWrapper node = this.heap[0];
        int j = leftNode(i);
        if (j < size) {
            int k = rightNode(j);
            if (k < size && this.heap[k].doc < this.heap[j].doc) {
                j = k;
            }
            if (this.heap[j].doc < node.doc) {
                do {
                    this.heap[i] = this.heap[j];
                    i = j;
                    j = leftNode(i);
                    k = rightNode(j);
                    if (k < size && this.heap[k].doc < this.heap[j].doc) {
                        j = k;
                    }
                } while (j < size && this.heap[j].doc < node.doc);
                this.heap[i] = node;
            }
        }
    }
    
    @Override
    public Iterator<DisiWrapper> iterator() {
        return Arrays.asList(this.heap).subList(0, this.size).iterator();
    }
}
