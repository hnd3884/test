package org.apache.poi.util;

import java.util.Iterator;

@Internal
public class StringCodepointsIterable implements Iterable<String>
{
    private final String string;
    
    public StringCodepointsIterable(final String string) {
        this.string = string;
    }
    
    @Override
    public Iterator<String> iterator() {
        return new StringCodepointsIterator();
    }
    
    private class StringCodepointsIterator implements Iterator<String>
    {
        private int index;
        
        private StringCodepointsIterator() {
            this.index = 0;
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean hasNext() {
            return this.index < StringCodepointsIterable.this.string.length();
        }
        
        @Override
        public String next() {
            final int codePoint = StringCodepointsIterable.this.string.codePointAt(this.index);
            this.index += Character.charCount(codePoint);
            return new String(Character.toChars(codePoint));
        }
    }
}
