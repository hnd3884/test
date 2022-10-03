package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Bounds;
import org.omg.CORBA.TypeCode;
import java.util.Vector;
import org.omg.CORBA.ExceptionList;

public class ExceptionListImpl extends ExceptionList
{
    private final int INITIAL_CAPACITY = 2;
    private final int CAPACITY_INCREMENT = 2;
    private Vector _exceptions;
    
    public ExceptionListImpl() {
        this._exceptions = new Vector(2, 2);
    }
    
    @Override
    public int count() {
        return this._exceptions.size();
    }
    
    @Override
    public void add(final TypeCode typeCode) {
        this._exceptions.addElement(typeCode);
    }
    
    @Override
    public TypeCode item(final int n) throws Bounds {
        try {
            return this._exceptions.elementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
    
    @Override
    public void remove(final int n) throws Bounds {
        try {
            this._exceptions.removeElementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
}
