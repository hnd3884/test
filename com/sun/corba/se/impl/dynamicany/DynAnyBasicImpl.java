package com.sun.corba.se.impl.dynamicany;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.DynamicAny.DynAny;
import org.omg.CORBA.TypeCode;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;

public class DynAnyBasicImpl extends DynAnyImpl
{
    private DynAnyBasicImpl() {
        this(null, null, false);
    }
    
    protected DynAnyBasicImpl(final ORB orb, final Any any, final boolean b) {
        super(orb, any, b);
        this.index = -1;
    }
    
    protected DynAnyBasicImpl(final ORB orb, final TypeCode typeCode) {
        super(orb, typeCode);
        this.index = -1;
    }
    
    @Override
    public void assign(final DynAny dynAny) throws TypeMismatch {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        super.assign(dynAny);
        this.index = -1;
    }
    
    @Override
    public void from_any(final Any any) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        super.from_any(any);
        this.index = -1;
    }
    
    @Override
    public Any to_any() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return DynAnyUtil.copy(this.any, this.orb);
    }
    
    @Override
    public boolean equal(final DynAny dynAny) {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        return dynAny == this || (this.any.type().equal(dynAny.type()) && this.any.equal(this.getAny(dynAny)));
    }
    
    @Override
    public void destroy() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.status == 0) {
            this.status = 2;
        }
    }
    
    @Override
    public DynAny copy() {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        try {
            return DynAnyUtil.createMostDerivedDynAny(this.any, this.orb, true);
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return null;
        }
    }
    
    @Override
    public DynAny current_component() throws TypeMismatch {
        return null;
    }
    
    @Override
    public int component_count() {
        return 0;
    }
    
    @Override
    public boolean next() {
        return false;
    }
    
    @Override
    public boolean seek(final int n) {
        return false;
    }
    
    @Override
    public void rewind() {
    }
    
    @Override
    public void insert_boolean(final boolean b) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 8) {
            throw new TypeMismatch();
        }
        this.any.insert_boolean(b);
    }
    
    @Override
    public void insert_octet(final byte b) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 10) {
            throw new TypeMismatch();
        }
        this.any.insert_octet(b);
    }
    
    @Override
    public void insert_char(final char c) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 9) {
            throw new TypeMismatch();
        }
        this.any.insert_char(c);
    }
    
    @Override
    public void insert_short(final short n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 2) {
            throw new TypeMismatch();
        }
        this.any.insert_short(n);
    }
    
    @Override
    public void insert_ushort(final short n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 4) {
            throw new TypeMismatch();
        }
        this.any.insert_ushort(n);
    }
    
    @Override
    public void insert_long(final int n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 3) {
            throw new TypeMismatch();
        }
        this.any.insert_long(n);
    }
    
    @Override
    public void insert_ulong(final int n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 5) {
            throw new TypeMismatch();
        }
        this.any.insert_ulong(n);
    }
    
    @Override
    public void insert_float(final float n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 6) {
            throw new TypeMismatch();
        }
        this.any.insert_float(n);
    }
    
    @Override
    public void insert_double(final double n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 7) {
            throw new TypeMismatch();
        }
        this.any.insert_double(n);
    }
    
    @Override
    public void insert_string(final String s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 18) {
            throw new TypeMismatch();
        }
        if (s == null) {
            throw new InvalidValue();
        }
        try {
            if (this.any.type().length() > 0 && this.any.type().length() < s.length()) {
                throw new InvalidValue();
            }
        }
        catch (final BadKind badKind) {}
        this.any.insert_string(s);
    }
    
    @Override
    public void insert_reference(final Object object) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 14) {
            throw new TypeMismatch();
        }
        this.any.insert_Object(object);
    }
    
    @Override
    public void insert_typecode(final TypeCode typeCode) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 12) {
            throw new TypeMismatch();
        }
        this.any.insert_TypeCode(typeCode);
    }
    
    @Override
    public void insert_longlong(final long n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 23) {
            throw new TypeMismatch();
        }
        this.any.insert_longlong(n);
    }
    
    @Override
    public void insert_ulonglong(final long n) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 24) {
            throw new TypeMismatch();
        }
        this.any.insert_ulonglong(n);
    }
    
    @Override
    public void insert_wchar(final char c) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 26) {
            throw new TypeMismatch();
        }
        this.any.insert_wchar(c);
    }
    
    @Override
    public void insert_wstring(final String s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 27) {
            throw new TypeMismatch();
        }
        if (s == null) {
            throw new InvalidValue();
        }
        try {
            if (this.any.type().length() > 0 && this.any.type().length() < s.length()) {
                throw new InvalidValue();
            }
        }
        catch (final BadKind badKind) {}
        this.any.insert_wstring(s);
    }
    
    @Override
    public void insert_any(final Any any) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 11) {
            throw new TypeMismatch();
        }
        this.any.insert_any(any);
    }
    
    @Override
    public void insert_dyn_any(final DynAny dynAny) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 11) {
            throw new TypeMismatch();
        }
        this.any.insert_any(dynAny.to_any());
    }
    
    @Override
    public void insert_val(final Serializable s) throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        final int value = this.any.type().kind().value();
        if (value != 29 && value != 30) {
            throw new TypeMismatch();
        }
        this.any.insert_Value(s);
    }
    
    @Override
    public Serializable get_val() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        final int value = this.any.type().kind().value();
        if (value != 29 && value != 30) {
            throw new TypeMismatch();
        }
        return this.any.extract_Value();
    }
    
    @Override
    public boolean get_boolean() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 8) {
            throw new TypeMismatch();
        }
        return this.any.extract_boolean();
    }
    
    @Override
    public byte get_octet() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 10) {
            throw new TypeMismatch();
        }
        return this.any.extract_octet();
    }
    
    @Override
    public char get_char() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 9) {
            throw new TypeMismatch();
        }
        return this.any.extract_char();
    }
    
    @Override
    public short get_short() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 2) {
            throw new TypeMismatch();
        }
        return this.any.extract_short();
    }
    
    @Override
    public short get_ushort() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 4) {
            throw new TypeMismatch();
        }
        return this.any.extract_ushort();
    }
    
    @Override
    public int get_long() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 3) {
            throw new TypeMismatch();
        }
        return this.any.extract_long();
    }
    
    @Override
    public int get_ulong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 5) {
            throw new TypeMismatch();
        }
        return this.any.extract_ulong();
    }
    
    @Override
    public float get_float() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 6) {
            throw new TypeMismatch();
        }
        return this.any.extract_float();
    }
    
    @Override
    public double get_double() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 7) {
            throw new TypeMismatch();
        }
        return this.any.extract_double();
    }
    
    @Override
    public String get_string() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 18) {
            throw new TypeMismatch();
        }
        return this.any.extract_string();
    }
    
    @Override
    public Object get_reference() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 14) {
            throw new TypeMismatch();
        }
        return this.any.extract_Object();
    }
    
    @Override
    public TypeCode get_typecode() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 12) {
            throw new TypeMismatch();
        }
        return this.any.extract_TypeCode();
    }
    
    @Override
    public long get_longlong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 23) {
            throw new TypeMismatch();
        }
        return this.any.extract_longlong();
    }
    
    @Override
    public long get_ulonglong() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 24) {
            throw new TypeMismatch();
        }
        return this.any.extract_ulonglong();
    }
    
    @Override
    public char get_wchar() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 26) {
            throw new TypeMismatch();
        }
        return this.any.extract_wchar();
    }
    
    @Override
    public String get_wstring() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 27) {
            throw new TypeMismatch();
        }
        return this.any.extract_wstring();
    }
    
    @Override
    public Any get_any() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 11) {
            throw new TypeMismatch();
        }
        return this.any.extract_any();
    }
    
    @Override
    public DynAny get_dyn_any() throws TypeMismatch, InvalidValue {
        if (this.status == 2) {
            throw this.wrapper.dynAnyDestroyed();
        }
        if (this.any.type().kind().value() != 11) {
            throw new TypeMismatch();
        }
        try {
            return DynAnyUtil.createMostDerivedDynAny(this.any.extract_any(), this.orb, true);
        }
        catch (final InconsistentTypeCode inconsistentTypeCode) {
            return null;
        }
    }
}
