package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.CORBA.TCKind;
import org.omg.DynamicAny.NameValuePair;
import org.omg.DynamicAny.NameDynAnyPair;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynValueBox;

public class DynValueBoxImpl extends DynValueCommonImpl implements DynValueBox
{
    private DynValueBoxImpl() {
        this(null, null, false);
    }
    
    protected DynValueBoxImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
    }
    
    protected DynValueBoxImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
    }
    
    @Override
    public Any get_boxed_value() throws InvalidValue {
        if (this.isNull) {
            throw new InvalidValue();
        }
        this.checkInitAny();
        return this.any;
    }
    
    @Override
    public void set_boxed_value(final Any any) throws TypeMismatch {
        if (!this.isNull && !any.type().equal(this.type())) {
            throw new TypeMismatch();
        }
        this.clearData();
        this.any = any;
        this.representations = 2;
        this.index = 0;
        this.isNull = false;
    }
    
    @Override
    public DynAny get_boxed_value_as_dyn_any() throws InvalidValue {
        if (this.isNull) {
            throw new InvalidValue();
        }
        this.checkInitComponents();
        return this.components[0];
    }
    
    @Override
    public void set_boxed_value_as_dyn_any(final DynAny dynAny) throws TypeMismatch {
        if (!this.isNull && !dynAny.type().equal(this.type())) {
            throw new TypeMismatch();
        }
        this.clearData();
        this.components = new DynAny[] { dynAny };
        this.representations = 4;
        this.index = 0;
        this.isNull = false;
    }
    
    @Override
    protected boolean initializeComponentsFromAny() {
        try {
            this.components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false) };
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean initializeComponentsFromTypeCode() {
        try {
            this.any = DynAnyUtil.createDefaultAnyOfType(this.any.type(), this.orb);
            this.components = new DynAny[] { DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, false) };
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return false;
        }
        return true;
    }
    
    @Override
    protected boolean initializeAnyFromComponents() {
        this.any = this.getAny(this.components[0]);
        return true;
    }
}
