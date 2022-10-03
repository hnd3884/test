package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Bounds;
import java.util.Vector;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ContextList;

public class ContextListImpl extends ContextList
{
    private final int INITIAL_CAPACITY = 2;
    private final int CAPACITY_INCREMENT = 2;
    private ORB _orb;
    private Vector _contexts;
    
    public ContextListImpl(final ORB orb) {
        this._orb = orb;
        this._contexts = new Vector(2, 2);
    }
    
    @Override
    public int count() {
        return this._contexts.size();
    }
    
    @Override
    public void add(final String s) {
        this._contexts.addElement(s);
    }
    
    @Override
    public String item(final int n) throws Bounds {
        try {
            return this._contexts.elementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
    
    @Override
    public void remove(final int n) throws Bounds {
        try {
            this._contexts.removeElementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
}
