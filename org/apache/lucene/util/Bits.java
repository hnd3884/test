package org.apache.lucene.util;

public interface Bits
{
    public static final Bits[] EMPTY_ARRAY = new Bits[0];
    
    boolean get(final int p0);
    
    int length();
    
    public static class MatchAllBits implements Bits
    {
        final int len;
        
        public MatchAllBits(final int len) {
            this.len = len;
        }
        
        @Override
        public boolean get(final int index) {
            return true;
        }
        
        @Override
        public int length() {
            return this.len;
        }
    }
    
    public static class MatchNoBits implements Bits
    {
        final int len;
        
        public MatchNoBits(final int len) {
            this.len = len;
        }
        
        @Override
        public boolean get(final int index) {
            return false;
        }
        
        @Override
        public int length() {
            return this.len;
        }
    }
}
