package com.sun.jmx.remote.protocol.iiop;

import org.omg.CORBA.NO_IMPLEMENT;
import org.omg.CORBA.portable.BoxedValueHelper;
import java.io.Serializable;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import java.io.IOException;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.Object;
import org.omg.CORBA_2_3.portable.InputStream;

public class ProxyInputStream extends InputStream
{
    protected final org.omg.CORBA.portable.InputStream in;
    
    public ProxyInputStream(final org.omg.CORBA.portable.InputStream in) {
        this.in = in;
    }
    
    @Override
    public boolean read_boolean() {
        return this.in.read_boolean();
    }
    
    @Override
    public char read_char() {
        return this.in.read_char();
    }
    
    @Override
    public char read_wchar() {
        return this.in.read_wchar();
    }
    
    @Override
    public byte read_octet() {
        return this.in.read_octet();
    }
    
    @Override
    public short read_short() {
        return this.in.read_short();
    }
    
    @Override
    public short read_ushort() {
        return this.in.read_ushort();
    }
    
    @Override
    public int read_long() {
        return this.in.read_long();
    }
    
    @Override
    public int read_ulong() {
        return this.in.read_ulong();
    }
    
    @Override
    public long read_longlong() {
        return this.in.read_longlong();
    }
    
    @Override
    public long read_ulonglong() {
        return this.in.read_ulonglong();
    }
    
    @Override
    public float read_float() {
        return this.in.read_float();
    }
    
    @Override
    public double read_double() {
        return this.in.read_double();
    }
    
    @Override
    public String read_string() {
        return this.in.read_string();
    }
    
    @Override
    public String read_wstring() {
        return this.in.read_wstring();
    }
    
    @Override
    public void read_boolean_array(final boolean[] array, final int n, final int n2) {
        this.in.read_boolean_array(array, n, n2);
    }
    
    @Override
    public void read_char_array(final char[] array, final int n, final int n2) {
        this.in.read_char_array(array, n, n2);
    }
    
    @Override
    public void read_wchar_array(final char[] array, final int n, final int n2) {
        this.in.read_wchar_array(array, n, n2);
    }
    
    @Override
    public void read_octet_array(final byte[] array, final int n, final int n2) {
        this.in.read_octet_array(array, n, n2);
    }
    
    @Override
    public void read_short_array(final short[] array, final int n, final int n2) {
        this.in.read_short_array(array, n, n2);
    }
    
    @Override
    public void read_ushort_array(final short[] array, final int n, final int n2) {
        this.in.read_ushort_array(array, n, n2);
    }
    
    @Override
    public void read_long_array(final int[] array, final int n, final int n2) {
        this.in.read_long_array(array, n, n2);
    }
    
    @Override
    public void read_ulong_array(final int[] array, final int n, final int n2) {
        this.in.read_ulong_array(array, n, n2);
    }
    
    @Override
    public void read_longlong_array(final long[] array, final int n, final int n2) {
        this.in.read_longlong_array(array, n, n2);
    }
    
    @Override
    public void read_ulonglong_array(final long[] array, final int n, final int n2) {
        this.in.read_ulonglong_array(array, n, n2);
    }
    
    @Override
    public void read_float_array(final float[] array, final int n, final int n2) {
        this.in.read_float_array(array, n, n2);
    }
    
    @Override
    public void read_double_array(final double[] array, final int n, final int n2) {
        this.in.read_double_array(array, n, n2);
    }
    
    @Override
    public org.omg.CORBA.Object read_Object() {
        return this.in.read_Object();
    }
    
    @Override
    public TypeCode read_TypeCode() {
        return this.in.read_TypeCode();
    }
    
    @Override
    public Any read_any() {
        return this.in.read_any();
    }
    
    @Deprecated
    @Override
    public Principal read_Principal() {
        return this.in.read_Principal();
    }
    
    @Override
    public int read() throws IOException {
        return this.in.read();
    }
    
    @Override
    public BigDecimal read_fixed() {
        return this.in.read_fixed();
    }
    
    @Override
    public Context read_Context() {
        return this.in.read_Context();
    }
    
    @Override
    public org.omg.CORBA.Object read_Object(final Class clazz) {
        return this.in.read_Object(clazz);
    }
    
    @Override
    public ORB orb() {
        return this.in.orb();
    }
    
    @Override
    public Serializable read_value() {
        return this.narrow().read_value();
    }
    
    @Override
    public Serializable read_value(final Class clazz) {
        return this.narrow().read_value(clazz);
    }
    
    @Override
    public Serializable read_value(final BoxedValueHelper boxedValueHelper) {
        return this.narrow().read_value(boxedValueHelper);
    }
    
    @Override
    public Serializable read_value(final String s) {
        return this.narrow().read_value(s);
    }
    
    @Override
    public Serializable read_value(final Serializable s) {
        return this.narrow().read_value(s);
    }
    
    @Override
    public Object read_abstract_interface() {
        return this.narrow().read_abstract_interface();
    }
    
    @Override
    public Object read_abstract_interface(final Class clazz) {
        return this.narrow().read_abstract_interface(clazz);
    }
    
    protected InputStream narrow() {
        if (this.in instanceof InputStream) {
            return (InputStream)this.in;
        }
        throw new NO_IMPLEMENT();
    }
    
    public org.omg.CORBA.portable.InputStream getProxiedInputStream() {
        return this.in;
    }
}
