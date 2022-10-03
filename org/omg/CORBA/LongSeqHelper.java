package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class LongSeqHelper
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
        if (LongSeqHelper.__typeCode == null) {
            LongSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_long);
            LongSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, LongSeqHelper.__typeCode);
            LongSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "LongSeq", LongSeqHelper.__typeCode);
        }
        return LongSeqHelper.__typeCode;
    }
    
    public static String id() {
        return LongSeqHelper._id;
    }
    
    public static int[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final int[] array = new int[read_long];
        inputStream.read_long_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final int[] array) {
        outputStream.write_long(array.length);
        outputStream.write_long_array(array, 0, array.length);
    }
    
    static {
        LongSeqHelper._id = "IDL:omg.org/CORBA/LongSeq:1.0";
        LongSeqHelper.__typeCode = null;
    }
}
