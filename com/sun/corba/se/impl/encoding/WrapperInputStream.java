package com.sun.corba.se.impl.encoding;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Context;
import java.math.BigDecimal;
import org.omg.CORBA.Principal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import java.io.Serializable;
import org.omg.CORBA.Object;
import java.io.IOException;
import java.util.Map;
import org.omg.CORBA_2_3.portable.InputStream;

public class WrapperInputStream extends InputStream implements TypeCodeReader
{
    private CDRInputStream stream;
    private Map typeMap;
    private int startPos;
    
    public WrapperInputStream(final CDRInputStream stream) {
        this.typeMap = null;
        this.startPos = 0;
        this.stream = stream;
        this.startPos = this.stream.getPosition();
    }
    
    @Override
    public int read() throws IOException {
        return this.stream.read();
    }
    
    @Override
    public int read(final byte[] array) throws IOException {
        return this.stream.read(array);
    }
    
    @Override
    public int read(final byte[] array, final int n, final int n2) throws IOException {
        return this.stream.read(array, n, n2);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        return this.stream.skip(n);
    }
    
    @Override
    public int available() throws IOException {
        return this.stream.available();
    }
    
    @Override
    public void close() throws IOException {
        this.stream.close();
    }
    
    @Override
    public void mark(final int n) {
        this.stream.mark(n);
    }
    
    @Override
    public void reset() {
        this.stream.reset();
    }
    
    @Override
    public boolean markSupported() {
        return this.stream.markSupported();
    }
    
    @Override
    public int getPosition() {
        return this.stream.getPosition();
    }
    
    @Override
    public void consumeEndian() {
        this.stream.consumeEndian();
    }
    
    @Override
    public boolean read_boolean() {
        return this.stream.read_boolean();
    }
    
    @Override
    public char read_char() {
        return this.stream.read_char();
    }
    
    @Override
    public char read_wchar() {
        return this.stream.read_wchar();
    }
    
    @Override
    public byte read_octet() {
        return this.stream.read_octet();
    }
    
    @Override
    public short read_short() {
        return this.stream.read_short();
    }
    
    @Override
    public short read_ushort() {
        return this.stream.read_ushort();
    }
    
    @Override
    public int read_long() {
        return this.stream.read_long();
    }
    
    @Override
    public int read_ulong() {
        return this.stream.read_ulong();
    }
    
    @Override
    public long read_longlong() {
        return this.stream.read_longlong();
    }
    
    @Override
    public long read_ulonglong() {
        return this.stream.read_ulonglong();
    }
    
    @Override
    public float read_float() {
        return this.stream.read_float();
    }
    
    @Override
    public double read_double() {
        return this.stream.read_double();
    }
    
    @Override
    public String read_string() {
        return this.stream.read_string();
    }
    
    @Override
    public String read_wstring() {
        return this.stream.read_wstring();
    }
    
    @Override
    public void read_boolean_array(final boolean[] array, final int n, final int n2) {
        this.stream.read_boolean_array(array, n, n2);
    }
    
    @Override
    public void read_char_array(final char[] array, final int n, final int n2) {
        this.stream.read_char_array(array, n, n2);
    }
    
    @Override
    public void read_wchar_array(final char[] array, final int n, final int n2) {
        this.stream.read_wchar_array(array, n, n2);
    }
    
    @Override
    public void read_octet_array(final byte[] array, final int n, final int n2) {
        this.stream.read_octet_array(array, n, n2);
    }
    
    @Override
    public void read_short_array(final short[] array, final int n, final int n2) {
        this.stream.read_short_array(array, n, n2);
    }
    
    @Override
    public void read_ushort_array(final short[] array, final int n, final int n2) {
        this.stream.read_ushort_array(array, n, n2);
    }
    
    @Override
    public void read_long_array(final int[] array, final int n, final int n2) {
        this.stream.read_long_array(array, n, n2);
    }
    
    @Override
    public void read_ulong_array(final int[] array, final int n, final int n2) {
        this.stream.read_ulong_array(array, n, n2);
    }
    
    @Override
    public void read_longlong_array(final long[] array, final int n, final int n2) {
        this.stream.read_longlong_array(array, n, n2);
    }
    
    @Override
    public void read_ulonglong_array(final long[] array, final int n, final int n2) {
        this.stream.read_ulonglong_array(array, n, n2);
    }
    
    @Override
    public void read_float_array(final float[] array, final int n, final int n2) {
        this.stream.read_float_array(array, n, n2);
    }
    
    @Override
    public void read_double_array(final double[] array, final int n, final int n2) {
        this.stream.read_double_array(array, n, n2);
    }
    
    @Override
    public org.omg.CORBA.Object read_Object() {
        return this.stream.read_Object();
    }
    
    @Override
    public Serializable read_value() {
        return this.stream.read_value();
    }
    
    @Override
    public TypeCode read_TypeCode() {
        return this.stream.read_TypeCode();
    }
    
    @Override
    public Any read_any() {
        return this.stream.read_any();
    }
    
    @Override
    public Principal read_Principal() {
        return this.stream.read_Principal();
    }
    
    @Override
    public BigDecimal read_fixed() {
        return this.stream.read_fixed();
    }
    
    @Override
    public Context read_Context() {
        return this.stream.read_Context();
    }
    
    @Override
    public ORB orb() {
        return this.stream.orb();
    }
    
    @Override
    public void addTypeCodeAtPosition(final TypeCodeImpl typeCodeImpl, final int n) {
        if (this.typeMap == null) {
            this.typeMap = new HashMap(16);
        }
        this.typeMap.put(new Integer(n), typeCodeImpl);
    }
    
    @Override
    public TypeCodeImpl getTypeCodeAtPosition(final int n) {
        if (this.typeMap == null) {
            return null;
        }
        return this.typeMap.get(new Integer(n));
    }
    
    @Override
    public void setEnclosingInputStream(final InputStream inputStream) {
    }
    
    @Override
    public TypeCodeReader getTopLevelStream() {
        return this;
    }
    
    @Override
    public int getTopLevelPosition() {
        return this.getPosition() - this.startPos;
    }
    
    @Override
    public void performORBVersionSpecificInit() {
        this.stream.performORBVersionSpecificInit();
    }
    
    @Override
    public void resetCodeSetConverters() {
        this.stream.resetCodeSetConverters();
    }
    
    @Override
    public void printTypeMap() {
        System.out.println("typeMap = {");
        final ArrayList list = new ArrayList(this.typeMap.keySet());
        Collections.sort((List<Comparable>)list);
        for (final Integer n : list) {
            System.out.println("  key = " + (int)n + ", value = " + ((TypeCodeImpl)this.typeMap.get(n)).description());
        }
        System.out.println("}");
    }
}
