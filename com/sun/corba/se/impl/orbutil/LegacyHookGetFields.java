package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectStreamClass;
import java.util.Hashtable;
import java.io.ObjectInputStream;

class LegacyHookGetFields extends ObjectInputStream.GetField
{
    private Hashtable fields;
    
    LegacyHookGetFields(final Hashtable fields) {
        this.fields = null;
        this.fields = fields;
    }
    
    @Override
    public ObjectStreamClass getObjectStreamClass() {
        return null;
    }
    
    @Override
    public boolean defaulted(final String s) throws IOException, IllegalArgumentException {
        return !this.fields.containsKey(s);
    }
    
    @Override
    public boolean get(final String s, final boolean b) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return b;
        }
        return this.fields.get(s);
    }
    
    @Override
    public char get(final String s, final char c) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return c;
        }
        return this.fields.get(s);
    }
    
    @Override
    public byte get(final String s, final byte b) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return b;
        }
        return this.fields.get(s);
    }
    
    @Override
    public short get(final String s, final short n) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return n;
        }
        return this.fields.get(s);
    }
    
    @Override
    public int get(final String s, final int n) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return n;
        }
        return this.fields.get(s);
    }
    
    @Override
    public long get(final String s, final long n) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return n;
        }
        return this.fields.get(s);
    }
    
    @Override
    public float get(final String s, final float n) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return n;
        }
        return this.fields.get(s);
    }
    
    @Override
    public double get(final String s, final double n) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return n;
        }
        return this.fields.get(s);
    }
    
    @Override
    public Object get(final String s, final Object o) throws IOException, IllegalArgumentException {
        if (this.defaulted(s)) {
            return o;
        }
        return this.fields.get(s);
    }
    
    @Override
    public String toString() {
        return this.fields.toString();
    }
}
