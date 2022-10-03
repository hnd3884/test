package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.NameValuePair;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynStruct;

public class DynStructImpl extends DynAnyComplexImpl implements DynStruct
{
    private DynStructImpl() {
        this(null, null, false);
    }
    
    protected DynStructImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynStructImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.index = 0;
    }
    
    @Override
    public NameValuePair[] get_members() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkInitComponents();
        return this.nameValuePairs;
    }
    
    @Override
    public NameDynAnyPair[] get_members_as_dyn_any() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkInitComponents();
        return this.nameDynAnyPairs;
    }
}
