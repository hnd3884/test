package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ValueMemberSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ValueMember[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ValueMember[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ValueMemberSeqHelper.__typeCode == null) {
            ValueMemberSeqHelper.__typeCode = ValueMemberHelper.type();
            ValueMemberSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ValueMemberSeqHelper.__typeCode);
            ValueMemberSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ValueMemberSeq", ValueMemberSeqHelper.__typeCode);
        }
        return ValueMemberSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ValueMemberSeqHelper._id;
    }
    
    public static ValueMember[] read(final InputStream inputStream) {
        final ValueMember[] array = new ValueMember[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ValueMemberHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ValueMember[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ValueMemberHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ValueMemberSeqHelper._id = "IDL:omg.org/CORBA/ValueMemberSeq:1.0";
        ValueMemberSeqHelper.__typeCode = null;
    }
}
