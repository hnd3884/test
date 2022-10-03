package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class ShortSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final short[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static short[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (ShortSeqHelper.__typeCode == null) {
            ShortSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_short);
            ShortSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, ShortSeqHelper.__typeCode);
            ShortSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "ShortSeq", ShortSeqHelper.__typeCode);
        }
        return ShortSeqHelper.__typeCode;
    }
    
    public static String id() {
        return ShortSeqHelper._id;
    }
    
    public static short[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final short[] array = new short[read_long];
        inputStream.read_short_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final short[] array) {
        outputStream.write_long(array.length);
        outputStream.write_short_array(array, 0, array.length);
    }
    
    static {
        ShortSeqHelper._id = "IDL:omg.org/CORBA/ShortSeq:1.0";
        ShortSeqHelper.__typeCode = null;
    }
}
