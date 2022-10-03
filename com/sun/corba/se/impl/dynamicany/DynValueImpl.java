package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.NameValuePair;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynValue;

public class DynValueImpl extends DynValueCommonImpl implements DynValue
{
    private DynValueImpl() {
        this(null, null, false);
    }
    
    protected DynValueImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynValueImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
    }
}
