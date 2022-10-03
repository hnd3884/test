package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class UShortSeqHelper
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
        if (UShortSeqHelper.__typeCode == null) {
            UShortSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_ushort);
            UShortSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, UShortSeqHelper.__typeCode);
            UShortSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "UShortSeq", UShortSeqHelper.__typeCode);
        }
        return UShortSeqHelper.__typeCode;
    }
    
    public static String id() {
        return UShortSeqHelper._id;
    }
    
    public static short[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final short[] array = new short[read_long];
        inputStream.read_ushort_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final short[] array) {
        outputStream.write_long(array.length);
        outputStream.write_ushort_array(array, 0, array.length);
    }
    
    static {
        UShortSeqHelper._id = "IDL:omg.org/CORBA/UShortSeq:1.0";
        UShortSeqHelper.__typeCode = null;
    }
}
