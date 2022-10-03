package com.sun.org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.ORB;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;

public final class ExcDescriptionSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final ExceptionDescription[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static ExceptionDescription[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ExcDescriptionSeqHelper.__typeCode == null) {
            ExcDescriptionSeqHelper.__typeCode = ExceptionDescriptionHelper.type();
            ExcDescriptionSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ExcDescriptionSeqHelper.__typeCode);
            ExcDescriptionSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ExcDescriptionSeq", ExcDescriptionSeqHelper.__typeCode);
        }
        return ExcDescriptionSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ExcDescriptionSeqHelper._id;
    }
    
    public static ExceptionDescription[] read(final InputStream inputStream) {
        final ExceptionDescription[] array = new ExceptionDescription[inputStream.read_long()];
        for (int i = 0; i < array.length; ++i) {
            array[i] = ExceptionDescriptionHelper.read(inputStream);
        }
        return array;
    }
    
    public static void write(final OutputStream outputStream, final ExceptionDescription[] array) {
        outputStream.write_long(array.length);
        for (int i = 0; i < array.length; ++i) {
            ExceptionDescriptionHelper.write(outputStream, array[i]);
        }
    }
    
    static {
        ExcDescriptionSeqHelper._id = "IDL:omg.org/CORBA/ExcDescriptionSeq:1.0";
        ExcDescriptionSeqHelper.__typeCode = null;
    }
}
