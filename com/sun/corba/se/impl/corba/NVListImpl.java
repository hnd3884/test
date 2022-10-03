package com.sun.corba.se.impl.corba;

import org.omg.CORBA.Bounds;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;
import com.sun.corba.se.spi.orb.ORB;
import java.util.Vector;
import org.omg.CORBA.NVList;

public class NVListImpl extends NVList
{
    private final int INITIAL_CAPACITY = 4;
    private final int CAPACITY_INCREMENT = 2;
    private Vector _namedValues;
    private ORB orb;
    
    public NVListImpl(final ORB orb) {
        this.orb = orb;
        this._namedValues = new Vector(4, 2);
    }
    
    public NVListImpl(final ORB orb, final int n) {
        this.orb = orb;
        this._namedValues = new Vector(n);
    }
    
    @Override
    public int count() {
        return this._namedValues.size();
    }
    
    @Override
    public NamedValue add(final int n) {
        final NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, "", new AnyImpl(this.orb), n);
        this._namedValues.addElement(namedValueImpl);
        return namedValueImpl;
    }
    
    @Override
    public NamedValue add_item(final String s, final int n) {
        final NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, s, new AnyImpl(this.orb), n);
        this._namedValues.addElement(namedValueImpl);
        return namedValueImpl;
    }
    
    @Override
    public NamedValue add_value(final String s, final Any any, final int n) {
        final NamedValueImpl namedValueImpl = new NamedValueImpl(this.orb, s, any, n);
        this._namedValues.addElement(namedValueImpl);
        return namedValueImpl;
    }
    
    @Override
    public NamedValue item(final int n) throws Bounds {
        try {
            return this._namedValues.elementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
    
    @Override
    public void remove(final int n) throws Bounds {
        try {
            this._namedValues.removeElementAt(n);
        }
        catch (final ArrayIndexOutOfBoundsException ex) {
            throw new Bounds();
        }
    }
}
