package org.omg.CORBA;

import org.omg.CORBA.DynAnyPackage.TypeMismatch;
import java.io.Serializable;
import org.omg.CORBA.DynAnyPackage.InvalidValue;
import org.omg.CORBA.DynAnyPackage.Invalid;

@Deprecated
public interface DynAny extends Object
{
    TypeCode type();
    
    void assign(final DynAny p0) throws Invalid;
    
    void from_any(final Any p0) throws Invalid;
    
    Any to_any() throws Invalid;
    
    void destroy();
    
    DynAny copy();
    
    void insert_boolean(final boolean p0) throws InvalidValue;
    
    void insert_octet(final byte p0) throws InvalidValue;
    
    void insert_char(final char p0) throws InvalidValue;
    
    void insert_short(final short p0) throws InvalidValue;
    
    void insert_ushort(final short p0) throws InvalidValue;
    
    void insert_long(final int p0) throws InvalidValue;
    
    void insert_ulong(final int p0) throws InvalidValue;
    
    void insert_float(final float p0) throws InvalidValue;
    
    void insert_double(final double p0) throws InvalidValue;
    
    void insert_string(final String p0) throws InvalidValue;
    
    void insert_reference(final Object p0) throws InvalidValue;
    
    void insert_typecode(final TypeCode p0) throws InvalidValue;
    
    void insert_longlong(final long p0) throws InvalidValue;
    
    void insert_ulonglong(final long p0) throws InvalidValue;
    
    void insert_wchar(final char p0) throws InvalidValue;
    
    void insert_wstring(final String p0) throws InvalidValue;
    
    void insert_any(final Any p0) throws InvalidValue;
    
    void insert_val(final Serializable p0) throws InvalidValue;
    
    Serializable get_val() throws TypeMismatch;
    
    boolean get_boolean() throws TypeMismatch;
    
    byte get_octet() throws TypeMismatch;
    
    char get_char() throws TypeMismatch;
    
    short get_short() throws TypeMismatch;
    
    short get_ushort() throws TypeMismatch;
    
    int get_long() throws TypeMismatch;
    
    int get_ulong() throws TypeMismatch;
    
    float get_float() throws TypeMismatch;
    
    double get_double() throws TypeMismatch;
    
    String get_string() throws TypeMismatch;
    
    Object get_reference() throws TypeMismatch;
    
    TypeCode get_typecode() throws TypeMismatch;
    
    long get_longlong() throws TypeMismatch;
    
    long get_ulonglong() throws TypeMismatch;
    
    char get_wchar() throws TypeMismatch;
    
    String get_wstring() throws TypeMismatch;
    
    Any get_any() throws TypeMismatch;
    
    DynAny current_component();
    
    boolean next();
    
    boolean seek(final int p0);
    
    void rewind();
}
