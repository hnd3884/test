package com.sun.corba.se.impl.orbutil;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Hashtable;
import java.io.ObjectOutputStream;

class LegacyHookPutFields extends ObjectOutputStream.PutField
{
    private Hashtable fields;
    
    LegacyHookPutFields() {
        this.fields = new Hashtable();
    }
    
    @Override
    public void put(final String s, final boolean b) {
        this.fields.put(s, new Boolean(b));
    }
    
    @Override
    public void put(final String s, final char c) {
        this.fields.put(s, new Character(c));
    }
    
    @Override
    public void put(final String s, final byte b) {
        this.fields.put(s, new Byte(b));
    }
    
    @Override
    public void put(final String s, final short n) {
        this.fields.put(s, new Short(n));
    }
    
    @Override
    public void put(final String s, final int n) {
        this.fields.put(s, new Integer(n));
    }
    
    @Override
    public void put(final String s, final long n) {
        this.fields.put(s, new Long(n));
    }
    
    @Override
    public void put(final String s, final float n) {
        this.fields.put(s, new Float(n));
    }
    
    @Override
    public void put(final String s, final double n) {
        this.fields.put(s, new Double(n));
    }
    
    @Override
    public void put(final String s, final Object o) {
        this.fields.put(s, o);
    }
    
    @Override
    public void write(final ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this.fields);
    }
}
