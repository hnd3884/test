package org.apache.commons.compress.harmony.pack200;

import java.util.Arrays;

public class IntList
{
    private int[] array;
    private int firstIndex;
    private int lastIndex;
    private int modCount;
    
    public IntList() {
        this(10);
    }
    
    public IntList(final int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException();
        }
        final int n = 0;
        this.lastIndex = n;
        this.firstIndex = n;
        this.array = new int[capacity];
    }
    
    public boolean add(final int object) {
        if (this.lastIndex == this.array.length) {
            this.growAtEnd(1);
        }
        this.array[this.lastIndex++] = object;
        ++this.modCount;
        return true;
    }
    
    public void add(final int location, final int object) {
        final int size = this.lastIndex - this.firstIndex;
        if (0 < location && location < size) {
            if (this.firstIndex == 0 && this.lastIndex == this.array.length) {
                this.growForInsert(location, 1);
            }
            else if ((location < size / 2 && this.firstIndex > 0) || this.lastIndex == this.array.length) {
                System.arraycopy(this.array, this.firstIndex, this.array, --this.firstIndex, location);
            }
            else {
                final int index = location + this.firstIndex;
                System.arraycopy(this.array, index, this.array, index + 1, size - location);
                ++this.lastIndex;
            }
            this.array[location + this.firstIndex] = object;
        }
        else if (location == 0) {
            if (this.firstIndex == 0) {
                this.growAtFront(1);
            }
            this.array[--this.firstIndex] = object;
        }
        else {
            if (location != size) {
                throw new IndexOutOfBoundsException();
            }
            if (this.lastIndex == this.array.length) {
                this.growAtEnd(1);
            }
            this.array[this.lastIndex++] = object;
        }
        ++this.modCount;
    }
    
    public void clear() {
        if (this.firstIndex != this.lastIndex) {
            Arrays.fill(this.array, this.firstIndex, this.lastIndex, -1);
            final int n = 0;
            this.lastIndex = n;
            this.firstIndex = n;
            ++this.modCount;
        }
    }
    
    public int get(final int location) {
        if (0 <= location && location < this.lastIndex - this.firstIndex) {
            return this.array[this.firstIndex + location];
        }
        throw new IndexOutOfBoundsException("" + location);
    }
    
    private void growAtEnd(final int required) {
        final int size = this.lastIndex - this.firstIndex;
        if (this.firstIndex >= required - (this.array.length - this.lastIndex)) {
            final int newLast = this.lastIndex - this.firstIndex;
            if (size > 0) {
                System.arraycopy(this.array, this.firstIndex, this.array, 0, size);
            }
            this.firstIndex = 0;
            this.lastIndex = newLast;
        }
        else {
            int increment = size / 2;
            if (required > increment) {
                increment = required;
            }
            if (increment < 12) {
                increment = 12;
            }
            final int[] newArray = new int[size + increment];
            if (size > 0) {
                System.arraycopy(this.array, this.firstIndex, newArray, 0, size);
                this.firstIndex = 0;
                this.lastIndex = size;
            }
            this.array = newArray;
        }
    }
    
    private void growAtFront(final int required) {
        final int size = this.lastIndex - this.firstIndex;
        if (this.array.length - this.lastIndex + this.firstIndex >= required) {
            final int newFirst = this.array.length - size;
            if (size > 0) {
                System.arraycopy(this.array, this.firstIndex, this.array, newFirst, size);
            }
            this.firstIndex = newFirst;
            this.lastIndex = this.array.length;
        }
        else {
            int increment = size / 2;
            if (required > increment) {
                increment = required;
            }
            if (increment < 12) {
                increment = 12;
            }
            final int[] newArray = new int[size + increment];
            if (size > 0) {
                System.arraycopy(this.array, this.firstIndex, newArray, newArray.length - size, size);
            }
            this.firstIndex = newArray.length - size;
            this.lastIndex = newArray.length;
            this.array = newArray;
        }
    }
    
    private void growForInsert(final int location, final int required) {
        final int size = this.lastIndex - this.firstIndex;
        int increment = size / 2;
        if (required > increment) {
            increment = required;
        }
        if (increment < 12) {
            increment = 12;
        }
        final int[] newArray = new int[size + increment];
        final int newFirst = increment - required;
        System.arraycopy(this.array, location + this.firstIndex, newArray, newFirst + location + required, size - location);
        System.arraycopy(this.array, this.firstIndex, newArray, newFirst, location);
        this.firstIndex = newFirst;
        this.lastIndex = size + increment;
        this.array = newArray;
    }
    
    public void increment(final int location) {
        if (0 > location || location >= this.lastIndex - this.firstIndex) {
            throw new IndexOutOfBoundsException("" + location);
        }
        final int[] array = this.array;
        final int n = this.firstIndex + location;
        ++array[n];
    }
    
    public boolean isEmpty() {
        return this.lastIndex == this.firstIndex;
    }
    
    public int remove(final int location) {
        final int size = this.lastIndex - this.firstIndex;
        if (0 > location || location >= size) {
            throw new IndexOutOfBoundsException();
        }
        int result;
        if (location == size - 1) {
            final int[] array = this.array;
            final int lastIndex = this.lastIndex - 1;
            this.lastIndex = lastIndex;
            result = array[lastIndex];
            this.array[this.lastIndex] = 0;
        }
        else if (location == 0) {
            result = this.array[this.firstIndex];
            this.array[this.firstIndex++] = 0;
        }
        else {
            final int elementIndex = this.firstIndex + location;
            result = this.array[elementIndex];
            if (location < size / 2) {
                System.arraycopy(this.array, this.firstIndex, this.array, this.firstIndex + 1, location);
                this.array[this.firstIndex++] = 0;
            }
            else {
                System.arraycopy(this.array, elementIndex + 1, this.array, elementIndex, size - location - 1);
                this.array[--this.lastIndex] = 0;
            }
        }
        if (this.firstIndex == this.lastIndex) {
            final int n = 0;
            this.lastIndex = n;
            this.firstIndex = n;
        }
        ++this.modCount;
        return result;
    }
    
    public int size() {
        return this.lastIndex - this.firstIndex;
    }
    
    public int[] toArray() {
        final int size = this.lastIndex - this.firstIndex;
        final int[] result = new int[size];
        System.arraycopy(this.array, this.firstIndex, result, 0, size);
        return result;
    }
    
    public void addAll(final IntList list) {
        this.growAtEnd(list.size());
        for (int i = 0; i < list.size(); ++i) {
            this.add(list.get(i));
        }
    }
}
