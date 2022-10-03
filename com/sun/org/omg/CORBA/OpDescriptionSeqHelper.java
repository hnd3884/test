package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class OpDescriptionSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final OperationDescription[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static OperationDescription[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (OpDescriptionSeqHelper.__typeCode == null) {
            OpDescriptionSeqHelper.__typeCode = OperationDescriptionHelper.type();
            OpDescriptionSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, OpDescriptionSeqHelper.__typeCode);
            OpDescriptionSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "OpDescriptionSeq", OpDescriptionSeqHelper.__typeCode);
        }
        return OpDescriptionSeqHelper.__typeCode;
    }
    
    public static String id() {
        return OpDescriptionSeqHelper._id;
    }
    
    public static OperationDescription[] read(final InputStream inputStream) {
        final OperationDescription[] array = new OperationDescription[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = OperationDescriptionHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final OperationDescription[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            OperationDescriptionHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        OpDescriptionSeqHelper._id = "IDL:omg.org/CORBA/OpDescriptionSeq:1.0";
        OpDescriptionSeqHelper.__typeCode = null;
    }
}
