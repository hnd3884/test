package org.omg.DynamicAny;

import java.io.Serializable;
import org.omg.CORBA.Object;
import org.omg.DynamicAny.DynAnyPackage.InvalidValue;
import org.omg.CORBA.Any;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;
import org.omg.CORBA.TypeCode;

public interface DynAnyOperations
{
    TypeCode type();
    
    void assign(final DynAny p0) throws TypeMismatch;
    
    void from_any(final Any p0) throws TypeMismatch, InvalidValue;
    
    Any to_any();
    
    boolean equal(final DynAny p0);
    
    void destroy();
    
    DynAny copy();
    
    void insert_boolean(final boolean p0) throws TypeMismatch, InvalidValue;
    
    void insert_octet(final byte p0) throws TypeMismatch, InvalidValue;
    
    void insert_char(final char p0) throws TypeMismatch, InvalidValue;
    
    void insert_short(final short p0) throws TypeMismatch, InvalidValue;
    
    void insert_ushort(final short p0) throws TypeMismatch, InvalidValue;
    
    void insert_long(final int p0) throws TypeMismatch, InvalidValue;
    
    void insert_ulong(final int p0) throws TypeMismatch, InvalidValue;
    
    void insert_float(final float p0) throws TypeMismatch, InvalidValue;
    
    void insert_double(final double p0) throws TypeMismatch, InvalidValue;
    
    void insert_string(final String p0) throws TypeMismatch, InvalidValue;
    
    void insert_reference(final org.omg.CORBA.Object p0) throws TypeMismatch, InvalidValue;
    
    void insert_typecode(final TypeCode p0) throws TypeMismatch, InvalidValue;
    
    void insert_longlong(final long p0) throws TypeMismatch, InvalidValue;
    
    void insert_ulonglong(final long p0) throws TypeMismatch, InvalidValue;
    
    void insert_wchar(final char p0) throws TypeMismatch, InvalidValue;
    
    void insert_wstring(final String p0) throws TypeMismatch, InvalidValue;
    
    void insert_any(final Any p0) throws TypeMismatch, InvalidValue;
    
    void insert_dyn_any(final DynAny p0) throws TypeMismatch, InvalidValue;
    
    void insert_val(final Serializable p0) throws TypeMismatch, InvalidValue;
    
    boolean get_boolean() throws TypeMismatch, InvalidValue;
    
    byte get_octet() throws TypeMismatch, InvalidValue;
    
    char get_char() throws TypeMismatch, InvalidValue;
    
    short get_short() throws TypeMismatch, InvalidValue;
    
    short get_ushort() throws TypeMismatch, InvalidValue;
    
    int get_long() throws TypeMismatch, InvalidValue;
    
    int get_ulong() throws TypeMismatch, InvalidValue;
    
    float get_float() throws TypeMismatch, InvalidValue;
    
    double get_double() throws TypeMismatch, InvalidValue;
    
    String get_string() throws TypeMismatch, InvalidValue;
    
    org.omg.CORBA.Object get_reference() throws TypeMismatch, InvalidValue;
    
    TypeCode get_typecode() throws TypeMismatch, InvalidValue;
    
    long get_longlong() throws TypeMismatch, InvalidValue;
    
    long get_ulonglong() throws TypeMismatch, InvalidValue;
    
    char get_wchar() throws TypeMismatch, InvalidValue;
    
    String get_wstring() throws TypeMismatch, InvalidValue;
    
    Any get_any() throws TypeMismatch, InvalidValue;
    
    DynAny get_dyn_any() throws TypeMismatch, InvalidValue;
    
    Serializable get_val() throws TypeMismatch, InvalidValue;
    
    boolean seek(final int p0);
    
    void rewind();
    
    boolean next();
    
    int component_count();
    
    DynAny current_component() throws TypeMismatch;
}
