package com.sun.corba.se.impl.dynamicany;

import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.BAD_OPERATION;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynEnum;

public class DynEnumImpl extends DynAnyBasicImpl implements DynEnum
{
    int currentEnumeratorIndex;
    
    private DynEnumImpl() {
        this(null, null, false);
    }
    
    protected DynEnumImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.currentEnumeratorIndex = -1;
        this.index = -1;
        try {
            this.currentEnumeratorIndex = this.any.extract_long();
        }
        catch (final BAD_OPERATION bad_OPERATION) {
            this.currentEnumeratorIndex = 0;
            this.any.type(this.any.type());
            this.any.insert_long(0);
        }
    }
    
    protected DynEnumImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.currentEnumeratorIndex = -1;
        this.index = -1;
        this.currentEnumeratorIndex = 0;
        this.any.insert_long(0);
    }
    
    private int memberCount() {
        int member_count = 0;
        try {
            member_count = this.any.type().member_count();
        }
        catch (final BadKind badKind) {}
        return member_count;
    }
    
    private String memberName(final int n) {
        String member_name = null;
        try {
            member_name = this.any.type().member_name(n);
        }
        catch (final BadKind badKind) {}
        catch (final Bounds bounds) {}
        return member_name;
    }
    
    private int computeCurrentEnumeratorIndex(final String s) {
        for (int memberCount = this.memberCount(), i = 0; i < memberCount; ++i) {
            if (this.memberName(i).equals(s)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public int component_count() {
        return 0;
    }
    
    @Override
    public DynAny current_component() throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        throw new TypeMismatch();
    }
    
    @Override
    public String get_as_string() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.memberName(this.currentEnumeratorIndex);
    }
    
    @Override
    public void set_as_string(final String s) throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        final int computeCurrentEnumeratorIndex = this.computeCurrentEnumeratorIndex(s);
        if (computeCurrentEnumeratorIndex == -1) {
            throw new InvalidValue();
        }
        this.currentEnumeratorIndex = computeCurrentEnumeratorIndex;
        this.any.insert_long(computeCurrentEnumeratorIndex);
    }
    
    @Override
    public int get_as_ulong() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return this.currentEnumeratorIndex;
    }
    
    @Override
    public void set_as_ulong(final int currentEnumeratorIndex) throws InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (currentEnumeratorIndex < 0 || currentEnumeratorIndex >= this.memberCount()) {
            throw new InvalidValue();
        }
        this.currentEnumeratorIndex = currentEnumeratorIndex;
        this.any.insert_long(currentEnumeratorIndex);
    }
}
