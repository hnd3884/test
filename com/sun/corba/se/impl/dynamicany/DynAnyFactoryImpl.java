package com.sun.corba.se.impl.dynamicany;

import org.omg.CORBA.TypeCode;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.Any;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.CORBA.LocalObject;

public class DynAnyFactoryImpl extends LocalObject implements DynAnyFactory
{
    private ORB orb;
    private String[] __ids;
    
    private DynAnyFactoryImpl() {
        this.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
        this.orb = null;
    }
    
    public DynAnyFactoryImpl(final ORB orb) {
        this.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAnyFactory:1.0" };
        this.orb = orb;
    }
    
    @Override
    public DynAny create_dyn_any(final Any any) throws InconsistentTypeCode {
        return DynAnyUtil.createMostDerivedDynAny(any, this.orb, true);
    }
    
    @Override
    public DynAny create_dyn_any_from_type_code(final TypeCode typeCode) throws InconsistentTypeCode {
        return DynAnyUtil.createMostDerivedDynAny(typeCode, this.orb);
    }
    
    public String[] _ids() {
        return this.__ids.clone();
    }
}
