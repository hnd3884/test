package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ParDescriptionSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ParameterDescription[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ParameterDescription[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ParDescriptionSeqHelper.__typeCode == null) {
            ParDescriptionSeqHelper.__typeCode = ParameterDescriptionHelper.type();
            ParDescriptionSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ParDescriptionSeqHelper.__typeCode);
            ParDescriptionSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ParDescriptionSeq", ParDescriptionSeqHelper.__typeCode);
        }
        return ParDescriptionSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ParDescriptionSeqHelper._id;
    }
    
    public static ParameterDescription[] read(final InputStream inputStream) {
        final ParameterDescription[] array = new ParameterDescription[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ParameterDescriptionHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ParameterDescription[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ParameterDescriptionHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ParDescriptionSeqHelper._id = "IDL:omg.org/CORBA/ParDescriptionSeq:1.0";
        ParDescriptionSeqHelper.__typeCode = null;
    }
}
