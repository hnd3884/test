package com.sun.corba.se.impl.dynamicany;

import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.NameValuePair;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynValueCommon;

abstract class DynValueCommonImpl extends DynAnyComplexImpl implements DynValueCommon
{
    protected boolean isNull;
    
    private DynValueCommonImpl() {
        this(null, null, false);
        this.isNull = true;
    }
    
    protected DynValueCommonImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.isNull = this.checkInitComponents();
    }
    
    protected DynValueCommonImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.isNull = true;
    }
    
    @Override
    public boolean is_null() {
        return this.isNull;
    }
    
    @Override
    public void set_to_null() {
        this.isNull = true;
        this.clearData();
    }
    
    @Override
    public void set_to_value() {
        if (this.isNull) {
            this.isNull = false;
        }
    }
    
    public NameValuePair[] get_members() throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.isNull) {
            throw new InvalidValue();
        }
        this.checkInitComponents();
        return this.nameValuePairs;
    }
    
    public NameDynAnyPair[] get_members_as_dyn_any() throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.isNull) {
            throw new InvalidValue();
        }
        this.checkInitComponents();
        return this.nameDynAnyPairs;
    }
    
    @Override
    public void set_members(final NameValuePair[] array) throws TypeMismatch, InvalidValue {
        super.set_members(array);
        this.isNull = false;
    }
    
    @Override
    public void set_members_as_dyn_any(final NameDynAnyPair[] array) throws TypeMismatch, InvalidValue {
        super.set_members_as_dyn_any(array);
        this.isNull = false;
    }
}
