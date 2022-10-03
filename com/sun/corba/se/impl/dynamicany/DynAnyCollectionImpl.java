package com.sun.corba.se.impl.dynamicany;

import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;

abstract class DynAnyCollectionImpl extends DynAnyConstructedImpl
{
    Any[] anys;
    
    private DynAnyCollectionImpl() {
        this(null, null, false);
    }
    
    protected DynAnyCollectionImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.anys = null;
    }
    
    protected DynAnyCollectionImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.anys = null;
    }
    
    protected void createDefaultComponentAt(final int n, final TypeCode typeCode) {
        try {
            this.components[n] = DynAnyUtil.createMostDerivedDynAny(typeCode, this.orb);
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {}
        this.anys[n] = this.getAny(this.components[n]);
    }
    
    protected TypeCode getContentType() {
        try {
            return this.any.type().content_type();
        }
        catch (final BadKind badKind) {
            return null;
        }
    }
    
    protected int getBound() {
        try {
            return this.any.type().length();
        }
        catch (final BadKind badKind) {
            return 0;
        }
    }
    
    public Any[] get_elements() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return (Any[])(this.checkInitComponents() ? this.anys : null);
    }
    
    protected abstract void checkValue(final Object[] p0) throws InvalidValue;
    
    public void set_elements(final Any[] anys) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkValue(anys);
        this.components = new DynAny[anys.length];
        this.anys = anys;
        final TypeCode contentType = this.getContentType();
        int i = 0;
        while (i < anys.length) {
            Label_0109: {
                if (anys[i] != null) {
                    if (!anys[i].type().equal(contentType)) {
                        this.clearData();
                        throw new TypeMismatch();
                    }
                    Label_0121: {
                        try {
                            this.components[i] = DynAnyUtil.createMostDerivedDynAny(anys[i], this.orb, false);
                            break Label_0121;
                        }
                        catch (final InconsistentTypeCode inconsistentTypeCode) {
                            throw new InvalidValue();
                        }
                        break Label_0109;
                    }
                    ++i;
                    continue;
                }
            }
            this.clearData();
            throw new InvalidValue();
        }
        this.index = ((anys.length == 0) ? -1 : 0);
        this.representations = 4;
    }
    
    public DynAny[] get_elements_as_dyn_any() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return (DynAny[])(this.checkInitComponents() ? this.components : null);
    }
    
    public void set_elements_as_dyn_any(final DynAny[] array) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        this.checkValue(array);
        this.components = ((array == null) ? DynAnyCollectionImpl.emptyComponents : array);
        this.anys = new Any[array.length];
        final TypeCode contentType = this.getContentType();
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == null) {
                this.clearData();
                throw new InvalidValue();
            }
            if (!array[i].type().equal(contentType)) {
                this.clearData();
                throw new TypeMismatch();
            }
            this.anys[i] = this.getAny(array[i]);
        }
        this.index = ((array.length == 0) ? -1 : 0);
        this.representations = 4;
    }
}
