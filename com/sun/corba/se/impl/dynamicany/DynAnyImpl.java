package com.sun.corba.se.impl.dynamicany;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.DynamicAny.DynAnyFactory;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Any;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.LocalObject;

abstract class DynAnyImpl extends LocalObject implements DynAny
{
    protected static final int NO_INDEX = -1;
    protected static final byte STATUS_DESTROYABLE = 0;
    protected static final byte STATUS_UNDESTROYABLE = 1;
    protected static final byte STATUS_DESTROYED = 2;
    protected ORB orb;
    protected ORBUtilSystemException wrapper;
    protected Any any;
    protected byte status;
    protected int index;
    private String[] __ids;
    
    protected DynAnyImpl() {
        this.orb = null;
        this.any = null;
        this.status = 0;
        this.index = -1;
        this.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAny:1.0" };
        this.wrapper = ORBUtilSystemException.get("rpc.presentation");
    }
    
    protected DynAnyImpl(final ORB orb, final Any any, final boolean b) {
        this.orb = null;
        this.any = null;
        this.status = 0;
        this.index = -1;
        this.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAny:1.0" };
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.presentation");
        if (b) {
            this.any = DynAnyUtil.copy(any, orb);
        }
        else {
            this.any = any;
        }
        this.index = -1;
    }
    
    protected DynAnyImpl(final ORB orb, final TypeCode typeCode) {
        this.orb = null;
        this.any = null;
        this.status = 0;
        this.index = -1;
        this.__ids = new String[] { "IDL:omg.org/DynamicAny/DynAny:1.0" };
        this.orb = orb;
        this.wrapper = ORBUtilSystemException.get(orb, "rpc.presentation");
        this.any = DynAnyUtil.createDefaultAnyOfType(typeCode, orb);
    }
    
    protected DynAnyFactory factory() {
        try {
            return (DynAnyFactory)this.orb.resolve_initial_references("DynAnyFactory");
        }
        catch (final InvalidName invalidName) {
            throw new RuntimeException("Unable to find DynAnyFactory");
        }
    }
    
    protected Any getAny() {
        return this.any;
    }
    
    protected Any getAny(final DynAny dynAny) {
        if (dynAny instanceof DynAnyImpl) {
            return ((DynAnyImpl)dynAny).getAny();
        }
        return dynAny.to_any();
    }
    
    protected void writeAny(final OutputStream outputStream) {
        this.any.write_value(outputStream);
    }
    
    protected void setStatus(final byte status) {
        this.status = status;
    }
    
    protected void clearData() {
        this.any.type(this.any.type());
    }
    
    @Override
    public TypeCode type() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.any.type();
    }
    
    @Override
    public void assign(final DynAny dynAny) throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any != null && !this.any.type().equal(dynAny.type())) {
            throw new TypeMismatch();
        }
        this.any = dynAny.to_any();
    }
    
    @Override
    public void from_any(final Any any) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any != null && !this.any.type().equal(any.type())) {
            throw new TypeMismatch();
        }
        Any copy;
        try {
            copy = DynAnyUtil.copy(any, this.orb);
        }
        catch (final Exception ex) {
            throw new InvalidValue();
        }
        if (!DynAnyUtil.isInitialized(copy)) {
            throw new InvalidValue();
        }
        this.any = copy;
    }
    
    @Override
    public abstract Any to_any();
    
    @Override
    public abstract boolean equal(final DynAny p0);
    
    @Override
    public abstract void destroy();
    
    @Override
    public abstract DynAny copy();
    
    public String[] _ids() {
        return this.__ids.clone();
    }
}
