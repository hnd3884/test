package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ULongLongSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final long[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static long[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ULongLongSeqHelper.__typeCode == null) {
            ULongLongSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulonglong);
            ULongLongSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ULongLongSeqHelper.__typeCode);
            ULongLongSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ULongLongSeq", ULongLongSeqHelper.__typeCode);
        }
        return ULongLongSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ULongLongSeqHelper._id;
    }
    
    public static long[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final long[] array = new long[read_long];
        inputStream.read_ulonglong_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final long[] array) {
        outputStream.write_long(array.length);
        outputStream.write_ulonglong_array(array, 0, array.length);
    }
    
    static {
        ULongLongSeqHelper._id = "IDL:omg.org/CORBA/ULongLongSeq:1.0";
        ULongLongSeqHelper.__typeCode = null;
    }
}
