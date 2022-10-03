package org.omg.CORBA;

import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class DoubleSeqHelper
{
    private static String _id;
    private static TypeCode __typeCode;
    
    public static void insert(final Any any, final double[] array) {
        final OutputStream create_output_stream = any.create_output_stream();
        any.type(type());
        write(create_output_stream, array);
        any.read_value(create_output_stream.create_input_stream(), type());
    }
    
    public static double[] extract(final Any any) {
        return read(any.create_input_stream());
    }
    
    public static synchronized TypeCode type() {
        if (DoubleSeqHelper.__typeCode == null) {
            DoubleSeqHelper.__typeCode = ORB.init().get_primitive_tc(TCKind.tk_double);
            DoubleSeqHelper.__typeCode = ORB.init().create_sequence_tc(0, DoubleSeqHelper.__typeCode);
            DoubleSeqHelper.__typeCode = ORB.init().create_alias_tc(id(), "DoubleSeq", DoubleSeqHelper.__typeCode);
        }
        return DoubleSeqHelper.__typeCode;
    }
    
    public static String id() {
        return DoubleSeqHelper._id;
    }
    
    public static double[] read(final InputStream inputStream) {
        final int read_long = inputStream.read_long();
        final double[] array = new double[read_long];
        inputStream.read_double_array(array, 0, read_long);
        return array;
    }
    
    public static void write(final OutputStream outputStream, final double[] array) {
        outputStream.write_long(array.length);
        outputStream.write_double_array(array, 0, array.length);
    }
    
    static {
        DoubleSeqHelper._id = "IDL:omg.org/CORBA/DoubleSeq:1.0";
        DoubleSeqHelper.__typeCode = null;
    }
}
