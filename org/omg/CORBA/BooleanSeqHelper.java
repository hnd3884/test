package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class BooleanSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final boolean[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static boolean[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (BooleanSeqHelper.__typeCode == null) {
            BooleanSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_boolean);
            BooleanSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, BooleanSeqHelper.__typeCode);
            BooleanSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "BooleanSeq", BooleanSeqHelper.__typeCode);
        }
        return BooleanSeqHelper.__typeCode;
    }
    
    public static String id() {
        return BooleanSeqHelper._id;
    }
    
    public static boolean[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final boolean[] array = new boolean[read_long];
        inputStream.read_boolean_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final boolean[] array) {
        outputStream.write_long(array.length);
        outputStream.write_boolean_array(array, 0, array.length);
    }
    
    static {
        BooleanSeqHelper._id = "IDL:omg.org/CORBA/BooleanSeq:1.0";
        BooleanSeqHelper.__typeCode = null;
    }
}
