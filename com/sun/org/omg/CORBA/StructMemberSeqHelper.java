package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class StructMemberSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final StructMember[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static StructMember[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (StructMemberSeqHelper.__typeCode == null) {
            StructMemberSeqHelper.__typeCode = StructMemberHelper.type();
            StructMemberSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, StructMemberSeqHelper.__typeCode);
            StructMemberSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "StructMemberSeq", StructMemberSeqHelper.__typeCode);
        }
        return StructMemberSeqHelper.__typeCode;
    }
    
    public static String id() {
        return StructMemberSeqHelper._id;
    }
    
    public static StructMember[] read(final InputStream inputStream) {
        final StructMember[] array = new StructMember[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = StructMemberHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final StructMember[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            StructMemberHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        StructMemberSeqHelper._id = "IDL:omg.org/CORBA/StructMemberSeq:1.0";
        StructMemberSeqHelper.__typeCode = null;
    }
}
