package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ULongSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final int[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static int[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ULongSeqHelper.__typeCode == null) {
            ULongSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_ulong);
            ULongSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ULongSeqHelper.__typeCode);
            ULongSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ULongSeq", ULongSeqHelper.__typeCode);
        }
        return ULongSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ULongSeqHelper._id;
    }
    
    public static int[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final int[] array = new int[read_long];
        inputStream.read_ulong_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final int[] array) {
        outputStream.write_long(array.length);
        outputStream.write_ulong_array(array, 0, array.length);
    }
    
    static {
        ULongSeqHelper._id = "IDL:omg.org/CORBA/ULongSeq:1.0";
        ULongSeqHelper.__typeCode = null;
    }
}
