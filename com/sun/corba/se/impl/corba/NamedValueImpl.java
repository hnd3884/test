package com.sun.corba.se.impl.corba;

import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.NamedValue;

public class NamedValueImpl extends NamedValue
{
    private String _name;
    private Any _value;
    private int _flags;
    private ORB _orb;
    
    public NamedValueImpl(final ORB orb) {
        this._orb = orb;
        this._value = new AnyImpl(this._orb);
    }
    
    public NamedValueImpl(final ORB orb, final String name, final Any value, final int flags) {
        this._orb = orb;
        this._name = name;
        this._value = value;
        this._flags = flags;
    }
    
    @Override
    public String name() {
        return this._name;
    }
    
    @Override
    public Any value() {
        return this._value;
    }
    
    @Override
    public int flags() {
        return this._flags;
    }
}
