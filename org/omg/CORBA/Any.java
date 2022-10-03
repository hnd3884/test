package org.omg.CORBA;

import java.math.BigDecimal;
import org.omg.CORBA.portable.Streamable;
import java.io.Serializable;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.IDLEntity;

public abstract class Any implements IDLEntity
{
    public abstract boolean equal(final Any p0);
    
    public abstract TypeCode type();
    
    public abstract void type(final TypeCode p0);
    
    public abstract void read_value(final InputStream p0, final TypeCode p1) throws MARSHAL;
    
    public abstract void write_value(final OutputStream p0);
    
    public abstract OutputStream create_output_stream();
    
    public abstract InputStream create_input_stream();
    
    public abstract short extract_short() throws BAD_OPERATION;
    
    public abstract void insert_short(final short p0);
    
    public abstract int extract_long() throws BAD_OPERATION;
    
    public abstract void insert_long(final int p0);
    
    public abstract long extract_longlong() throws BAD_OPERATION;
    
    public abstract void insert_longlong(final long p0);
    
    public abstract short extract_ushort() throws BAD_OPERATION;
    
    public abstract void insert_ushort(final short p0);
    
    public abstract int extract_ulong() throws BAD_OPERATION;
    
    public abstract void insert_ulong(final int p0);
    
    public abstract long extract_ulonglong() throws BAD_OPERATION;
    
    public abstract void insert_ulonglong(final long p0);
    
    public abstract float extract_float() throws BAD_OPERATION;
    
    public abstract void insert_float(final float p0);
    
    public abstract double extract_double() throws BAD_OPERATION;
    
    public abstract void insert_double(final double p0);
    
    public abstract boolean extract_boolean() throws BAD_OPERATION;
    
    public abstract void insert_boolean(final boolean p0);
    
    public abstract char extract_char() throws BAD_OPERATION;
    
    public abstract void insert_char(final char p0) throws DATA_CONVERSION;
    
    public abstract char extract_wchar() throws BAD_OPERATION;
    
    public abstract void insert_wchar(final char p0);
    
    public abstract byte extract_octet() throws BAD_OPERATION;
    
    public abstract void insert_octet(final byte p0);
    
    public abstract Any extract_any() throws BAD_OPERATION;
    
    public abstract void insert_any(final Any p0);
    
    public abstract org.omg.CORBA.Object extract_Object() throws BAD_OPERATION;
    
    public abstract void insert_Object(final org.omg.CORBA.Object p0);
    
    public abstract Serializable extract_Value() throws BAD_OPERATION;
    
    public abstract void insert_Value(final Serializable p0);
    
    public abstract void insert_Value(final Serializable p0, final TypeCode p1) throws MARSHAL;
    
    public abstract void insert_Object(final org.omg.CORBA.Object p0, final TypeCode p1) throws BAD_PARAM;
    
    public abstract String extract_string() throws BAD_OPERATION;
    
    public abstract void insert_string(final String p0) throws DATA_CONVERSION, MARSHAL;
    
    public abstract String extract_wstring() throws BAD_OPERATION;
    
    public abstract void insert_wstring(final String p0) throws MARSHAL;
    
    public abstract TypeCode extract_TypeCode() throws BAD_OPERATION;
    
    public abstract void insert_TypeCode(final TypeCode p0);
    
    @Deprecated
    public Principal extract_Principal() throws BAD_OPERATION {
        throw new NO_IMPLEMENT();
    }
    
    @Deprecated
    public void insert_Principal(final Principal principal) {
        throw new NO_IMPLEMENT();
    }
    
    public Streamable extract_Streamable() throws BAD_INV_ORDER {
        throw new NO_IMPLEMENT();
    }
    
    public void insert_Streamable(final Streamable streamable) {
        throw new NO_IMPLEMENT();
    }
    
    public BigDecimal extract_fixed() {
        throw new NO_IMPLEMENT();
    }
    
    public void insert_fixed(final BigDecimal bigDecimal) {
        throw new NO_IMPLEMENT();
    }
    
    public void insert_fixed(final BigDecimal bigDecimal, final TypeCode typeCode) throws BAD_INV_ORDER {
        throw new NO_IMPLEMENT();
    }
}
