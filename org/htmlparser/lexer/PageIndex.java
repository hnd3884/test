package org.htmlparser.lexer;

import org.htmlparser.util.sort.Ordered;
import org.htmlparser.util.sort.Sort;
import org.htmlparser.util.sort.Sortable;
import java.io.Serializable;

public class PageIndex implements Serializable, Sortable
{
    protected static final int mStartIncrement = 100;
    protected int mIncrement;
    protected int mCount;
    protected int[] mIndices;
    protected Page mPage;
    
    public PageIndex(final Page page) {
        this.mPage = page;
        this.mIndices = new int[this.mIncrement];
        this.mCount = 0;
        this.mIncrement = 200;
    }
    
    public PageIndex(final Page page, final int cursor) {
        this(page);
        this.mIndices[0] = cursor;
        this.mCount = 1;
    }
    
    public PageIndex(final Page page, final int[] cursors) {
        this.mPage = page;
        this.mIndices = cursors;
        this.mCount = cursors.length;
    }
    
    public Page getPage() {
        return this.mPage;
    }
    
    public int size() {
        return this.mCount;
    }
    
    public int capacity() {
        return this.mIndices.length;
    }
    
    public int add(final Cursor cursor) {
        final int position = cursor.getPosition();
        int ret;
        if (0 == this.mCount) {
            ret = 0;
            this.insertElementAt(position, ret);
        }
        else {
            final int last = this.mIndices[this.mCount - 1];
            if (position == last) {
                ret = this.mCount - 1;
            }
            else if (position > last) {
                ret = this.mCount;
                this.insertElementAt(position, ret);
            }
            else {
                ret = Sort.bsearch(this, cursor);
                if (ret >= this.size() || position != this.mIndices[ret]) {
                    this.insertElementAt(position, ret);
                }
            }
        }
        return ret;
    }
    
    public int add(final int cursor) {
        return this.add(new Cursor(this.getPage(), cursor));
    }
    
    public void remove(final Cursor cursor) {
        final int i = Sort.bsearch(this, cursor);
        if (i < this.size() && cursor.getPosition() == this.mIndices[i]) {
            this.removeElementAt(i);
        }
    }
    
    public void remove(final int cursor) {
        this.remove(new Cursor(this.getPage(), cursor));
    }
    
    public int elementAt(final int index) {
        if (index >= this.mCount) {
            throw new IndexOutOfBoundsException("index " + index + " beyond current limit");
        }
        return this.mIndices[index];
    }
    
    public int row(final Cursor cursor) {
        int ret = Sort.bsearch(this, cursor);
        if (ret < this.mCount && cursor.getPosition() == this.mIndices[ret]) {
            ++ret;
        }
        return ret;
    }
    
    public int row(final int cursor) {
        return this.row(new Cursor(this.getPage(), cursor));
    }
    
    public int column(final Cursor cursor) {
        final int row = this.row(cursor);
        int previous;
        if (0 != row) {
            previous = this.elementAt(row - 1);
        }
        else {
            previous = 0;
        }
        return cursor.getPosition() - previous;
    }
    
    public int column(final int cursor) {
        return this.column(new Cursor(this.getPage(), cursor));
    }
    
    public int[] get() {
        final int[] ret = new int[this.size()];
        System.arraycopy(this.mIndices, 0, ret, 0, this.size());
        return ret;
    }
    
    protected int bsearch(final int cursor) {
        return Sort.bsearch(this, new Cursor(this.getPage(), cursor));
    }
    
    protected int bsearch(final int cursor, final int first, final int last) {
        return Sort.bsearch(this, new Cursor(this.getPage(), cursor), first, last);
    }
    
    protected void insertElementAt(final int cursor, final int index) {
        if (index >= this.capacity() || this.size() == this.capacity()) {
            final int[] new_values = new int[Math.max(this.capacity() + this.mIncrement, index + 1)];
            this.mIncrement *= 2;
            if (index < this.capacity()) {
                System.arraycopy(this.mIndices, 0, new_values, 0, index);
                System.arraycopy(this.mIndices, index, new_values, index + 1, this.capacity() - index);
            }
            else {
                System.arraycopy(this.mIndices, 0, new_values, 0, this.capacity());
            }
            this.mIndices = new_values;
        }
        else if (index < this.size()) {
            System.arraycopy(this.mIndices, index, this.mIndices, index + 1, this.capacity() - (index + 1));
        }
        this.mIndices[index] = cursor;
        ++this.mCount;
    }
    
    protected void removeElementAt(final int index) {
        System.arraycopy(this.mIndices, index + 1, this.mIndices, index, this.capacity() - (index + 1));
        this.mIndices[this.capacity() - 1] = 0;
        --this.mCount;
    }
    
    public int first() {
        return 0;
    }
    
    public int last() {
        return this.mCount - 1;
    }
    
    public Ordered fetch(final int index, final Ordered reuse) {
        Cursor ret;
        if (null != reuse) {
            ret = (Cursor)reuse;
            ret.mPosition = this.mIndices[index];
            ret.mPage = this.getPage();
        }
        else {
            ret = new Cursor(this.getPage(), this.mIndices[index]);
        }
        return ret;
    }
    
    public void swap(final int i, final int j) {
        final int temp = this.mIndices[i];
        this.mIndices[i] = this.mIndices[j];
        this.mIndices[j] = temp;
    }
}
