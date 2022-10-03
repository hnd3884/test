package org.omg.CORBA;

import java.io.Serializable;
import org.omg.CORBA.portable.ValueBase;

public interface DataInputStream extends ValueBase
{
    Any read_any();
    
    boolean read_boolean();
    
    char read_char();
    
    char read_wchar();
    
    byte read_octet();
    
    short read_short();
    
    short read_ushort();
    
    int read_long();
    
    int read_ulong();
    
    long read_longlong();
    
    long read_ulonglong();
    
    float read_float();
    
    double read_double();
    
    String read_string();
    
    String read_wstring();
    
    org.omg.CORBA.Object read_Object();
    
    Object read_Abstract();
    
    Serializable read_Value();
    
    TypeCode read_TypeCode();
    
    void read_any_array(final AnySeqHolder p0, final int p1, final int p2);
    
    void read_boolean_array(final BooleanSeqHolder p0, final int p1, final int p2);
    
    void read_char_array(final CharSeqHolder p0, final int p1, final int p2);
    
    void read_wchar_array(final WCharSeqHolder p0, final int p1, final int p2);
    
    void read_octet_array(final OctetSeqHolder p0, final int p1, final int p2);
    
    void read_short_array(final ShortSeqHolder p0, final int p1, final int p2);
    
    void read_ushort_array(final UShortSeqHolder p0, final int p1, final int p2);
    
    void read_long_array(final LongSeqHolder p0, final int p1, final int p2);
    
    void read_ulong_array(final ULongSeqHolder p0, final int p1, final int p2);
    
    void read_ulonglong_array(final ULongLongSeqHolder p0, final int p1, final int p2);
    
    void read_longlong_array(final LongLongSeqHolder p0, final int p1, final int p2);
    
    void read_float_array(final FloatSeqHolder p0, final int p1, final int p2);
    
    void read_double_array(final DoubleSeqHolder p0, final int p1, final int p2);
}
