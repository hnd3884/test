package org.bouncycastle.asn1.eac;

import java.util.Enumeration;
import java.util.Hashtable;

public class Flags
{
    int value;
    
    public Flags() {
        this.value = 0;
    }
    
    public Flags(final int value) {
        this.value = 0;
        this.value = value;
    }
    
    public void set(final int n) {
        this.value |= n;
    }
    
    public boolean isSet(final int n) {
        return (this.value & n) != 0x0;
    }
    
    public int getFlags() {
        return this.value;
    }
    
    String decode(final Hashtable hashtable) {
        final StringJoiner stringJoiner = new StringJoiner(" ");
        final Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            final Integer n = (Integer)keys.nextElement();
            if (this.isSet(n)) {
                stringJoiner.add((String)hashtable.get(n));
            }
        }
        return stringJoiner.toString();
    }
    
    private class StringJoiner
    {
        String mSeparator;
        boolean First;
        StringBuffer b;
        
        public StringJoiner(final String mSeparator) {
            this.First = true;
            this.b = new StringBuffer();
            this.mSeparator = mSeparator;
        }
        
        public void add(final String s) {
            if (this.First) {
                this.First = false;
            }
            else {
                this.b.append(this.mSeparator);
            }
            this.b.append(s);
        }
        
        @Override
        public String toString() {
            return this.b.toString();
        }
    }
}
