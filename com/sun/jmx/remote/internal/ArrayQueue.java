package com.sun.jmx.remote.internal;

import java.util.AbstractList;

public class ArrayQueue<T> extends AbstractList<T>
{
    private int capacity;
    private T[] queue;
    private int head;
    private int tail;
    
    public ArrayQueue(final int n) {
        this.capacity = n + 1;
        this.queue = this.newArray(n + 1);
        this.head = 0;
        this.tail = 0;
    }
    
    public void resize(int capacity) {
        final int size = this.size();
        if (capacity < size) {
            throw new IndexOutOfBoundsException("Resizing would lose data");
        }
        if (++capacity == this.capacity) {
            return;
        }
        final T[] array = this.newArray(capacity);
        for (int i = 0; i < size; ++i) {
            array[i] = this.get(i);
        }
        this.capacity = capacity;
        this.queue = array;
        this.head = 0;
        this.tail = size;
    }
    
    private T[] newArray(final int n) {
        return (T[])new Object[n];
    }
    
    @Override
    public boolean add(final T t) {
        this.queue[this.tail] = t;
        final int tail = (this.tail + 1) % this.capacity;
        if (tail == this.head) {
            throw new IndexOutOfBoundsException("Queue full");
        }
        this.tail = tail;
        return true;
    }
    
    @Override
    public T remove(final int n) {
        if (n != 0) {
            throw new IllegalArgumentException("Can only remove head of queue");
        }
        if (this.head == this.tail) {
            throw new IndexOutOfBoundsException("Queue empty");
        }
        final T t = this.queue[this.head];
        this.queue[this.head] = null;
        this.head = (this.head + 1) % this.capacity;
        return t;
    }
    
    @Override
    public T get(final int n) {
        final int size = this.size();
        if (n < 0 || n >= size) {
            throw new IndexOutOfBoundsException("Index " + n + ", queue size " + size);
        }
        return this.queue[(this.head + n) % this.capacity];
    }
    
    @Override
    public int size() {
        int n = this.tail - this.head;
        if (n < 0) {
            n += this.capacity;
        }
        return n;
    }
}
