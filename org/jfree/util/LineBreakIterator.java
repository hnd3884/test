package org.jfree.util;

import java.util.Iterator;

public class LineBreakIterator implements Iterator
{
    public static final int DONE = -1;
    private char[] text;
    private int position;
    
    public LineBreakIterator() {
        this.setText("");
    }
    
    public LineBreakIterator(final String text) {
        this.setText(text);
    }
    
    public String getText() {
        return new String(this.text);
    }
    
    public boolean hasNext() {
        return this.position != -1;
    }
    
    public Object next() {
        if (this.position == -1) {
            return null;
        }
        final int lastFound = this.position;
        int pos = this.nextWithEnd();
        if (pos == -1) {
            return new String(this.text, lastFound, this.text.length - lastFound);
        }
        if (pos > 0) {
            for (int end = lastFound; pos > end && (this.text[pos - 1] == '\n' || this.text[pos - 1] == '\r'); --pos) {}
        }
        return new String(this.text, lastFound, pos - lastFound);
    }
    
    public synchronized int nextPosition() {
        if (this.text == null) {
            return -1;
        }
        if (this.position == -1) {
            return -1;
        }
        final int nChars = this.text.length;
        int nextChar = this.position;
        while (nextChar < nChars) {
            boolean eol = false;
            char c = '\0';
            int i;
            for (i = nextChar; i < nChars; ++i) {
                c = this.text[i];
                if (c == '\n' || c == '\r') {
                    eol = true;
                    break;
                }
            }
            nextChar = i;
            if (eol) {
                ++nextChar;
                if (c == '\r' && nextChar < nChars && this.text[nextChar] == '\n') {
                    ++nextChar;
                }
                return this.position = nextChar;
            }
        }
        return this.position = -1;
    }
    
    public int nextWithEnd() {
        final int pos = this.position;
        if (pos == -1) {
            return -1;
        }
        if (pos == this.text.length) {
            return this.position = -1;
        }
        final int retval = this.nextPosition();
        if (retval == -1) {
            return this.text.length;
        }
        return retval;
    }
    
    public void remove() {
        throw new UnsupportedOperationException("This iterator is read-only.");
    }
    
    public void setText(final String text) {
        this.position = 0;
        this.text = text.toCharArray();
    }
}
